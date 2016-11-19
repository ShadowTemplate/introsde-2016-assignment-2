# Assignment 2

**Introduction to Service Design and Engineering | University of Trento**

This file aims to provide a short documentation for the second course assignment.  
The original instructions can be found [here](https://sites.google.com/a/unitn.it/introsde_2016-17/lab-sessions/assignments/assignment-2).

The project was developed individually.

Server URL: [https://introsde-a2-server.herokuapp.com/](https://introsde-a2-server.herokuapp.com/)

Additional notes:
* *HealthProfile* is dynamic;
* Extra requests (*R#10*, *R#11*, *R#12*) and responses (*3.10*, *3.11*, *3.12*) implemented.

## Project structure

The project repository is made up of the following *files* and **folders**:
* **client**: client module
    * **src/introsde/client**: Java source code
        * *Main.java*: executes requests to the server
        * *RequestLog.java*: utility class to log requests results
    * *ivy.xml*: Ivy configuration file containing client dependencies  
* **common**: common module
    * **src/introsde/common/to**: Java source code shared between client and server (Transfer Objects)
        * *HealthProfile.java*, *MeasureHistory.java*, *MeasureType.java*, *MeasureTypes.java*, *Person.java*: annotated TOs
    * *ivy.xml*: Ivy configuration file containing TOs dependencies
* **server**: server module
    * **src**: application back end 
        * **introsde/server**: Java source code
            * **dao**: layer converting model objects to TOs
                * *EntityDAO.java*: retrieves model objects by using the persistence layer
                * *TOFactory.java*: builds TOs from object models 
            * **model**: model objects
                *HealthProfile.java*, *MeasureHistory.java*, *MeasureType.java*, *Person.java*: POJOs annotated to be persisted via JPA
            * **persistence**: layer handling persistence via JPA
                * *EntityManagerProxy.java*: proxy class to JPA *EntityManager* allowing only a restricted set of operations 
                * *PersistenceManager.java*: singleton that executes JPA operations on data
            * **resource**: layer exposing REST operations to clients 
                * *ApplicationServer.java*: Jersey configuration class and redirect filter
                * *InitResource.java*: exposes utility operations on database
                * *MeasureTypeResource.java*: exposes operations that handle *MeasureType* objects 
                * *PersonResource.java*: exposes operations that handle *People* objects
                * *ResourceDispatcher.java*: executes client requests; embeds the application logic
        * **META-INF**: metadata directory
            * *persistence.xml*: JPA configuration file            
    * **web**: web application
        * **WEB-INF**: server resources
            * *web.xml*: Java EE deployment descriptor
        * *index.html*, *loading_cube.gif*: home page files 
    * *ivy.xml*: Ivy configuration file containing server dependencies 
* *build.xml*: Ant configuration file containing task definitions (see next paragraph)
* *client-server-json.log*: client execution log in JSON format  
* *client-server-xml.log*: client execution log in XML format
* *README.md*: this file


According to the request, the *main* method in the *Evaluator* class completes the tasks by using the following methods:

1. *printPeople*: prints all the people in *people.xml* by evaluating the XPath expression

    ```
    //person
    ```
2. *printHealthProfile*: prints the health profile information for the person with a specific id in *people.xml* by evaluating the XPath expression

    ```
    /people/person[@id=input_id]/healthprofile
    ```
3. *searchByWeight*: finds people in *people.xml* satisfying a user-defined boolean condition on their weight by evaluating the XPath expression   

    ```
    //healthprofile[weight input_operator input_value]/parent::person
    ```
4. *runXMLMarshalling*: generates random people and performs marshalling via JAXB storing data in a new XML file
5. *runXMLUnmarshalling*: performs unmarshalling via JAXB of the file created during the previous step
6. *runJSONMarshalling*: generates random people and performs marshalling via Jackson storing data in a new JSON file


## Project tasks

Some Ant tasks are defined inside *build.xml*. An overview of what each task does follows. Tasks' dependencies are in brackets:
* *download-ivy*: downloads Ivy jar from the Maven repository
* *install-ivy* (*download-ivy*): adds Ivy jar to the working directory
* *retrieve.common* (*install-ivy*): downloads ivy dependencies for common module
* *init.common* (*retrieve.common*): initializes common workspace
* *retrieve.server* (*install-ivy*): downloads ivy dependencies for server module
* *init.server* (*retrieve.server*): initializes server workspace
* *compile.server* (*init.server, retrieve.common*): compiles server code 
* *build.server* (*compile.server*): packages server application into a war
* *deploy.server* (*build.server*): deploys application war to Heroku
* *retrieve.client* (*install-ivy*): downloads ivy dependencies for client module
* *init.client* (*retrieve.client*): initializes client workspace
* *compile.client* (*init.client, retrieve.common*): compiles client code 
* *build.client* (*compile.client*): packages client application into a jar
* *execute.client* (*build.client*): executes client requests to server


## How to run

To run the code simply clone this project into your computer with:
```
$ git clone https://github.com/ShadowTemplate/introsde-2016-assignment-2.git
$ cd introsde-2016-assignment-2
```

To deploy the server on Heroku run the *deploy.server* Ant task (make sure [heroku-cli](https://devcenter.heroku.com/articles/heroku-command-line) in installed and set up on your pc):
```
$ ant deploy.server
```

To execute client requests run the *execute.client* Ant task:
```
$ ant execute.client
```
