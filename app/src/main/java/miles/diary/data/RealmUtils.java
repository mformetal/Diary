package miles.diary.data;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import miles.diary.data.model.Entry;
import miles.diary.util.Logg;
import miles.diary.util.FileUtils;
import rx.Observable;

public class RealmUtils {

    private final static SimpleDateFormat dateFormatter
            = new SimpleDateFormat("EEE, MMM d, ''yy", Locale.getDefault());

    private RealmUtils() {}

    public static String formatDateString(Entry entry) {
        return dateFormatter.format(entry.getDate());
    }
}
