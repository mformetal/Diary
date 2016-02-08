package miles.diary.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import miles.diary.R;
import miles.diary.data.model.Entry;
import miles.diary.util.AnimUtils;

/**
 * Created by mbpeele on 2/7/16.
 */
public class EntryFragment extends Fragment {

    @Bind(R.id.fragment_entry_body) TextView textView;
    @Bind(R.id.fragment_entry_image) ImageView imageView;

    public final static String DATA = "data";

    private Realm realm;
    private Entry entry;

    public static EntryFragment newInstance(String bodyKey) {
        EntryFragment fragment = new EntryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DATA, bodyKey);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        entry = realm.where(Entry.class)
                .equalTo(Entry.KEY, getArguments().getString(DATA))
                .findFirst();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);
        ButterKnife.bind(this, view);
        textView.setText(entry.getBody());

        if (entry.getUri() != null) {
            Glide.with(getActivity())
                    .fromString()
                    .load(entry.getUri())
                    .animate(AnimUtils.REVEAL)
                    .into(imageView);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
