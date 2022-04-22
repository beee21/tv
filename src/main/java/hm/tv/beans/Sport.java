package hm.tv.beans;

import lombok.Data;

import java.util.List;

@Data
public class Sport {
    private String dateStr;
    private List<Match> matches;
}
