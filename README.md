# Alert-Hub (Microservices)

## 📌 Overview

* Alert-Hub provides real-time notifications to project managers about task updates in platforms like Jira, ClickUp, and GitHub.
* Built with a scalable microservices architecture (10 distinct services).

## 🚀 Features

* **Real-time Notifications**: Instant updates triggered by changes in external task management tools.
* **Microservices-Based**: Modular design for easy scalability and maintainability.
* **Robust Messaging**: Reliable communication using Apache Kafka.
* **Efficient Traffic Management**: Integrated Load-Balancer ensures high availability.
* **Secure Access Control**: Centralized API-Gateway handles secure routing and authentication.

## 🛠️ Technology Stack

* **Backend Framework**: Java with Spring Boot
* **Messaging Broker**: Apache Kafka
* **Gateway & Routing**: Spring Cloud Gateway
* **Load Balancing**: Integrated Load-Balancer for scalability

## 📚 Microservices Structure

* **Notification Service**: Generates and dispatches alerts.
* **Event Listener Service**: Connects and listens to external task management tools.
* **User Service**: Handles user authentication and permissions.
* **Evaluation Service**: Manages performance metrics.
* **Security Service**: Oversees secure access and permissions.
* Additional services for data management, logging, and analytics.


