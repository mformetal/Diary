package miles.diary.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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
import miles.diary.data.adapter.BackendAdapterListener;
import miles.diary.data.adapter.EntryAdapter;
import miles.diary.data.model.Entry;
import miles.diary.ui.SpacingDecoration;
import miles.diary.util.AnimUtils;
import miles.diary.util.Logg;

public class HomeActivity extends TransitionActivity implements BackendAdapterListener {

    @Bind(R.id.activity_home_recycler) RecyclerView recyclerView;
    @Bind(R.id.activity_home_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_home_loading) ProgressBar progressBar;
    @Bind(R.id.activity_home_fab) FloatingActionButton fab;

    private final static int RESULT_CODE_ENTRY = 1;

    private EntryAdapter entryAdapter;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setActionBar(toolbar);

        entryAdapter = new EntryAdapter(this, realm);
        recyclerView.setItemAnimator(new FadeInAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(entryAdapter);
        recyclerView.addItemDecoration(new SpacingDecoration(
                ContextCompat.getDrawable(this, R.drawable.recycler_divider)));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NewEntryActivity.class);
                startActivityForResult(intent, RESULT_CODE_ENTRY);
            }
        });
    }

    @Override
    void onEnter(View root, Intent calledIntent, boolean hasSavedInstanceState) {
        View view = toolbar.getChildAt(0);
        if (view != null && view instanceof TextView) {
            TextView textView = (TextView) view;
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(
                    AnimUtils.pop(fab, 0f, 1f)
                            .setDuration(AnimUtils.longAnim(this)),
                    AnimUtils.textScale(textView, null, .8f, 1f)
                            .setDuration(AnimUtils.longAnim(this)),
                    AnimUtils.alpha(textView, 0f, 1f)
                            .setDuration(AnimUtils.longAnim(this)));
            animatorSet.start();
        }
    }

    @Override
    void onExit(View root) {
        finishWithDefaultTransition();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_CODE_ENTRY:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        entryAdapter.addEntry(extras);

                        if (emptyView != null) {
                            root.removeView(emptyView);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onLoadCompleted() {
        dismissLoading();
    }

    @Override
    public void onLoadError(Throwable throwable) {
        Logg.log(throwable);
        dismissLoading();
    }

    @Override
    public void onLoadEmpty() {
        if (emptyView == null) {
            dismissLoading();
            ViewStub viewStub = (ViewStub) findViewById(R.id.activity_home_no_entries);
            emptyView = viewStub.inflate();
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), NewEntryActivity.class);
                    startActivityForResult(intent, RESULT_CODE_ENTRY);
                }
            });
        }
    }

    @Override
    public void onLoadStart() {
        showLoading();
    }

    private void dismissLoading() {
        ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(progressBar,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f));
        scale.setDuration(AnimUtils.longAnim(this));
        scale.setInterpolator(new AnticipateOvershootInterpolator());

        ObjectAnimator gone = AnimUtils.gone(progressBar).setDuration(AnimUtils.longAnim(this));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scale, gone);
        animatorSet.start();
    }

    private void showLoading() {
        ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(progressBar,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f));
        scale.setDuration(AnimUtils.longAnim(this));
        scale.setInterpolator(new AnticipateOvershootInterpolator());

        ObjectAnimator gone = AnimUtils.visible(progressBar).setDuration(AnimUtils.longAnim(this));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scale, gone);
        animatorSet.start();
    }
}
