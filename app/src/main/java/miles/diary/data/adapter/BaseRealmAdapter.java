package miles.diary.data.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.RealmObject;
import miles.diary.ui.activity.BaseActivity;

/**
 * Created by mbpeele on 2/3/16.
 */
public abstract class BaseRealmAdapter<T extends RealmObject, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements Adapter<T> {

    protected final List<T> data;
    protected final BaseActivity host;
    protected final LayoutInflater layoutInflater;

    public BaseRealmAdapter(BaseActivity activity) {
        host = activity;
        layoutInflater = LayoutInflater.from(activity);
        data = new ArrayList<>();
    }

    @Override
    public T getObject(int item) {
        return data.get(item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean addData(T object) {
        boolean addition = data.add(object);
        if (addition) {
            notifyDataSetChanged();
        }

        return addition;
    }

    @Override
    public void addAtPosition(T object, int position) {
        data.add(position, object);
        notifyItemInserted(position);
    }

    @Override
    public boolean addAll(Collection<T> objects) {
        boolean addition = data.addAll(objects);
        if (addition) {
            notifyDataSetChanged();
        }

        return addition;
    }

    @Override
    public List<T> getData() { return data; }

    @Override
    public void removeObject(int position) {
        data.remove(position);
    }

    @Override
    public boolean removeObject(T realmObject) {
        boolean removal =  data.remove(realmObject);
        if (removal) {
            notifyDataSetChanged();
        }

        return removal;
    }

    @Override
    public boolean isDataValid(RealmObject realmObject) {
        return realmObject.isValid();
    }

    @Override
    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }
}
