package com.rdxio.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.rdxio.model.Loan;
import java.time.LocalDate;
import java.util.List;

@Repository
public class LoanRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LoanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Loan> findOverpaidLoans() {
        String query = """
            SELECT invoice_id, balance, loan_maturity, last_repayment_date, loan_release_date
            FROM loan_staging_table 
            WHERE balance < 0
            AND loan_maturity >= CURRENT_DATE
            ORDER BY balance ASC
            """;
        
        return jdbcTemplate.query(query, getLoanRowMapper());
    }

    public List<Loan> findCompatibleLoansWithOpenBalance(LocalDate paymentDate, double maxAmount) {
        String query = """
            SELECT invoice_id, balance, loan_maturity, last_repayment_date, loan_release_date
            FROM loan_staging_table 
            WHERE balance > 0
            AND balance <= ?
            AND loan_release_date <= ?
            AND loan_maturity >= ?
            ORDER BY loan_maturity ASC, balance DESC
            """;
        
        return jdbcTemplate.query(query, getLoanRowMapper(), maxAmount, paymentDate, paymentDate);
    }

    private RowMapper<Loan> getLoanRowMapper() {
        return (rs, rowNum) -> new Loan(
            rs.getString("invoice_id"),
            rs.getDouble("balance"),
            rs.getDate("loan_maturity").toLocalDate(),
            rs.getDate("last_repayment_date").toLocalDate(),
            rs.getDate("loan_release_date").toLocalDate()
        );
    }

    public void updateLoanBalance(String invoiceId, double newBalance, LocalDate repaymentDate) {
        String query = """
            UPDATE loan_staging_table 
            SET balance = ?, 
                last_repayment_date = ?
            WHERE invoice_id = ?
            """;
        
        jdbcTemplate.update(query, newBalance, repaymentDate, invoiceId);
    }
} 