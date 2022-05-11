package hm.tv.beans;

import lombok.Data;

@Data
public class Match {
    private String playTime;
    private String game;
    private String name;
    private String link;
    private String teamFlag;
    private String guestTeamName;
    private String guestTeamLink;
    private String masterTeamName;
    private String masterTeamLink;
    private Integer id;
}
