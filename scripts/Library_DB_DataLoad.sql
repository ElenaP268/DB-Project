USE `Library`;
-- Before running the script, copy the data files to the folder C:\db_project\data\
-- or update the script with the file location before running the script
-- For the below deletes to work, remove the safe mode in the MySQL workbench
-- TO load the data remove server-file-priv to ''
 
DELETE FROM BORROWER;
DELETE FROM BOOK_AUTHORS;
DELETE FROM BOOK;
DELETE FROM AUTHORS;

LOAD DATA INFILE "C:\\db_project\\data\\borrower.csv"
INTO TABLE BORROWER
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(Card_id,ssn,Bname,address,phone);

LOAD DATA INFILE "C:\\db_project\\data\\authors.csv"
INTO TABLE AUTHORS
FIELDS TERMINATED BY '\t'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(Author_id,Name);

LOAD DATA INFILE "C:\\db_project\\data\\book.csv"
INTO TABLE BOOK
FIELDS TERMINATED BY '\t'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(ISBN,Title);

LOAD DATA INFILE "C:\\db_project\\data\\book_authors.csv"
INTO TABLE BOOK_AUTHORS
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(Author_id,ISBN);
