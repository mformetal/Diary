package mformetal.diary.util;

/**
 * Created by mbpeele on 5/7/16.
 */
public enum UriType {
    IMAGE,
    VIDEO,
    GIF,
    NONE;

    private static String[] IMAGE_TYPES = new String[] {"jpeg", "png"};
    private static String[] GIF_TYPES = new String[] {"gif"};

    public static UriType type(String content) {
        for (String imageType: IMAGE_TYPES) {
            if (content.contains(imageType)) {
                return IMAGE;
            }
        }

        for (String imageType: GIF_TYPES) {
            if (content.contains(imageType)) {
                return GIF;
            }
        }

        return VIDEO;
    }

}
