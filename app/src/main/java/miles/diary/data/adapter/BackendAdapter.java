package miles.diary.data.adapter;

import android.support.v7.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by mbpeele on 2/3/16.
 */
public abstract class BackendAdapter<T extends RealmObject, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private RealmResults<T> data;
    protected Realm realm;
    private BackendAdapterListener<T> listener;

    public BackendAdapter(Realm realm1) {
        realm = realm1;
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public void setListener(BackendAdapterListener<T> backendAdapterListener) {
        listener = backendAdapterListener;
    }

    public T getItem(int position) {
        return data.get(position);
    }

    public RealmResults<T> getData() { return data; }

    public abstract void loadData(Realm realm);

    public abstract void addData(T object);

    public void setData(RealmResults<T> results) {
        data = results;
    }

    public void propogateError(Throwable throwable) {
        if (listener != null) {
            listener.onLoadError(throwable);
        }
    }

    public void propogateCompletion() {
        if (listener != null) {
            listener.onLoadCompleted();
        }
    }

    public void propogateEmpty() {
        if (listener != null) {
            listener.onLoadEmpty();
        }
    }

    public void propogateStart() {
        if (listener != null) {
            listener.onLoadStart();
        }
    }
}
