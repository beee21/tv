package hm.tv.beans;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Lrc {
    public  String lineLyric;
    public  float time;

}
