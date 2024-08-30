# Group Allocation System

This is a group allocation system designed to automatically assign students to groups within specific modules.
The module convenor can create a set of questions to better understand which students would go together in a group well and once the students have answered these questions they will be added to a group.
You can see a live version of the site here:
https://group-allocator.media-cam.com

## Highlighted Features:

### As a convenor you can:
* View, create and edit modules.

* View, create and edit question sets.

* Add students to the system individually, or collectively using a csv file.

## Prerequisites:

* MySQL Server > 5.7 from https://dev.mysql.com/downloads/mysql/
* Gradle 7.2 from https://gradle.org/releases/
* JDK 11 from https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html
* Get a recaptcha V2 Site and Secret Key from https://www.google.com/recaptcha/admin

## Installation
### Download and install the required prerequisites
* Download and extract the repository from GitLab
* Create an SQL database named groupAllocation
* Make sure your SQL server is running
* Navigate to the directory you downloaded the repository and go to : `src/main/resources/application.properties`
Modify this file to contain your database url, name and password.
* Also in the application.properties file, change the following lines for your Google recaptcha: `google.recaptcha.key.site=YOUR_SITE_KEY`, `google.recaptcha.key.secret-key=YOUR_SECRET_KEY`, `recaptcha.validation.secret-key=YOUR_SECRET_KEY`
* Also in the application.properties file, edit the email information to match your own: `spring.mail.username=YOUR_EMAIL_ADDRESS`, `spring.mail.password=YOUR_EMAIL_PASSWORD`
* Navigate to localhost:8080 in a web-browser


### Commit names:
* Cameron Ward (cw467): C Ward, Cameron, CameronWard301
* Josh Wilkins (jrw40): J R Wilkins
* Zeynep Aksay (zna2): Z N Aksay
* Muneer Fakih (mf314): Muneer Fakih
* Aditya Shukla (as1264): A Shukla
