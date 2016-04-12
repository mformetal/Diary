package miles.diary.data.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by mbpeele on 3/27/16.
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
public class Profile extends RealmObject implements IRealmInterface {

    public final static String KEY = "name";

    @PrimaryKey
    private String name;
    private String uri;

    
}
