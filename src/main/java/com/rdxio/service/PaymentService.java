package com.rdxio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@SuppressWarnings("unused")
public class PaymentService {
    @Value("${payment.api.url}")
    private String apiUrl;
    
    @Value("${payment.api.token}")
    private String bearerToken;
    
    private final RestTemplate restTemplate = new RestTemplate();

    public void processPayment(String fromLoanId, String toLoanId, double amount) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(bearerToken);

            PaymentRequest paymentRequest = new PaymentRequest(fromLoanId, toLoanId, amount);
            HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Payment API call failed with status " + 
                    response.getStatusCode() + ": " + response.getBody());
            }

            System.out.println("From Loan ID: " + paymentRequest.getFromLoanId());
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to process payment from " + fromLoanId + 
                " to " + toLoanId + " for amount " + amount + ": " + e.getMessage(), e);
        }
    }


    private static class PaymentRequest {
        public String fromLoanId;
        public String toLoanId;
        public double amount;

        public PaymentRequest(String fromLoanId, String toLoanId, double amount) {
            this.fromLoanId = fromLoanId;
            this.toLoanId = toLoanId;
            this.amount = amount;
        }

        public String getToLoanId() {
            return toLoanId;
        }

        public String getFromLoanId() {
            return fromLoanId;
        }
    }
} 