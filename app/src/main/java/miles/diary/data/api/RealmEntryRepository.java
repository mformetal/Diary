package miles.diary.data.api;

import org.jetbrains.annotations.NotNull;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import miles.diary.data.model.realm.Entry;

/**
 * Created by mbpeele on 3/2/16.
 */
public class RealmEntryRepository implements EntryRepository {

    private Realm realm;

    @Override
    public void open() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void close() {
        if (!realm.isClosed()) {
            realm.close();
        }
    }

    @NotNull
    @Override
    public OrderedRealmCollection<Entry> getAllEntries() {
        return realm.where(Entry.class).findAll();
    }
}
