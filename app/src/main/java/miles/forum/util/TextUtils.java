package miles.forum.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mbpeele on 1/14/16.
 */
public final class TextUtils {

    private static HashMap<String, Typeface> mFontMap;
    private final static String DEFAULT_FONT_NAME = "Roboto-Medium.ttf";

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
        Typeface typeface = mFontMap.get(DEFAULT_FONT_NAME);
        if (typeface == null) {
            throw new IllegalArgumentException(
                    "Font name must match file name in assets/fonts/ directory: " + DEFAULT_FONT_NAME);
        }
        return typeface;
    }
}
