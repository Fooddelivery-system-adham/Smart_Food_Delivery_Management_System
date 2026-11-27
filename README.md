Smart Food Delivery Management System: 
Course Project: SE-10636313 / Software Engineering Project  
  Author / Student: Adham & Ahmad  
  Technology Stack: Java 21, PostgreSQL (Schema: "SWproject"), Maven, JUnit 5, Mockito, JaCoCo, Jakarta Mail  
  Project Overview:  
  The Smart Food Delivery Management System is an end-to-end, multi-layered Java application designed to manage food delivery workflows seamlessly. The system supports authentication, restaurant and menu browsing, GPS-based automatic distance calculation, dynamic pricing strategies, polymorphic orders and driver fleet management, Observer-based live order status tracking, automated email invoice generation, and cash payment processing.  

How to Set Up & Run:  
1. Database & Configuration Setup  
Create a file named db.conf in the project root directory:  
db.url=jdbc:postgresql://localhost:5432/postgres  
db.user=postgres  
db.password=YOUR_POSTGRES_PASSWORD  
# SMTP Email Settings (for Jakarta Mail)  
email.host=smtp.gmail.com  
email.port=587  
email.username=your_email@gmail.com  
email.password=your_16_char_app_password  
2. Execute SQL Database Setup  
Run the PostgreSQL setup script to create schema "SWproject", tables, constraints, and sample Palestinian users/drivers/restaurants.  

  

