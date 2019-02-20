## pluralsight course spring-rest

https://app.pluralsight.com/player?course=spring-rest&author=peter-vanrijn

Following this course originally a FriendController is manually coded.
Then various tests added.

As at
```
git tag -a v1.1.basic.HATEOS -m "basic hateos"
```


## with spring-boot-starter-data-rest added to pom,
 the FriendController is not explicitly needed

Postman
```
 http://localhost:8080/friend becomes http://localhost:8080/friends
 Content-Type: application/json
 PUT
 {
        "id": 27,                     // POST doesn't need id
        "firstName": "tim",
        "lastName": "nice",
        "age":66,
        "addresses":[
          {
            "street": "my street",
            "city":"bristol"
          },
          {
            "street": "my street 2",
            "city":"bath"
          }
        ]
  }
```

## Many to One

next when changing to Many to One Db table connections you have to clear down the db:

As at
```
        git tag -a v1.2.basicManyToOne.HATEOS -m "many to one hateos"
```

in changing db - login to mysql

```
show tables;
        +-------------------+
        | Tables_in_friends |
        +-------------------+
        | address           |
        | address_friends   |
        | friend            |
        | friend_addresses  |
        +-------------------+
drop table friend_addresses;
drop table friend;
drop table address;
drop table address_friends;
```

 run app again with OneToMany
```
show tables;
        +-------------------+
        | Tables_in_friends |
        +-------------------+
        | address           |
        | friend            |
        +-------------------+
    mysql> describe friend;
        +------------+--------------+------+-----+---------+----------------+
        | Field      | Type         | Null | Key | Default | Extra          |
        +------------+--------------+------+-----+---------+----------------+
        | id         | int(11)      | NO   | PRI | NULL    | auto_increment |
        | age        | int(11)      | NO   |     | NULL    |                |
        | first_name | varchar(255) | YES  |     | NULL    |                |
        | last_name  | varchar(255) | YES  |     | NULL    |                |
        | married    | bit(1)       | NO   |     | NULL    |                |
        +------------+--------------+------+-----+---------+----------------+
        5 rows in set (0.00 sec)

    mysql> describe address;
        +-----------+--------------+------+-----+---------+----------------+
        | Field     | Type         | Null | Key | Default | Extra          |
        +-----------+--------------+------+-----+---------+----------------+
        | id        | int(11)      | NO   | PRI | NULL    | auto_increment |
        | city      | varchar(255) | YES  |     | NULL    |                |
        | street    | varchar(255) | YES  |     | NULL    |                |
        | friend_id | int(11)      | YES  | MUL | NULL    |                |
        +-----------+--------------+------+-----+---------+----------------+
        4 rows in set (0.00 sec)
```

```
        postman POST http://localhost:8080/friends
{
        "firstName": "tim",
        "lastName": "nice",
        "age":66,
        "addresses":[
        ]
}
```
```
        postman RESPONSE:
{
        "firstName": "tim",
        "lastName": "nice",
        "age": 66,
        "_links": {
        "self": {
        "href": "http://localhost:8080/friends/5"
        },
        "friend": {
        "href": "http://localhost:8080/friends/5"
        },
        "addresses": {
        "href": "http://localhost:8080/friends/5/addresses"
        }
        }
}
```
```
        postman POST http://localhost:8080/addresses
{
        "street": "my street",
        "city":"bristol",
        "friend":"http://localhost:8080/friends/5"
}
```
```
        postman GET http://localhost:8080/friends  and we see link now exists

        {
        "_embedded": {
        "friends": [
        {
        "firstName": "tim",
        "lastName": "nice",
        "age": 66,
        "_links": {
        "self": {
        "href": "http://localhost:8080/friends/5"
        },
        "friend": {
        "href": "http://localhost:8080/friends/5"
        },
        "addresses": {
        "href": "http://localhost:8080/friends/5/addresses"
        }
        }
        }
        ]
        },
        "_links": {
        "self": {
        "href": "http://localhost:8080/friends"
        },
        "profile": {
        "href": "http://localhost:8080/profile/friends"
        }
        }
        }
```


## Many to Many

next change to Many to Many - Address and Friend class changes

run App


```
        mysql> show tables;
        +-------------------+
        | Tables_in_friends |
        +-------------------+
        | address           |
        | address_friends   |
        | friend            |
        +-------------------+
        3 rows in set (0.00 sec)


        address and friend have the same db description.

        describe address_friends;
        +--------------+---------+------+-----+---------+-------+
        | Field        | Type    | Null | Key | Default | Extra |
        +--------------+---------+------+-----+---------+-------+
        | addresses_id | int(11) | NO   | PRI | NULL    |       |
        | friends_id   | int(11) | NO   | PRI | NULL    |       |
        +--------------+---------+------+-----+---------+-------+

        This is a linking table
```

