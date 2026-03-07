SET NOCOUNT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    /* =========================================================
       1) CLEAR EXISTING DATA (child tables first)
       ========================================================= */
    DELETE FROM [OverdraftEvent];
    DELETE FROM [InterestPosting];
    DELETE FROM [Transfer];
    DELETE FROM [Loan];
    DELETE FROM [Transactions];
    DELETE FROM [Account];
    DELETE FROM [Customer];

    /* Reseed identity tables */
    DBCC CHECKIDENT ('[Customer]', RESEED, 0);
    DBCC CHECKIDENT ('[Transactions]', RESEED, 0);
    DBCC CHECKIDENT ('[Loan]', RESEED, 0);
    DBCC CHECKIDENT ('[Transfer]', RESEED, 0);
    DBCC CHECKIDENT ('[InterestPosting]', RESEED, 0);
    DBCC CHECKIDENT ('[OverdraftEvent]', RESEED, 0);

    /* =========================================================
       2) INSERT CUSTOMERS / ADMINS
       Notes:
       - National IDs below are synthetic demo values, not real persons
       - Passwords are placeholders only; production must use hashes
       ========================================================= */
    INSERT INTO [Customer]
        (full_name, national_id, email, password, phone, address, tier, role, status, date_created)
    VALUES
        (N'Ahmed Hassan Ali',      N'29801011500123', N'ahmed.hassan@example.com',   N'Demo@1234', N'+201001234567', N'Nasr City, Cairo, Egypt',      N'GOLD',     N'CUSTOMER', N'ACTIVE', '2025-09-15T09:00:00'),
        (N'Mona El-Sayed Mohamed', N'29005222400145', N'mona.elsayed@example.com',   N'Demo@1234', N'+201002345678', N'Mohandessin, Giza, Egypt',    N'PLATINUM', N'CUSTOMER', N'ACTIVE', '2025-09-18T10:30:00'),
        (N'Youssef Ibrahim Abdelrahman', N'30107151200189', N'youssef.ibrahim@example.com', N'Demo@1234', N'+201003456789', N'Alexandria, Egypt', N'STANDARD', N'CUSTOMER', N'ACTIVE', '2025-10-01T11:15:00'),
        (N'Fatma Mahmoud Saleh',   N'29511291800156', N'fatma.mahmoud@example.com',  N'Demo@1234', N'+201004567890', N'Mansoura, Dakahlia, Egypt',  N'SILVER',   N'CUSTOMER', N'ACTIVE', '2025-10-05T12:00:00'),
        (N'Karim Abdelrahman Nabil', N'29903031200167', N'karim.nabil@example.com',  N'Demo@1234', N'+201005678901', N'Heliopolis, Cairo, Egypt',   N'STANDARD', N'CUSTOMER', N'ACTIVE', '2025-10-10T13:20:00'),
        (N'Nourhan Samir Fawzy',   N'28708272400178', N'nourhan.samir@example.com',  N'Demo@1234', N'+201006789012', N'Zamalek, Cairo, Egypt',       N'PLATINUM', N'CUSTOMER', N'ACTIVE', '2025-10-12T14:10:00'),
        (N'Omar Fathy Mostafa',    N'30009061800134', N'omar.fathy@example.com',     N'Demo@1234', N'+201007890123', N'Tanta, Gharbia, Egypt',       N'SILVER',   N'CUSTOMER', N'ACTIVE', '2025-10-15T15:00:00'),
        (N'Laila Tarek Amin',      N'29612102400191', N'laila.tarek@example.com',    N'Demo@1234', N'+201008901234', N'Maadi, Cairo, Egypt',         N'GOLD',     N'CUSTOMER', N'ACTIVE', '2025-10-20T16:25:00'),

        (N'Mahmoud Nabil',         N'EMP-ADM-1001',   N'mahmoud.nabil@bank.local',   N'Admin@1234', N'+201009012345', N'Head Office, Cairo, Egypt',  NULL,        N'ADMIN',    N'ACTIVE', '2025-08-01T08:00:00'),
        (N'Salma Adel',            N'EMP-ADM-1002',   N'salma.adel@bank.local',      N'Admin@1234', N'+201009112233', N'Head Office, Cairo, Egypt',  NULL,        N'ADMIN',    N'ACTIVE', '2025-08-01T08:10:00');

    /* =========================================================
       3) INSERT ACCOUNTS
       All accounts are in EGP for Egypt-based banking
       ========================================================= */
    INSERT INTO [Account]
        (account_number, customer_id, account_type, balance, currency, status, date_opened)
    VALUES
        (N'EG-001-1000001', (SELECT customer_id FROM [Customer] WHERE email = N'ahmed.hassan@example.com'),    N'SAVINGS',  52500.00,  N'EGP', N'ACTIVE', '2025-11-01T09:00:00'),
        (N'EG-001-1000002', (SELECT customer_id FROM [Customer] WHERE email = N'ahmed.hassan@example.com'),    N'CURRENT',  18000.00,  N'EGP', N'ACTIVE', '2025-12-15T10:00:00'),
        (N'EG-001-1000003', (SELECT customer_id FROM [Customer] WHERE email = N'mona.elsayed@example.com'),    N'SAVINGS',  76500.00,  N'EGP', N'ACTIVE', '2025-10-15T11:00:00'),
        (N'EG-002-1000004', (SELECT customer_id FROM [Customer] WHERE email = N'youssef.ibrahim@example.com'), N'CURRENT',  -1200.00,  N'EGP', N'ACTIVE', '2025-12-20T12:00:00'),
        (N'EG-003-1000005', (SELECT customer_id FROM [Customer] WHERE email = N'fatma.mahmoud@example.com'),   N'SAVINGS',  15320.75,  N'EGP', N'ACTIVE', '2025-12-01T09:30:00'),
        (N'EG-001-1000006', (SELECT customer_id FROM [Customer] WHERE email = N'karim.nabil@example.com'),     N'CURRENT',   8840.00,  N'EGP', N'ACTIVE', '2026-01-08T09:15:00'),
        (N'EG-001-1000007', (SELECT customer_id FROM [Customer] WHERE email = N'nourhan.samir@example.com'),   N'SAVINGS', 346000.00,  N'EGP', N'ACTIVE', '2025-09-01T08:45:00'),
        (N'EG-004-1000008', (SELECT customer_id FROM [Customer] WHERE email = N'omar.fathy@example.com'),      N'CURRENT',   2200.00,  N'EGP', N'ACTIVE', '2026-01-12T10:20:00'),
        (N'EG-001-1000009', (SELECT customer_id FROM [Customer] WHERE email = N'laila.tarek@example.com'),     N'SAVINGS',  12850.00,  N'EGP', N'ACTIVE', '2026-01-01T13:00:00');

    /* =========================================================
       4) INSERT TRANSACTIONS
       ========================================================= */

    DECLARE @trxOverdraft INT;

    /* Ahmed Hassan - Savings */
    INSERT INTO [Transactions]
        (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code)
    VALUES
        (N'EG-001-1000001', N'DEPOSIT',      50000.00, '2025-11-01T09:05:00', N'Mahmoud Nabil', N'Initial cash deposit',                 50000.00, N'DEP-20251101-0001'),
        (N'EG-001-1000001', N'SALARY',        5000.00, '2025-12-28T09:00:00', N'SYSTEM',         N'Monthly salary transfer',             55000.00, N'SAL-20251228-0001'),
        (N'EG-001-1000001', N'BILL_PAYMENT',  2500.00, '2026-01-05T18:30:00', N'Ahmed Hassan Ali', N'Electricity and utilities payment', 52500.00, N'BIL-20260105-0001');

    /* Ahmed Hassan - Current */
    INSERT INTO [Transactions]
        (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code)
    VALUES
        (N'EG-001-1000002', N'DEPOSIT',      20000.00, '2026-01-10T10:00:00', N'Salma Adel',     N'Cash deposit at branch',              20000.00, N'DEP-20260110-0002'),
        (N'EG-001-1000002', N'TRANSFER_OUT',  2000.00, '2026-02-02T11:40:00', N'Ahmed Hassan Ali', N'Transfer to Omar Fathy',             18000.00, N'TRX-20260202-0001');

    /* Mona */
    INSERT INTO [Transactions]
        (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code)
    VALUES
        (N'EG-001-1000003', N'DEPOSIT',      75000.00, '2025-10-15T11:15:00', N'Mahmoud Nabil', N'Initial deposit',                      75000.00, N'DEP-20251015-0003'),
        (N'EG-001-1000003', N'INTEREST',      1500.00, '2026-02-15T00:05:00', N'SYSTEM',        N'Monthly savings interest posting',     76500.00, N'INT-20260215-0001');

    /* Youssef - overdraft case */
    INSERT INTO [Transactions]
        (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code)
    VALUES
        (N'EG-002-1000004', N'DEPOSIT',      3000.00, '2026-01-03T09:10:00', N'Salma Adel',       N'Cash deposit',                      3000.00, N'DEP-20260103-0004');

    INSERT INTO [Transactions]
        (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code)
    VALUES
        (N'EG-002-1000004', N'ATM_WITHDRAWAL', 4200.00, '2026-01-20T20:15:00', N'ATM-002-ALEX', N'ATM withdrawal causing overdraft', -1200.00, N'OVD-20260120-0001');

    SET @trxOverdraft = SCOPE_IDENTITY();

    /* Fatma */
    INSERT INTO [Transactions]
        (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code)
    VALUES
        (N'EG-003-1000005', N'DEPOSIT',      15000.00, '2025-12-01T09:35:00', N'Mahmoud Nabil', N'Initial deposit',                      15000.00, N'DEP-20251201-0005'),
        (N'EG-003-1000005', N'INTEREST',       320.75, '2026-02-15T00:06:00', N'SYSTEM',        N'Monthly savings interest posting',     15320.75, N'INT-20260215-0002');

    /* Karim */
    INSERT INTO [Transactions]
        (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code)
    VALUES
        (N'EG-001-1000006', N'DEPOSIT',       5000.00, '2026-01-08T09:20:00', N'Salma Adel',     N'Cash deposit at branch',               5000.00, N'DEP-20260108-0006'),
        (N'EG-001-1000006', N'TRANSFER_IN',   5000.00, '2026-02-10T14:00:00', N'Nourhan Samir Fawzy', N'Transfer received from Nourhan', 10000.00, N'TRX-20260210-0002'),
        (N'EG-001-1000006', N'BILL_PAYMENT',  1160.00, '2026-02-12T17:45:00', N'Karim Abdelrahman Nabil', N'Internet and mobile bills', 8840.00, N'BIL-20260212-0006');

    /* Nourhan */
    INSERT INTO [Transactions]
        (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code)
    VALUES
        (N'EG-001-1000007', N'DEPOSIT',      350000.00, '2025-09-01T08:50:00', N'Mahmoud Nabil',     N'Initial high-value deposit',          350000.00, N'DEP-20250901-0007'),
        (N'EG-001-1000007', N'TRANSFER_OUT',   5000.00, '2026-02-10T14:00:00', N'Nourhan Samir Fawzy', N'Transfer to Karim Nabil',           345000.00, N'TRX-20260210-0002'),
        (N'EG-001-1000007', N'INTEREST',       1000.00, '2026-02-15T00:07:00', N'SYSTEM',            N'Monthly savings interest posting',   346000.00, N'INT-20260215-0003');

    /* Omar */
    INSERT INTO [Transactions]
        (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code)
    VALUES
        (N'EG-004-1000008', N'DEPOSIT',       5000.00, '2026-01-12T10:30:00', N'Salma Adel',       N'Initial cash deposit',                 5000.00, N'DEP-20260112-0008'),
        (N'EG-004-1000008', N'TRANSFER_IN',   2000.00, '2026-02-02T11:40:00', N'Ahmed Hassan Ali', N'Transfer received from Ahmed',         7000.00, N'TRX-20260202-0001'),
        (N'EG-004-1000008', N'ATM_WITHDRAWAL',4800.00, '2026-02-25T21:00:00', N'ATM-004-TANTA',    N'ATM cash withdrawal',                  2200.00, N'ATM-20260225-0008');

    /* Laila */
    INSERT INTO [Transactions]
        (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code)
    VALUES
        (N'EG-001-1000009', N'DEPOSIT',       12000.00, '2026-01-01T13:05:00', N'Salma Adel',      N'Initial savings deposit',             12000.00, N'DEP-20260101-0009'),
        (N'EG-001-1000009', N'TRANSFER_IN',    1000.00, '2026-02-18T15:20:00', N'Hany Internal Demo', N'Transfer received',               13000.00, N'TRX-20260218-0003'),
        (N'EG-001-1000009', N'BILL_PAYMENT',    150.00, '2026-02-20T12:10:00', N'Laila Tarek Amin', N'Water bill payment',                 12850.00, N'BIL-20260220-0009');

    /* =========================================================
       5) INSERT TRANSFERS
       ========================================================= */
    INSERT INTO [Transfer]
        (from_account_no, to_account_no, amount, timestamp, reference_code, status)
    VALUES
        (N'EG-001-1000002', N'EG-004-1000008', 2000.00, '2026-02-02T11:40:00', N'TRX-20260202-0001', N'COMPLETED'),
        (N'EG-001-1000007', N'EG-001-1000006', 5000.00, '2026-02-10T14:00:00', N'TRX-20260210-0002', N'COMPLETED'),
        (N'EG-001-9999999', N'EG-001-1000009', 1000.00, '2026-02-18T15:20:00', N'TRX-20260218-0003', N'COMPLETED');
    /*
      WARNING:
      The third transfer above uses a non-existent source account and will FAIL because of FK constraints.
      That is the inevitable result of trying to keep the sample transaction while your schema requires
      both sides of Transfer to exist. So this row must be corrected or removed.
    */

    /* Remove the invalid insert and replace it with a valid one */
    DELETE FROM [Transfer] WHERE reference_code = N'TRX-20260218-0003';

    INSERT INTO [Account]
        (account_number, customer_id, account_type, balance, currency, status, date_opened)
    VALUES
        (N'EG-001-1000010', (SELECT customer_id FROM [Customer] WHERE email = N'laila.tarek@example.com'),
         N'CURRENT', 96750.00, N'EGP', N'ACTIVE', '2025-12-20T09:00:00');

    INSERT INTO [Transactions]
        (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code)
    VALUES
        (N'EG-001-1000010', N'DEPOSIT',      100000.00, '2025-12-20T09:05:00', N'Mahmoud Nabil', N'Initial current account deposit',     100000.00, N'DEP-20251220-0010'),
        (N'EG-001-1000010', N'TRANSFER_OUT',   1000.00, '2026-02-18T15:20:00', N'Laila Tarek Amin', N'Transfer to savings account',        99000.00, N'TRX-20260218-0003'),
        (N'EG-001-1000010', N'CASH_WITHDRAWAL',2250.00, '2026-03-01T11:00:00', N'Laila Tarek Amin', N'Cash withdrawal at branch',         96750.00, N'CSH-20260301-0010');

    INSERT INTO [Transfer]
        (from_account_no, to_account_no, amount, timestamp, reference_code, status)
    VALUES
        (N'EG-001-1000010', N'EG-001-1000009', 1000.00, '2026-02-18T15:20:00', N'TRX-20260218-0003', N'COMPLETED');

    /* =========================================================
       6) INSERT INTEREST POSTINGS
       ========================================================= */
    INSERT INTO [InterestPosting]
        (account_number, amount, rate_used, timestamp)
    VALUES
        (N'EG-001-1000003', 1500.00, 0.0200, '2026-02-15T00:05:00'),
        (N'EG-003-1000005',  320.75, 0.0210, '2026-02-15T00:06:00'),
        (N'EG-001-1000007', 1000.00, 0.0030, '2026-02-15T00:07:00');

    /* =========================================================
       7) INSERT OVERDRAFT EVENT
       ========================================================= */
    INSERT INTO [OverdraftEvent]
        (account_number, transaction_id, amount, timestamp, alert_sent)
    VALUES
        (N'EG-002-1000004', @trxOverdraft, 1200.00, '2026-01-20T20:16:00', 1);

    /* =========================================================
       8) INSERT LOANS
       ========================================================= */
    INSERT INTO [Loan]
        (customer_id, amount, loan_type, duration_months, purpose, interest_rate, status, submission_date, decision_date)
    VALUES
        ((SELECT customer_id FROM [Customer] WHERE email = N'ahmed.hassan@example.com'),
         150000.00, N'PERSONAL', 36, N'Used car purchase', 18.50, N'APPROVED', '2026-01-07T10:00:00', '2026-01-10T14:00:00'),

        ((SELECT customer_id FROM [Customer] WHERE email = N'mona.elsayed@example.com'),
         950000.00, N'HOME', 180, N'Apartment purchase in Sheikh Zayed', 14.25, N'PENDING', '2026-02-01T11:30:00', NULL),

        ((SELECT customer_id FROM [Customer] WHERE email = N'youssef.ibrahim@example.com'),
         15000.00, N'PERSONAL', 12, N'Salary advance / emergency expenses', 22.00, N'REJECTED', '2026-01-22T09:40:00', '2026-01-24T13:10:00'),

        ((SELECT customer_id FROM [Customer] WHERE email = N'nourhan.samir@example.com'),
         500000.00, N'BUSINESS', 48, N'Expand boutique retail operations', 17.75, N'APPROVED', '2025-12-10T12:15:00', '2025-12-18T15:45:00'),

        ((SELECT customer_id FROM [Customer] WHERE email = N'fatma.mahmoud@example.com'),
         80000.00, N'EDUCATION', 24, N'Postgraduate tuition fees', 16.90, N'PENDING', '2026-02-11T16:20:00', NULL);

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    THROW;
END CATCH;