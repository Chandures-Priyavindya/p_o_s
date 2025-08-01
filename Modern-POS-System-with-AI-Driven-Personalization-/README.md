# Modern Point of Sale (POS) System with AI-driven Personalization

<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-21%20(LTS)-blue" alt="Java">
  <img src="https://img.shields.io/badge/MySQL-8.0+-orange" alt="MySQL">
  <img src="https://img.shields.io/badge/Docker-Containerized-2496ED" alt="Docker">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
</div>

## 📋 Overview

This is the **Modern Point of Sale (POS) System** designed to deliver advanced, AI-driven, and reliable POS solutions for retail and hospitality businesses. The system is architected as a modular, cloud-ready application using **Spring Boot**, **MySQL** (with Hibernate and native queries), **React.js (with Next.js)**, and is containerized via **Docker**.

The solution supports seamless sales processing, inventory management, customer engagement, multi-mode payments, powerful analytics, offline operation with auto-sync, and integration with both hardware and third-party services.

## 🚀 Technology Stack

### 🔧 Backend
- **Spring Boot** `3.5.x`
- **Java** `21 (LTS)`
- **MySQL** for relational data storage
- **Hibernate** with native SQL queries
- **Spring Security** with **JWT** for secure authentication
- **Spring Data JPA** for ORM
- **ModelMapper** for DTO-to-entity mapping
- **OpenAPI (Swagger UI)** for REST API documentation
- **Layered Architecture**
- **Docker** for containerization

### ⚙️ Build & Tools
- **Maven** for build and dependencies
- **Lombok** to reduce boilerplate code
- **Mockito** and **Spring Boot Test** for testing

## 📦 Project Dependencies

Key dependencies used in this project:
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `mysql-connector-java`
- `springdoc-openapi-starter-webmvc-ui`
- `modelmapper`
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson`
- `lombok`
- `mockito-core`
- `spring-boot-starter-test`

## ✅ System Features Overview

### 1. Sales Processing
- Scan/add products via barcode or manual entry
- Support cash, card, mobile wallets, and contactless payments
- Apply discounts, promotions, and loyalty rewards
- Generate digital and printed receipts

### 2. Inventory Management
- Add, update, and delete product records
- Real-time stock tracking with automated, AI-driven reorder suggestions
- Multi-location inventory synchronization and alerts

### 3. Customer Management & Loyalty
- Store customer profiles with purchase history
- Manage loyalty points and personalized promotions
- Advanced customer targeting using AI analytics

### 4. Authentication & Security
- Role-based access control for users
- Biometric login and multi-factor authentication
- Data encryption at rest and in transit

### 5. Reporting & Analytics
- Real-time dashboards for sales, inventory, and staff performance
- Predictive analytics for sales forecasting and staff optimization

### 6. Hardware Integration
- Supports barcode scanners, receipt printers, cash drawers, biometric devices, NFC payment terminals

### 7. Offline Mode
- Full sales and inventory operations during internet outages
- Automatic data sync to cloud backend when connectivity is restored

## 📂 Project Structure

```
📂 modern-pos-system/
├── 📂 src/
│   ├── 📂 main/java/com/residuesolution/pos/
│   │   ├── 📂 controller/
│   │   ├── 📂 dto/
│   │   ├── 📂 entity/
│   │   ├── 📂 service/
│   │   ├── 📂 repository/
│   │   └── 📄 ModernPosSystemApplication.java
│   └── 📂 resources/
│       └── 📄 application.yml
├── 📄 Dockerfile
├── 📄 pom.xml
└── 📄 README.md
```
## 📚 API Documentation

The API is documented using OpenAPI 3.0 and accessible via Swagger UI at `/swagger-ui.html` when the application is running.
