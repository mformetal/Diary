package miles.diary.data.model.google;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mbpeele on 3/5/16.
 */
public class LikelyPlace {

    private final float likelihood;
    private final PlaceInfo placeInfo;

    public LikelyPlace(PlaceLikelihood placeLikelihood) {
        likelihood = placeLikelihood.getLikelihood();
        placeInfo = new PlaceInfo(placeLikelihood.getPlace());
    }

    public float getLikelihood() {
        return likelihood;
    }

    public PlaceInfo getPlaceInfo() {
        return placeInfo;
    }

    public static List<LikelyPlace> fromBuffer(PlaceLikelihoodBuffer buffer) {
        List<LikelyPlace> list = new ArrayList<>();
        for (PlaceLikelihood placeLikelihood : buffer) {
            list.add(new LikelyPlace(placeLikelihood));
        }

        buffer.release();
        return list;
    }
}
