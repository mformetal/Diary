package miles.diary.ui.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import miles.diary.R;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.ui.widget.TypefaceEditText;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 1/16/16.
 */
public class NewEntryActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.activity_new_entry_root) CoordinatorLayout root;
    @Bind(R.id.fragment_entry_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_new_entry_body) TypefaceEditText bodyInput;
    @Bind(R.id.activity_new_entry_photo) CircleImageView photo;
    @Bind(R.id.activity_new_entry_location) CircleImageView location;

    public static final String RESULT_BODY = "body";
    public static final String RESULT_URI = "uri";
    public static final String RESULT_PLACE_NAME = "place";
    public static final String RESULT_PLACE_ID = "placeId";
    public static final String RESULT_TEMPERATURE = "temperature";
    private final static int REQUEST_LOCATION = 1;
    private final static int REQUEST_IMAGE = 2;

    private String placeName;
    private String placeId;
    private String temperature;
    private Uri imageUri;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        setActionBar(toolbar);

        tintImages();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    placeName = extras.getString(LocationActivity.RESULT_LOCATION_NAME);
                    placeId = extras.getString(LocationActivity.RESULT_LOCATION_ID);
                    temperature = extras.getString(LocationActivity.RESULT_TEMPERATURE);
                } else {
                    placeName = null;
                    placeId = null;
                    temperature = null;
                }

                tintImages();
                break;
            case REQUEST_IMAGE:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();

                    loadThumbnailFromUri();
                }

                tintImages();
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
                    result.putExtra(NewEntryActivity.RESULT_TEMPERATURE, temperature);
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
    @OnClick({R.id.activity_new_entry_photo, R.id.activity_new_entry_location})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_new_entry_photo:
                Intent intent = new Intent(this, UriActivity.class);
                intent.setData(imageUri);

                if (imageUri == null) {
                    startActivityForResult(intent, REQUEST_IMAGE);
                } else {
                    ActivityOptions transitionActivityOptions =
                            ActivityOptions.makeSceneTransitionAnimation(this, photo,
                                    getString(R.string.transition_image));
                    startActivityForResult(intent, REQUEST_IMAGE, transitionActivityOptions.toBundle());
                }
                break;
            case R.id.activity_new_entry_location:
                Intent intent1 = new Intent(this, LocationActivity.class);
                intent1.putExtra(LocationActivity.RESULT_LOCATION_NAME, placeName);
                intent1.putExtra(LocationActivity.RESULT_LOCATION_ID, placeId);
                intent1.putExtra(LocationActivity.RESULT_TEMPERATURE, temperature);
                ActivityOptions transitionActivityOptions =
                        ActivityOptions.makeSceneTransitionAnimation(this, location,
                                getString(R.string.transition_location));
                startActivityForResult(intent1, REQUEST_LOCATION, transitionActivityOptions.toBundle());
                break;
        }
    }

    private void loadThumbnailFromUri() {
        if (imageUri != null) {
            photo.clearColorFilter();
            Glide.with(this)
                    .fromUri()
                    .animate(R.anim.glide_scale_in)
                    .load(imageUri)
                    .centerCrop()
                    .into(photo);
        }
    }

    private void tintImages() {
        int unactivated = ContextCompat.getColor(this, R.color.muted_gray);
        int activated = ContextCompat.getColor(this, R.color.accent);

        if (imageUri == null) {
            photo.setColorFilter(unactivated);
        }

        if (placeName == null) {
            location.setColorFilter(unactivated);
        } else {
            location.setColorFilter(activated);
        }
    }
}
