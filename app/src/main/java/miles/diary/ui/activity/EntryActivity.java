package miles.diary.ui.activity;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.graphics.Palette;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.ui.fragment.CameraFragment;
import miles.diary.util.Logg;
import miles.diary.util.PhotoFileUtils;

/**
 * Created by mbpeele on 1/16/16.
 */
public class EntryActivity extends BaseActivity {

    public static final String RESULT_TITLE = "title";
    public static final String RESULT_BODY = "body";
    public static final String RESULT_URI = "uri";
    private static final String CAMERA_FRAGMENT = "camera";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        getFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, CameraFragment.newInstance(), CAMERA_FRAGMENT)
                .commit();
    }

    @Override
    public void onBackPressed() {
        CameraFragment fragment =
                (CameraFragment) getFragmentManager().findFragmentByTag(CAMERA_FRAGMENT);
        if (fragment != null) {
            if (!fragment.onBackPressed()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}
