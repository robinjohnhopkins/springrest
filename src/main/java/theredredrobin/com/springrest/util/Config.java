package theredredrobin.com.springrest.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="my")
public class Config {

    private String name = new String();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        System.out.println("Config.setName is " + name);
    }
}