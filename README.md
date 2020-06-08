# Nemolib_App
This application uses API [UWB Nemolibrary](https://github.com/drewandersen/nemolib) to execute the NemoProfile algorithm. It can be also used for motif detection.


We used [Angular](https://angular.io/) (v7.3.5) for the front end, and [Spring Boot](https://spring.io) (v2.1.2.RELEASE) for the back end.  
Both of the front-end and back-end applications are deployed on my AWS.  
To access the back end api, please go to [Swagger UI](https://bioresearch.css.uwb.edu:8082/swagger-ui.html#/).
To acces the front end web page, please go to [NemoLib Application](https://bioresearch.css.uwb.edu/biores/nemo/).  
~~Note the above links might be invalid because I am using my personal AWS account and I only have one EC2 instance for US West.~~

To learn more, please contact wyxiao@uw.edu, hsuy717@uw.edu.

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
Although we has successfully implemented a web-based network motif application and it solves many problems that current applications have, there are still improvements we would like to work on in the future.
##### Modified data model
Current the frontend reads the results from the backend and process the result string to display. It is useable but there are plenty of smarter ways to do this. For example, update the output section of the Nemolibrary so the frontend can receive more modularized data.  
##### Add file management for back-end
Currently we are using Linux server for our back-end Spring application. All uploaded files were saved in side the server and have to be deleted manually. There should be a new API for the Storage service to control those files.
##### Add feature to read direct graph
The current application can only detect network motif based on the an undirected graph. We can improve it by implementing another class which can read direct graph and detect network motif.
##### Provide motif visualization
Our web application can provide user the motif data including graph label, relative frequency, random mean frequency, Z score, P value, and NemoProfile . However, the data is in text format. Since network is essentially graph, if we can add the visualization for each motif and where they are in a network, it will give users more insights about the results.
