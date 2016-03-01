package miles.diary.data.model;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import miles.diary.util.TextUtils;

/**
 * Created by mbpeele on 1/18/16.
 */
public class Entry extends RealmObject {

    public final static String KEY = "body";

    @PrimaryKey private String body;
    @Required private Date date;
    private String uri;
    private String placeName;
    private String placeId;
    private String weather;

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

    public static String formatDiaryPrefaceText(Entry entry) {
        return "Dear Diary, " +
                TextUtils.repeat(2, TextUtils.LINE_SEPERATOR) +
                TextUtils.repeat(5, TextUtils.TAB) +
                entry.getBody();
    }

    public static Entry construct(Realm realm, String body, Uri uri, String placeName,
                                  String placeId, String temperature) {
        realm.beginTransaction();
        Entry entry = realm.createObject(Entry.class);
        entry.setBody(body);
        entry.setDate(new Date());
        if (uri != null) {
            entry.setUri(uri.toString());
        }
        entry.setPlaceName(placeName);
        entry.setPlaceId(placeId);
        entry.setWeather(temperature);
        realm.commitTransaction();
        realm.refresh();
        return entry;
    }
}
