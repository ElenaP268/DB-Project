UPDATE BOOK_LOANS 
SET Date_in = CURRENT_DATE 
WHERE Loan_id = 3
AND Date_in IS NULL;
