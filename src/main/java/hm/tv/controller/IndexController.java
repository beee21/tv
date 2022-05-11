package hm.tv.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hm.tv.beans.*;
import hm.tv.utils.RequestUtil;
import org.jsoup.Jsoup;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@RestController
public class IndexController {

    @GetMapping("item")
    public List<PlayItem> item(@RequestParam String tid) {
        System.out.println(tid);
        return RequestUtil.home(tid);
    }

    @GetMapping("play")
    public Item play(@RequestParam String attr, HttpServletResponse response, HttpServletRequest request) {
        return RequestUtil.getPlayUrl(attr);
    }

    @GetMapping("sport")
    public List<Sport> sport(@RequestParam(required = false, defaultValue = "") String game) {
        List<Sport> sports = new ArrayList<>();
        try {
            String text = Jsoup.connect("https://70zhibo.com/api/web/indexMatchList?game=" + game).ignoreContentType(true).get().text();
            JSONArray array = JSON.parseArray(text);
            for (int i = 0; i < array.size(); i++) {
                Sport sport = new Sport();
                JSONObject jsonObject = array.getJSONObject(i);
                String dateStr = jsonObject.getString("dateStr");
                sport.setDateStr(dateStr);
                List<Match> list = new ArrayList<>();
                JSONArray matches = jsonObject.getJSONArray("matches");
                for (int x = 0; x < matches.size(); x++) {
                    JSONObject object = matches.getJSONObject(x);
                    Match match = JSONObject.toJavaObject(object, Match.class);
                    JSONArray lives = object.getJSONArray("lives");
                    if (lives.size() > 0) {
                        String link = lives.getJSONObject(0).getString("link");
                        match.setLink(link);
                    }
                    /*match.setGuestTeamLink(download(match.getGuestTeamLink()));
                    match.setMasterTeamLink(download(match.getMasterTeamLink()));*/
                    list.add(match);
                }
                sport.setMatches(list);
                sports.add(sport);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println(JSON.toJSONString(sports));
        return sports;
    }

    @GetMapping("/link/{id}")
    public String link(@PathVariable String id) {
        try {
            String text = Jsoup.connect("http://70zhibo.com/api/web/match/" + id).ignoreContentType(true).get().text();
            JSONObject object = JSON.parseObject(text);
            JSONArray lives = object.getJSONArray("lives");
            if (lives.size() > 0) {
                return lives.getJSONObject(0).getString("link");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*private String download(String fileUrl) throws Exception {
        if(Objects.isNull(fileUrl))
            return null;
        String[] split = fileUrl.split("/");
        String filePath = "home\\" + split[split.length - 1];
        File file = new File(filePath);
        if (!file.exists()) {
            new FileOutputStream(filePath).getChannel()
                    .transferFrom(Channels.newChannel(new URL("https://70zhibo.com"+fileUrl).openStream()), 0, Long.MAX_VALUE);
        }
        if (file.exists()) {
            byte[] b = Files.readAllBytes(Paths.get(filePath));
            String base64 = Base64.getEncoder().encodeToString(b);
            return base64;
        }
        return null;
    }*/
}
