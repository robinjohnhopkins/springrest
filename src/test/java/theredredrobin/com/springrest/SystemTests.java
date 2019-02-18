package theredredrobin.com.springrest;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import theredredrobin.com.springrest.model.Friend;

public class SystemTests {

    // This test assumes mvn spring-boot:run is running

    @Test
    public void testCreateReadDeleteOriginal(){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/friend";
        Friend[] friendsStart = restTemplate.getForObject(url, Friend[].class);

        Friend friend = new Friend("Gordon", "Moron");
        ResponseEntity<Friend> entity = restTemplate.postForEntity(url, friend, Friend.class);
        Friend[] friends = restTemplate.getForObject(url, Friend[].class);
        //Assertions.assertThat(friends).extracting(Friend::getFirstName).containsOnly("Gordon");
        Assertions.assertThat(friends).extracting(Friend::getFirstName).contains("Gordon");
        Assertions.assertThat(friends.length == friendsStart.length + 1);

        restTemplate.delete(url + "/" + entity.getBody().getId());
        //Assertions.assertThat(restTemplate.getForObject(url,Friend[].class)).isEmpty();
        Friend[] friendsAfter = restTemplate.getForObject(url, Friend[].class);
        Assertions.assertThat(friendsAfter.length == friendsStart.length);


    }
}
