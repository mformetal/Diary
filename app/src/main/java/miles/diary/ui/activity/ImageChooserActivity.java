package miles.diary.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.repacked.apache.commons.io.FileUtils;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import miles.diary.R;
import miles.diary.ui.widget.TypefaceButton;
import miles.diary.ui.ErrorDialog;
import miles.diary.util.PhotoFileUtils;

/**
 * Created by mbpeele on 1/29/16.
 */
public class ImageChooserActivity extends BaseActivity implements View.OnClickListener {

    private final static int REQUEST_IMAGE_GALLERY = 4;
    private final static int REQUEST_IMAGE_CAMERA = 5;
    private final static int REQUEST_IMAGE_VIDEO = 6;
    private final static String IMAGE_URI = "uri";

    @Bind(R.id.activity_image_chooser_image) ImageView imageView;
    @Bind(R.id.activity_image_chooser_photo) TypefaceButton photo;
    @Bind(R.id.activity_image_chooser_camera) TypefaceButton camera;
    @Bind(R.id.activity_image_chooser_video) TypefaceButton video;

    private File mFile;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_chooser);

        Intent intent = getIntent();
        if (intent != null) {
            Uri data = intent.getData();
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
                    uri = Uri.fromFile(mFile);
                    break;
                case REQUEST_IMAGE_VIDEO:
                    break;
            }

            if (uri != null) {
                Glide.with(this)
                        .fromUri()
                        .asBitmap()
                        .load(uri)
                        .into(imageView);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (uri != null) {
            Intent intent = new Intent();
            intent.setData(uri);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    @OnClick({R.id.activity_image_chooser_photo, R.id.activity_image_chooser_camera,
            R.id.activity_image_chooser_video})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_image_chooser_photo:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
                break;
            case R.id.activity_image_chooser_camera:
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        mFile = PhotoFileUtils.createPhotoFile(this);

                        if (mFile != null) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAMERA);
                        }
                    }
                } else {
                    ErrorDialog.newInstance(getString(R.string.activity_entry_no_camera_error))
                            .show(getFragmentManager(), "error");
                }
                break;
            case R.id.activity_image_chooser_video:
                break;
        }
    }
}
