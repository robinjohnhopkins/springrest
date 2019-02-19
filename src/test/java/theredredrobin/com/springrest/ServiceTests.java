package theredredrobin.com.springrest;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import theredredrobin.com.springrest.model.Friend;
import theredredrobin.com.springrest.services.FriendService;
import java.util.stream.StreamSupport;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ServiceTests {

    @Autowired
    private FriendService friendService;

    @Test
    public void testCreateReadDeleteTestDb(){

        Assert.assertNotNull("friendService cannot be null", friendService);

        Iterable<Friend> friendsStart = friendService.findAll();
        int countStart = 0;
        for (Friend amigo:friendsStart) {
            ++countStart;
        }

        Friend friend = new Friend("Gordon", "Moron");
        Friend savedFriend = friendService.save(friend);
        Iterable<Friend> friends = friendService.findAll();
        //Assertions.assertThat(friends).extracting(Friend::getFirstName).containsOnly("Gordon");
        Assertions.assertThat(friends).extracting(Friend::getFirstName).contains("Gordon");
        // using java 8 stream we do not need for each
        long count = StreamSupport.stream(friends.spliterator(), false).count();
        Assertions.assertThat(count == countStart + 1);

        friendService.deleteById(savedFriend.getId());
        Iterable<Friend> friendsAfter = friendService.findAll();

        Assertions.assertThat(friendsAfter).extracting(Friend::getFirstName).doesNotContain("Gordon");

    }
}
