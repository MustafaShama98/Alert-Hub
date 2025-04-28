const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');
const { runConsumer } = require('./src/kafka/consumer');

const app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());

// Serve static files from the templates directory
app.use('/static', express.static(path.join(__dirname, 'src/templates')));

// Start Kafka consumer
runConsumer().catch(error => {
    console.error('Failed to start Kafka consumer:', error);
    process.exit(1);
});

// Add a health check endpoint
app.get('/health', (req, res) => {
    res.status(200).json({ status: 'OK' });
});

const port = process.env.PORT || 3000;
app.listen(port, () => {
    console.log(`Email service listening on port ${port}`);
});

module.exports = app;
