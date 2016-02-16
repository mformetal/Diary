package miles.diary.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.InputType;
import android.widget.TextView;

import com.joanzapata.iconify.fonts.WeathericonsIcons;
import com.joanzapata.iconify.fonts.WeathericonsModule;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import miles.diary.R;
import miles.diary.data.model.weather.Weather;

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

    public static void adjustTextSize(Paint textPaint, String text, int height) {
        textPaint.setTextSize(100);
        textPaint.setTextScaleX(1.0f);
        Rect bounds = new Rect();
        // ask the paint for the bounding rect if it were to draw this
        // text
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        // get the height that would have been produced
        int h = bounds.height();
        // make the text text up 70% of the height
        float target = (float) height * .2f;
        // figure out what textSize setting would create that height
        // of text
        float size = ((target/h) * 100f);
        // and set it into the paint
        textPaint.setTextSize(size);
    }

    public static  void adjustTextScale(Paint textPaint, String text, float width, int paddingLeft,
                                        int paddingRight) {
        // do calculation with scale of 1.0 (no scale)
        textPaint.setTextScaleX(1.0f);
        Rect bounds = new Rect();
        // ask the paint for the bounding rect if it were to draw this
        // text.
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        // determine the width
        int w = bounds.width();
        // calculate the baseline to use so that the
        // entire text is visible including the descenders
        // determine how much to scale the width to fit the view
        float xscale = ((float) (width - paddingLeft - paddingRight)) / w;
        // set the scale for the text paint
        textPaint.setTextScaleX(xscale * .6f);
    }

    public static String getWeatherIcon(String iconFromAPI) {
        String icon;
        switch (iconFromAPI) {
            case "01d":
                icon = WeathericonsIcons.wi_forecast_io_clear_day.key();
                break;
            case "01n":
                icon = WeathericonsIcons.wi_forecast_io_clear_night.key();
                break;
            case "02d":
                icon = WeathericonsIcons.wi_forecast_io_partly_cloudy_day.key();
                break;
            case "02n":
                icon = WeathericonsIcons.wi_forecast_io_partly_cloudy_night.key();
                break;
            case "03n":
            case "03d":
            case "04d":
            case "04n":
                icon = WeathericonsIcons.wi_cloudy.key();
                break;
            case "09d":
            case "09n":
                icon = WeathericonsIcons.wi_rain.key();
                break;
            case "10d":
                icon = WeathericonsIcons.wi_day_rain.key();
                break;
            case "10n":
                icon = WeathericonsIcons.wi_night_rain.key();
                break;
            case "11d":
            case "11n":
                icon = WeathericonsIcons.wi_thunderstorm.key();
                break;
            case "13d":
            case "13n":
                icon = WeathericonsIcons.wi_snow.key();
                break;
            case "50d":
            case "50n":
                icon = WeathericonsIcons.wi_cloudy_gusts.key();
                break;
            default:
                icon = WeathericonsIcons.wi_day_sunny.key();
                break;
        }
        return "{" + icon + "}";
    }
}
