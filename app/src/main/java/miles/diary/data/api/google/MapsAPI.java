package miles.diary.data.api.google;

import miles.diary.data.model.google.apiresponse.PlaceResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by mbpeele on 3/5/16.
 */
interface MapsAPI {

    @GET("maps/api/place/nearbysearch/json")
    Observable<PlaceResponse> searchNearby(@Query("location") String location,
                                           @Query("radius") Float radius,
                                           @Query("key") String key);
}
