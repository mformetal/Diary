package miles.diary.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import butterknife.Bind;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import miles.diary.R;
import miles.diary.data.adapter.BackendAdapter;
import miles.diary.data.adapter.BackendAdapterListener;
import miles.diary.data.adapter.EntryRecycler;
import miles.diary.ui.SpacingDecoration;
import miles.diary.util.AnimUtils;
import miles.diary.util.Logg;

public class HomeActivity extends TransitionActivity implements BackendAdapterListener {

    @Bind(R.id.activity_home_recycler) RecyclerView recyclerView;
    @Bind(R.id.activity_home_root) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.activity_home_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_home_loading) ProgressBar progressBar;
    @Bind(R.id.activity_home_fab) FloatingActionButton fab;

    private final static int RESULT_CODE_ENTRY = 1001;

    private EntryRecycler entryRecycler;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setActionBar(toolbar);

        entryRecycler = new EntryRecycler(this, realm);
        recyclerView.setItemAnimator(new FadeInAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(entryRecycler);
        recyclerView.addItemDecoration(new SpacingDecoration(
                ContextCompat.getDrawable(this, R.drawable.recycler_divider)));

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NewEntryActivity.class);
            startActivityForResult(intent, RESULT_CODE_ENTRY);
        });
    }

    @Override
    public void onEnter(View root, Intent intent, boolean hasSavedInstanceState) {
        fab.setScaleX(0f);
        fab.setScaleY(0f);

        ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(fab,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f));
        scale.setDuration(AnimUtils.longAnim(this));
        scale.setInterpolator(new AnticipateOvershootInterpolator());
        scale.start();

        View view = toolbar.getChildAt(0);
        if (view != null && view instanceof TextView) {
            view.setAlpha(0f);
            view.setScaleX(0.8f);

            view.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .setDuration(AnimUtils.longAnim(this))
                    .setInterpolator(new FastOutSlowInInterpolator());
        }
    }

    @Override
    public void onExit(View root, Intent intent, boolean hasSavedInstanceState) {
        finishWithDefaultTransition();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_CODE_ENTRY:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        entryRecycler.addEntry(extras);

                        if (emptyView != null) {
                            coordinatorLayout.removeView(emptyView);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onCompleted() {
        dismissLoading();
    }

    @Override
    public void onError(Throwable throwable) {
        Logg.log(throwable);
        dismissLoading();
    }

    @Override
    public void onEmpty() {
        if (emptyView == null) {
            dismissLoading();
            ViewStub viewStub = (ViewStub) findViewById(R.id.activity_home_no_entries);
            emptyView = viewStub.inflate();
            emptyView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), NewEntryActivity.class);
                startActivityForResult(intent, RESULT_CODE_ENTRY);
            });
        }
    }

    private void dismissLoading() {
        ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(progressBar,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f));
        scale.setDuration(AnimUtils.longAnim(this));
        scale.setInterpolator(new AnticipateOvershootInterpolator());

        ObjectAnimator gone = AnimUtils.gone(progressBar, AnimUtils.longAnim(this));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scale, gone);
        animatorSet.start();
    }
}
