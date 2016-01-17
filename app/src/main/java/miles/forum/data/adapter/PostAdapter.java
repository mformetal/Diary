package miles.forum.data.adapter;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewClickEvent;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import miles.forum.R;
import miles.forum.data.SimpleObserver;
import miles.forum.data.model.Post;
import miles.forum.ui.widget.LikeButton;
import miles.forum.ui.widget.TypefaceButton;
import miles.forum.ui.widget.TypefaceTextView;
import miles.forum.util.Logg;
import miles.forum.util.ViewUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by mbpeele on 1/14/16.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Post> mDataList;
    private List<String> mLikedPosts;

    private final static String LIKED_POSTS = "liked";
    private int mLastPos = -1;

    public PostAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDataList = new ArrayList<>();

        List<String> arrayList = ParseUser.getCurrentUser().getList(LIKED_POSTS);
        if (arrayList != null) {
            mLikedPosts = arrayList;
        } else {
            mLikedPosts = new ArrayList<>();
        }
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.adapter_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        final Post post = mDataList.get(position);

        if (position > mLastPos && !holder.hasAnimated) {
            runEnterAnimation(holder, position);
            mLastPos = position;
        }

        holder.time.setText(post.formatCreatedAtTime());
        holder.title.setText(post.getTitle());
        holder.comment.setText(post.getCommentsCountAsString());
        holder.share.setText(post.getShareCountAsString());
        holder.like.setText(post.getLikeCountAsString());

//        if (mLikedPosts.contains(post.getObjectId())) {
//            holder.like.setLiked(true, false);
//        }

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start ForumActivity
            }
        });

        RxView.clickEvents(holder.like)
                .map(new Func1<ViewClickEvent, Integer>() {
                    @Override
                    public Integer call(ViewClickEvent viewClickEvent) {
                        int count = post.getLikesCount();
                        if (!holder.like.hasLiked()) {
                            mLikedPosts.remove(post.getObjectId());
                            post.putLikesCount(count - 1);
                        } else {
                            mLikedPosts.add(post.getObjectId());
                            post.putLikesCount(count + 1);
                        }
                        return 1;
                    }
                })
                .buffer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<List<Integer>>() {
                    @Override
                    public void onNext(List<Integer> o) {
                        super.onNext(o);
                        if (o.size() > 0) {
                            post.saveEventually();

                            ParseUser user = ParseUser.getCurrentUser();
                            user.put(LIKED_POSTS, mLikedPosts);
                            user.saveEventually();
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void addPost(Post post) {
        mDataList.add(post);
        notifyDataSetChanged();
    }

    public Post removePost(int position) {
        return mDataList.remove(position);
    }

    public void runEnterAnimation(PostViewHolder holder, int position) {
        holder.itemView.setAlpha(0f);
        ViewUtils.visible(holder.itemView, 350).start();
        holder.hasAnimated = true;
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public final static class PostViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.adapter_post_layout_time) TypefaceTextView time;
        @Bind(R.id.adapter_post_layout_title) TypefaceTextView title;
        @Bind(R.id.adapter_post_layout_like) LikeButton like;
        @Bind(R.id.adapter_post_layout_comment) TypefaceButton comment;
        @Bind(R.id.adapter_post_layout_share) TypefaceButton share;

        private boolean hasAnimated = false;

        public PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
