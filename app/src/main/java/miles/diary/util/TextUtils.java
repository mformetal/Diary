package miles.diary.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.InputType;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import miles.diary.R;

/**
 * Created by mbpeele on 1/14/16.
 */
public final class TextUtils {

    private TextUtils() {}

    private static HashMap<String, Typeface> mFontMap;

    private static void initializeFontMap(Context context) {
        mFontMap = new HashMap<>();
        AssetManager assetManager = context.getAssets();
        try {
            String[] fontFileNames = assetManager.list("fonts");
            for (String fontFileName : fontFileNames) {
                Typeface typeface = Typeface.createFromAsset(assetManager, "fonts/" + fontFileName);
                mFontMap.put(fontFileName, typeface);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Typeface getDefaultFont(Context context) {
        if (mFontMap == null) {
            initializeFontMap(context);
        }
        String font = context.getResources().getString(R.string.default_font);
        Typeface typeface = mFontMap.get(font);
        if (typeface == null) {
            throw new IllegalArgumentException(
                    "Font name must match file name in assets/fonts/ directory: " + font);
        }
        return typeface;
    }

    public static Typeface getFont(Context context, String font) {
        if (mFontMap == null) {
            initializeFontMap(context);
        }
        Typeface typeface = mFontMap.get(font);
        if (typeface == null) {
            throw new IllegalArgumentException(
                    "Font name must match file name in assets/fonts/ directory: " + font);
        }
        return typeface;
    }

    public static String randomString(int len) {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
}
