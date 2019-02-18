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
import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class FriendController {

    @Autowired
    FriendService friendService;

    @PostMapping("/friend")
    Friend create(@Valid @RequestBody Friend friend) throws ValidationException {
// NB afteradding @Valid, don't need this validation!
//        if (friend.getId() == 0 && friend.getFirstName() != null && friend.getLastName() != null )
            return friendService.save(friend);
//        else
//            throw new ValidationException("friend cannot be created.!");
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    List<FieldErrorMessage>  exceptionHandler(MethodArgumentNotValidException e){
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<FieldErrorMessage> fieldErrorMessages = fieldErrors.stream().map(fieldError -> new FieldErrorMessage(fieldError.getField(), fieldError.getDefaultMessage())).collect(Collectors.toList());
        return fieldErrorMessages;
    }
// moved to class ControllerExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(ValidationException.class)
//    //ResponseEntity<String> exceptionHandler(ValidationException e){
//    ErrorMessage exceptionHandler(ValidationException e){
//        //return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
//        return new ErrorMessage("400", e.getMessage());
//    }

    @GetMapping("/friend")
    Iterable<Friend> read(){
        return friendService.findAll();
    }

    @PutMapping("/friend")
    ResponseEntity<Friend> update(@RequestBody Friend friend) {
        if (friendService.findById(friend.getId()).isPresent())
            return new ResponseEntity<>(friendService.save(friend), HttpStatus.OK);
        else
            return new ResponseEntity<>(friend, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/friend/{id}")
    void delete (@PathVariable Integer id){
        friendService.deleteById(id);
    }
    @GetMapping("/friend/{id}")
    Optional<Friend> findById(@PathVariable Integer id){
        return friendService.findById(id);
    }
    @GetMapping("/friend/search")
    Iterable<Friend> findByQuery(@RequestParam(value = "first", required = false) String firstName,
                                 @RequestParam(value = "lastName", required = false) String lastName){
        if (firstName != null && lastName != null)
            return friendService.findByFirstNameAndLastName(firstName, lastName);
        else if (firstName != null)
            return friendService.findByFirstName(firstName);
        else if (lastName != null)
            return friendService.findByLastName(lastName);
        else
            return friendService.findAll();
    }

}
