const nodemailer = require('nodemailer');

class EmailService {
    constructor() {
        this.transporter = nodemailer.createTransport({
            host: 'smtp.gmail.com',
            port: 587,
            secure: false,
            auth: {
                user: process.env.EMAIL_USER,
                pass: process.env.EMAIL_PASSWORD
            }
        });
    }

    async verifyConnection() {
        await this.transporter.verify();
        console.log('Email service connection verified');
    }

    async sendEmail(to, subject, html, text) {
        const mailOptions = {
            from: `"Alert Hub" <${process.env.EMAIL_USER}>`,
            to,
            subject,
            html,
            text // Fallback plain text
        };

        try {
            await this.transporter.sendMail(mailOptions);
            console.log('Email sent successfully');
        } catch (error) {
            console.error('Error sending email:', error);
            throw error;
        }
    }
}

module.exports = EmailService; 