package miles.diary.ui.fragment;

import android.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by mbpeele on 3/7/16.
 */
public class BaseFragment extends Fragment {

    public BaseFragment() {}

    public void bind(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
