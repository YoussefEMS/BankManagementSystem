-- Seed data for Bank Management System (Microsoft SQL Server)

-- Insert customers
INSERT INTO [Customer] (full_name, national_id, email, phone, address, tier, status) VALUES
('Ahmed Hassan', 'ID001', 'ahmed@example.com', '01001234567', '123 Nile Street, Cairo', 'GOLD', 'ACTIVE'),
('Fatima Ali', 'ID002', 'fatima@example.com', '01101234567', '456 Tahrir Square, Giza', 'SILVER', 'ACTIVE'),
('Mohamed Ibrahim', 'ID003', 'mohamed@example.com', '01201234567', '789 Suez Road, Alexandria', 'BRONZE', 'ACTIVE');

-- Insert accounts
INSERT INTO [Account] (account_number, customer_id, account_type, balance, currency, status, date_opened) VALUES
('ACC001', 1, 'CHECKING', 5000.00, 'USD', 'ACTIVE', '2023-01-15T00:00:00'),
('ACC002', 1, 'SAVINGS', 15000.00, 'USD', 'ACTIVE', '2023-02-20T00:00:00'),
('ACC003', 2, 'CHECKING', 3500.00, 'USD', 'ACTIVE', '2023-03-10T00:00:00'),
('ACC004', 3, 'SAVINGS', 25000.00, 'USD', 'ACTIVE', '2023-04-05T00:00:00');

-- Insert transactions for ACC001 (Ahmed's checking account - 10 transactions)
INSERT INTO [Transactions] (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code) VALUES
('ACC001', 'Deposit', 2000.00, '2024-12-01T10:00:00', 'TELLER001', 'Initial deposit', 2000.00, 'REF001'),
('ACC001', 'Withdrawal', 500.00, '2024-12-05T14:30:00', 'ATM', 'Cash withdrawal', 1500.00, 'REF002'),
('ACC001', 'TransferCredit', 800.00, '2024-12-10T09:15:00', 'SYSTEM', 'Transfer from ACC002', 2300.00, 'REF003'),
('ACC001', 'Withdrawal', 300.00, '2024-12-12T16:45:00', 'ATM', 'Shopping withdrawal', 2000.00, 'REF004'),
('ACC001', 'Deposit', 1500.00, '2024-12-15T11:00:00', 'TELLER002', 'Salary deposit', 3500.00, 'REF005'),
('ACC001', 'InterestPosting', 25.00, '2024-12-20T23:59:00', 'SYSTEM', 'Monthly interest', 3525.00, 'REF006'),
('ACC001', 'TransferDebit', 600.00, '2024-12-22T13:20:00', 'SYSTEM', 'Transfer to ACC003', 2925.00, 'REF007'),
('ACC001', 'Withdrawal', 200.00, '2024-12-24T10:30:00', 'ATM', 'Holiday expense', 2725.00, 'REF008'),
('ACC001', 'Deposit', 1275.00, '2024-12-26T15:45:00', 'TELLER001', 'Refund deposit', 4000.00, 'REF009'),
('ACC001', 'InterestPosting', 30.00, '2024-12-31T23:59:00', 'SYSTEM', 'End of month interest', 4030.00, 'REF010');

-- Insert transactions for ACC002 (Ahmed's savings account - 8 transactions)
INSERT INTO [Transactions] (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code) VALUES
('ACC002', 'Deposit', 10000.00, '2023-02-20T08:00:00', 'TELLER001', 'Opening deposit', 10000.00, 'REF011'),
('ACC002', 'InterestPosting', 75.00, '2023-03-20T23:59:00', 'SYSTEM', 'Monthly interest', 10075.00, 'REF012'),
('ACC002', 'Deposit', 2000.00, '2024-06-10T10:15:00', 'TELLER002', 'Additional savings', 12075.00, 'REF013'),
('ACC002', 'TransferDebit', 800.00, '2024-12-10T09:15:00', 'SYSTEM', 'Transfer to ACC001', 11275.00, 'REF014'),
('ACC002', 'InterestPosting', 85.00, '2024-09-30T23:59:00', 'SYSTEM', 'Quarterly interest', 11360.00, 'REF015'),
('ACC002', 'Deposit', 3000.00, '2024-11-05T14:30:00', 'TELLER001', 'Bonus deposit', 14360.00, 'REF016'),
('ACC002', 'InterestPosting', 110.00, '2024-12-31T23:59:00', 'SYSTEM', 'Year-end interest', 14470.00, 'REF017'),
('ACC002', 'Withdrawal', 1000.00, '2024-12-27T12:00:00', 'TELLER002', 'Partial withdrawal', 13470.00, 'REF018');

-- Insert transactions for ACC003 (Fatima's checking account - 6 transactions)
INSERT INTO [Transactions] (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code) VALUES
('ACC003', 'Deposit', 3500.00, '2023-03-10T09:00:00', 'TELLER001', 'Opening deposit', 3500.00, 'REF019'),
('ACC003', 'Withdrawal', 500.00, '2024-05-15T15:30:00', 'ATM', 'Cash withdrawal', 3000.00, 'REF020'),
('ACC003', 'TransferDebit', 600.00, '2024-12-22T13:20:00', 'SYSTEM', 'Transfer from ACC001', 2400.00, 'REF021'),
('ACC003', 'Deposit', 800.00, '2024-12-23T11:00:00', 'TELLER002', 'Christmas bonus', 3200.00, 'REF022'),
('ACC003', 'InterestPosting', 24.00, '2024-12-31T23:59:00', 'SYSTEM', 'Monthly interest', 3224.00, 'REF023'),
('ACC003', 'Withdrawal', 224.00, '2024-12-26T16:45:00', 'ATM', 'Shopping', 3000.00, 'REF024');

-- Insert transactions for ACC004 (Mohamed's savings account - 6 transactions)
INSERT INTO [Transactions] (account_number, type, amount, timestamp, performed_by, note, balance_after, reference_code) VALUES
('ACC004', 'Deposit', 25000.00, '2023-04-05T10:00:00', 'TELLER001', 'Opening deposit', 25000.00, 'REF025'),
('ACC004', 'InterestPosting', 187.50, '2023-05-05T23:59:00', 'SYSTEM', 'Monthly interest', 25187.50, 'REF026'),
('ACC004', 'Deposit', 5000.00, '2024-08-20T14:15:00', 'TELLER002', 'Additional savings', 30187.50, 'REF027'),
('ACC004', 'InterestPosting', 226.40, '2024-10-05T23:59:00', 'SYSTEM', 'Quarterly interest', 30413.90, 'REF028'),
('ACC004', 'Withdrawal', 5000.00, '2024-12-18T10:30:00', 'TELLER001', 'Planned withdrawal', 25413.90, 'REF029'),
('ACC004', 'InterestPosting', 190.60, '2024-12-31T23:59:00', 'SYSTEM', 'Year-end interest', 25604.50, 'REF030');
