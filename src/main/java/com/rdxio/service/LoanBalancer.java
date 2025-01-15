package com.rdxio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rdxio.model.Loan;
import com.rdxio.repository.LoanRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanBalancer {
    private final LoanRepository loanRepository;
    private final PaymentService paymentService;

    @Autowired
    public LoanBalancer(LoanRepository loanRepository, PaymentService paymentService) {
        this.loanRepository = loanRepository;
        this.paymentService = paymentService;
    }

    public void balanceLoans() {
        List<Loan> overpaidLoans = loanRepository.findOverpaidLoans();
        
        for (Loan overpaidLoan : overpaidLoans) {
            try {
                processOverpaidLoan(overpaidLoan);
            } catch (Exception e) {
                // If any loan fails, stop the entire process
                throw new RuntimeException("Loan balancing failed for loan " + 
                    overpaidLoan.getInvoiceId() + ": " + e.getMessage(), e);
            }
        }
    }

    private void processOverpaidLoan(Loan overpaidLoan) {
        double availableAmount = Math.abs(overpaidLoan.getBalance());
        LocalDate overpaidLoanRepaymentDate = overpaidLoan.getLastRepaymentDate();
        
        List<Loan> openLoans = loanRepository.findCompatibleLoansWithOpenBalance(
            overpaidLoanRepaymentDate, availableAmount);

        for (Loan openLoan : openLoans) {
            LocalDate transferDate = determineTransferDate(overpaidLoan, openLoan);
            if (transferDate == null) continue;

            double transferAmount = Math.min(availableAmount, openLoan.getBalance());
            
            // Process the payment - if it fails, the exception will propagate up
            paymentService.processPayment(
                overpaidLoan.getInvoiceId(), 
                openLoan.getInvoiceId(), 
                transferAmount
            );
            
            availableAmount -= transferAmount;
            if (availableAmount <= 0) break;
        }
    }

    private LocalDate determineTransferDate(Loan overpaidLoan, Loan openLoan) {
        LocalDate proposedDate = overpaidLoan.getLastRepaymentDate();
        
        // If the proposed date is valid for both loans, use it
        if (overpaidLoan.isValidPaymentDate(proposedDate) && openLoan.isValidPaymentDate(proposedDate)) {
            return proposedDate;
        }
        
        // If not, find a valid date within the open loan's range
        LocalDate alternativeDate = findValidDateForOpenLoan(openLoan);
        if (alternativeDate != null && overpaidLoan.isValidPaymentDate(alternativeDate)) {
            return alternativeDate;
        }
        
        return null;
    }

    private LocalDate findValidDateForOpenLoan(Loan openLoan) {
        LocalDate proposedDate = openLoan.getLastRepaymentDate();
        if (proposedDate.isBefore(openLoan.getLoanReleaseDate())) {
            proposedDate = openLoan.getLoanReleaseDate();
        }
        
        if (proposedDate.isAfter(openLoan.getLoanMaturity())) {
            return null;
        }
        
        return proposedDate;
    }
} 