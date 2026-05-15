const express = require('express');
const app = express();
app.use(express.json());

const accountsRouter = require('./routes/accounts');
const paymentsRouter = require('./routes/payments');

app.use('/api/v1/accounts', accountsRouter);
app.use('/api/v1/payments', paymentsRouter);

const PORT = 3000;
app.listen(PORT, () => {
  console.log(`Bank API Server running on http://localhost:${PORT}`);
  console.log('');
  console.log('Available endpoints:');
  console.log('  GET    http://localhost:3000/api/v1/accounts');
  console.log('  GET    http://localhost:3000/api/v1/accounts/:accountId');
  console.log('  POST   http://localhost:3000/api/v1/accounts');
  console.log('  PUT    http://localhost:3000/api/v1/accounts/:accountId');
  console.log('  DELETE http://localhost:3000/api/v1/accounts/:accountId');
  console.log('  POST   http://localhost:3000/api/v1/payments');
  console.log('  GET    http://localhost:3000/api/v1/payments/:paymentId');
  console.log('  PUT    http://localhost:3000/api/v1/payments/:paymentId');
  console.log('  DELETE http://localhost:3000/api/v1/payments/:paymentId');
});