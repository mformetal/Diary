package miles.diary.data.model.realm;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import miles.diary.ui.activity.NewEntryActivity;

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

    private Entry(EntryBuilder builder) {
        date = builder.date;
        dateMillis = date.getTime();
        body = builder.body;
        uri = builder.uri;
        placeName = builder.placeName;
        placeId = builder.placeId;
        weather = builder.weather;
        longitude = builder.longitude;
        latitude = builder.latitude;
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

    public Entry update(String body, Uri uri, String placeName, String placeId) {
        setBody(body);
        if (uri != null) {
            setUri(uri.toString());
        }
        setPlaceName(placeName);
        setPlaceId(placeId);
        return this;
    }

    public static Entry fromBundle(Bundle bundle) {
        final String body = bundle.getString(NewEntryActivity.BODY);
        final Uri uri = bundle.getParcelable(NewEntryActivity.URI);
        final String placeName = bundle.getString(NewEntryActivity.PLACE_NAME);
        final String placeId = bundle.getString(NewEntryActivity.PLACE_ID);
        final String weather = bundle.getString(NewEntryActivity.TEMPERATURE);
        final Location location = bundle.getParcelable(NewEntryActivity.LOCATION);

        return Entry.builder()
                .setBody(body)
                .setDate()
                .setUri(uri)
                .setPlaceId(placeId)
                .setPlaceName(placeName)
                .setWeather(weather)
                .setLocation(location)
                .createEntry();
    }

    public static EntryBuilder builder() {
        return new EntryBuilder();
    }

    public static class EntryBuilder {

        private String body;
        private String uri;
        private String placeName;
        private String placeId;
        private Double latitude;
        private Double longitude;
        private String weather;
        private Date date;

        private EntryBuilder() {

        }

        public EntryBuilder setBody(String body) {
            this.body = body;
            return this;
        }

        public EntryBuilder setUri(Uri uri) {
            if (uri != null) {
                this.uri = uri.toString();
            }
            return this;
        }

        public EntryBuilder setDate() {
            date = new Date();
            return this;
        }

        public EntryBuilder setPlaceName(String placeName) {
            this.placeName = placeName;
            return this;
        }

        public EntryBuilder setPlaceId(String placeId) {
            this.placeId = placeId;
            return this;
        }

        public EntryBuilder setLocation(Location location) {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
            return this;
        }

        public EntryBuilder setWeather(String weather) {
            this.weather = weather;
            return this;
        }

        public Entry createEntry() {
            return new Entry(this);
        }
    }
}
