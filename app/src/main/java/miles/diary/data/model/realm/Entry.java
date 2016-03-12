package miles.diary.data.model.realm;

import android.location.Location;
import android.net.Uri;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Builder;
import miles.diary.util.TextUtils;

@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
public class Entry extends RealmObject implements IRealmInterface {

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
        setDate(date).setDateMillis(date.getTime());
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

        return new Entry(new Date())
                .setBody(body)
                .setUri(uriString)
                .setPlaceName(placeName)
                .setPlaceId(placeId)
                .setWeather(weather)
                .setLatitude(latitude)
                .setLongitude(longitude);
    }

    public static Entry update(Entry entry, String body, Uri uri, String placeName, String placeId) {
        entry.setBody(body);
        if (uri != null) {
            entry.setUri(uri.toString());
        }
        return entry.setPlaceName(placeName)
                .setPlaceId(placeId);
    }
}
