package theredredrobin.com.springrest.controllers;


import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import theredredrobin.com.springrest.controllers.FriendController;
import theredredrobin.com.springrest.model.Friend;

import javax.validation.ValidationException;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringrestApplicationTests {

	@Autowired
	FriendController friendController;

	@Test
	public void contextLoads() {
		Assert.assertNotNull(friendController);
	}

	@Test(expected = ValidationException.class)
	public void testErrorHandlingVallidationExceptionThrown(){
		friendController.errorTest();
	}

}