```
        postman
        GET http://localhost:8080/friends/
        {
        "_embedded": {
        "friends": [
        {
        "firstName": "tim",
        "lastName": "nice",
        "age": 66,
        "_links": {
        "self": {
        "href": "http://localhost:8080/friends/5"
        },
        "friend": {
        "href": "http://localhost:8080/friends/5"
        },
        "addresses": {
        "href": "http://localhost:8080/friends/5/addresses"
        }
        }
        }
        ]
        },
        "_links": {
        "self": {
        "href": "http://localhost:8080/friends/"
        },
        "profile": {
        "href": "http://localhost:8080/profile/friends"
        }
        }
        }
```
```
        GET http://localhost:8080/friends/5/addresses
        {
        "_embedded": {
        "addresses": []             <-- see empty
        },
        "_links": {
        "self": {
        "href": "http://localhost:8080/friends/5/addresses"
        }
        }
        }
```
```
        GET http://localhost:8080/addresses
        {
        "_embedded": {
        "addresses": [
        {
        "street": "my street",
        "city": "bristol",
        "_links": {
        "self": {
        "href": "http://localhost:8080/addresses/1"
        },
        "address": {
        "href": "http://localhost:8080/addresses/1"
        },
        "friends": {
        "href": "http://localhost:8080/addresses/1/friends"
        }
        }
        }
        ]
        },
        "_links": {
        "self": {
        "href": "http://localhost:8080/addresses"
        },
        "profile": {
        "href": "http://localhost:8080/profile/addresses"
        }
        }
        }
```
```
        GET http://localhost:8080/addresses/1/friends
        {
        "_embedded": {
        "friends": []           <-- empty
        },
        "_links": {
        "self": {
        "href": "http://localhost:8080/addresses/1/friends"
        }
        }
        }
```
```
        POST http://localhost:8080/addresses/1/friends
        raw http://localhost:8080/friends/5
        Content-type: text/uri-list
```

        Now the coupling is correct
```
        GET http://localhost:8080/friends
        {
        "_embedded": {
        "friends": [
        {
        "firstName": "tim",
        "lastName": "nice",
        "age": 66,
        "_links": {
        "self": {
        "href": "http://localhost:8080/friends/5"
        },
        "friend": {
        "href": "http://localhost:8080/friends/5"
        },
        "addresses": {
        "href": "http://localhost:8080/friends/5/addresses"
        }
        }
        }
        ]
        },
        "_links": {
        "self": {
        "href": "http://localhost:8080/friends"
        },
        "profile": {
        "href": "http://localhost:8080/profile/friends"
        }
        }
        }

        GET http://localhost:8080/friends/5
        {
        "firstName": "tim",
        "lastName": "nice",
        "age": 66,
        "_links": {
        "self": {
        "href": "http://localhost:8080/friends/5"
        },
        "friend": {
        "href": "http://localhost:8080/friends/5"
        },
        "addresses": {
        "href": "http://localhost:8080/friends/5/addresses"
        }
        }
        }

        GET http://localhost:8080/friends/5/addresses
        {
        "_embedded": {
        "addresses": [
        {
        "street": "my street",
        "city": "bristol",
        "_links": {
        "self": {
        "href": "http://localhost:8080/addresses/1"
        },
        "address": {
        "href": "http://localhost:8080/addresses/1"
        },
        "friends": {
        "href": "http://localhost:8080/addresses/1/friends"
        }
        }
        }
        ]
        },
        "_links": {
        "self": {
        "href": "http://localhost:8080/friends/5/addresses"
        }
        }
        }


        GET http://localhost:8080/addresses/
        {
        "_embedded": {
        "addresses": [
        {
        "street": "my street",
        "city": "bristol",
        "_links": {
        "self": {
        "href": "http://localhost:8080/addresses/1"
        },
        "address": {
        "href": "http://localhost:8080/addresses/1"
        },
        "friends": {
        "href": "http://localhost:8080/addresses/1/friends"
        }
        }
        }
        ]
        },
        "_links": {
        "self": {
        "href": "http://localhost:8080/addresses/"
        },
        "profile": {
        "href": "http://localhost:8080/profile/addresses"
        }
        }
        }

        GET http://localhost:8080/addresses/1/friends
        {
        "_embedded": {
        "friends": [
        {
        "firstName": "tim",
        "lastName": "nice",
        "age": 66,
        "_links": {
        "self": {
        "href": "http://localhost:8080/friends/5"
        },
        "friend": {
        "href": "http://localhost:8080/friends/5"
        },
        "addresses": {
        "href": "http://localhost:8080/friends/5/addresses"
        }
        }
        }
        ]
        },
        "_links": {
        "self": {
        "href": "http://localhost:8080/addresses/1/friends"
        }
        }
        }

```

## compile
```
mvn clean install
```

## run from jar specifying alternate properties file fom current directory
```
java -Dspring.profiles.active=test -Dspring.config.location=./application-test.properties  -jar target/springrest-0.0.1-SNAPSHOT.jar
```

## Docker
### build docker image
```
docker build -t  springrest:v1 .
```

### run docker compose

There is an external to jar properties file that we want to use to supply the properties.
This is located in the same dir as the docker-compose.yml and has db connection params.


```
docker-compose  -f docker-compose/docker-compose.yml up
```

## Kubernetes
### Rename docker image

above we named the image springrest:v1 - next we add another tag to the image id then remove the original tag

