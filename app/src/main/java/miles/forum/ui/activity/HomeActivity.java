package miles.forum.ui.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.OnClick;
import miles.forum.R;
import miles.forum.data.ActivitySubscriber;
import miles.forum.data.adapter.PostAdapter;
import miles.forum.data.model.Post;
import miles.forum.data.ParseObservable;
import miles.forum.data.service.PostService;
import miles.forum.ui.SimpleTabListener;
import miles.forum.ui.SpacingDecoration;
import miles.forum.util.Logg;
import miles.forum.util.ViewUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.activity_home_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_home_fab) FloatingActionButton fab;
    @Bind(R.id.activity_home_tab_layout) TabLayout tabLayout;
    @Bind(R.id.activity_home_recycler) RecyclerView recyclerView;
    @Bind(R.id.activity_home_root) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.activity_home_progress) ProgressBar progressBar;
    @Bind(R.id.activity_home_fab_loading) ProgressBar fabProgressBar;

    private final static int REQUEST_POST = 1001;

    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(miles.forum.R.layout.activity_home);

        setSupportActionBar(toolbar);

        fabProgressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.primary_text), PorterDuff.Mode.SRC_ATOP);

        postAdapter = new PostAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);
        recyclerView.addItemDecoration(new SpacingDecoration(20));

        String[] array = getResources().getStringArray(R.array.activity_home_tabs);
        for (String string: array) {
            tabLayout.addTab(tabLayout.newTab().setText(string));
        }
        tabLayout.setOnTabSelectedListener(new SimpleTabListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getPosts(postService.getPosts(PostService.NEW));
                        break;
                    case 1:
                        getPosts(postService.getPosts(PostService.TOP));
                        break;
                }
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                postAdapter.removePost(position);
                postAdapter.notifyItemRemoved(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        getPosts(postService.getPosts(PostService.NEW));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_POST:
                ViewUtils.visible(fab, 0).start();
                if (resultCode == RESULT_OK) {
                    Bundle extra = data.getExtras();
                    if (extra != null) {
                        String text = data.getStringExtra(PostActivity.RESULT_TEXT);
                        savePost(new Post(text));
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(miles.forum.R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.action_refresh:
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        getPosts(postService.refreshCurrentData(PostService.NEW));
                        break;
                    case 1:
                        getPosts(postService.refreshCurrentData(PostService.TOP));
                        break;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @OnClick({R.id.activity_home_fab})
    public void onClick(View v) {
        int[] location = new int[2];
        fab.getLocationOnScreen(location);

        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra(PostActivity.LEFT, location[0]);
        intent.putExtra(PostActivity.TOP, location[1]);
        intent.putExtra(PostActivity.WIDTH, fab.getWidth());
        intent.putExtra(PostActivity.HEIGHT, fab.getHeight());
        intent.putExtra(PostActivity.START_COLOR, ContextCompat.getColor(this, R.color.accent));

        startActivityForResult(intent, REQUEST_POST);

        overridePendingTransition(0, 0);

        ViewUtils.gone(fab, 200).start();
    }

    private void getPosts(Observable<Post> posts) {
        if (hasConnection()) {
            posts.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ActivitySubscriber<Post>(this) {
                        @Override
                        public void onCompleted() {
                            ViewUtils.gone(progressBar, 350).start();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logg.log(e);
                        }

                        @Override
                        public void onNext(Post st) {
                            postAdapter.addPost(st);
                        }

                        @Override
                        public void onStart() {
                            postAdapter.clear();
                            coordinatorLayout.bringChildToFront(progressBar);
                            ViewUtils.visible(progressBar, 350).start();
                        }
                    });
        } else {
            showSnackbar(R.string.error_no_internet, coordinatorLayout,
                    null, Snackbar.LENGTH_SHORT);
        }
    }

    private void savePost(Post post) {
        if (hasConnection()) {
            ParseObservable.save(post)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ActivitySubscriber<Post>(this) {
                        @Override
                        public void onCompleted() {
                            ViewUtils.invisible(fabProgressBar, 200).start();
                            showSnackbar(R.string.activity_home_saved_post_snackbar,
                                    coordinatorLayout, null, Snackbar.LENGTH_SHORT);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logg.log(e);
                        }

                        @Override
                        public void onNext(Post post) {
                            postAdapter.addPost(post);
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
                            ViewUtils.visible(fabProgressBar, 150).start();
                        }
                    });
        } else {
            showSnackbar(R.string.error_no_internet, coordinatorLayout,
                    null, Snackbar.LENGTH_SHORT);
        }
    }
}
