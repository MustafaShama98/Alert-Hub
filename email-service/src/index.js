const { Kafka } = require('kafkajs');
const NotificationDTO = require('./dto/NotificationDTO');
const EmailClient = require('./client/EmailClient');

const kafka = new Kafka({
    clientId: 'email-service',
    brokers: ['localhost:9092']
});

const consumer = kafka.consumer({ groupId: 'email-consumer-group' });
const emailClient = new EmailClient();

const run = async () => {
    await consumer.connect();
    await consumer.subscribe({ topic: 'email-topic', fromBeginning: true });

    await consumer.run({
        eachMessage: async ({ topic, partition, message }) => {
            console.log({
                partition,
                offset: message.offset,
                headers: message.headers,
                value: message.value.toString(),
            });

            try {
                const notification = NotificationDTO.fromKafkaMessage(message);
                const content = notification.getNotificationContent();
                
                console.log('Processing notification:', {
                    type: notification.type,
                    user: notification.getUserInfo(),
                    subject: content.subject,
                    content: content.content
                });

                // Send email using the email client
                await emailClient.sendEmail(
                    notification.getUserInfo().email,
                    content.subject,
                    content.content
                );
            } catch (error) {
                console.error('Error processing message:', error);
            }
        },
    });
};

run().catch(console.error); 