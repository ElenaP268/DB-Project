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
Date_out varchar(20),
Due_Date varchar(20),
Date_in varchar(20),
constraint pkLoan_Id primary key (Loan_id),
constraint fkBLoanIsbn foreign key (Isbn) references BOOK(Isbn),
constraint fkCard_id foreign key (Card_id) references BORROWER(Card_id));

Create table FINES(
Loan_id int, 
Fine_amt DECIMAL(10, 2), 
Paid TINYINT(1),
constraint pkfLoan_id primary key(Loan_id),
constraint fkLoan_id foreign key(Loan_id) references BOOK_LOANS(Loan_id)); 

