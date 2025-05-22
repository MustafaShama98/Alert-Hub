class NotificationDTO {
  constructor(type, topic, timestamp, user, data, message) {
    this.type = type || "UNKNOWN";
    this.topic = topic;
    this.timestamp = timestamp || Date.now();
    this.user = user || {};
    this.data = data || {};
    this.message = message || {};
  }

  static fromJSON(json) {
    try {
      // If the input is already an object, use it directly
      const data = typeof json === "object" ? json : JSON.parse(json);

      return new NotificationDTO(
        data.type,
        data.topic,
        data.timestamp,
        data.user,
        data.data,
        data.message
      );
    } catch (error) {
      console.error("Error parsing notification:", error);
      return new NotificationDTO(
        "ERROR",
        "email-topic",
        Date.now(),
        {},
        { error: "Failed to parse notification" }
      );
    }
  }

  getFormattedTimestamp() {
    return new Date(this.timestamp).toLocaleString();
  }

  getFormattedType() {
    return (this.type || "UNKNOWN").replace(/_/g, " ").toLowerCase();
  }

  formatMessage() {
    const mesagge = this.message;
    return {
      subject: `New Scheduler Notification`,
      content: `
                <div class="analysis-result">
                    <h2>🏆  ${mesagge}</h2>
                    
                </div>
            `,
    };
  }
  getUserInfo() {
    return {
      userId: this.user?.userId || "unknown",
      email: this.user?.email || "unknown",
      name: this.user?.name || "unknown",
    };
  }

  getNotificationContent() {
    switch (this.type) {
      case "MOST_LABEL_SEARCH":
        return this.formatMostLabelContent();
      case "LABEL_AGGREGATE":
        return this.formatLabelAggregateContent();
      case "TASK_AMOUNT":
        return this.formatTaskAmountContent();
      case "ACTION":
        return this.formatMessage();
      default:
        return {
          subject: "Notification",
          content: "Unknown notification type",
        };
    }
  }

  formatMostLabelContent() {
    const data = this.data;
    return {
      subject: `Most Used Label Analysis: ${data.label}`,
      content: `
                <div class="analysis-result">
                    <h2>🏆 Label Analysis Results</h2>
                    <table>
                        <tr>
                            <th>Developer</th>
                            <td>${data.developerName}</td>
                        </tr>
                        <tr>
                            <th>Developer ID</th>
                            <td>${data.developerId}</td>
                        </tr>
                        <tr>
                            <th>Label</th>
                            <td><span class="label-count">${data.label}</span></td>
                        </tr>
                        <tr>
                            <th>Task Count</th>
                            <td><span class="label-count">${data.count}</span> tasks</td>
                        </tr>
                        <tr>
                            <th>Time Frame</th>
                            <td>Last ${data.timeFrameDays} days</td>
                        </tr>
                    </table>
                    <p><strong>Summary:</strong> Developer ${data.developerName} has the highest usage of label '${data.label}' 
                    with ${data.count} tasks in the analyzed period.</p>
                </div>
            `,
    };
  }

  formatLabelAggregateContent() {
    const data = this.data;
    const labelRows = data.labels
      .map(
        (label) =>
          `<tr>
                <td>${label.label}</td>
                <td><span class="label-count">${label.count}</span> tasks</td>
            </tr>`
      )
      .join("");

    return {
      subject: `Label Analysis Report for ${data.developerId}`,
      content: `
                <div class="analysis-result">
                    <h2>📊 Label Usage Analysis</h2>
                    <p>Analysis for developer ${data.developerId} over the last ${data.timeFrameDays} days:</p>
                    
                    <table>
                        <thead>
                            <tr>
                                <th>Label</th>
                                <th>Task Count</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${labelRows}
                        </tbody>
                    </table>
                </div>
            `,
    };
  }

  formatTaskAmountContent() {
    const data = this.data;
    return {
      subject: `Task Analysis for ${data.developerName}`,
      content: `
                <div class="analysis-result">
                    <h2>📋 Task Amount Analysis</h2>
                    <table>
                        <tr>
                            <th>Developer</th>
                            <td>${data.developerName}</td>
                        </tr>
                        <tr>
                            <th>Developer ID</th>
                            <td>${data.developerId}</td>
                        </tr>
                        <tr>
                            <th>Total Tasks</th>
                            <td><span class="label-count">${data.taskCount}</span> tasks</td>
                        </tr>
                        <tr>
                            <th>Time Frame</th>
                            <td>Last ${data.timeFrameDays} days</td>
                        </tr>
                    </table>
                    <p><strong>Summary:</strong> Developer ${data.developerName} has completed 
                    <span class="label-count">${data.taskCount}</span> tasks in the analyzed period.</p>
                </div>
            `,
    };
  }

  toJSON() {
    return {
      type: this.type,
      topic: this.topic,
      timestamp: this.timestamp,
      user: this.user,
      data: this.data,
      mesagge: this.message,
    };
  }
}

module.exports = NotificationDTO;
