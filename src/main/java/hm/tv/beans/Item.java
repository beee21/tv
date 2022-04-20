package hm.tv.beans;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
@Data
@Accessors(chain = true)
public class Item {
    private String url;
    private List<String> program;
}
