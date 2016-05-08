package miles.diary.data.model.google;

import com.google.android.gms.location.places.Place;

/**
 * Created by mbpeele on 5/7/16.
 */
public class CopiedPlace {

    private String name;
    private String id;

    public CopiedPlace(Place place) {
        setName(place.getName().toString());
        setId(place.getId());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
