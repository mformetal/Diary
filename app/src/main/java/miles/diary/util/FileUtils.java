package miles.diary.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mbpeele on 1/20/16.
 */
public class FileUtils {

    private static final File FILE_DIR = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS);
    private static final String FOLDER_NAME = "Diary";
    private static final String IMAGE_TYPE = ".jpg";

    private FileUtils() {}

    public static File createPhotoFile() throws IOException {
        File dir = new File(FILE_DIR.toString() + "/" + FOLDER_NAME + "/");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return File.createTempFile(imageFileName, IMAGE_TYPE, dir);
    }

    public static Uri addFileToFolder(Context context, String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
        return contentUri;
    }

    public static UriType getUriType(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String contentResolverType = contentResolver.getType(uri);
        if (contentResolverType != null) {
            return UriType.type(contentResolverType);
        } else {
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            if (extension != null) {
                String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                return UriType.type(mime);
            } else {
                return UriType.NONE;
            }
        }
    }
}
