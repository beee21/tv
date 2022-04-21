package hm.tv.beans;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PlayItem {
    public  String name;
    public  String url;

}
