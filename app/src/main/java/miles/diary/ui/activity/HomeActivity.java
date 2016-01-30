package miles.diary.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.Toolbar;

import butterknife.Bind;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import miles.diary.R;
import miles.diary.data.ActivitySubscriber;
import miles.diary.data.RealmUtils;
import miles.diary.data.adapter.EntryAdapter;
import miles.diary.data.model.Entry;
import miles.diary.ui.SpacingDecoration;
import miles.diary.util.Logg;

public class HomeActivity extends BaseActivity {

    @Bind(R.id.activity_home_recycler) RecyclerView recyclerView;
    @Bind(R.id.activity_home_root) FrameLayout coordinatorLayout;
    @Bind(R.id.activity_home_toolbar) Toolbar toolbar;
    private View emptyView;

    private final static int RESULT_CODE_ENTRY = 1001;

    private EntryAdapter entryAdapter;
    private RealmUtils realmUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(miles.diary.R.layout.activity_home);

        setActionBar(toolbar);

        realmUtils = new RealmUtils(this);

        entryAdapter = new EntryAdapter(this);
        recyclerView.setItemAnimator(new FadeInAnimator());
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(entryAdapter);
        recyclerView.addItemDecoration(new SpacingDecoration(
                getResources().getDimensionPixelSize(R.dimen.adapter_entry_spacing),
                ContextCompat.getColor(this, R.color.accent)));

        realmUtils.getEntries()
                .subscribe(new ActivitySubscriber<RealmResults<Entry>>(this) {
                    @Override
                    public void onStart() {
                        entryAdapter.clear();
                    }

                    @Override
                    public void onNext(RealmResults<Entry> entries) {
                        if (entries.isLoaded()) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_entry:
                Intent intent = new Intent(this, EntryActivity.class);
                startActivityForResult(intent, RESULT_CODE_ENTRY);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
