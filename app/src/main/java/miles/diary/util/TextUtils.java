package miles.diary.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import com.joanzapata.iconify.fonts.WeathericonsIcons;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import miles.diary.R;
import miles.diary.data.error.IllegalFontException;

/**
 * Created by mbpeele on 1/14/16.
 */
public final class TextUtils {

    public final static String LINE_SEPERATOR = System.lineSeparator();
    public final static String ELLIPSIS = "\u2026";
    public final static String TAB = "\u0009";

    private static HashMap<String, Typeface> fontMap;

    private TextUtils() {}

    private static void initializeFontMap(Context context) {
        fontMap = new HashMap<>();
        AssetManager assetManager = context.getAssets();
        try {
            String[] fontFileNames = assetManager.list("fonts");
            for (String fontFileName : fontFileNames) {
                Typeface typeface = Typeface.createFromAsset(assetManager, "fonts/" + fontFileName);
                fontMap.put(fontFileName, typeface);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Typeface getDefaultFont(Context context) {
        if (fontMap == null) {
            initializeFontMap(context);
        }
        String font = context.getResources().getString(R.string.default_font);
        Typeface typeface = fontMap.get(font);
        if (typeface == null) {
           throw new IllegalFontException(font);
        }
        return typeface;
    }

    public static Typeface getFont(Context context, String font) {
        if (fontMap == null) {
            initializeFontMap(context);
        }
        Typeface typeface = fontMap.get(font);
        if (typeface == null) {
            throw new IllegalFontException(font);
        }
        return typeface;
    }

    public static String repeat(int count, String toRepeat) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            stringBuilder.append(toRepeat);
        }

        return stringBuilder.toString();
    }

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormatter
                = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        return dateFormatter.format(date);
    }

    public static String formatTime(Date date) {
        SimpleDateFormat dateFormat
                = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return dateFormat.format(date);
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
