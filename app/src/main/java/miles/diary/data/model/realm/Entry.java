package miles.diary.data.model.realm;

import android.net.Uri;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import miles.diary.util.TextUtils;

/**
 * Created by mbpeele on 1/18/16.
 */
public class Entry extends RealmObject {

    public final static String KEY = "dateMillis";

    @Required private Date date;
    @PrimaryKey private long dateMillis;
    @Required private String body;
    private String uri;
    private String placeName;
    private String placeId;
    private String weather;

    public Entry() {
        super();
    }

    public Entry(String body, Uri uri, String placeName,
              String placeId, String temperature) {
        super();
        setBody(body);
        Date date = new Date();
        setDate(date);
        setDateMillis(date.getTime());
        if (uri != null) {
            setUri(uri.toString());
        }
        setPlaceName(placeName);
        setPlaceId(placeId);
        setWeather(temperature);
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public void setDateMillis(long dateMillis) {
        this.dateMillis = dateMillis;
    }

    public static String formatDiaryPrefaceText(Entry entry) {
        return "Dear Diary, " +
                TextUtils.repeat(2, TextUtils.LINE_SEPERATOR) +
                TextUtils.repeat(5, TextUtils.TAB) +
                entry.getBody();
    }

    public static Entry update(Entry entry, String body, Uri uri, String placeName,
                               String placeId, String temperature) {
        entry.setBody(body);
        entry.setPlaceId(placeId);
        entry.setPlaceName(placeName);
        entry.setWeather(temperature);
        if (uri != null) {
            entry.setUri(uri.toString());
        }
        return entry;
    }
}
