package hm.tv.utils;

import hm.tv.beans.Item;
import hm.tv.beans.PlayItem;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public class RequestUtil {
    final static String domain = "http://iptv807.com/";

    public static List<PlayItem> home(String tid) {

        Map<String, String> list = new HashMap<>();
        list.put("港台-1", "http://iptv807.com/?tid=gt&t=" + new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(new Date()));
        ;
        //   list.put("体育","http://iptv807.com/?tid=ty");
        //  list.put("卫视","http://iptv807.com/?tid=ws");


        Document document = document(domain + "?tid=" + tid);
        Elements aClass = document.select("a");
        List<PlayItem> items = new ArrayList<>();
        for (Element element : aClass) {
            String text = element.text();
            String attr = element.attr("href");
            if (attr.contains("act=play")) {
                // attr
                PlayItem playItem = new PlayItem();
                playItem.setName(text);

                // if(!m3u8Url.contains("pullhls3948069e")){
                //  playItem.setLoad(true);
                playItem.setUrl(attr);
                items.add(playItem);
            }

        }
        return items;
        // System.out.println(JSON.toJSONString(menus));
    }


   /* public static String getM3u8Url(String attr) {
        //播放链接
        String getPlayUrl = getPlayUrl(domain + attr);
        //播放实际链接
        Connection.Response response = null;
        try {
            response = Jsoup.connect(getPlayUrl).timeout(5_000).ignoreContentType(true).followRedirects(true).execute();
            System.out.println(response.body());
            getPlayUrl = response.url().toString();
            System.out.println( getPlayUrl);
            return getPlayUrl;
        } catch (Exception e) {
            // throw new RuntimeException(e);
        }

       return null;

    }*/

    public static Item getPlayUrl(String attr) {
        String url = domain + attr;
        Document document = document(url);
        Item item = new Item();
        Elements uls = document.select("ul");
        Element ul = uls.get(uls.size() - 1);
        if (Objects.nonNull(ul)) {
            Elements lis = ul.select("li");
            List<PlayItem> list = new ArrayList<>();
            List<PlayItem> playList = new ArrayList<>();
            boolean playing = false;
            for (int i = lis.size() - 1; i >= 0; i--) {
                Element li = lis.get(i);
                if (li.hasText() && li.childrenSize() > 0) {
                    String text = li.text();
                    if (Objects.nonNull(text) && text.contains(":")) {
                        PlayItem playItem = new PlayItem();
                        text = text.trim();
                        if (text.endsWith("回看")) {
                            text = text.substring(0, text.length() - 2);
                            playItem.setUrl("2");
                        } else if (text.endsWith("直播中")) {
                            text = text.substring(0, text.length() - 3);
                            playItem.setUrl("1");
                        } else {
                            String s = text.split(" ")[0];
                            LocalTime localTime = LocalTime.parse(s);
                            if (localTime.isBefore(LocalTime.now(ZoneId.of("UTC+8")))) {
                                if (playing) {
                                    playItem.setUrl("2");
                                } else {
                                    playItem.setUrl("1");
                                    playing = true;
                                }
                            }
                        }
                        playItem.setName(text);
                        if ("2".equals(playItem.getUrl()))
                            playList.add(playItem);
                        else
                            list.add(playItem);
                    }
                }
            }
            Collections.reverse(list);
            Collections.reverse(playList);
            list.addAll(playList);
            item.setProgram(list);
        }

        Element playURL = document.getElementById("playURL");
        Elements scripts = document.select("script");
        String str = null, key = null;

        String attr1 = playURL.selectFirst("option").attr("value");
        ClassPathResource resource = new ClassPathResource("playera.js");
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        try {
            InputStreamReader fileReader = new InputStreamReader(inputStream);
            engine.eval(fileReader);
            if (engine instanceof Invocable) {
                Invocable in = (Invocable) engine;
                for (Element script : scripts) {
                    String data = script.data();
                    if (data.contains("=bdecodeb")) {
                        if (data.contains("hken"))
                            continue;
                        String[] kvs = data.split(";");
                        for (String kv : kvs) {
                            int i = kv.indexOf("=");
                            if (i < 0)
                                continue;
                            String k = kv.substring(0, i);
                            String v = kv.substring(i + 1, kv.length() - 1);
                            if (v.contains("\""))
                                str = v.trim().replaceAll("\"", "");
                            else if (v.contains("bdecodeb")) {

                            } else {
                                v = kv.substring(i + 1, kv.length());
                                engine.eval("var key=" + v.trim());
                                key = engine.get("key").toString();
                            }
                        }
                        break;
                    } else {
                        if (data.contains("=")&& !data.contains("window")) {
                            engine.eval(data);
                        }
                    }

                }
                Object bdecodeb = in.invokeFunction("bdecodeb", str, key);
                String replace = bdecodeb.toString().replace("<script>", "").replace("</script>", "");
                engine.eval(replace);
                Object startPlayer = in.invokeFunction("startPlayer", attr1);
                String playUrl = startPlayer.toString().replace("player", "play");
                item.setUrl(playUrl);
                return item;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Document document(String url) {
        try {
            return Jsoup.connect(url).header("User-Agent", "Mozilla/5.0 (iPad; CPU OS 13_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/87.0.4280.77 Mobile/15E148 Safari/604.1").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        getPlayUrl("?act=play&token=e7e2be69c8aec6dcba34af21afacc929&tid=gt&id=1");
    }
}
