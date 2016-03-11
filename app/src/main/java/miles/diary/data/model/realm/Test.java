package miles.diary.data.model.realm;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by mbpeele on 3/11/16.
 */
@Accessors(fluent = true)
@Data
public class Test {

    private String name;
    private int errors;
}
