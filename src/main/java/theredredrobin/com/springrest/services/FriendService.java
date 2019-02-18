package theredredrobin.com.springrest.services;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import theredredrobin.com.springrest.model.Friend;

@Repository
public interface FriendService extends CrudRepository<Friend, Integer> {
//    Iterable<Friend> findByFirstNameAndLastName(String firstName, String lastName);
//    Iterable<Friend> findByFirstName(String firstName);
//    Iterable<Friend> findByLastName(String lastName);
}
