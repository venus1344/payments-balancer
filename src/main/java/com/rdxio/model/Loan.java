package com.rdxio.model;

import java.time.LocalDate;

public class Loan {
    private String invoiceId;
    private double balance;
    private LocalDate loanMaturity;
    private LocalDate lastRepaymentDate;
    private LocalDate loanReleaseDate;

    public Loan(String invoiceId, double balance, LocalDate loanMaturity, 
                LocalDate lastRepaymentDate, LocalDate loanReleaseDate) {
        this.invoiceId = invoiceId;
        this.balance = balance;
        this.loanMaturity = loanMaturity;
        this.lastRepaymentDate = lastRepaymentDate;
        this.loanReleaseDate = loanReleaseDate;
    }

    // Getters and setters
    public String getInvoiceId() { return invoiceId; }
    public double getBalance() { return balance; }
    public LocalDate getLoanMaturity() { return loanMaturity; }
    public LocalDate getLastRepaymentDate() { return lastRepaymentDate; }
    public LocalDate getLoanReleaseDate() { return loanReleaseDate; }

    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setLoanMaturity(LocalDate loanMaturity) { this.loanMaturity = loanMaturity; }
    public void setLastRepaymentDate(LocalDate lastRepaymentDate) { this.lastRepaymentDate = lastRepaymentDate; }
    public void setLoanReleaseDate(LocalDate loanReleaseDate) { this.loanReleaseDate = loanReleaseDate; }

    // Utility method to check if a payment date is valid for this loan
    public boolean isValidPaymentDate(LocalDate paymentDate) {
        return !paymentDate.isBefore(loanReleaseDate) && 
               !paymentDate.isAfter(loanMaturity);
    }
} 