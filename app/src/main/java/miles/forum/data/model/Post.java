package miles.forum.data.model;

import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Instant;
import org.joda.time.Interval;

import java.util.Calendar;
import java.util.Date;

import miles.forum.util.Logg;

/**
 * Created by mbpeele on 1/14/16.
 */
@ParseClassName("Post")
public class Post extends ParseObject {

    public final static String TITLE_KEY = "title";
    public final static String LIKE_COUNT_KEY = "likes";
    public final static String COMMENTS_COUNT_KEY = "comments";
    public final static String SHARE_COUNT_KEY = "shares";

    public Post() {}

    public Post(String string) {
        super();
        put(LIKE_COUNT_KEY, 0);
        put(COMMENTS_COUNT_KEY, 0);
        put(TITLE_KEY, string);
        put(SHARE_COUNT_KEY, 0);
    }

    public String getTitle() { return getString(TITLE_KEY); }

    public int getLikesCount() {
        return getInt(LIKE_COUNT_KEY);
    }

    public int getCommentsCount() {
        return getInt(COMMENTS_COUNT_KEY);
    }

    public int getSharesCount() {
        return getInt(SHARE_COUNT_KEY);
    }

    public void putLikesCount(int count) {
        put(LIKE_COUNT_KEY, count);
    }

    public void putSharesCount(int shares) {
        put(SHARE_COUNT_KEY, shares);
    }

    public void putCommentsCount(int count) {
        put(COMMENTS_COUNT_KEY, count);
    }

    public String getLikeCountAsString() {
        return String.valueOf(getLikesCount());
    }

    public String getCommentsCountAsString() {
        return String.valueOf(getCommentsCount());
    }

    public String getShareCountAsString() { return String.valueOf(getSharesCount()); }

    public String formatCreatedAtTime() {
        Hours hours = Hours.hoursBetween(new DateTime(getCreatedAt()), new Instant());
        int diff = hours.getHours();

        String plural = (diff > 1) ? " hours ago" : " hour ago";

        return "Submitted " + String.valueOf(diff) + plural;
    }
}
