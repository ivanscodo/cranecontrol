# TBA - Group
## Interviewee: Ivan Sotelo Codo

Application built for the hiring process of the candidate Ivan Sotelo Codo.

To run this application follow the instructions bellow.
First you need to have installed on your computer:   
1 - Maven 3 or latest  
2 - Java 11 or latest  
3 - Port 27017 available to connect to the database  
4 - Port 8080 available to be used by the application  

   
Clone the repo: https://github.com/ivanscodo/cranecontrol  
1 - run `mvn clean install` on the folder you cloned the application  
2 - run `java -jar cranecontrol-0.0.1-SNAPSHOT.jar.original` inside the target folder  
3 - open your favorite browser and access the url [http://localhost:8080/swagger-ui.html] to see all the available endpoints.  
If you prefer to use command line, follow below some commands you can use to test the application:  
4 - The DB is a MongoDB instance running on Mongo Atlas so the instance will be removed after the end of the hiring process  

<ul>
<li>Create lanes
<ul>
<li><code>curl --request POST \
            --url http://localhost:8080/lane \
            --header 'content-type: application/json' 
            --data '{"lanes":1, "positions":10}'</code>
</li>
</ul>
</li>
<li>Fetch lanes
<ul>
<li><code>curl --request GET \
            --url http://localhost:8080/lane \
            --header 'content-type: application/json'</code>
</ul>
</li>
<li>Find lane by id
<ul>
<li><code>curl --request GET \
            --url http://localhost:8080/lane/{id} \
            --header 'content-type: application/json'</code></li>
</ul>
</li>
</ul>

Observations  
Based on time constraints some stuff aren't done.  
We could have added some features like delete by id, delete all, docker support, more validations, more tests, documentation, and logs, but the application is runnable and tested.  
