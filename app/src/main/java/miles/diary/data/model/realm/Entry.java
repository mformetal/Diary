package miles.diary.data.model.realm;

import android.location.Location;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Entry extends RealmObject implements RealmModel, ClusterItem {

    public final static String KEY = "dateMillis";

    @Required
    private Date date;
    @PrimaryKey
    private long dateMillis;
    @Required
    private String body;

    private String uri;
    private String placeName;
    private String placeId;
    private String weather;
    private Double latitude;
    private Double longitude;

    public Entry() {
        super();
    }

    public Entry(Date date) {
        super();
        setDate(date);
        setDateMillis(date.getTime());
    }

    @Override
    public LatLng getPosition() {
        if (hasLocation()) {
            return new LatLng(latitude, longitude);
        } else {
            throw new NullPointerException("Attempt to get the LatLng of an Entry that does not " +
                    "have location data");
        }
    }

    public boolean hasImageUri() {
        return uri != null;
    }

    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;

        setDateMillis(date.getTime());
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public void setDateMillis(long dateMillis) {
        this.dateMillis = dateMillis;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public static Entry construct(String body, Uri uri, String placeName, String placeId,
                                  String weather, Location location) {
        String uriString = null;
        if (uri != null) {
            uriString = uri.toString();
        }

        Double latitude = null;
        Double longitude = null;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        Entry entry = new Entry(new Date());
        entry.setBody(body);
        entry.setUri(uriString);
        entry.setWeather(weather);
        entry.setPlaceId(placeId);
        entry.setPlaceName(placeName);
        entry.setLatitude(latitude);
        entry.setLongitude(longitude);
        return entry;
    }

    public static Entry update(Entry entry, String body, Uri uri, String placeName, String placeId) {
        entry.setBody(body);
        if (uri != null) {
            entry.setUri(uri.toString());
        }
        entry.setPlaceName(placeName);
        entry.setPlaceId(placeId);
        return entry;
    }
}
