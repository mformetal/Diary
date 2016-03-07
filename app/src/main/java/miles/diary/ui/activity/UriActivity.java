package miles.diary.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;
import miles.diary.R;
import miles.diary.ui.PaletteWindows;
import miles.diary.ui.widget.RoundedImageView;
import miles.diary.ui.widget.TypefaceButton;
import miles.diary.util.AnimUtils;
import miles.diary.util.FileUtils;
import miles.diary.util.IntentUtils;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 1/29/16.
 */
public class UriActivity extends BaseActivity implements View.OnClickListener{

    private final static int REQUEST_GALLERY = 1;
    private final static int REQUEST_CAMERA = 2;
    private final static int REQUEST_VIDEO = 3;
    private final static int REQUESET_IMAGE_PERMISSION = 4;

    @Bind(R.id.activity_uri_image_view)
    RoundedImageView imageView;
    @Bind(R.id.activity_uri_video_view) VideoView videoView;
    @Bind(R.id.activity_uri_button_row) LinearLayout buttonRow;
    @Bind(R.id.activity_uri_gallery) TypefaceButton photo;
    @Bind(R.id.activity_uri_camera) TypefaceButton camera;
    @Bind(R.id.activity_uri_video) TypefaceButton video;

    private File mFile;
    private Uri uri;
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
            buttonRow.setEnabled(false);
            Snackbar.make(root, R.string.activity_entry_no_camera_error,
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
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
                if (!permissionsGranted(grantResults)) {
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
                    .asBitmap()
                    .animate(AnimUtils.REVEAL)
                    .load(uri)
                    .centerCrop()
                    .listener(new RequestListener<Uri, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, Uri model,
                                                   Target<Bitmap> target, boolean isFirstResource) {
                            Logg.log(e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Uri model,
                                                       Target<Bitmap> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            Palette.from(resource)
                                    .maximumColorCount(3)
                                    .clearFilters()
                                    .generate(new PaletteWindows(UriActivity.this, resource));
                            return false;
                        }
                    })
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
//        PreDrawer.addPreDrawer(imageView, new PreDrawer.OnPreDrawListener<RoundedImageView>() {
//            @Override
//            public boolean onPreDraw(final RoundedImageView view) {
//                if (view.getWidth() != 0 && view.getHeight() != 0) {
//                    ArcMotion arcMotion = new ArcMotion();
//                    arcMotion.setMinimumHorizontalAngle(50f);
//                    arcMotion.setMinimumVerticalAngle(50f);
//
//                    RoundedImageViewTransition reveal = new RoundedImageViewTransition(
//                            view.getWidth() / 2f, 0);
//                    reveal.addTarget(view);
//                    reveal.setPathMotion(arcMotion);
//                    reveal.addListener(new SimpleTransitionListener() {
//                        @Override
//                        public void onTransitionStart(Transition transition) {
//                            ViewUtils.gone(buttonRow);
//                        }
//
//                        @Override
//                        public void onTransitionEnd(Transition transition) {
//                            for (int i = 0; i < buttonRow.getChildCount(); i++) {
//                                AnimUtils.visible(buttonRow.getChildAt(i)).start();
//                            }
//                        }
//                    });
//
//                    RoundedImageViewTransition unreveal = new RoundedImageViewTransition(
//                            0, view.getHeight() / 2f);
//                    unreveal.addTarget(view);
//                    unreveal.setPathMotion(arcMotion);
//                    unreveal.addListener(new SimpleTransitionListener() {
//                        @Override
//                        public void onTransitionStart(Transition transition) {
//                            super.onTransitionStart(transition);
//                            for (int i = 0; i < buttonRow.getChildCount(); i++) {
//                                AnimUtils.gone(buttonRow.getChildAt(i)).start();
//                            }
//                        }
//                    });
//
////                    getWindow().setSharedElementEnterTransition(reveal);
////                    getWindow().setSharedElementReturnTransition(unreveal);
//                }
//
//                return true;
//            }
//        });
    }
}
