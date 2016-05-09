package miles.diary.ui.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import io.realm.Case;
import io.realm.RealmResults;
import io.realm.Sort;
import miles.diary.R;
import miles.diary.data.adapter.EntryAdapter;
import miles.diary.data.model.realm.Entry;
import miles.diary.data.model.realm.Search;
import miles.diary.data.model.realm.Sorter;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.data.rx.DataLoadingSubscriber;
import miles.diary.ui.PreDrawer;
import miles.diary.ui.TintingSearchListener;
import miles.diary.ui.transition.FabContainerTransition;
import miles.diary.ui.widget.SearchWidget;
import miles.diary.util.AnimUtils;
import miles.diary.util.ColorsUtils;
import miles.diary.util.DataLoadingListener;
import miles.diary.util.Logg;
import rx.functions.Func1;

public class HomeActivity extends BaseActivity implements DataLoadingListener<List<Entry>> {

    @Bind(R.id.activity_home_recycler) RecyclerView recyclerView;
    @Bind(R.id.activity_home_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_home_loading) ProgressBar progressBar;
    @Bind(R.id.activity_home_fab) FloatingActionButton fab;
    @Bind(R.id.activity_home_search_widget) SearchWidget searchWidget;
    @Bind(R.id.activity_home_drawer) DrawerLayout drawerLayout;
    @Bind(R.id.activity_home_navigation_view) NavigationView navigationView;

    private final static int RESULT_CODE_NEW_ENTRY = 1;
    public final static int RESULT_CODE_ENTRY = 2;

    private EntryAdapter entryAdapter;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        runEnterAnimation();

        setActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        addNavigationViewClickListener();

        if (storage.getBoolean("firstTime", true)) {
            drawerLayout.openDrawer(GravityCompat.START);
            storage.setBoolean("firstTime", false);
        }

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

        addSearchListener();
    }

    @Override
    public void onBackPressed() {
        if (searchWidget.interceptBackButton()) {
            return;
        }

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home_search:
                int[] pos = new int[2];
                View search = toolbar.findViewById(R.id.menu_home_search);
                search.getLocationOnScreen(pos);
                searchWidget.toggle(pos);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {
            case RESULT_CODE_NEW_ENTRY:
                if (resultCode == RESULT_OK) {
                    final Bundle bundle = data.getExtras();
                    repository.uploadObject(Entry.fromBundle(bundle))
                            .subscribe(new ActivitySubscriber<Entry>(this) {
                                @Override
                                public void onNext(Entry entry) {
                                    entryAdapter.addAndSort(entry);

                                    scrollToTop();

                                    if (emptyView != null) {
                                        emptyView.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
                break;
            case RESULT_CODE_ENTRY:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    final EntryActivity.Action action = (EntryActivity.Action)
                            bundle.getSerializable(EntryActivity.INTENT_KEY);
                    if (action != null) {
                        long id = bundle.getLong(EntryActivity.INTENT_ACTION, -1);
                        if (id != -1) {
                            Entry entry = repository.get(Entry.class, id);
                            executeAction(action, entry);
                        }
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
    public void onLoadData(List<Entry> entries) {
        if (entries.isEmpty()) {
            onLoadEmpty();
        } else {
            entryAdapter.addAll(entries);
        }
    }

    @Override
    public void onLoadStart() {
        showLoading();
    }

    private void addNavigationViewClickListener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_menu_home_calendar:
                        break;
                    case R.id.navigation_menu_home_map:
                        startActivity(new Intent(HomeActivity.this, MapActivity.class));
                        break;
                    case R.id.navigation_menu_home_ideas:
                        break;
                }
                return false;
            }
        });
    }

    private void addSearchListener() {
        int color = ContextCompat.getColor(this, R.color.window_background);
        color = ColorsUtils.modifyAlpha(color, .7f);
        final Search search = Search.builder()
                .setCasing(Case.INSENSITIVE)
                .setUseOr(true)
                .setFieldNames("body", "placeName")
                .setSortKeys("dateMillis")
                .setSortOrders(Sort.DESCENDING)
                .createRealmSearch();

        SearchWidget.SearchListener searchListener = new TintingSearchListener(root, color) {
            @Override
            public void onSearchShow(int[] position) {
                super.onSearchShow(position);
                fab.animate().alpha(.4f).setDuration(350);
            }

            @Override
            public void onSearchDismiss(int[] position) {
                super.onSearchDismiss(position);
                fab.animate().alpha(1f).setDuration(350);
            }

            @Override
            public void onSearchTextChanged(final String text) {
                super.onSearchTextChanged(text);
                search.constraint = text;

                repository.search(Entry.class, search)
                        .debounce(150, TimeUnit.MILLISECONDS)
                        .subscribe(new ActivitySubscriber<List<Entry>>(HomeActivity.this) {
                            @Override
                            public void onNext(List<Entry> entries) {
                                root.bringChildToFront(recyclerView);
                                entryAdapter.setData(entries, true);

                                scrollToTop();
                            }
                        });
            }
        };

        searchWidget.addSearchListener(searchListener);
    }

    private void scrollToTop() {
        LinearLayoutManager linearLayoutManager =
                (LinearLayoutManager) recyclerView.getLayoutManager();
        if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
            linearLayoutManager.scrollToPosition(0);
        }
    }

    private void fetchData() {
        Sorter sorter = new Sorter(new String[] {"dateMillis"}, new Sort[] {Sort.DESCENDING});

        repository.getAllSorted(Entry.class, sorter)
                .subscribe(new DataLoadingSubscriber<List<Entry>>(this));
    }

    private void runEnterAnimation() {
        PreDrawer.addPreDrawer(root, new PreDrawer.OnPreDrawListener<ViewGroup>() {
            @Override
            public boolean onPreDraw(ViewGroup view) {
                fab.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.glide_pop));

                TextView textView = (TextView) toolbar.getChildAt(0);

                AnimUtils.textScale(textView, null, .8f, 1f)
                        .setDuration(AnimUtils.longAnim(view.getContext()))
                        .start();

                AnimUtils.alpha(textView, 0f, 1f)
                        .setDuration(AnimUtils.longAnim(view.getContext()))
                        .start();
                return true;
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
            AnimUtils.gone(progressBar)
                    .setDuration(AnimUtils.longAnim(this))
                    .start();
        }
    }

    private void showLoading() {
        if (progressBar.getVisibility() != View.VISIBLE) {
            AnimUtils.visible(progressBar)
                    .setDuration(AnimUtils.longAnim(this))
                    .start();
        }
    }

    private void executeAction(EntryActivity.Action action, final Entry entry) {
        switch (action) {
            case DELETE:
                entryAdapter.removeObject(entry);
                if (entryAdapter.isEmpty()) {
                    onLoadEmpty();
                }
                addSubscription(repository.deleteObject(entry).subscribe());
                break;
            case EDIT:
                entryAdapter.update(entry);
                break;
        }
    }
}
