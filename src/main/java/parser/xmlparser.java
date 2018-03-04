package parser;

import lombok.NonNull;
import org.json.JSONObject;
import org.w3c.dom.Document;

/**
 * Created by Ameer on 3/3/18.
 */
public interface xmlparser {

    public JSONObject convert(@NonNull final Document document);

}
