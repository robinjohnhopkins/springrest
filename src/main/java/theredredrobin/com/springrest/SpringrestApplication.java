package theredredrobin.com.springrest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import theredredrobin.com.springrest.util.Config;

//@RestController
@SpringBootApplication
public class SpringrestApplication {

	//@Autowired
	//Config config;

	public static void main(String[] args) {
		SpringApplication.run(SpringrestApplication.class, args);
	}
	// Tried this to see if I could add a rest endpoint AND use HATEOS
	// NO - this endpoint is not mapped when spring-boot-starter-data-rest added to pom,
	// Also with HATEOS, the FriendController is not explicitly needed
	// I have autowired the config though which allowed me to test out setting alternate
	// profiles on commandline
	//
	//@RequestMapping("/abc")
	//public String printConfig(){
	//	System.out.println("config is " + config.getName());
	//	return "config is " + config.getName();
	//}
}
