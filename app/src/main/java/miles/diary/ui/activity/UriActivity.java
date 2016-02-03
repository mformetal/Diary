package miles.diary.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;
import miles.diary.R;
import miles.diary.ui.PreDrawer;
import miles.diary.ui.widget.TypefaceButton;
import miles.diary.util.AnimUtils;
import miles.diary.util.IntentUtils;
import miles.diary.util.FileUtils;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 1/29/16.
 */
public class UriActivity extends TransitionActivity implements View.OnClickListener {

    private final static int REQUEST_GALLERY = 4;
    private final static int REQUEST_CAMERA = 5;
    private final static int REQUEST_VIDEO = 6;

    @Bind(R.id.activity_uri_image_view) ImageView imageView;
    @Bind(R.id.activity_uri_video_view) VideoView videoView;
    @Bind(R.id.activity_uri_button_row) LinearLayout buttonRow;
    @Bind(R.id.activity_uri_gallery) TypefaceButton photo;
    @Bind(R.id.activity_uri_camera) TypefaceButton camera;
    @Bind(R.id.activity_uri_video) TypefaceButton video;

    private File mFile;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uri);

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            uri = intent.getData();

            if (FileUtils.isImageUri(this, uri)) {
                loadImageUri();
            } else {
                loadVideoUri();
            }
        }
    }

    @Override
    public void onEnter(View root, boolean hasSavedInstanceState) {
        ValueAnimator color = ValueAnimator.ofObject(new ArgbEvaluator(),
                Color.TRANSPARENT, ContextCompat.getColor(this, R.color.scrim));
        color.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                getWindow().getDecorView().setBackgroundColor((int) animation.getAnimatedValue());
            }
        });
        color.setDuration(AnimUtils.mediumAnim(this));

        ObjectAnimator slide;
        if (uri != null && !FileUtils.isImageUri(this, uri)) {
            slide = ObjectAnimator.ofPropertyValuesHolder(root,
                    PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -root.getWidth(), 0f),
                    PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f));
            slide.setDuration(AnimUtils.mediumAnim(this));
            slide.setInterpolator(new AccelerateInterpolator());
        } else {
            slide = ObjectAnimator.ofPropertyValuesHolder(root,
                    PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -root.getWidth() / 2f, 0f),
                    PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f));
            slide.setDuration(AnimUtils.mediumAnim(this));
            slide.setInterpolator(new AccelerateInterpolator());
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(color, slide);
        animatorSet.start();
    }

    @Override
    public void onExit(View root, boolean hasSavedInstanceState) {
        ValueAnimator color = ValueAnimator.ofObject(new ArgbEvaluator(),
                ContextCompat.getColor(this, R.color.scrim), Color.TRANSPARENT);
        color.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                getWindow().getDecorView().setBackgroundColor((int) animation.getAnimatedValue());
            }
        });
        color.setDuration(AnimUtils.mediumAnim(this));

        ObjectAnimator slide;
        if (uri != null && !FileUtils.isImageUri(this, uri)) {
            slide = ObjectAnimator.ofPropertyValuesHolder(root,
                    PropertyValuesHolder.ofFloat(View.TRANSLATION_X, root.getWidth()),
                    PropertyValuesHolder.ofFloat(View.ALPHA, 0f));
            slide.setDuration(AnimUtils.mediumAnim(this));
            slide.setInterpolator(new AccelerateInterpolator());
        } else {
            slide = ObjectAnimator.ofPropertyValuesHolder(root,
                    PropertyValuesHolder.ofFloat(View.TRANSLATION_X, root.getWidth() / 2f),
                    PropertyValuesHolder.ofFloat(View.ALPHA, 0f));
            slide.setDuration(AnimUtils.mediumAnim(this));
            slide.setInterpolator(new AccelerateInterpolator());
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(color, slide);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }
        });
        animatorSet.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_GALLERY:
                    uri = data.getData();
                    loadImageUri();
                    break;
                case REQUEST_CAMERA:
                    uri = FileUtils.addFileToGallery(this, mFile.getAbsolutePath());
                    loadImageUri();
                    break;
                case REQUEST_VIDEO:
                    uri = data.getData();
                    loadVideoUri();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (uri != null) {
            Intent intent = new Intent();
            intent.setData(uri);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    @Override
    @OnClick({R.id.activity_uri_gallery, R.id.activity_uri_camera, R.id.activity_uri_video})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_uri_gallery:
                Intent intent = new Intent();
                intent.setType(IntentUtils.IMAGE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_GALLERY);
                break;
            case R.id.activity_uri_camera:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        mFile = FileUtils.createPhotoFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (mFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
                        startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                    }
                }
                break;
            case R.id.activity_uri_video:
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO);
                }
                break;
        }
    }

    private void loadImageUri() {
        if (uri != null) {
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);

            ViewPropertyAnimation.Animator animator = new ViewPropertyAnimation.Animator() {
                @Override
                public void animate(View view) {
                    PreDrawer preDrawer = new PreDrawer(view) {
                        @Override
                        public void notifyPreDraw() {
                            Animator reveal = ViewAnimationUtils.createCircularReveal(view,
                                    view.getWidth() / 2, view.getHeight() / 2, 0,
                                    Math.max(view.getWidth(), view.getHeight()));
                            reveal.setDuration(500);
                            reveal.setInterpolator(new FastOutSlowInInterpolator());
                            reveal.start();
                        }
                    };
                }
            };

            Glide.with(this)
                    .fromUri()
                    .animate(animator)
                    .load(uri)
                    .into(imageView);
        }
    }

    public void loadVideoUri() {
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.start();
    }
}
