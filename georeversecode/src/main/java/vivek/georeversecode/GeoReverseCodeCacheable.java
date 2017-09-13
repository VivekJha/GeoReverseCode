package vivek.georeversecode;

import java.util.Date;

public class GeoReverseCodeCacheable {
private String longitude;
private String latitude;
private String  address;
private Date dateTime;

public String getLongitude() {
	return longitude;
}
public void setLongitude(String longitude) {
	this.longitude = longitude;
}
public String getLatitude() {
	return latitude;
}
public void setLatitude(String latitude) {
	this.latitude = latitude;
}
public String getAddress() {
	return address;
}
public void setAddress(String address) {
	this.address = address;
}
public Date getDateTime() {
	return dateTime;
}
public void setDateTime(Date dateTime) {
	this.dateTime = dateTime;
}

@Override
	public String toString() {
		return"GeoReverseCodeCacheable[longitude:"+longitude+"  latitude:"+latitude+"  Time:"+dateTime+"  address:"+address+"]";
	}

}
