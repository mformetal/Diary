package miles.diary.util;

import android.support.v7.graphics.Palette;

/**
 * Created by mbpeele on 1/27/16.
 */
public class ColorUtils {

    private ColorUtils() {}

    public static Palette.Swatch mostPopulous(Palette palette) {
        Palette.Swatch mostPopulous = null;
        if (palette != null) {
            for (Palette.Swatch swatch : palette.getSwatches()) {
                if (mostPopulous == null || swatch.getPopulation() > mostPopulous.getPopulation()) {
                    mostPopulous = swatch;
                }
            }
        }
        return mostPopulous;
    }
}
