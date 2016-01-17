package miles.forum.data;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.concurrent.TimeUnit;

import bolts.Continuation;
import bolts.Task;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created on 28-Apr-15.
 */
public class ParseObservable<T extends ParseObject> {

    private Class<T> mSubClass;

    public ParseObservable(Class<T> subclass) {
        mSubClass = subclass;
    }

    public static <R> Observable<R> toObservable(final Task<R> task) {
        return Observable.create(new Observable.OnSubscribe<R>() {
            @Override
            public void call(final Subscriber<? super R> subscriber) {
                task.continueWith(new Continuation<R, Object>() {
                    @Override
                    public Object then(Task<R> task) {
                        if (task.isCancelled()) {
                            subscriber.unsubscribe();
                        } else if (task.isFaulted()) {
                            subscriber.onError(task.getError());
                        } else {
                            R r = task.getResult();

                            if (r != null)
                                subscriber.onNext(r);
                            subscriber.onCompleted();

                        }
                        return null;
                    }
                });
            }
        });
    }

    public static <R extends ParseObject> Observable<R> find(final ParseQuery<R> query) {
        return toObservable(query.findInBackground())
                .flatMap(new Func1<List<R>, Observable<R>>() {
                    @Override
                    public Observable<R> call(List<R> rs) {
                        return Observable.from(rs);
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Observable.just(query)
                                .doOnNext(new Action1<ParseQuery<R>>() {
                                    @Override
                                    public void call(ParseQuery<R> rParseQuery) {
                                        rParseQuery.cancel();
                                    }
                                })
                                .timeout(1, TimeUnit.SECONDS)
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Action1<ParseQuery<R>>() {
                                    @Override
                                    public void call(ParseQuery<R> rParseQuery) {

                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {

                                    }
                                });

                    }
                });
    }

    public static <R extends ParseObject> Observable<Integer> count(final ParseQuery<R> query) {
        return toObservable(query.countInBackground())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Observable.just(query)
                                .doOnNext(new Action1<ParseQuery<R>>() {
                                    @Override
                                    public void call(ParseQuery<R> rParseQuery) {
                                        rParseQuery.cancel();
                                    }
                                })
                                .timeout(1, TimeUnit.SECONDS)
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Action1<ParseQuery<R>>() {
                                    @Override
                                    public void call(ParseQuery<R> rParseQuery) {

                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {

                                    }
                                });

                    }
                });
    }


    public static <R extends ParseObject> Observable<R> pin(R object) {
        return toObservable(object.pinInBackground()).map(new Func1<Void, R>() {
            @Override
            public R call(Void aVoid) {
                return null;
            }
        });
    }

    public static <R extends ParseObject> Observable<R> pin(final List<R> objects) {
        return toObservable(ParseObject.pinAllInBackground(objects)).flatMap(new Func1<Void, Observable<R>>() {
            @Override
            public Observable<R> call(Void aVoid) {
                return Observable.from(objects);
            }
        });
    }

    public static <R extends ParseObject> Observable<R> all(final ParseQuery<R> query) {
        return count(query).flatMap(
                new Func1<Integer, Observable<R>>() {
                    @Override
                    public Observable<R> call(Integer integer) {
                        return all(query, integer);
                    }
                });
    }

    /**
     * limit 10000 by skip
     */
    public static <R extends ParseObject> Observable<R> all(ParseQuery<R> query, int count) {
        final int limit = 1000; // limit limitation
        query.setSkip(0);
        query.setLimit(limit);
        Observable<R> find = find(query);
        for (int i = limit; i < count; i += limit) {
            if (i >= 10000) break; // skip limitation
            query.setSkip(i);
            query.setLimit(limit);
            find.concatWith(find(query));
        }
        return find.distinct(
                new Func1<R, Object>() {
                    @Override
                    public Object call(R r) {
                        return r.getObjectId();
                    }
                });


    }

    public static <R extends ParseObject> Observable<R> first(final ParseQuery<R> query) {
        return toObservable(query.getFirstInBackground())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Observable.just(query)
                                .doOnNext(new Action1<ParseQuery<R>>() {
                                    @Override
                                    public void call(ParseQuery<R> rParseQuery) {
                                        rParseQuery.cancel();
                                    }
                                })
                                .timeout(1, TimeUnit.SECONDS)
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Action1<ParseQuery<R>>() {
                                    @Override
                                    public void call(ParseQuery<R> rParseQuery) {

                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {

                                    }
                                });

                    }
                });

    }

    public static <R extends ParseObject> Observable<R> save(R object) {
        return toObservable(object.saveInBackground()).map(new Func1<Void, R>() {
            @Override
            public R call(Void aVoid) {
                return null;
            }
        });
    }
}