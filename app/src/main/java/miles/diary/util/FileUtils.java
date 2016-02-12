package miles.diary.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mbpeele on 1/20/16.
 */
public class FileUtils {

    private final static String MIME_IMAGE = "image/jpeg";
    private final static String MIME_VIDEO = "video/mp4";
    private final static String AUTH_BITMAP_FILENAME = "miles:diary:auth:bitmap";

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

    public static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
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

    public static Bitmap getAuthBitmap(Context context) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inMutable = true;
            options.inPreferQualityOverSpeed = true;

            InputStream test = context.openFileInput(AUTH_BITMAP_FILENAME);
            bitmap = BitmapFactory.decodeStream(test, null, options);
            test.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static void deleteAuthBitmap(Context context) {
        context.deleteFile(AUTH_BITMAP_FILENAME);
    }
}
