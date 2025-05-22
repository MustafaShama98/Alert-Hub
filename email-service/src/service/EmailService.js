class EmailService {
    constructor(emailClient) {
        this.emailClient = emailClient;
    }

    async processNotification(notification) {
        const { type, user, data } = notification;
        let subject = '';
        let message = '';

        switch (type) {
            case 'MOST_LABEL_SEARCH':
                subject = `Most Used Label Analysis: ${data.label}`;
                message = `
                    Developer ${data.developerName} has been identified as the most active in the "${data.label}" category.
                    
                    Analysis Details:
                    - Label: ${data.label}
                    - Number of Tasks: ${data.count}
                    - Time Period: Last ${data.timeFrameDays} days
                    
                    This developer has shown significant activity in handling ${data.label}-related tasks.
                `;
                break;
            // ... other cases
        }

        return {
            type,
            user: {
                userId: user.userId || 'unknown',
                email: user.email || 'unknown',
                name: user.name || 'unknown'
            },
            subject,
            message
        };
    }
} 