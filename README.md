pluralsight course spring-rest

https://app.pluralsight.com/player?course=spring-rest&author=peter-vanrijn

Following this course originally a FriendController is manually coded.
Then various tests added.

As at
git tag -a v1.1.basic.HATEOS -m "basic hateos"


// with spring-boot-starter-data-rest added to pom,
// the FriendController is not explicitly needed
//
// http://localhost:8080/friend becomes http://localhost:8080/friends
// Content-Type: application/json
// PUT
// {
//        "id": 27,                     // POST doesn't need id
//        "firstName": "tim",
//        "lastName": "nice",
//        "age":66,
//        "addresses":[
//          {
//            "street": "my street",
//            "city":"bristol"
//          },
//          {
//            "street": "my street 2",
//            "city":"bath"
//          }
//        ]
//  }


next when changing to Many to One Db table connections you have to clear down the db:

As at
git tag -a v1.2.basicManyToOne.HATEOS -m "many to one hateos"

// in changing db - login to mysql
//show tables;
//        +-------------------+
//        | Tables_in_friends |
//        +-------------------+
//        | address           |
//        | address_friends   |
//        | friend            |
//        | friend_addresses  |
//        +-------------------+
//drop table friend_addresses;
//drop table friend;
//drop table address;
//drop table address_friends;
//
// run app again with OneToMany
//show tables;
//        +-------------------+
//        | Tables_in_friends |
//        +-------------------+
//        | address           |
//        | friend            |
//        +-------------------+
//    mysql> describe friend;
//        +------------+--------------+------+-----+---------+----------------+
//        | Field      | Type         | Null | Key | Default | Extra          |
//        +------------+--------------+------+-----+---------+----------------+
//        | id         | int(11)      | NO   | PRI | NULL    | auto_increment |
//        | age        | int(11)      | NO   |     | NULL    |                |
//        | first_name | varchar(255) | YES  |     | NULL    |                |
//        | last_name  | varchar(255) | YES  |     | NULL    |                |
//        | married    | bit(1)       | NO   |     | NULL    |                |
//        +------------+--------------+------+-----+---------+----------------+
//        5 rows in set (0.00 sec)
//
//    mysql> describe address;
//        +-----------+--------------+------+-----+---------+----------------+
//        | Field     | Type         | Null | Key | Default | Extra          |
//        +-----------+--------------+------+-----+---------+----------------+
//        | id        | int(11)      | NO   | PRI | NULL    | auto_increment |
//        | city      | varchar(255) | YES  |     | NULL    |                |
//        | street    | varchar(255) | YES  |     | NULL    |                |
//        | friend_id | int(11)      | YES  | MUL | NULL    |                |
//        +-----------+--------------+------+-----+---------+----------------+
//        4 rows in set (0.00 sec)

postman POST http://localhost:8080/friends
//{
//        "firstName": "tim",
//        "lastName": "nice",
//        "age":66,
//        "addresses":[
//        ]
//}

postman RESPONSE:
//{
//        "firstName": "tim",
//        "lastName": "nice",
//        "age": 66,
//        "_links": {
//        "self": {
//        "href": "http://localhost:8080/friends/5"
//        },
//        "friend": {
//        "href": "http://localhost:8080/friends/5"
//        },
//        "addresses": {
//        "href": "http://localhost:8080/friends/5/addresses"
//        }
//        }
//}

postman POST http://localhost:8080/addresses
//{
//        "street": "my street",
//        "city":"bristol",
//        "friend":"http://localhost:8080/friends/5"
//}

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
