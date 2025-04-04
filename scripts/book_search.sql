SELECT B.Isbn AS ISBN, 
       B.Title AS TITLE, 
       A.Name AS AUTHORS, 
       CASE 
           WHEN BL.Date_in IS NULL THEN 'Checked Out' 
           ELSE 'Available' 
       END AS STATUS
FROM BOOK AS B
LEFT JOIN BOOK_AUTHORS AS BA ON B.Isbn = BA.Isbn
LEFT JOIN AUTHORS AS A ON BA.Author_id = A.Author_id
LEFT JOIN BOOK_LOANS AS BL ON B.Isbn = BL.Isbn
WHERE B.Isbn LIKE '%substring%' 
   OR B.Title LIKE '%substring%'
   OR A.Name LIKE '%substring%';
-- substring can be updated to any search query text