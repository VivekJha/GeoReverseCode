package vivek.georeversecode;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("restClient")
public class GeoReverseCodeClient {
	private static Logger log = Logger.getLogger(GeoReverseCodeClient.class.getName());
	@Autowired
	private RestTemplate restTemplate;

	public String getGeoAddress() {
		log.info("\t**************************************************************************************************");
		log.info("\tAdd in browser : http://localhost:8585/getGeoAddress/<latitude>,<longitude> to get the address");
		log.info("\t**************************************************************************************************\n");
		return restTemplate.getForObject("http://localhost:8585/getGeoAddress/40.71,-74.00", String.class);
	}
	

	public String getLastt10GeoAddress() {
		log.info("\t**************************************************************************************************");
		log.info("\t LAST 10 SEARCHED ADDRESS");
		log.info("\t**************************************************************************************************\n");
		return restTemplate.getForObject("http://localhost:8585/getLastAddress", String.class);
	}
	
}
