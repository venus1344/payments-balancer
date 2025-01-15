# A simple Payments Balancer
A payments balancer intended to clear out open balances with overpaid loans.

Core issue:
- The original repayment workflow does not allow for direct repayments to a specific loan. In some instances, a $5000 loan is overpaid with $5090, and so on.
- The loan is fully paid, and thus has a 90 excess. This excess in our platform is not tracked, and thus cannot be used to repay other loans.

This project aims to solve this issue by creating a new workflow that allows for direct repayments to a specific loan.

### PS:
- Ofcourse, a cron job would do ideally. 
- This is a simple POC for my learning purposes.
- No good practises are followed.
- The REST payment service is mocked. Not actual.

