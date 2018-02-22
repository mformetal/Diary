package miles.diary.data.adapter

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by mbpeele on 2/28/16.
 */
abstract class TypedViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(model: T)
}
