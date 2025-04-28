class EmailTemplate {
    static getStyles() {
        return `
            body {
                font-family: Arial, sans-serif;
                line-height: 1.6;
                color: #333;
                max-width: 600px;
                margin: 0 auto;
            }
            .header {
                background-color: #f8f9fa;
                padding: 20px;
                text-align: center;
                border-bottom: 3px solid #007bff;
            }
            .logo {
                max-width: 200px;
                height: auto;
                margin-bottom: 15px;
            }
            .content {
                padding: 20px;
                background-color: #ffffff;
            }
            .footer {
                text-align: center;
                padding: 20px;
                font-size: 12px;
                color: #666;
                background-color: #f8f9fa;
            }
            .alert {
                padding: 15px;
                margin-bottom: 20px;
                border: 1px solid #ddd;
                border-radius: 4px;
                background-color: #f8f9fa;
            }
            .timestamp {
                color: #666;
                font-size: 12px;
            }
        `;
    }

    static createTemplate(notification) {
        const logoUrl = `https://preparecenter.org/wp-content/uploads/2021/04/AlertHub.png`;

        return `
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                ${this.getStyles()}
            </style>
        </head>
        <body>
            <div class="header">
                <img src="${logoUrl}" alt="Alert Hub Logo" class="logo">
                <h2>Alert Hub Notification</h2>
                <p>${notification.getFormattedType()}</p>
            </div>
            <div class="content">
                <div class="alert">
                    ${notification.message}
                </div>
                <p class="timestamp">Generated on: ${notification.getFormattedTimestamp()}</p>
            </div>
            <div class="footer">
                <p>This is an automated message from Alert Hub</p>
                <p>© ${new Date().getFullYear()} Alert Hub. All rights reserved.</p>
            </div>
        </body>
        </html>
        `;
    }

    static getSubjectForType(type) {
        const subjects = {
            'MOST_LABEL_SEARCH': '🏆 Developer Label Analysis Results',
            'LABEL_AGGREGATE': '📊 Developer Label Statistics',
            'TASK_AMOUNT': '📋 Developer Task Summary',
            'DEFAULT': '🔔 Alert Hub Notification'
        };
        return subjects[type] || subjects.DEFAULT;
    }
}

module.exports = EmailTemplate; 