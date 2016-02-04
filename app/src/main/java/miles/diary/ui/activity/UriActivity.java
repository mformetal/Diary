package miles.diary.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;
import miles.diary.R;
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
    private final static int START_DELAY = 100;

    @Bind(R.id.activity_uri_image_view) ImageView imageView;
    @Bind(R.id.activity_uri_video_view) VideoView videoView;
    @Bind(R.id.activity_uri_button_row) LinearLayout buttonRow;
    @Bind(R.id.activity_uri_gallery) TypefaceButton photo;
    @Bind(R.id.activity_uri_camera) TypefaceButton camera;
    @Bind(R.id.activity_uri_video) TypefaceButton video;

    private File mFile;
    private Uri uri;

    private float originX, originY;

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
    public void onEnter(View root, Intent intent, boolean hasSavedInstanceState) {
        enterColor.setDuration(AnimUtils.mediumAnim(this)).start();

        if (uri != null) {
            Bundle extra = intent.getExtras();
            originX = extra.getFloat(IntentUtils.TOUCH_X);
            originY = extra.getFloat(IntentUtils.TOUCH_Y);

            ObjectAnimator slide = ObjectAnimator.ofPropertyValuesHolder(root,
                    PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, root.getHeight() / 5, 0),
                    PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f));
            slide.setDuration(AnimUtils.longAnim(this));
            slide.setInterpolator(new FastOutSlowInInterpolator());

            Animator reveal = ViewAnimationUtils.createCircularReveal(root,
                    (int) originX, (int) originY,
                    0, (float) Math.hypot(root.getWidth(), root.getHeight()));
            reveal.setDuration(AnimUtils.longAnim(this));
            reveal.setInterpolator(new FastOutSlowInInterpolator());

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(slide, reveal);
            animatorSet.start();
        } else {
            buttonRow.setAlpha(0f);
            buttonRow.setTranslationY(root.getHeight());

            buttonRow.animate()
                    .translationY(0)
                    .alpha(1)
                    .setStartDelay(START_DELAY)
                    .setDuration(AnimUtils.mediumAnim(this))
                    .setInterpolator(new DecelerateInterpolator()).start();
        }
    }

    @Override
    public void onExit(View root, Intent intent, boolean hasSavedInstanceState) {
        exitColor.setDuration(AnimUtils.mediumAnim(this)).start();

        if (uri != null) {
            if (originX == 0) {
                originX = root.getWidth() / 8f;
                originY = root.getHeight() * .9f;
            }

            ObjectAnimator slide = ObjectAnimator.ofPropertyValuesHolder(root,
                    PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, root.getHeight() / 5),
                    PropertyValuesHolder.ofFloat(View.ALPHA, 0f));
            slide.setDuration(AnimUtils.longAnim(this));
            slide.setInterpolator(new FastOutSlowInInterpolator());

            Animator reveal = ViewAnimationUtils.createCircularReveal(root,
                    (int) originX, (int) originY,
                    (float) Math.hypot(root.getWidth(), root.getHeight()), 0);
            reveal.setDuration(AnimUtils.longAnim(this));
            reveal.setInterpolator(new FastOutSlowInInterpolator());

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(slide, reveal);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    finishWithoutDefaultTransition();
                }
            });
            animatorSet.start();
        } else {
            buttonRow.animate()
                    .translationY(root.getHeight())
                    .alpha(0f)
                    .setStartDelay(START_DELAY)
                    .setDuration(AnimUtils.mediumAnim(this))
                    .withEndAction(this::finishWithDefaultTransition)
                    .setInterpolator(new AccelerateInterpolator()).start();
        }
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
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType(IntentUtils.IMAGE);
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

            Glide.with(this)
                    .fromUri()
                    .animate(android.R.anim.fade_in)
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
