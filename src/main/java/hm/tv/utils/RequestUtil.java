package hm.tv.utils;

import hm.tv.beans.Menu;
import hm.tv.beans.PlayItem;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RequestUtil {
    static List<Menu> menus = new ArrayList<>();
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


    public static String getM3u8Url(String attr) {
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

    }

    public static String getPlayUrl(String attr) {
        String url=domain + attr;
        Document document = document(url);
        Element playURL = document.getElementById("playURL");
        Elements scripts = document.select("script");
        String str = null, key = null;

        String attr1 = playURL.selectFirst("option").attr("value");
        String js = "D:\\dev\\project\\music\\src\\main\\resources\\playera.js";
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        try {
            FileReader fileReader = new FileReader(js);
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
                        if (data.contains("=")) {
                            engine.eval(data);
                        }
                    }

                }
                Object bdecodeb = in.invokeFunction("bdecodeb", str, key);
                String replace = bdecodeb.toString().replace("<script>", "").replace("</script>", "");
                engine.eval(replace);
                Object startPlayer = in.invokeFunction("startPlayer", attr1);
                String m3u8Url = startPlayer.toString().replace("player", "play");
                return m3u8Url;
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
}
