# Server

Web server is made for Saving data that sensors in control system detect in database and Using that data in the Android application. Web server is based on AWS EC2  infrastructure  and  implemented  using  LAMP(Apache,MySQL,PHP in Linux) stack.

## Server Specification

* Apache Version : Apache/2.4.18(Ubuntu)
* PHP Version : 7.1.13-1
* MySQL Version : 5.7.21

## Database Structure
Database is composed of three tables: (1) a table for storing a Waggle's ID and location, (2) a table for storing a battery status log, and (3) a table for storing only the latest value among second table’s logs. The log table stores temperature, humidity, current, voltage, heater and fan on / off status.

![](../Document/DB_Design/Waggle_RDB.png)

## Simple Explanation
* **waggle\_receive\_function\_with\_csv.php**<br />
 It is used to deal with requests from a control system with Raspberry pi. Once it gets requests from it, It tries to connect with DB(function_dbconnection.php). It sends a message to FCM server to alert when the battery remain is under 20 percent(function_notification.php). It also backs up data in DB every week(function_csv.php). And It stores the information from control system in DB. The information is stacked into BatteryStatus_log table and updated into BatteryStatus table.

* **waggle\_data\_req.php**<br />
 It is used to deal with requests from an android app named WaggleBattery. It give responses like the Waggle list, WaggleBattery status and so on.
