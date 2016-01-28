package miles.diary.ui.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.OnClick;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import miles.diary.R;
import miles.diary.data.ActivitySubscriber;
import miles.diary.data.RealmUtils;
import miles.diary.data.adapter.EntryAdapter;
import miles.diary.data.model.Entry;
import miles.diary.ui.GridSpacingDecoration;
import miles.diary.util.Logg;
import miles.diary.util.ViewUtils;

public class HomeActivity extends BaseActivity {

    @Bind(R.id.fab_loading) FloatingActionButton fab;
    @Bind(R.id.activity_home_recycler) RecyclerView recyclerView;
    @Bind(R.id.activity_home_root) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.fab_loading_bar) ProgressBar fabProgressBar;
    @Bind(R.id.activity_home_toolbar) Toolbar toolbar;
    private View emptyView;

    private final static int RESULT_CODE_ENTRY = 1001;

    private EntryAdapter entryAdapter;
    private RealmUtils realmUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(miles.diary.R.layout.activity_home);

        setSupportActionBar(toolbar);

        realmUtils = new RealmUtils(this);

        fabProgressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.icons), PorterDuff.Mode.SRC_ATOP);

        entryAdapter = new EntryAdapter(this);
        recyclerView.setItemAnimator(new FadeInAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(entryAdapter);
        recyclerView.addItemDecoration(new GridSpacingDecoration(20));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EntryActivity.class);
                startActivityForResult(intent, RESULT_CODE_ENTRY);
            }
        });

        realmUtils.getEntries().subscribe(new ActivitySubscriber<RealmResults<Entry>>(this) {
                    @Override
                    public void onStart() {
                        entryAdapter.clear();
                        ViewUtils.visible(fabProgressBar, 350).start();
                    }

                    @Override
                    public void onNext(RealmResults<Entry> entries) {
                        if (entries.isLoaded()) {
                            ViewUtils.invisible(fabProgressBar, 350).start();
                            if (entries.isEmpty()) {
                                emptyView = ((ViewStub) findViewById(R.id.activity_home_no_entries)).inflate();
                                emptyView.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                            Intent intent = new Intent(v.getContext(),
                                                    EntryActivity.class);
                                            startActivityForResult(intent, RESULT_CODE_ENTRY);
                                        }
                                        return false;
                                    }
                                });
                            } else {
                                for (Entry entry : entries) {
                                    entryAdapter.addData(entry);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logg.log(e);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmUtils.closeRealm();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_CODE_ENTRY:
                if (resultCode == RESULT_OK) {
                    Bundle extra = data.getExtras();
                    if (extra != null) {
                        String title = extra.getString(EntryActivity.RESULT_TITLE);
                        String body = extra.getString(EntryActivity.RESULT_BODY);
                        Uri uri = extra.getParcelable(EntryActivity.RESULT_URI);

                        Entry entry = realmUtils.addEntry(title, body, uri);
                        if (emptyView != null) {
                            coordinatorLayout.removeView(emptyView);
                        }
                        entryAdapter.addData(entry);
                    }
                }
                break;
        }
    }
}
