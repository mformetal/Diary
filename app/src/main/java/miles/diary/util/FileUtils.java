package miles.diary.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mbpeele on 1/20/16.
 */
public class FileUtils {

    private final static String MIME_IMAGE = "image/jpeg";
    private final static String MIME_VIDEO = "video/mp4";

    private FileUtils() {}

    public static File createPhotoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    public static Uri addFileToGallery(Context context, String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
        return contentUri;
    }

    public static boolean isImageUri(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String contentResolverType = contentResolver.getType(uri);
        if (contentResolverType != null) {
            return contentResolverType.equals(MIME_IMAGE);
        } else {
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            if (extension != null) {
                String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                return type.equals(MIME_IMAGE);
            }
        }

        return false;
    }

    public static Observable<byte[]> saveBitmap(final Context context, final Bitmap bitmap,
                                                final String name) {
        return Observable.create(new Observable.OnSubscribe<byte[]>() {
            @Override
            public void call(Subscriber<? super byte[]> subscriber) {
                subscriber.onStart();

                try {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bytes = stream.toByteArray();

                    OutputStream outputStream
                            = context.openFileOutput(name, Context.MODE_PRIVATE);
                    outputStream.write(bytes);

                    subscriber.onNext(bytes);
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Bitmap> getBitmap(final Context context, final String name,
                                               final int reqWidth, final int reqHeight) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                subscriber.onStart();

                try {
                    InputStream test = context.openFileInput(name);
                    Bitmap bitmap = BitmapFactory.decodeStream(test);

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    float scaleWidth = ((float) reqWidth) / width;
                    float scaleHeight = ((float) reqHeight) / height;

                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    Bitmap resizedBitmap = Bitmap.createBitmap(
                            bitmap, 0, 0, width, height, matrix, false);
                    bitmap.recycle();

                    subscriber.onNext(resizedBitmap);
                } catch (IOException e) {
                    subscriber.onError(e);
                }

                subscriber.onCompleted();
            }
        });
    }

    public static Observable<byte[]> getBitmapBytes(final Context context, final String name) {
        return Observable.create(new Observable.OnSubscribe<byte[]>() {
            @Override
            public void call(Subscriber<? super byte[]> subscriber) {
                subscriber.onStart();

                try {
                    InputStream inputStream = context.openFileInput(name);
                    subscriber.onNext(ByteStreams.toByteArray(inputStream));
                } catch (IOException e) {
                    subscriber.onError(e);
                }

                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static void deleteBitmapFile(Context context, String name) {
        context.deleteFile(name);
    }

    public static boolean isFileAvailable(Context context, String name) {
        File file = context.getFileStreamPath(name);
        return !(file == null || !file.exists());
    }
}
