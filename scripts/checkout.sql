INSERT INTO BOOK_LOANS (Loan_id, Card_id, Isbn, Date_out, Due_date)
SELECT 
	3,
    b.Card_id, 
    bk.Isbn, 
    CURRENT_DATE, 
    DATE_ADD(CURRENT_DATE, INTERVAL 14 DAY)
FROM BORROWER b, BOOK bk
WHERE b.Card_id = '1003' 
AND bk.Isbn = '9780262033'
AND NOT EXISTS (
    SELECT 1 FROM BOOK_LOANS bl 
    WHERE bl.Isbn = bk.Isbn AND bl.Date_in IS NULL
)
AND (SELECT COUNT(*) FROM BOOK_LOANS WHERE Card_id = '1003' AND Date_in IS NULL) < 3
AND NOT EXISTS (
    SELECT 1 FROM FINES f 
    WHERE f.Loan_id IN (SELECT Loan_id FROM BOOK_LOANS WHERE Card_id = '1003' AND Date_in IS NULL) 
    AND f.Paid = 0
);
