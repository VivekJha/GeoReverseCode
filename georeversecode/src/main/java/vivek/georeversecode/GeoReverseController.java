package vivek.georeversecode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

@RestController
public class GeoReverseController implements GeoRepository {

	private static Logger log = Logger.getLogger(GeoReverseController.class.getName());
	private static ConcurrentHashMap<String, GeoReverseCodeCacheable> cacheableGeoCode = new ConcurrentHashMap<>();
	private static final String FORMATTED_ADDRESS = "formatted_address";
	private static final String COUNTRY = "country";
	private static final String LOCALITY = "locality";
	private static final String LONG_NAME = "long_name";
	private static final String TYPES = "types";
	private static final String ADDRESS_COMPONENTS = "address_components";
	private static final String RESULTS = "results";
	private static final String API_KEY = "AIzaSyA9H3ftW_Z9aof8TsPhsov-ucYkv7GE-L8";

	@RequestMapping(value = "/getGeoAddress/{latitude},{longitude}", method = RequestMethod.GET)
	public String getGeoAddress(@PathVariable String latitude, @PathVariable String longitude) {
		// validate(longitude,latitude);
		return getAddress(latitude, longitude);
	}
	
	@RequestMapping(value = "/getLastAddress", method = RequestMethod.GET)
	public String getLast10GeoAddress() {
		
		StringBuilder sb = new StringBuilder();
		int count =1;
			for(Entry<String, GeoReverseCodeCacheable> e: cacheableGeoCode.entrySet()){			
				sb.append(count+".[Latitude,Longitude:"+e.getKey()+", Time: "+e.getValue().getDateTime()+", Address:"+e.getValue().getAddress()+"]");
				count++;
				sb.append("\n");
			}
			log.info("===================================================================");
			log.info(sb.toString());
			log.info("===================================================================");
			
			return sb.toString();
	}

	public boolean validate(String latitude, String longitude) {
		final String input = longitude + latitude;
		String regex = "^(\\-?\\d+(\\.\\d+)?),\\s*(\\-?\\d+(\\.\\d+)?)$";
		final Pattern pattern = Pattern.compile(regex);
		if (!pattern.matcher(input).matches()) {
			throw new IllegalArgumentException("Invalid String");
		}

		return true;
	}

	/**
	 * Get the address from reverse Geo Code Google Resful API and convert it to
	 * JSON object to fetch the address in human readable format.
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	@Cacheable("geoReverseCodeCacheable")
	@Override
	public String getAddress(String latitude, String longitude) {
		String address = "";
		String apiOutPut;
		StringBuilder addressFromAPI = new StringBuilder();
		GeoReverseCodeCacheable geoCode = new GeoReverseCodeCacheable() ;
		try {
			URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude
					+ "&key=" + API_KEY);
			log.info("\nMaking connection to URL:" + url + "\n");
			// making connection
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			// Reading data's from url
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			log.info("Output from Server .... \n");
			while ((apiOutPut = br.readLine()) != null) {
				addressFromAPI.append(apiOutPut);
			}
			log.debug("Response from Google API: " + addressFromAPI);

			// Converting JSON formatted string into JSON object
			JSONObject json = (JSONObject) JSONObject.fromObject(addressFromAPI.toString());
			JSONArray results = json.getJSONArray(RESULTS);
			JSONObject rec = results.getJSONObject(0);
			JSONArray address_components = rec.getJSONArray(ADDRESS_COMPONENTS);
			for (int i = 0; i < address_components.size(); i++) {
				JSONObject rec1 = address_components.getJSONObject(i);
				JSONArray types = rec1.getJSONArray(TYPES);
				String comp = types.getString(0);

				if (comp.equals(LOCALITY)) {
					log.info("City---" + rec1.getString(LONG_NAME));
				} else if (comp.equals(COUNTRY)) {
					log.info("Country ---" + rec1.getString(LONG_NAME));
				}
			}

			String formatted_address = rec.getString(FORMATTED_ADDRESS);
			log.info("===================================================================");
			log.info("Formatted address ---" + formatted_address);
			log.info("===================================================================");
			address = formatted_address;
			
			geoCode.setAddress(formatted_address);
			geoCode.setDateTime(new Date());
			geoCode.setLatitude(latitude);
			geoCode.setLongitude(longitude);
			String mapKey = latitude+","+longitude;
			
			if(cacheableGeoCode.size()<10){
			cacheableGeoCode.put(mapKey, geoCode);
			}
			
			
			conn.disconnect();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException jex) {
			jex.printStackTrace();
		}

		return address;

	}

}
