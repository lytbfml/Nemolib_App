# Nemolib_App
This application uses API [UWB Nemolibrary](https://github.com/drewandersen/nemolib) to executing the NemoProfile algorithm. It can be also used for motif detection.


We used [Angular](https://angular.io/) (v7.3.5) for the front end, and [Spring Boot](https://spring.io) (v2.1.2.RELEASE) for the back end.  
Both of the front-end and back-end applications are deployed on my AWS.  
To access the back end api, please go to [Swagger UI](http://34.221.211.106:8080/swagger-ui.html#/).  
To acces the front end web page, please go to [NemoLib Application](http://nemolibapp.s3-website-us-west-2.amazonaws.com/).  
Note the above links might be invalid because I am using my personal AWS account.

To learn more, please contact wyxiao@uw.com, hsuy717@uw.edu.

## Getting Start
To run the program on your own server, please read the following

### Prerequisites

- Unix-like operating system (macOS or Linux), or Windows Subsystem for Linux (WSL)
- Nodejs should be installed
- Angular CLI should be installed
- C compiler for [Nauty](http://pallini.di.uniroma1.it/)
- Java 1.8

### Basic installation

Install nodejs to your system
```
sudo apt-get install nodejs
```
Install Angular
```
npm install -g @angular/cli
```
Install C and Java and all other build tools
```
sudo apt-get install build-essential
```

## Using Nemolib Application
1. Clone the repository
2. Go to folder [nemolib_backend](nemolib_backend)
and compile Spring Boot:
```
mvn package
```
Then copy the jar file under the nemolib_backend folder
```
cp target/<name>.jar ./
```
Start the spring boot application
```
java -jar <name>.jar
```
Note the server address will be localhost:8080

3. Go to folder [nemolib_frontend](nemolib_frontend)
and install modules:
```
npm install
```
Then start the server
```
ng serve
```
Note the server address will be localhost:4200, however you can change it to 4201 by add `--port 4201`  
And change the permission of the labelg program: `chmod u+x src/main/resources/labelg`

## Advanced Topics

#### Future Work
Current the frontend reads the results from the backend and process the result string to display. It is useable but there are plenty of smarter ways to do this. For example, update the output section of the Nemolibrary so the frontend can receive more modularized data.  
