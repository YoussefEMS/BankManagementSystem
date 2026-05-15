const express = require('express');
const router = express.Router();

// ── In-memory database ────────────────────────────────────────────────────────
let accounts = [
  {
    accountId:   "ACC-001",
    customerId:  "CUST-001",
    accountType: "SAVINGS",
    balance:     1000.00,
    currency:    "USD",
    status:      "ACTIVE",
    phone:       "+1-111-111-1111",
    email:       "alice@example.com",
    address:     "10 Wall St, New York",
    createdAt:   "2026-01-10T08:00:00Z"
  },
  {
    accountId:   "ACC-002",
    customerId:  "CUST-002",
    accountType: "CHECKING",
    balance:     2500.00,
    currency:    "USD",
    status:      "ACTIVE",
    phone:       "+1-222-222-2222",
    email:       "bob@example.com",
    address:     "20 Broadway, New York",
    createdAt:   "2026-02-14T09:30:00Z"
  },
  {
    accountId:   "ACC-003",
    customerId:  "CUST-003",
    accountType: "SAVINGS",
    balance:     0.00,
    currency:    "USD",
    status:      "ACTIVE",
    phone:       "+1-333-333-3333",
    email:       "carol@example.com",
    address:     "30 Park Ave, New York",
    createdAt:   "2026-03-01T10:00:00Z"
  }
];

// ── GET 1: Get ALL accounts ───────────────────────────────────────────────────
// GET /api/v1/accounts
router.get('/', (req, res) => {
  return res.status(200).json({
    data: accounts,
    meta: {
      totalCount: accounts.length,
      timestamp:  new Date().toISOString()
    }
  });
});

// ── GET 2: Get account by ID ──────────────────────────────────────────────────
// GET /api/v1/accounts/:accountId
router.get('/:accountId', (req, res) => {
  const account = accounts.find(a => a.accountId === req.params.accountId);

  if (!account) {
    return res.status(404).json({
      error: {
        code:      "ACCOUNT_NOT_FOUND",
        message:   `Account ${req.params.accountId} does not exist`,
        timestamp: new Date().toISOString()
      }
    });
  }

  return res.status(200).json({
    data: account,
    meta: { timestamp: new Date().toISOString() }
  });
});

// ── POST 1: Create a new account ──────────────────────────────────────────────
// POST /api/v1/accounts
// Body: { customerId, accountType, initialBalance, currency }
router.post('/', (req, res) => {
  const { customerId, accountType, initialBalance, currency } = req.body;

  // Validation
  if (!customerId) {
    return res.status(400).json({
      error: { code: "MISSING_FIELD", message: "customerId is required" }
    });
  }
  if (!accountType || !["SAVINGS", "CHECKING"].includes(accountType)) {
    return res.status(400).json({
      error: { code: "INVALID_ACCOUNT_TYPE", message: "accountType must be SAVINGS or CHECKING" }
    });
  }
  if (initialBalance === undefined || initialBalance < 100) {
    return res.status(400).json({
      error: { code: "INVALID_BALANCE", message: "initialBalance must be at least $100" }
    });
  }

  const newAccount = {
    accountId:   "ACC-" + Date.now(),
    customerId:  customerId,
    accountType: accountType,
    balance:     initialBalance,
    currency:    currency || "USD",
    status:      "ACTIVE",
    phone:       null,
    email:       null,
    address:     null,
    createdAt:   new Date().toISOString()
  };

  accounts.push(newAccount);

  return res.status(201).json({
    data: newAccount,
    meta: { timestamp: new Date().toISOString() }
  });
});

// ── PUT 1: Update account contact info ────────────────────────────────────────
// PUT /api/v1/accounts/:accountId
// Body: { phone, email, address }
router.put('/:accountId', (req, res) => {
  const index = accounts.findIndex(a => a.accountId === req.params.accountId);

  if (index === -1) {
    return res.status(404).json({
      error: {
        code:    "ACCOUNT_NOT_FOUND",
        message: `Account ${req.params.accountId} does not exist`
      }
    });
  }

  if (accounts[index].status === "FROZEN") {
    return res.status(409).json({
      error: {
        code:    "ACCOUNT_FROZEN",
        message: "Cannot update a frozen account"
      }
    });
  }

  const { phone, email, address } = req.body;

  accounts[index] = {
    ...accounts[index],
    phone:        phone   || accounts[index].phone,
    email:        email   || accounts[index].email,
    address:      address || accounts[index].address,
    lastModified: new Date().toISOString()
  };

  return res.status(200).json({
    data: accounts[index],
    meta: { timestamp: new Date().toISOString() }
  });
});

// ── DELETE 1: Close an account ────────────────────────────────────────────────
// DELETE /api/v1/accounts/:accountId
router.delete('/:accountId', (req, res) => {
  const index = accounts.findIndex(a => a.accountId === req.params.accountId);

  if (index === -1) {
    return res.status(404).json({
      error: {
        code:    "ACCOUNT_NOT_FOUND",
        message: `Account ${req.params.accountId} does not exist`
      }
    });
  }

  if (accounts[index].balance !== 0) {
    return res.status(400).json({
      error: {
        code:           "ACCOUNT_NOT_EMPTY",
        message:        "Account balance must be zero before closing",
        details: {
          currentBalance: accounts[index].balance
        }
      }
    });
  }

  accounts.splice(index, 1);

  return res.status(204).send();
});

module.exports = router;