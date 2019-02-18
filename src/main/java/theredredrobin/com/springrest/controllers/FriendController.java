package theredredrobin.com.springrest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import theredredrobin.com.springrest.model.Friend;
import theredredrobin.com.springrest.services.FriendService;
import theredredrobin.com.springrest.util.ErrorMessage;
import theredredrobin.com.springrest.util.FieldErrorMessage;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

//postman POST http://localhost:8080/friends
//{
//        "firstName": "tim",
//        "lastName": "nice",
//        "age":66,
//        "addresses":[
//        ]
//}

// postman RESPONSE:
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

//postman POST http://localhost:8080/addresses
//{
//        "street": "my street",
//        "city":"bristol",
//        "friend":"http://localhost:8080/friends/5"
//}

//postman GET http://localhost:8080/friends  and we see link now exists
/*
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
*/


//@RestController
//public class FriendController {
//
//    @Autowired
//    FriendService friendService;
//
//    @PostMapping("/friend")
//    Friend create(@Valid @RequestBody Friend friend) throws ValidationException {
//// NB afteradding @Valid, don't need this validation!
////        if (friend.getId() == 0 && friend.getFirstName() != null && friend.getLastName() != null )
//            return friendService.save(friend);
////        else
////            throw new ValidationException("friend cannot be created.!");
//    }
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    List<FieldErrorMessage>  exceptionHandler(MethodArgumentNotValidException e){
//        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
//        List<FieldErrorMessage> fieldErrorMessages = fieldErrors.stream().map(fieldError -> new FieldErrorMessage(fieldError.getField(), fieldError.getDefaultMessage())).collect(Collectors.toList());
//        return fieldErrorMessages;
//    }
//// moved to class ControllerExceptionHandler
////    @ResponseStatus(HttpStatus.BAD_REQUEST)
////    @ExceptionHandler(ValidationException.class)
////    //ResponseEntity<String> exceptionHandler(ValidationException e){
////    ErrorMessage exceptionHandler(ValidationException e){
////        //return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
////        return new ErrorMessage("400", e.getMessage());
////    }
//
//    @GetMapping("/friend")
//    Iterable<Friend> read(){
//        return friendService.findAll();
//    }
//
//    @PutMapping("/friend")
//    ResponseEntity<Friend> update(@RequestBody Friend friend) {
//        if (friendService.findById(friend.getId()).isPresent())
//            return new ResponseEntity<>(friendService.save(friend), HttpStatus.OK);
//        else
//            return new ResponseEntity<>(friend, HttpStatus.BAD_REQUEST);
//    }
//
//    @DeleteMapping("/friend/{id}")
//    void delete (@PathVariable Integer id){
//        friendService.deleteById(id);
//    }
//    @GetMapping("/friend/{id}")
//    Optional<Friend> findById(@PathVariable Integer id){
//        return friendService.findById(id);
//    }
//    @GetMapping("/friend/search")
//    Iterable<Friend> findByQuery(@RequestParam(value = "first", required = false) String firstName,
//                                 @RequestParam(value = "lastName", required = false) String lastName){
//        if (firstName != null && lastName != null)
//            return friendService.findByFirstNameAndLastName(firstName, lastName);
//        else if (firstName != null)
//            return friendService.findByFirstName(firstName);
//        else if (lastName != null)
//            return friendService.findByLastName(lastName);
//        else
//            return friendService.findAll();
//    }
//
//    @GetMapping("/error/test")
//    Friend errorTest() {
//        throw new ValidationException("error test called - presumably part of exception testing");
//    }
//
//}
