package miles.forum.data.service;

import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import miles.forum.data.ParseObservable;
import miles.forum.data.model.Post;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by mbpeele on 1/15/16.
 */
public class PostService {

    public final static String NEW = "createdAt";
    public final static String TOP = "likes";

    private Observable<Post> newPosts;
    private Observable<Post> topPosts;

    public PostService() {
    }

    public Observable<Post> getPosts(String filter) {
        switch (filter) {
            case NEW:
                if (newPosts == null) {
                    ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                    query.orderByDescending("createdAt");
                    newPosts =  ParseObservable.find(query).cache();
                }

                return newPosts;
            case TOP:
                if (topPosts == null) {
                    ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                    query.orderByDescending("likes");
                    topPosts = ParseObservable.find(query).cache();
                }

                return topPosts;
            default:
                throw new IllegalArgumentException("Incorrect use of PostService, wrong filter");
        }
    }

    public Observable<Post> refreshCurrentData(String filter) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        switch (filter) {
            case NEW:
                query.orderByDescending("createdAt");
                return (newPosts =  ParseObservable.find(query).cache());
            case TOP:
                query.orderByDescending("likes");
                return (topPosts = ParseObservable.find(query).cache());
            default:
                throw new IllegalArgumentException("Incorrect use of PostService, wrong filter");
        }
    }
}
