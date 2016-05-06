package miles.diary.data.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mbpeele on 3/27/16.
 */
public class Profile extends RealmObject implements RealmModel {

    @PrimaryKey
    private String name;
    private String uri;


}
