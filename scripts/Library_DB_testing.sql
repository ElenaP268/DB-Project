CREATE DATABASE IF NOT EXISTS `library`;
USE `library`;
DROP TABLE IF EXISTS FINES;
DROP TABLE IF EXISTS BOOK_LOANS;
DROP TABLE IF EXISTS BOOK_AUTHORS;
DROP TABLE IF EXISTS BOOK;
DROP TABLE IF EXISTS AUTHORS;
DROP TABLE IF EXISTS BORROWER;
create table BOOK (
Isbn char(10),
Title varchar(255),
constraint pkisbn primary key(Isbn));

Create table AUTHORS(
Author_id int, 
`Name` varchar(50) NOT NULL, 
constraint pkAuthor_id primary key(Author_id));

Create table BOOK_AUTHORS (
Author_id int, 
Isbn char(10), 
constraint fkAuthor_id Foreign Key(Author_id) references AUTHORS(Author_id),
constraint fkIsbn Foreign Key(Isbn) references BOOK(Isbn));

Create table BORROWER(
Card_id char(10), 
Ssn char(11) NOT NULL, 
Bname varchar(50) NOT NULL,
Address varchar(50) NOT NULL,
Phone varchar(14),
constraint pkCard_id primary Key(Card_id)); 

Create table BOOK_LOANS (
Loan_id int, 
Isbn char(10), 
Card_id char(11), 
Date_out Date,
Due_Date Date,
Date_in Date,
constraint pkLoan_Id primary key (Loan_id),
constraint fkBLoanIsbn foreign key (Isbn) references BOOK(Isbn),
constraint fkCard_id foreign key (Card_id) references BORROWER(Card_id));

Create table FINES(
Loan_id int, 
Fine_amt DECIMAL(10, 2), 
Paid TINYINT(1),
constraint pkfLoan_id primary key(Loan_id),
constraint fkLoan_id foreign key(Loan_id) references BOOK_LOANS(Loan_id)); 


-- dummy data for testing
INSERT INTO BOOK (Isbn, Title) VALUES 
('9783161484', 'Database Systems'),
('9781234567', 'Introduction to Algorithms'),
('9780262033', 'Artificial Intelligence: A Modern Approach');

INSERT INTO AUTHORS (Author_id, Name) VALUES 
(1, 'Ramez Elmasri'),
(2, 'Thomas H. Cormen'),
(3, 'Stuart Russell');

INSERT INTO BOOK_AUTHORS (Isbn, Author_id) VALUES 
('9783161484', 1),
('9781234567', 2),
('9780262033', 3);

INSERT INTO BORROWER (Card_id, Bname, Address, Phone, Ssn) VALUES 
('1001', 'Alice Johnson', '123 Library St, TX', '123-456-7890', '111-22-3333'),
('1002', 'Bob Smith', '456 Book Rd, NY', '987-654-3210', '444-55-6666'),
('1003', 'Charlie Davis', '789 Novel Ave, CA', '555-888-9999', '777-88-9999');

INSERT INTO BOOK_LOANS (Loan_id, Card_id, Isbn, Date_out, Due_date, Date_in) VALUES 
(1, '1001', '9783161484', '2024-03-15', '2024-03-29', NULL),
(2, '1002', '9781234567', '2024-03-10', '2024-03-24', '2024-03-22');

INSERT INTO FINES (Loan_id, Fine_amt, Paid) VALUES 
(1, 0.50, 0),  -- Alice has an unpaid fine
(2, 0.00, 1);  -- Bob paid their fine


