package miles.diary.ui.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.OnClick;
import miles.diary.R;
import miles.diary.data.model.WeatherResponse;
import miles.diary.ui.PreDrawer;
import miles.diary.ui.widget.TypefaceEditText;

/**
 * Created by mbpeele on 1/16/16.
 */
public class NewEntryActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.activity_entry_root) CoordinatorLayout root;
    @Bind(R.id.activity_entry_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_entry_body) TypefaceEditText bodyInput;
    @Bind(R.id.activity_entry_photo) ImageView photoFab;
    @Bind(R.id.activity_entry_location) ImageView locationFab;

    public static final String RESULT_BODY = "body";
    public static final String RESULT_URI = "uri";
    public static final String RESULT_PLACE_NAME = "place";
    public static final String RESULT_PLACE_ID = "placeId";
    public static final String RESULT_WEATHER = "weather";
    private final static int REQUEST_LOCATION = 1;
    private final static int REQUEST_IMAGE = 2;

    private String placeName;
    private String placeId;
    private Uri imageUri;
    private WeatherResponse.Weather weather;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        setActionBar(toolbar);

        new PreDrawer(root) {
            @Override
            public void notifyPreDraw() {
                tintImages();
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    placeName = extras.getString(LocationActivity.RESULT_LOCATION_NAME);
                    placeId = extras.getString(LocationActivity.RESULT_LOCATION_ID);
                    weather = extras.getParcelable(LocationActivity.RESULT_WEATHER);
                } else {
                    placeName = null;
                    placeId = null;
                }

                tintImages();
                break;
            case REQUEST_IMAGE:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();

                    loadThumbnailFromUri();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entry, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_entry_done:
                String body = bodyInput.getTextAsString();
                if (!body.isEmpty()) {
                    Intent result = new Intent();
                    result.putExtra(NewEntryActivity.RESULT_BODY, body);
                    result.putExtra(NewEntryActivity.RESULT_URI, imageUri);
                    result.putExtra(NewEntryActivity.RESULT_PLACE_NAME, placeName);
                    result.putExtra(NewEntryActivity.RESULT_PLACE_ID, placeId);
                    result.putExtra(NewEntryActivity.RESULT_WEATHER, weather);
                    setResult(Activity.RESULT_OK, result);
                    finish();
                } else {
                    Snackbar.make(root, R.string.activity_entry_no_text_error,
                            Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @OnClick({R.id.activity_entry_photo, R.id.activity_entry_location})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_entry_photo:
                Intent intent = new Intent(this, UriActivity.class);
                intent.setData(imageUri);

                if (imageUri == null) {
                    startActivityForResult(intent, REQUEST_IMAGE);
                } else {
                    ActivityOptions transitionActivityOptions =
                            ActivityOptions.makeSceneTransitionAnimation(this, photoFab,
                                    getString(R.string.transition_entry_to_uri_activity));
                    startActivityForResult(intent, REQUEST_IMAGE, transitionActivityOptions.toBundle());
                }
                break;
            case R.id.activity_entry_location:
                Intent intent1 = new Intent(this, LocationActivity.class);
                ActivityOptions transitionActivityOptions =
                        ActivityOptions.makeSceneTransitionAnimation(this, locationFab,
                                getString(R.string.transition_entry_to_location_activity));
                startActivityForResult(intent1, REQUEST_LOCATION, transitionActivityOptions.toBundle());
                break;
        }
    }

    private void tintImages() {
        int gray = ContextCompat.getColor(this, R.color.muted_gray);
        int accent = ContextCompat.getColor(this, R.color.accent);

        if (placeId == null) {
            locationFab.setColorFilter(gray);
        } else {
           locationFab.setColorFilter(accent);
        }

        if (imageUri == null) {
           photoFab.setColorFilter(gray, PorterDuff.Mode.SRC_IN);
        } else {
            photoFab.clearColorFilter();
        }
    }

    private void loadThumbnailFromUri() {
        if (imageUri != null) {
            photoFab.clearColorFilter();
            Glide.with(this)
                    .fromUri()
                    .asBitmap()
                    .animate(R.anim.glide_scale_in)
                    .load(imageUri)
                    .centerCrop()
                    .into(photoFab);
        }
    }
}
