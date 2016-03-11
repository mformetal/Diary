package miles.diary.ui.activity;

import android.animation.AnimatorSet;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.List;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.data.adapter.EntryAdapter;
import miles.diary.data.api.db.DataLoadingListener;
import miles.diary.data.model.realm.Entry;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.ui.DividerDecoration;
import miles.diary.ui.PreDrawer;
import miles.diary.ui.transition.FabContainerTransition;
import miles.diary.util.AnimUtils;
import miles.diary.util.Logg;

public class HomeActivity extends BaseActivity implements DataLoadingListener {

    @Bind(R.id.activity_home_recycler) RecyclerView recyclerView;
    @Bind(R.id.activity_home_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_home_loading) ProgressBar progressBar;
    @Bind(R.id.activity_home_fab) FloatingActionButton fab;

    private final static int RESULT_CODE_NEW_ENTRY = 1;
    public final static int RESULT_CODE_ENTRY = 2;

    private EntryAdapter entryAdapter;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        PreDrawer.addPreDrawer(root, new PreDrawer.OnPreDrawListener<ViewGroup>() {
            @Override
            public boolean onPreDraw(ViewGroup view) {
                AnimUtils.pop(fab, 0f, 1f).setDuration(AnimUtils.longAnim(view.getContext())).start();
                return true;
            }
        });

        setActionBar(toolbar);

        fetchData();

        entryAdapter = new EntryAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(entryAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewEntryActivity();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {
            case RESULT_CODE_NEW_ENTRY:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    final String body = bundle.getString(NewEntryActivity.BODY);
                    final Uri uri = bundle.getParcelable(NewEntryActivity.URI);
                    final String placeName = bundle.getString(NewEntryActivity.PLACE_NAME);
                    final String placeId = bundle.getString(NewEntryActivity.PLACE_ID);
                    final String weather = bundle.getString(NewEntryActivity.TEMPERATURE);

                    dataManager.uploadObject(new Entry(body, uri, placeName, placeId, weather))
                            .subscribe(new ActivitySubscriber<Entry>(this) {
                                @Override
                                public void onNext(Entry entry) {
                                    entryAdapter.addData(entry);
                                }
                            });

                    if (emptyView != null) {
                        emptyView.setVisibility(View.GONE);
                    }
                }
                break;
            case RESULT_CODE_ENTRY:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    final EntryActivity.Action action = (EntryActivity.Action)
                            bundle.getSerializable(EntryActivity.INTENT_KEY);
                    if (action != null) {
                        long id = bundle.getLong(EntryActivity.INTENT_ACTION);
                        Entry entry = dataManager.get(Entry.class, id);
                        executeAction(action, entry);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onLoadComplete() {
        dismissLoading();
    }

    @Override
    public void onLoadError(Throwable throwable) {
        Logg.log(throwable);
        dismissLoading();
    }

    @Override
    public void onLoadEmpty() {
        dismissLoading();

        if (emptyView == null) {
            emptyView = ((ViewStub) findViewById(R.id.activity_home_stub)).inflate();
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewEntryActivity();
            }
        });
    }

    @Override
    public void onLoadStart() {
        showLoading();
    }

    private void fetchData() {
        dataManager.loadObjects(Entry.class)
                .subscribe(new ActivitySubscriber<List<Entry>>(this, true) {
                    @Override
                    public void onNext(List<Entry> entries) {
                        entryAdapter.addData(entries);
                        if (entries.isEmpty()) {
                            onLoadEmpty();
                        }
                    }
                });
    }

    private void startNewEntryActivity() {
        Intent intent = new Intent(this, NewEntryActivity.class);
        intent.putExtra(FabContainerTransition.START_COLOR, ContextCompat.getColor(this, R.color.accent));
        intent.putExtra(FabContainerTransition.END_COLOR, ContextCompat.getColor(this, R.color.window_background));
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, fab,
                getString(R.string.transition_fab_dialog_new_entry));
        startActivityForResult(intent, RESULT_CODE_NEW_ENTRY, options.toBundle());
    }

    private void dismissLoading() {
        if (progressBar.getVisibility() != View.GONE) {
            AnimUtils.gone(progressBar).setDuration(AnimUtils.longAnim(this)).start();
        }
    }

    private void showLoading() {
        if (progressBar.getVisibility() != View.VISIBLE) {
            AnimUtils.visible(progressBar).setDuration(AnimUtils.longAnim(this)).start();
        }
    }

    private void executeAction(EntryActivity.Action action, final Entry entry) {
        switch (action) {
            case DELETE:
                entryAdapter.removeData(entry);
                if (entryAdapter.isEmpty()) {
                    onLoadEmpty();
                }
                addSubscription(dataManager.deleteObject(entry).subscribe());
                break;
            case EDIT:
                entryAdapter.clear();
                fetchData();
                break;
            case FAVORITE:
                break;
        }
    }
}
