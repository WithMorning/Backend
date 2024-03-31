package go.alarm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AlarmApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlarmApplication.class, args);
    }

}
