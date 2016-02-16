package miles.diary.ui.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.transition.Transition;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import icepick.State;
import miles.diary.R;
import miles.diary.ui.SimpleTransitionListener;
import miles.diary.ui.widget.CornerImageView;
import miles.diary.ui.widget.TypefaceButton;
import miles.diary.util.AnimUtils;
import miles.diary.util.IntentUtils;
import miles.diary.util.FileUtils;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 1/29/16.
 */
public class UriActivity extends BaseActivity implements View.OnClickListener{

    private final static int REQUEST_GALLERY = 1;
    private final static int REQUEST_CAMERA = 2;
    private final static int REQUEST_VIDEO = 3;
    private final static int REQUESET_IMAGE_PERMISSION = 4;

    @Bind(R.id.activity_uri_root) ViewGroup root;
    @Bind(R.id.activity_uri_image_view) CornerImageView imageView;
    @Bind(R.id.activity_uri_video_view) VideoView videoView;
    @Bind(R.id.activity_uri_button_row) LinearLayout buttonRow;
    @Bind(R.id.activity_uri_gallery) TypefaceButton photo;
    @Bind(R.id.activity_uri_camera) TypefaceButton camera;
    @Bind(R.id.activity_uri_video) TypefaceButton video;

    private File mFile;
    private Uri uri;

    @State
    boolean uriChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uri);
        setupTransitions();

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            String[] camera = new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(camera)) {
                ActivityCompat.requestPermissions(this, camera, REQUESET_IMAGE_PERMISSION);
            }
        } else {
            Snackbar.make(root, R.string.activity_entry_no_camera_error,
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, v -> {
                        finish();
                    }).show();
        }

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
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUESET_IMAGE_PERMISSION:
                if (!permissionsGranted(grantResults, 2)) {
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri oldUri = uri;
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

            uriChanged = oldUri != uri;
        }
    }

    @Override
    public void onBackPressed() {
        if (uri != null) {
            Intent intent = new Intent();
            intent.setData(uri);
            setResult(RESULT_OK, intent);
        }
        if (uriChanged) {
            overridePendingTransition(0, android.R.anim.fade_out);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    @OnClick({R.id.activity_uri_gallery, R.id.activity_uri_camera, R.id.activity_uri_video})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_uri_gallery:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType(IntentUtils.IMAGE);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
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

    private void loadVideoUri() {
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.start();
    }

    private void setupTransitions() {
        final Transition enterTransition = getWindow().getSharedElementEnterTransition();
        final Transition returnTransition = getWindow().getSharedElementReturnTransition();

        if (enterTransition != null && returnTransition != null) {
            enterTransition.addListener(new SimpleTransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    super.onTransitionStart(transition);
                    enterTransition.removeListener(this);

                    returnTransition.addListener(new SimpleTransitionListener() {
                        @Override
                        public void onTransitionStart(Transition transition) {
                            super.onTransitionEnd(transition);

                            buttonRow.animate()
                                    .translationY(root.getHeight())
                                    .alpha(0f)
                                    .setDuration(AnimUtils.mediumAnim(getApplicationContext()))
                                    .setInterpolator(new FastOutSlowInInterpolator())
                                    .start();

                            ObjectAnimator corner = ObjectAnimator.ofFloat(imageView,
                                    CornerImageView.CORNERS,
                                    0, Math.min(imageView.getWidth(), imageView.getHeight()) / 2f);
                            corner.setDuration(AnimUtils.longAnim(getApplicationContext()));
                            corner.setInterpolator(new FastOutSlowInInterpolator());
                            corner.start();
                        }
                    });

                    buttonRow.setTranslationY(root.getHeight());
                    buttonRow.setAlpha(0f);

                    buttonRow.animate()
                            .translationY(0f)
                            .alpha(1f)
                            .setDuration(AnimUtils.mediumAnim(getApplicationContext()))
                            .setInterpolator(new FastOutSlowInInterpolator())
                            .start();

                    ObjectAnimator corner = ObjectAnimator.ofFloat(imageView,
                            CornerImageView.CORNERS,
                            Math.max(imageView.getWidth(), imageView.getHeight()) / 2f, 0);
                    corner.setDuration(AnimUtils.longAnim(getApplicationContext()));
                    corner.setInterpolator(new FastOutSlowInInterpolator());
                    corner.start();
                }
            });
        }
    }
}
