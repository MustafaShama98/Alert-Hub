require("dotenv").config();
const { Kafka, logLevel } = require("kafkajs");
const NotificationDTO = require("../dto/NotificationDTO");
const EmailTemplate = require("../templates/emailTemplate");
const EmailService = require("../services/EmailService");

// Initialize Kafka client
const kafka = new Kafka({
  clientId: "email-service",
  brokers: ["localhost:9092"],
  logLevel: logLevel.INFO,
});

// Initialize consumer with specific configuration
const consumer = kafka.consumer({
  groupId: "email-service-group",
  maxBytes: 5242880, // 5MB
  maxBytesPerPartition: 1048576, // 1MB
});

const admin = kafka.admin();
const emailService = new EmailService();

// Function to clean up Kafka pipeline
const cleanupKafkaPipeline = async () => {
  try {
    await admin.connect();
    await admin.deleteGroups(["email-service-group"]);
    console.log("Successfully cleaned up Kafka pipeline");
  } catch (error) {
    console.error("Error cleaning up Kafka pipeline:", error);
  } finally {
    await admin.disconnect();
  }
};

const isValidJSON = (str) => {
  try {
    JSON.parse(str);
    return true;
  } catch (e) {
    return false;
  }
};

const runConsumer = async () => {
  try {
    await cleanupKafkaPipeline();
    await emailService.verifyConnection();
    await consumer.connect();
    console.log("Connected to Kafka");

    await consumer.subscribe({
      topic: "email-topic",
      fromBeginning: false,
    });
    console.log("Subscribed to email-topic");

    await consumer.run({
      autoCommit: true,
      eachMessage: async ({ topic, partition, message }) => {
        try {
          if (!message || !message.value) {
            console.warn("Received empty message, skipping...");
            return;
          }

          let messageStr;
          try {
            messageStr = message.value.toString('utf8');
            if (!isValidJSON(messageStr)) {
              console.error("Invalid JSON message received:", messageStr);
              return;
            }
          } catch (error) {
            console.error("Error converting message to string:", error);
            return;
          }

          console.log("Received Kafka message:", {
            topic,
            partition,
            offset: message.offset,
            headers: message.headers,
            value: messageStr,
          });

          const parsedMessage = JSON.parse(messageStr);
          const notification = new NotificationDTO(
            parsedMessage.type,
            parsedMessage.topic,
            parsedMessage.timestamp,
            parsedMessage.user,
            parsedMessage.data
          );

          const userInfo = notification.getUserInfo();
          const { subject, content } = notification.getNotificationContent();

          console.log("Processing notification:", {
            type: notification.type,
            user: userInfo,
            subject: subject,
          });

          if (!userInfo.email || !subject || !content) {
            console.error("Missing required email data:", { userInfo, subject, content });
            return;
          }

          await emailService.sendEmail(
            userInfo.email,
            subject,
            content,
            null
          );

          console.log("Successfully sent email to:", userInfo.email);
        } catch (error) {
          console.error("Error processing message:", error);
          // Don't rethrow the error to prevent consumer from crashing
        }
      },
    });
  } catch (error) {
    console.error("Error in consumer:", error);
    // Attempt to reconnect
    await consumer.disconnect();
    setTimeout(runConsumer, 5000);
  }
};

// Handle graceful shutdown
const shutdown = async () => {
  try {
    await consumer.disconnect();
    console.log("Consumer disconnected");
    process.exit(0);
  } catch (error) {
    console.error("Error during shutdown:", error);
    process.exit(1);
  }
};

process.on("SIGTERM", shutdown);
process.on("SIGINT", shutdown);

module.exports = { runConsumer };
