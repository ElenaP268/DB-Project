SELECT bl.Loan_id, bl.Isbn, bk.Title, bl.Date_out, bl.Due_date, b.Bname
FROM BOOK_LOANS bl
JOIN BOOK bk ON bl.Isbn = bk.Isbn
JOIN BORROWER b ON bl.Card_id = b.Card_id
WHERE (bl.Isbn = '9780262033' OR b.Card_id = '1003' OR b.Bname LIKE '%Charlie%')
AND bl.Date_in IS NULL;
