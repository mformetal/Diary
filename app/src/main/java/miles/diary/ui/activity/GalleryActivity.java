package miles.diary.ui.activity;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.Toolbar;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.data.adapter.GalleryAdapter;
import miles.diary.ui.SpacingDecoration;

/**
 * Created by mbpeele on 1/29/16.
 */
public class GalleryActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int REQUEST_GALLERY = 1;
    private final static int REQUEST_CAMERA = 2;
    private final static int REQUEST_VIDEO = 3;
    private final static int REQUESET_IMAGE_PERMISSION = 4;
    private static final int LOADER_ID = 1;

    @Bind(R.id.activity_gallery_toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_gallery_recycler)
    RecyclerView recyclerView;

    private GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            String[] camera = new String[] {Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(camera)) {
                ActivityCompat.requestPermissions(this, camera, REQUESET_IMAGE_PERMISSION);
            } else {
                getLoaderManager().initLoader(LOADER_ID, null, this);

                RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, 3,
                        LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.addItemDecoration(new SpacingDecoration(20));
                adapter = new GalleryAdapter(this);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_uri, menu);
        return super.onCreateOptionsMenu(menu);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[] {MediaStore.Images.Thumbnails.DATA};
        return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setCursor(null);
    }
}
