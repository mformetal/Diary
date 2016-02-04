package miles.diary.data.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;
import miles.diary.data.model.Entry;
import miles.diary.ui.activity.BaseActivity;

/**
 * Created by mbpeele on 2/3/16.
 */
public abstract class BackendAdapter<T extends RealmObject, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    protected RealmResults<T> mResults;
    protected LayoutInflater mLayoutInflater;
    protected BaseActivity mActivity;
    protected Realm mRealm;
    private BackendAdapterListener listener;

    public BackendAdapter(BaseActivity baseActivity, Realm realm) {
        mRealm = realm;
        mRealm.addChangeListener(this::notifyDataSetChanged);

        mActivity = baseActivity;
        listener = (BackendAdapterListener) baseActivity;

        mLayoutInflater = LayoutInflater.from(mActivity);

        loadData(mRealm);

        notifyDataSetChanged();
    }

    protected abstract void loadData(Realm realm);

    protected abstract void setData(RealmResults<Entry> data);

    public T getItem(int position) {
        return mResults.get(position);
    }

    public void propogateError(Throwable throwable) {
        if (listener != null) {
            listener.onError(throwable);
        }
    }

    public void propogateCompletion() {
        if (listener != null) {
            listener.onCompleted();
        }
    }

    public void propogateEmpty() {
        if (listener != null) {
            listener.onEmpty();
        }
    }

    public interface BackendAdapterListener {

        void onCompleted();

        void onError(Throwable throwable);

        void onEmpty();
    }
}
