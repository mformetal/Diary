package miles.diary.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import butterknife.Bind;
import butterknife.ButterKnife;
import miles.diary.R;
import miles.diary.data.model.realm.Entry;
import miles.diary.ui.widget.CircleImageView;
import miles.diary.ui.widget.TypefaceTextView;

/**
 * Created by mbpeele on 4/17/16.
 */
public class EntryClusterRenderer extends DefaultClusterRenderer<Entry> {

    private final IconGenerator iconGenerator;
    private final IconGenerator clusterIconGenerator;
    private final LayoutInflater layoutInflater;

    @Bind(R.id.marker_entry_image)
    CircleImageView imageView;
    @Bind(R.id.marker_entry_text)
    TypefaceTextView textView;

    public EntryClusterRenderer(Context context, GoogleMap map, ClusterManager<Entry> clusterManager) {
        super(context, map, clusterManager);

        iconGenerator = new IconGenerator(context.getApplicationContext());
        clusterIconGenerator = new IconGenerator(context.getApplicationContext());
        layoutInflater = LayoutInflater.from(context);

        View multiProfile = layoutInflater.inflate(R.layout.marker_entry, null);
        ButterKnife.bind(this, multiProfile);
    }

}
