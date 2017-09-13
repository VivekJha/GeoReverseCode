package vivek.georeversecode;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class GeoReveseCodeApplication {
	private static Logger log = Logger.getLogger(GeoReveseCodeApplication.class.getName());
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(GeoReveseCodeApplication.class, args);
		GeoReverseCodeClient client = (GeoReverseCodeClient) context.getBean("restClient");
		log.info(client.getGeoAddress());
		log.info(client.getLastt10GeoAddress());
	}

	@Bean
	public RestTemplate geRestTemplate() {
		return new RestTemplate();
	}
}
