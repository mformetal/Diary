package miles.diary.ui.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import icepick.State;
import miles.diary.DiaryApplication;
import miles.diary.R;
import miles.diary.data.adapter.GalleryAdapter;
import miles.diary.ui.SpacingDecoration;
import miles.diary.ui.fragment.ConfirmationDialog;
import miles.diary.ui.fragment.DismissingDialogFragment;
import miles.diary.util.FileUtils;

/**
 * Created by mbpeele on 1/29/16.
 */
public class GalleryActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int RESULT_CAMERA = 1;
    public final static int RESULT_SELECT = 2;
    private final static int PERMISSION_IMAGE = 3;
    private static final int LOADER_ID = 4;

    @Bind(R.id.activity_gallery_toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_gallery_recycler)
    RecyclerView recyclerView;

    private GalleryAdapter adapter;
    @State File cameraFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            String[] camera = new String[] {Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(camera)) {
                ActivityCompat.requestPermissions(this, camera, PERMISSION_IMAGE);
            } else {
                setupGallery();
            }
        } else {
            ConfirmationDialog dialog =
                    ConfirmationDialog.newInstance(getString(R.string.no_camera_error));
            dialog.setDismissListener(new DismissingDialogFragment.OnDismissListener() {
                @Override
                public void onDismiss(DismissingDialogFragment fragment) {
                    finish();
                }
            });
            dialog.show(getFragmentManager(), CONFIRMATION_DIALOG);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri;
            switch (requestCode) {
                case RESULT_CAMERA:
                    uri = FileUtils.addFileToFolder(this, cameraFile.getAbsolutePath());
                    Intent intent = new Intent(this, UriActivity.class);
                    intent.setData(uri);
                    startActivityForResult(intent, RESULT_SELECT);
                    break;
                case RESULT_SELECT:
                    uri = data.getData();
                    Intent intent1 = new Intent();
                    intent1.setData(uri);
                    setResult(RESULT_OK, intent1);
                    finish();
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_gallery_camera:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        cameraFile = FileUtils.createPhotoFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (cameraFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                        startActivityForResult(takePictureIntent, RESULT_CAMERA);
                    }
                }
                break;
            case android.R.id.home:
                finishAfterTransition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_IMAGE:
                if (!permissionsGranted(grantResults)) {
                    finish();
                } else {
                    setupGallery();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

        Uri queryUri = MediaStore.Files.getContentUri("external");

        return new CursorLoader(this,
                queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setCursor(null);
    }

    private void setupGallery() {
        getLoaderManager().initLoader(LOADER_ID, null, this);

        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, 3,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new SpacingDecoration(20));
        adapter = new GalleryAdapter(this);
        recyclerView.setAdapter(adapter);
    }
}
