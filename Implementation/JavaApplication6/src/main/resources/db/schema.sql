-- Bank Management System Database Schema (Microsoft SQL Server)

-- Drop tables if they exist (for fresh setup)
IF OBJECT_ID('OverdraftEvent', 'U') IS NOT NULL DROP TABLE [OverdraftEvent];
IF OBJECT_ID('InterestPosting', 'U') IS NOT NULL DROP TABLE [InterestPosting];
IF OBJECT_ID('Transfer', 'U') IS NOT NULL DROP TABLE [Transfer];
IF OBJECT_ID('Loan', 'U') IS NOT NULL DROP TABLE [Loan];
IF OBJECT_ID('Transactions', 'U') IS NOT NULL DROP TABLE [Transactions];
IF OBJECT_ID('Account', 'U') IS NOT NULL DROP TABLE [Account];
IF OBJECT_ID('Customer', 'U') IS NOT NULL DROP TABLE [Customer];

-- Customer / User Table
-- Stores both regular bank customers (role = 'CUSTOMER')
-- and admin/teller staff        (role = 'ADMIN').
-- Admins do not require a tier; the tier column may be NULL for them.
CREATE TABLE [Customer] (
    customer_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(255) NOT NULL,
    national_id NVARCHAR(50) NOT NULL UNIQUE,        -- national ID for customers, staff ID for admins
    email NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    phone NVARCHAR(20),
    address NVARCHAR(500),
    tier NVARCHAR(50),                                -- NULL for ADMIN users
    role NVARCHAR(20) NOT NULL DEFAULT 'CUSTOMER'
        CHECK (role IN ('CUSTOMER', 'ADMIN')),        -- enforce valid roles
    status NVARCHAR(50) DEFAULT 'ACTIVE',
    date_created DATETIME2 DEFAULT SYSDATETIME()
);

-- Index on role for fast filtering (e.g. loading all admins or all customers)
CREATE INDEX idx_customer_role ON [Customer](role);

-- Account Table
CREATE TABLE [Account] (
    account_number NVARCHAR(50) PRIMARY KEY,
    customer_id INT NOT NULL REFERENCES [Customer](customer_id),
    account_type NVARCHAR(50) NOT NULL,
    balance DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    currency NVARCHAR(10) DEFAULT 'USD',
    status NVARCHAR(50) DEFAULT 'ACTIVE',
    date_opened DATETIME2 DEFAULT SYSDATETIME()
);

-- Create index on customer_id for faster lookups
CREATE INDEX idx_account_customer_id ON [Account](customer_id);

-- Transactions Table
CREATE TABLE [Transactions] (
    transaction_id INT IDENTITY(1,1) PRIMARY KEY,
    account_number NVARCHAR(50) NOT NULL REFERENCES [Account](account_number),
    type NVARCHAR(50) NOT NULL,
    amount DECIMAL(18, 2) NOT NULL,
    timestamp DATETIME2 DEFAULT SYSDATETIME(),
    performed_by NVARCHAR(100),
    note NVARCHAR(MAX),
    balance_after DECIMAL(18, 2),
    reference_code NVARCHAR(100)
);

-- Create indexes for transaction lookups
CREATE INDEX idx_transactions_account_number ON [Transactions](account_number);
CREATE INDEX idx_transactions_timestamp ON [Transactions](timestamp);
CREATE INDEX idx_transactions_account_timestamp ON [Transactions](account_number, timestamp);

-- Loan Table
CREATE TABLE [Loan] (
    loan_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL REFERENCES [Customer](customer_id),
    amount DECIMAL(18, 2) NOT NULL,
    loan_type NVARCHAR(50) NOT NULL,
    duration_months INT NOT NULL,
    purpose NVARCHAR(500),
    interest_rate DECIMAL(5, 2),
    status NVARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    submission_date DATETIME2 DEFAULT SYSDATETIME(),
    decision_date DATETIME2
);

CREATE INDEX idx_loan_customer_id ON [Loan](customer_id);
CREATE INDEX idx_loan_status ON [Loan](status);

-- Transfer Table
CREATE TABLE [Transfer] (
    transfer_id INT IDENTITY(1,1) PRIMARY KEY,
    from_account_no NVARCHAR(50) NOT NULL REFERENCES [Account](account_number),
    to_account_no NVARCHAR(50) NOT NULL REFERENCES [Account](account_number),
    amount DECIMAL(18, 2) NOT NULL,
    timestamp DATETIME2 DEFAULT SYSDATETIME(),
    reference_code NVARCHAR(100) NOT NULL,
    status NVARCHAR(20) DEFAULT 'COMPLETED'
);

CREATE INDEX idx_transfer_from ON [Transfer](from_account_no);
CREATE INDEX idx_transfer_to ON [Transfer](to_account_no);

-- InterestPosting Table
CREATE TABLE [InterestPosting] (
    posting_id INT IDENTITY(1,1) PRIMARY KEY,
    account_number NVARCHAR(50) NOT NULL REFERENCES [Account](account_number),
    amount DECIMAL(18, 2) NOT NULL,
    rate_used DECIMAL(5, 4) NOT NULL,
    timestamp DATETIME2 DEFAULT SYSDATETIME()
);

CREATE INDEX idx_interest_posting_account ON [InterestPosting](account_number);

-- OverdraftEvent Table
CREATE TABLE [OverdraftEvent] (
    overdraft_id INT IDENTITY(1,1) PRIMARY KEY,
    account_number NVARCHAR(50) NOT NULL REFERENCES [Account](account_number),
    transaction_id INT NOT NULL REFERENCES [Transactions](transaction_id),
    amount DECIMAL(18, 2) NOT NULL,
    timestamp DATETIME2 DEFAULT SYSDATETIME(),
    alert_sent BIT DEFAULT 0
);

CREATE INDEX idx_overdraft_account ON [OverdraftEvent](account_number);
