class NotificationDTO {
  constructor(type, topic, timestamp, user, data, message) {
    this.type = type;
    this.topic = topic;
    this.timestamp = timestamp;
    this.user = user;
    this.data = data;
    this.message = message;
  }

  static fromKafkaMessage(message) {
    const parsed = JSON.parse(message.value);
    return new NotificationDTO(
      parsed.type,
      parsed.topic,
      parsed.timestamp,
      parsed.user,
      parsed.data,
      parsed.message
    );
  }

  getUserInfo() {
    return {
      userId: this.user?.userId || "unknown",
      email: this.user?.email || "unknown",
      name: this.user?.name || "unknown",
    };
  }

  getMessage() {
    switch (this.type) {
      case 'MOST_LABEL_SEARCH':
        return `
          Developer ${this.data.developerName} has been identified as the most active in the "${this.data.label}" category.
          
          Analysis Details:
          - Label: ${this.data.label}
          - Number of Tasks: ${this.data.count}
          - Time Period: Last ${this.data.timeFrameDays} days
          
          This developer has shown significant activity in handling ${this.data.label}-related tasks.
        `;
      default:
        return '';
    }
  }

  getFormattedTimestamp() {
    return new Date(this.timestamp).toLocaleString();
  }

  getFormattedType() {
    return (this.type || "UNKNOWN").replace(/_/g, " ").toLowerCase();
  }

  formatMessage() {
    const actionData = this.data || {};
    const conditions = actionData.condition || [];
    
    // Format conditions for display
    const formattedConditions = conditions.map((andGroup, index) => {
      return `Group ${index + 1}: Metric ID(s) ${andGroup.join(' AND ')}`;
    }).join('\nOR\n');

    // Format date and time
    const runDay = actionData.runOnDay || 'Not specified';
    const runTime = actionData.runOnTime ? `${actionData.runOnTime[0]}:${String(actionData.runOnTime[1]).padStart(2, '0')}` : 'Not specified';
    const createDate = actionData.createDate ? `${actionData.createDate[0]}-${String(actionData.createDate[1]).padStart(2, '0')}-${String(actionData.createDate[2]).padStart(2, '0')}` : 'Not specified';
    const lastUpdate = actionData.lastUpdate ? 
      `${actionData.lastUpdate[0]}-${String(actionData.lastUpdate[1]).padStart(2, '0')}-${String(actionData.lastUpdate[2]).padStart(2, '0')} ${String(actionData.lastUpdate[3]).padStart(2, '0')}:${String(actionData.lastUpdate[4]).padStart(2, '0')}` 
      : 'Not specified';

    return {
      subject: `⏰ Scheduled Action: ${actionData.actionType || 'Alert'}`,
      content: `
        <div class="analysis-result" style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
            <h2 style="color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px;">⏰ Scheduled Action Details</h2>
            
            <div style="background-color: #f8f9fa; padding: 15px; border-radius: 6px; margin: 15px 0;">
                <h3 style="color: #2c3e50; margin-top: 0;">📅 Schedule Information</h3>
                <table style="width: 100%; border-collapse: collapse;">
                    <tr>
                        <th style="text-align: left; padding: 8px; color: #666;">Runs On</th>
                        <td style="padding: 8px;">${runDay} at ${runTime}</td>
                    </tr>
                    <tr>
                        <th style="text-align: left; padding: 8px; color: #666;">Status</th>
                        <td style="padding: 8px;">${actionData.enabled ? '✅ Enabled' : '❌ Disabled'}</td>
                    </tr>
                </table>
            </div>

            <div style="background-color: #f8f9fa; padding: 15px; border-radius: 6px; margin: 15px 0;">
                <h3 style="color: #2c3e50; margin-top: 0;">🎯 Action Details</h3>
                <table style="width: 100%; border-collapse: collapse;">
                    <tr>
                        <th style="text-align: left; padding: 8px; color: #666;">Action Type</th>
                        <td style="padding: 8px;">${actionData.actionType || 'N/A'}</td>
                    </tr>
                    <tr>
                        <th style="text-align: left; padding: 8px; color: #666;">Message</th>
                        <td style="padding: 8px;">${this.message || 'No message provided'}</td>
                    </tr>
                    <tr>
                        <th style="text-align: left; padding: 8px; color: #666;">Created By</th>
                        <td style="padding: 8px;">${actionData.name || 'Unknown'}</td>
                    </tr>
                </table>
            </div>

            <div style="background-color: #f8f9fa; padding: 15px; border-radius: 6px; margin: 15px 0;">
                <h3 style="color: #2c3e50; margin-top: 0;">🔍 Conditions</h3>
                <pre style="background-color: #fff; padding: 10px; border-radius: 4px; white-space: pre-wrap;">${formattedConditions}</pre>
            </div>

            <div style="background-color: #f8f9fa; padding: 15px; border-radius: 6px; margin: 15px 0;">
                <h3 style="color: #2c3e50; margin-top: 0;">📊 Additional Information</h3>
                <table style="width: 100%; border-collapse: collapse;">
                    <tr>
                        <th style="text-align: left; padding: 8px; color: #666;">Created Date</th>
                        <td style="padding: 8px;">${createDate}</td>
                    </tr>
                    <tr>
                        <th style="text-align: left; padding: 8px; color: #666;">Last Updated</th>
                        <td style="padding: 8px;">${lastUpdate}</td>
                    </tr>
                    <tr>
                        <th style="text-align: left; padding: 8px; color: #666;">Last Run</th>
                        <td style="padding: 8px;">${actionData.lastRun || 'Never'}</td>
                    </tr>
                    <tr>
                        <th style="text-align: left; padding: 8px; color: #666;">Action ID</th>
                        <td style="padding: 8px;">#${actionData.id || 'N/A'}</td>
                    </tr>
                </table>
            </div>
        </div>
      `
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
