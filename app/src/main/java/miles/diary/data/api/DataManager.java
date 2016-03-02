package miles.diary.data.api;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.common.collect.Lists;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import miles.diary.data.model.Entry;
import miles.diary.ui.activity.NewEntryActivity;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by mbpeele on 3/2/16.
 */
public class DataManager implements DataManagerInterface {

    private Application application;
    private Realm realm;

    public DataManager(Application application) {
        this.application = application;
    }

    @Override
    public void init() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void close() {
        realm.close();
    }

    @Override
    public <T extends RealmObject> Observable<List<T>> loadObjects(Class<T> tClass) {
        return realm.where(tClass)
                .findAllAsync()
                .asObservable()
                .filter(new Func1<RealmResults<T>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<T> ts) {
                        return isDataValid(ts);
                    }
                })
                .map(new Func1<RealmResults<T>, List<T>>() {
                    @Override
                    public List<T> call(RealmResults<T> ts) {
                        return Lists.newArrayList(ts);
                    }
                })
                .first();
    }

    @Override
    public <T extends RealmObject> T getObject(Class<T> tClass, String key) {
        return realm.where(tClass)
                .equalTo(Entry.KEY, key)
                .findFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RealmObject> T uploadObject(RealmObject realmObject) {
        if (hasConnection()) {
            realm.beginTransaction();
            realm.copyToRealm(realmObject);
            realm.commitTransaction();
            realm.refresh();

            return (T) realmObject;
        } else {
            return null;
        }
    }

    @Override
    public boolean isDataValid(RealmResults realmResults) {
        return realmResults.isValid() && realmResults.isLoaded();
    }

    @Override
    public boolean isDataValid(RealmObject realmObject) {
        return realmObject.isValid() && realmObject.isLoaded();
    }

    @Override
    public boolean hasConnection() {
        return !realm.isClosed();
    }
}