```
docker tag springrest:v1 <your_docker_hub_name>/springrest:v1
docker push <your_docker_hub_name>/springrest:v1
docker rmi springrest:v1
```

image is now on https://hub.docker.com/

## install kubernetes
see README.md in

https://github.com/robinjohnhopkins/Simple-Web-Server-cpp11-Docker-eg

## run minikube dashboard to see effect of command line invocations in a browser!
```
minikube dashboard
```

## create secret
```
kubectl create secret generic prod.props --from-literal=dbpassword=supersecret --from-file=./kubernetes/application-prod.properties
```
Here we have a kubernetes specific properties file and a dbpassword that will be passed into pods as an environment variable.


## run mysql pod
```
kubectl create -f mysql-deployment.yaml
kubectl get all
NAME                         READY     STATUS    RESTARTS   AGE
pod/mysql-8664987864-7hnk8   1/1       Running   0          11d

NAME                 TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)    AGE
service/kubernetes   ClusterIP   10.96.0.1    <none>        443/TCP    11d
service/mysql        ClusterIP   None         <none>        3306/TCP   11d

NAME                    DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/mysql   1         1         1            1           11d

NAME                               DESIRED   CURRENT   READY     AGE
replicaset.apps/mysql-8664987864   1         1         1         11d

```


## mysql command prompt in minikube dashboard

In kubernetes dashboard, show pods, click on mysql-1234567890-abcde pod

click EXEC

```
mysql -p

mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| friends            |
| mysql              |
| performance_schema |
+--------------------+
4 rows in set (0.00 sec)

mysql> create database thingy;
Query OK, 1 row affected (0.00 sec)

mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| friends            |
| mysql              |
| performance_schema |
| thingy             |
+--------------------+
5 rows in set (0.00 sec)
exit
exit
```
Above `friends` was already created.
This was because we specified MYSQL_DATABASE in the yaml.

see https://hub.docker.com/_/mysql   for explanation of terms

e.g. MYSQL_DATABASE - optional and allows you to specify the name of a database to be created on image startup

If you map a directory to mysql and do not specify MYSQL_DATABASE then you might need to go into a command line
and as a one off task create the db as shown above. After which each restart will use the already created db.

## mysql command prompt from commandline
```
kubectl get pods
NAME                     READY     STATUS    RESTARTS   AGE
mysql-8664987864-7hnk8   1/1       Running   0          11d

kubectl exec -it mysql-8664987864-7hnk8 -- /bin/bash

mysql -p
Enter password:

mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| friends            |
| mysql              |
| performance_schema |
+--------------------+
4 rows in set (0.00 sec)
```

## run java spring rest app
```
kubectl create -f svc.yml

kubectl create -f deploy.yml

kubectl get all
NAME                                     READY     STATUS    RESTARTS   AGE
pod/mysql-8664987864-7hnk8               1/1       Running   0          11d
pod/springrest-deploy-65f85b49b4-lpvrf   1/1       Running   0          11d

NAME                     TYPE        CLUSTER-IP    EXTERNAL-IP   PORT(S)          AGE
service/kubernetes       ClusterIP   10.96.0.1     <none>        443/TCP          11d
service/mysql            ClusterIP   None          <none>        3306/TCP         11d
service/springrest-svc   NodePort    10.99.82.73   <none>        8080:30001/TCP   11d

NAME                                DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/mysql               1         1         1            1           11d
deployment.apps/springrest-deploy   1         1         1            1           11d

NAME                                           DESIRED   CURRENT   READY     AGE
replicaset.apps/mysql-8664987864               1         1         1         11d
replicaset.apps/springrest-deploy-65f85b49b4   1         1         1         11d

kubectl logs pod/springrest-deploy-65f85b49b4-lpvrf
...
2019-02-08 19:54:09.679  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2019-02-08 19:54:09.691  INFO 1 --- [           main] t.com.springrest.SpringrestApplication   : Started SpringrestApplication in 13.015 seconds (JVM running for 14.126)
```
Here the pod has started up and connected to the mysql db called 'friends'.

## check a GET rest end point in browser
```
minikube service list
|-------------|----------------------|---------------------------|
|  NAMESPACE  |         NAME         |            URL            |
|-------------|----------------------|---------------------------|
| default     | kubernetes           | No node port              |
| default     | mysql                | No node port              |
| default     | springrest-svc       | http://192.168.x.x:30001 |
| kube-system | kube-dns             | No node port              |
| kube-system | kubernetes-dashboard | No node port              |
|-------------|----------------------|---------------------------|
```
Here the springrest-svc has exposed the internal port 8080 to external port 30001.
Thus you can try the following endpont in a browser.

http://192.168.x.x:30001

Then as we have implemented a hateos REST service you see:
```
{
  "_links" : {
    "friends" : {
      "href" : "http://192.168.x.x:30001/friends"
    },
    "addresses" : {
      "href" : "http://192.168.x.x:30001/addresses"
    },
    "profile" : {
      "href" : "http://192.168.x.x:30001/profile"
    }
  }
}
```

The rest service itself is self-documenting.
Postman can again be used to test out the GET, POST, PUT, DELETE commands as shown above.
