require('dotenv').config();
const { Kafka, logLevel } = require('kafkajs');
const NotificationDTO = require('../dto/NotificationDTO');
const EmailTemplate = require('../templates/emailTemplate');
const EmailService = require('../services/EmailService');

// Initialize Kafka client
const kafka = new Kafka({
    clientId: 'email-service',
    brokers: ['localhost:9092'],
    logLevel: logLevel.INFO
});

// Initialize consumer with specific configuration
const consumer = kafka.consumer({ 
    groupId: 'email-service-group',
    maxBytes: 5242880, // 5MB
    maxBytesPerPartition: 1048576, // 1MB
});

const admin = kafka.admin();
const emailService = new EmailService();

// Function to clean up Kafka pipeline
const cleanupKafkaPipeline = async () => {
    try {
        await admin.connect();
        
        // Delete the consumer group to reset offsets
        await admin.deleteGroups(['email-service-group']);
        
        console.log('Successfully cleaned up Kafka pipeline');
    } catch (error) {
        console.error('Error cleaning up Kafka pipeline:', error);
    } finally {
        await admin.disconnect();
    }
};

const runConsumer = async () => {
    try {
        // Clean up pipeline before starting
        await cleanupKafkaPipeline();

        // Verify email service
        await emailService.verifyConnection();

        // Connect to Kafka
        await consumer.connect();
        console.log('Connected to Kafka');

        await consumer.subscribe({ 
            topic: 'email-topic', 
            fromBeginning: false, // Start from latest messages
        });
        console.log('Subscribed to email-topic');

        await consumer.run({
            autoCommit: true,
            eachMessage: async ({ topic, partition, message }) => {
                try {
                    // Safely convert buffer to string
                    let messageStr = '';
                    if (message.value) {
                        messageStr = Buffer.from(message.value).toString('utf8');
                    }

                    console.log('Received Kafka message:', {
                        topic,
                        partition,
                        offset: message.offset,
                        headers: message.headers,
                        value: messageStr
                    });

                    let messageValue;
                    try {
                        messageValue = messageStr ? JSON.parse(messageStr) : {};
                    } catch (parseError) {
                        console.error('Error parsing message value:', parseError);
                        messageValue = {};
                    }

                    const messageType = messageValue.type || 'DEFAULT';
                    
                    // Create notification DTO from message
                    const notification = NotificationDTO.fromJSON({
                        type: messageType,
                        message: messageValue.message || '',
                        timestamp: messageValue.timestamp || Date.now()
                    });

                    console.log('Created notification:', {
                        type: notification.type,
                        message: notification.message,
                        timestamp: notification.timestamp
                    });

                    // Generate email content
                    const htmlContent = EmailTemplate.createTemplate(notification);
                    const subject = EmailTemplate.getSubjectForType(notification.type);

                    // Send email
                    await emailService.sendEmail(
                        process.env.EMAIL_USER,
                        subject,
                        htmlContent,
                        notification.message
                    );

                    console.log('Successfully processed message');

                } catch (error) {
                    console.error('Error processing message:', error);
                }
            }
        });

    } catch (error) {
        console.error('Error in consumer:', error);
    }
};

// Handle graceful shutdown
const shutdown = async () => {
    try {
        await consumer.disconnect();
        console.log('Consumer disconnected');
        process.exit(0);
    } catch (error) {
        console.error('Error during shutdown:', error);
        process.exit(1);
    }
};

process.on('SIGTERM', shutdown);
process.on('SIGINT', shutdown);

module.exports = { runConsumer };