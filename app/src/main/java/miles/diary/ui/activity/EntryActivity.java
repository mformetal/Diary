package miles.diary.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.ui.SimpleTextWatcher;
import miles.diary.ui.widget.CircledCoordinatorLayout;
import miles.diary.ui.widget.TypefaceButton;
import miles.diary.ui.widget.TypefaceEditText;
import miles.diary.util.Logg;
import miles.diary.util.PhotoFileUtils;
import miles.diary.util.TextUtils;
import miles.diary.util.ViewUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mbpeele on 1/16/16.
 */
public class EntryActivity extends BaseActivity {

    public static final String LEFT = "fabLeft";
    public static final String TOP = "fabTop";
    public static final String WIDTH = "fabWidth";
    public static final String HEIGHT = "fabHeight";
    public static final String START_COLOR = "fabStartColor";
    public static final String RESULT_TITLE = "title";
    public static final String RESULT_BODY = "body";
    public static final String RESULT_BYTES = "bytes";

    @Bind(R.id.activity_post_input_title) TypefaceEditText titleInput;
    @Bind(R.id.activity_post_input_body) TypefaceEditText bodyInput;
    @Bind(R.id.activity_post_confirm) TypefaceButton confirm;
    @Bind(R.id.activity_post_fab) FloatingActionButton fab;
    @Bind(R.id.activity_post_root) CircledCoordinatorLayout content;
    @Bind(R.id.activity_post_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_entry_image) ImageView imageView;
    @Bind(R.id.activity_post_appbar) AppBarLayout appBarLayout;
    @Bind(R.id.activity_post_collapsing) CollapsingToolbarLayout collapsingToolbarLayout;

    private AnimatorSet animatorSet;
    private String filePath;
    private Uri uri;

    public static final int REQUEST_IMAGE_CAMERA = 1;
    public static final int REQUEST_IMAGE_GALLERY = 2;
    public static final int REQUEST_PERMISSION_CAMERA_CODE = 3;
    private int fabLeft, fabTop, fabWidth, fabHeight, fabStartColor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.activity_entry_name);

            View view = toolbar.getChildAt(0);
            if (view != null && view instanceof TextView) {
                ((TextView) view).setTypeface(TextUtils.getFont(this, getString(R.string.default_font)));
            }
        }

        Bundle bundle = getIntent().getExtras();
        fabLeft = bundle.getInt(LEFT);
        fabTop = bundle.getInt(TOP);
        fabWidth = bundle.getInt(WIDTH);
        fabHeight = bundle.getInt(HEIGHT);
        fabStartColor = bundle.getInt(START_COLOR);

        final ViewTreeObserver observer = content.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (observer.isAlive()) {
                    observer.removeOnPreDrawListener(this);
                } else {
                    content.getViewTreeObserver().removeOnPreDrawListener(this);
                }

                if (savedInstanceState == null) {
                    runEnterAnimation();
                } else {
                    getWindow().getDecorView().setBackgroundColor(
                            ContextCompat.getColor(EntryActivity.this, R.color.scrim));
                    content.setRadius(ViewUtils.dominantMeasurement(content));

                    if (content.getWidth() > content.getHeight()) {
                        int old = fabTop;
                        fabTop = fabLeft;
                        fabLeft = old;
                    }
                }
                return true;
            }
        });

        titleInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                collapsingToolbarLayout.setTitle(s);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog builder = new Dialog(v.getContext());
                builder.setContentView(R.layout.dialog_image_chooser);
                builder.findViewById(R.id.dialog_from_camera).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                        showCamera();
                    }
                });
                builder.findViewById(R.id.dialog_from_gallery).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                        showGallery();
                    }
                });
                builder.show();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getTextAsString();
                String body = bodyInput.getTextAsString();
                if (title.isEmpty() || body.isEmpty()) {
                    Snackbar.make(content, R.string.activity_post_no_input_error,
                            Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_TITLE, title);
                    intent.putExtra(RESULT_BODY, body);
                    if (uri != null) {
                        intent.putExtra(RESULT_BYTES, uri);
                    }
                    setResult(RESULT_OK, intent);
                    runExitAnimation();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CAMERA_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCamera();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            uri = null;
            switch (requestCode) {
                case REQUEST_IMAGE_GALLERY:
                    uri = data.getData();
                    break;
                case REQUEST_IMAGE_CAMERA:
                    uri = PhotoFileUtils.addFileToGallery(this, filePath);
                    break;
            }

            if (uri != null) {
                Glide.with(this)
                        .fromUri()
                        .asBitmap()
                        .load(uri)
                        .fitCenter()
                        .animate(android.R.anim.fade_in)
                        .listener(new RequestListener<Uri, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target,
                                                           boolean isFromMemoryCache, boolean isFirstResource) {
                                Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {

                                    }
                                });
                                return false;
                            }
                        })
                        .into(imageView);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (animatorSet != null && !animatorSet.isRunning()) {
            runExitAnimation();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (filePath != null) {
            outState.putString("filePath", filePath);
        }

        if (uri != null) {
            outState.putString("bytes", uri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        filePath = savedInstanceState.getString("filePath");
        uri = Uri.parse(savedInstanceState.getString("bytes"));
    }

    private void runEnterAnimation() {
        float fabCx = fabLeft + fabWidth / 2f, fabCy = fabTop;
        content.setPivotX(fabCx);
        content.setPivotY(fabCy);

        Animator radius = ObjectAnimator.ofFloat(content, CircledCoordinatorLayout.RADIUS, fabWidth,
                (float) Math.hypot(fabCx, fabCy));
        radius.setInterpolator(new DecelerateInterpolator());
        radius.setDuration(400);

        Animator visibility = ViewUtils.visible(content, 700);

        ValueAnimator alpha = ValueAnimator.ofObject(new ArgbEvaluator(),
                Color.TRANSPARENT, ContextCompat.getColor(this, R.color.scrim));
        alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                getWindow().getDecorView().setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        alpha.setDuration(400);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(radius, alpha, visibility);
        animatorSet.start();
    }

    private void runExitAnimation() {
        Animator radius = ObjectAnimator.ofFloat(content, CircledCoordinatorLayout.RADIUS, 0f);
        radius.setInterpolator(new FastOutSlowInInterpolator());
        radius.setDuration(500);

        Animator visibility = ObjectAnimator.ofFloat(content, View.ALPHA, 0f);
        visibility.setDuration(600);

        ValueAnimator alpha = ValueAnimator.ofObject(new ArgbEvaluator(),
                ContextCompat.getColor(this, R.color.scrim), Color.TRANSPARENT);
        alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                getWindow().getDecorView().setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        alpha.setDuration(400);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(radius, alpha, visibility);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }
        });
        animatorSet.start();
    }

    private void showCamera() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            String[] permissions = new String[] {
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkPermissions(permissions)) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = PhotoFileUtils.createPhotoFile();
                        filePath = photoFile.getAbsolutePath();
                    } catch (IOException e) {
                        Logg.log(e);
                    }

                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAMERA);
                    }
                }
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CAMERA_CODE);
            }
        }
    }

    private void showGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }
}
