package miles.diary.data.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.RealmObject;
import miles.diary.ui.activity.BaseActivity;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 2/3/16.
 */
abstract class BaseRealmAdapter<T extends RealmObject, VH extends RecyclerView.ViewHolder>
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
    public boolean addObject(T object) {
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
    public void setData(List<T> collection) {
        if (animateContentsChanging()) {
            animateTo(collection);
        } else {
            clear();
            addAll(collection);
        }
    }

    @Override
    public void removeObject(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean removeObject(T realmObject) {
        if (data.contains(realmObject)) {
            int index = data.indexOf(realmObject);
            data.remove(realmObject);
            notifyItemRemoved(index);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isObjectValid(RealmObject realmObject) {
        return realmObject.isValid();
    }

    @Override
    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    private void animateTo(List<T> newData) {
        applyAndAnimateRemovals(newData);
        applyAndAnimateAdditions(newData);
        applyAndAnimateMovedItems(newData);
    }

    private void applyAndAnimateRemovals(List<T> newData) {
        for (int i = data.size() - 1; i >= 0; i--) {
            final T model = data.get(i);
            if (!newData.contains(model)) {
                data.remove(model);
                notifyItemRemoved(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<T> newData) {
        for (int i = 0; i < newData.size(); i++) {
            final T model = newData.get(i);
            if (!data.contains(model)) {
                data.add(i, model);
                notifyItemInserted(i);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<T> newData) {
        for (int toPosition = newData.size() - 1; toPosition >= 0; toPosition--) {
            final T model = newData.get(toPosition);
            final int fromPosition = data.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                data.remove(fromPosition);
                data.add(toPosition, model);
                notifyItemMoved(fromPosition, toPosition);
            }
        }
    }
}
