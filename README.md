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

