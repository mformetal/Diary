package miles.diary.data;


import android.content.Context;
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

    private Realm realm;
    private Observable<RealmResults<Entry>> entries;
    private Context context;

    public RealmUtils(Context cxt) {
        context = cxt;
        realm = Realm.getDefaultInstance();
        getEntries();
    }

    public Entry addEntry(String title, String body, Uri uri) {
        realm.beginTransaction();
        Entry entry = realm.createObject(Entry.class);
        entry.setId(UUID.randomUUID().toString());
        entry.setTitle(title);
        entry.setBody(body);
        entry.setDate(new Date());
        if (uri != null) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                byte[] bytes = FileUtils.readBytes(inputStream);
                entry.setBytes(bytes);
            } catch (IOException e) {
                Logg.log(e);
            }
        }
        realm.commitTransaction();
        return entry;
    }

    public void deleteEntry(Entry entry) {
        realm.beginTransaction();
        entry.removeFromRealm();
        realm.commitTransaction();
    }

    public static String formatDateString(Entry entry) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, MMM d, ''yy", Locale.getDefault());
        return dateFormatter.format(entry.getDate());
    }

    public Observable<RealmResults<Entry>> getEntries() {
        if (entries == null) {
            entries = realm.where(Entry.class)
                    .findAllAsync()
                    .asObservable()
                    .cache();
        }

        return entries;
    }

    public Observable<RealmResults<Entry>> searchEntries(String text) {
        return realm.where(Entry.class)
                .beginGroup()
                    .contains("title", text)
                    .or()
                    .contains("body", text)
                    .or()
                    .contains("date", text)
                .endGroup()
                .findAllAsync()
                .asObservable();
    }

    public void closeRealm() {
        realm.close();
    }
}
