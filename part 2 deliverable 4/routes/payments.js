const express = require('express');
const router = express.Router();

// ── In-memory database ────────────────────────────────────────────────────────
let payments = [
  {
    paymentId:    "PAY-001",
    accountId:    "ACC-001",
    amount:       250.00,
    currency:     "USD",
    paymentMethod:"STRIPE",
    status:       "COMPLETED",
    description:  "Loan repayment April 2026",
    timestamp:    "2026-04-01T10:00:00Z"
  },
  {
    paymentId:    "PAY-002",
    accountId:    "ACC-002",
    amount:       75.00,
    currency:     "USD",
    paymentMethod:"PAYPAL",
    status:       "PROCESSING",
    description:  "Utility bill payment",
    timestamp:    "2026-05-10T14:30:00Z"
  }
];

// ── GET 3 (bonus): Get all payments — useful for Postman verification ─────────
// GET /api/v1/payments
router.get('/', (req, res) => {
  return res.status(200).json({
    data: payments,
    meta: {
      totalCount: payments.length,
      timestamp:  new Date().toISOString()
    }
  });
});

// ── POST 2: Process a new payment ─────────────────────────────────────────────
// POST /api/v1/payments
// Body: { accountId, amount, currency, paymentMethod, description }
router.post('/', (req, res) => {
  const { accountId, amount, currency, paymentMethod, description } = req.body;

  // Validation
  if (!accountId) {
    return res.status(400).json({
      error: { code: "MISSING_FIELD", message: "accountId is required" }
    });
  }
  if (!amount || amount <= 0) {
    return res.status(400).json({
      error: { code: "INVALID_AMOUNT", message: "amount must be greater than 0" }
    });
  }
  if (amount > 100000) {
    return res.status(400).json({
      error: { code: "AMOUNT_EXCEEDS_LIMIT", message: "amount cannot exceed $100,000 per transaction" }
    });
  }
  if (!paymentMethod || !["STRIPE", "PAYPAL", "SQUARE"].includes(paymentMethod)) {
    return res.status(400).json({
      error: { code: "INVALID_PAYMENT_METHOD", message: "paymentMethod must be STRIPE, PAYPAL, or SQUARE" }
    });
  }

  const newPayment = {
    paymentId:    "PAY-" + Date.now(),
    accountId:    accountId,
    amount:       amount,
    currency:     currency     || "USD",
    paymentMethod:paymentMethod,
    status:       "PROCESSING",
    description:  description  || "",
    timestamp:    new Date().toISOString()
  };

  payments.push(newPayment);

  return res.status(202).json({
    data: newPayment,
    meta: { timestamp: new Date().toISOString() }
  });
});

// ── GET payment by ID ─────────────────────────────────────────────────────────
// GET /api/v1/payments/:paymentId
router.get('/:paymentId', (req, res) => {
  const payment = payments.find(p => p.paymentId === req.params.paymentId);

  if (!payment) {
    return res.status(404).json({
      error: {
        code:    "PAYMENT_NOT_FOUND",
        message: `Payment ${req.params.paymentId} does not exist`
      }
    });
  }

  return res.status(200).json({
    data: payment,
    meta: { timestamp: new Date().toISOString() }
  });
});

// ── PUT 2: Update payment status ──────────────────────────────────────────────
// PUT /api/v1/payments/:paymentId
// Body: { status }
router.put('/:paymentId', (req, res) => {
  const index = payments.findIndex(p => p.paymentId === req.params.paymentId);

  if (index === -1) {
    return res.status(404).json({
      error: {
        code:    "PAYMENT_NOT_FOUND",
        message: `Payment ${req.params.paymentId} does not exist`
      }
    });
  }

  const { status } = req.body;

  const validStatuses = ["PENDING", "PROCESSING", "COMPLETED", "FAILED", "REFUNDED"];
  if (!status || !validStatuses.includes(status)) {
    return res.status(400).json({
      error: {
        code:    "INVALID_STATUS",
        message: `status must be one of: ${validStatuses.join(", ")}`
      }
    });
  }

  if (payments[index].status === "COMPLETED" && status !== "REFUNDED") {
    return res.status(409).json({
      error: {
        code:    "INVALID_STATUS_TRANSITION",
        message: "A COMPLETED payment can only be changed to REFUNDED"
      }
    });
  }

  payments[index] = {
    ...payments[index],
    status:       status,
    lastModified: new Date().toISOString()
  };

  return res.status(200).json({
    data: payments[index],
    meta: { timestamp: new Date().toISOString() }
  });
});

// ── DELETE 2: Cancel a payment ────────────────────────────────────────────────
// DELETE /api/v1/payments/:paymentId
router.delete('/:paymentId', (req, res) => {
  const index = payments.findIndex(p => p.paymentId === req.params.paymentId);

  if (index === -1) {
    return res.status(404).json({
      error: {
        code:    "PAYMENT_NOT_FOUND",
        message: `Payment ${req.params.paymentId} does not exist`
      }
    });
  }

  if (payments[index].status === "COMPLETED") {
    return res.status(400).json({
      error: {
        code:    "CANNOT_DELETE_COMPLETED",
        message: "Cannot delete a completed payment. Use refund instead."
      }
    });
  }

  payments.splice(index, 1);

  return res.status(204).send();
});

module.exports = router;