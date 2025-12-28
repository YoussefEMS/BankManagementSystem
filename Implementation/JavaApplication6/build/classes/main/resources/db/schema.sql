-- Bank Management System Database Schema (Microsoft SQL Server)

-- Drop tables if they exist (for fresh setup)
IF OBJECT_ID('Transactions', 'U') IS NOT NULL DROP TABLE [Transactions];
IF OBJECT_ID('Account', 'U') IS NOT NULL DROP TABLE [Account];
IF OBJECT_ID('Customer', 'U') IS NOT NULL DROP TABLE [Customer];

-- Customer Table
CREATE TABLE [Customer] (
    customer_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(255) NOT NULL,
    national_id NVARCHAR(50) NOT NULL UNIQUE,
    email NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    phone NVARCHAR(20),
    address NVARCHAR(500),
    tier NVARCHAR(50),
    status NVARCHAR(50) DEFAULT 'ACTIVE',
    date_created DATETIME2 DEFAULT SYSDATETIME()
);

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
