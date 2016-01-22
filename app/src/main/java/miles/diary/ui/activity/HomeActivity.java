package miles.diary.ui.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.OnClick;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import miles.diary.R;
import miles.diary.data.ActivitySubscriber;
import miles.diary.data.RealmUtils;
import miles.diary.data.adapter.EntryAdapter;
import miles.diary.data.model.Entry;
import miles.diary.ui.GridSpacingDecoration;
import miles.diary.util.Logg;
import miles.diary.util.ViewUtils;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.fab_loading) FloatingActionButton fab;
    @Bind(R.id.activity_home_recycler) RecyclerView recyclerView;
    @Bind(R.id.activity_home_root) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.fab_loading_bar) ProgressBar fabProgressBar;
    @Bind(R.id.activity_home_toolbar) Toolbar toolbar;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(entryAdapter);
        recyclerView.addItemDecoration(new GridSpacingDecoration(20));

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
                            for (Entry entry : entries) {
                                entryAdapter.addData(entry);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home_search:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("fabVisibility", fab.getVisibility());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int visibility = savedInstanceState.getInt("fabVisibility");
        if (visibility == View.GONE) {
            fab.setVisibility(View.GONE);
        }
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
                        Uri uri = extra.getParcelable(EntryActivity.RESULT_BYTES);

                        Entry entry = realmUtils.addEntry(title, body, uri);
                        entryAdapter.addData(entry);
                    }
                }
                break;
        }
    }

    @Override
    @OnClick({R.id.fab_loading})
    public void onClick(View v) {
        int[] location = new int[2];
        fab.getLocationOnScreen(location);

        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra(EntryActivity.LEFT, location[0]);
        intent.putExtra(EntryActivity.TOP, location[1]);
        intent.putExtra(EntryActivity.WIDTH, fab.getWidth());
        intent.putExtra(EntryActivity.HEIGHT, fab.getHeight());
        intent.putExtra(EntryActivity.START_COLOR, ContextCompat.getColor(this, R.color.accent));

        startActivityForResult(intent, RESULT_CODE_ENTRY);

        overridePendingTransition(0, 0);
    }
}
