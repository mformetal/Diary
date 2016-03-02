package miles.diary.data.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import miles.diary.data.model.Entry;
import miles.diary.ui.activity.BaseActivity;

/**
 * Created by mbpeele on 2/3/16.
 */
public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    protected List<RealmObject> data;
    protected final BaseActivity host;
    protected final LayoutInflater layoutInflater;

    public BaseAdapter(BaseActivity activity) {
        host = activity;
        layoutInflater = LayoutInflater.from(activity);
        data = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public abstract RealmObject getItem(int position);

    public <T extends RealmObject> boolean addData(T object) {
        boolean addition = data.add(object);
        if (addition) {
            notifyItemInserted(data.size());
        }

        return addition;
    }

    public boolean addData(Collection<? extends RealmObject> objects) {
        boolean addition = data.addAll(objects);
        if (addition) {
            notifyDataSetChanged();
        }

        return addition;
    }

    public List<RealmObject> getData() { return data; }
}
