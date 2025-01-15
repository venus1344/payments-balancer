package com.rdxio.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.rdxio.service.LoanBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LoanBalancerWorker {
    private static final Logger logger = LoggerFactory.getLogger(LoanBalancerWorker.class);
    private final LoanBalancer loanBalancer;

    @Autowired
    public LoanBalancerWorker(LoanBalancer loanBalancer) {
        this.loanBalancer = loanBalancer;
    }

    @Scheduled(fixedDelayString = "${loan.balancer.interval:300000}")  // Default 5 minutes
    public void run() {
        try {
            loanBalancer.balanceLoans();
        } catch (Exception e) {
            logger.error("Loan balancing process failed: {}", e.getMessage(), e);
            // The process will try again at the next scheduled interval
        }
    }
} 