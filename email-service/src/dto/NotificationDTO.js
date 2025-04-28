class NotificationDTO {
    constructor(type, message, timestamp) {
        this.type = type || 'UNKNOWN';
        this.message = message || 'No message provided';
        this.timestamp = timestamp || Date.now();
    }

    static fromJSON(json) {
        try {
            // If the input is already an object, use it directly
            if (typeof json === 'object' && json !== null) {
                return new NotificationDTO(
                    json.type,
                    json.message,
                    json.timestamp
                );
            }

            // Parse the JSON string
            const data = JSON.parse(json);
            return new NotificationDTO(
                data.type,
                data.message,
                data.timestamp
            );
        } catch (error) {
            console.error('Error parsing notification:', error);
            return new NotificationDTO(
                'ERROR',
                'Failed to parse notification: ' + json,
                Date.now()
            );
        }
    }

    getFormattedTimestamp() {
        return new Date(this.timestamp).toLocaleString();
    }

    getFormattedType() {
        return (this.type || 'UNKNOWN').replace(/_/g, ' ').toLowerCase();
    }

    toJSON() {
        return {
            type: this.type,
            message: this.message,
            timestamp: this.timestamp
        };
    }
}

module.exports = NotificationDTO; 