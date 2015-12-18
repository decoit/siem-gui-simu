# DECOIT SIEM-GUI (SIMU edition) #

The DECOIT SIEM-GUI was developed during the [iMonitor](http://www.imonitor-project.de/) and [SIMU](http://www.simu-project.de/) research projects, while this repository contains the iMonitor version of it. Developed as a Java web application it serves as the administrator front-end of the SIEM system developed in that project. It is based on the Spring Framework version 4.1 and makes heavy use of advanced technologies such as WebSockets. The back-end of the SIEM-GUI was developed as a Java web application that runs on a Apache Tomcat 8 application server. It serves a web-interface written with AngularJS to allow users of the SIEM system access to detected incidents and raw event data.

## Preparation ##

To use the SIEM-GUI several steps are required in advance to make sure everything builds and runs fine. It relies on a number of external services and tools.

* Java 8
* Maven 3
* Request Tracker (RT)
* DECOIT RT-Connector
* Apache Tomcat 8

### Java 8 and Maven 3 ###

Maven is required to build this project. The SIEM-GUI uses Java 8 features and thus a Java 8 (preferably Oracle JDK) is required. It will not run on Java 7 or below!

### Request Tracker (RT) ###

The ticket system RT is required by the SIEM-GUI as well. It serves as user management system and ticket system to track incident processing. RT must be accessible via HTTP or HTTPS by the SIEM-GUI and required some additional configuration. Please refer to the section Configuration below, the detailed configuration of RT is described there.

### DECOIT RT-Connector ###

To access the REST API of RT our RT-Connector is required. It can be downloaded [here](https://github.com/decoit/rt-connector). Instruction how to build and install it can be found there as well.

### Apache Tomcat 8 ###

The Apache Tomcat 8 application server is required to serve the SIEM-GUI WAR file. It must be configured to use a Java 8 JRE. If you are planning to use RT via HTTPS the server for RT must be configured properly and the certificate file must be importet into the TrustStore that will be used by the Tomcat domain that serves the SIEM-GUI application. Please refer to the [RT-Connector](https://github.com/decoit/rt-connector) documentation for information on how to setup a proper certificate and import it into a key store file.

## Build and installation ##

For further information about how to install and run the application please refer to the Best Practice Report (German) available on the [SIMU project website](http://www.simu-project.de/).
