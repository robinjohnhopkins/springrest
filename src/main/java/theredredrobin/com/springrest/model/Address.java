package theredredrobin.com.springrest.model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String street;
  private String city;

//  @ManyToOne
//  private Friend friend;
//
//  public Friend getFriend() {
//    return friend;
//  }
//
//  public void setFriend(Friend friend) {
//    this.friend = friend;
//  }

  @ManyToMany
  private Set<Friend> friends;

  public Set<Friend> getFriends() {
    return friends;
  }

  public void setFriends(Set<Friend> friends) {
    this.friends = friends;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }
}
