package miles.diary.data.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by mbpeele on 2/28/16.
 */
abstract class BindingViewHolder<T> extends RecyclerView.ViewHolder {

    public BindingViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public abstract void bind(T model);
}
