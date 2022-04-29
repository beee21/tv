package hm.tv.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hm.tv.beans.Lrc;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


@RestController
public class MiguController {
    @GetMapping("/search")
    public JSONObject search(@RequestParam String keyWord, @RequestParam(required = false, defaultValue = "1") int pageNo) {
        JSONObject jsonObject = new JSONObject();
        JSONObject res = new JSONObject();
        String url = "https://pd.musicapp.migu.cn/MIGUM2.0/v1.0/content/search_all.do?ua=Android_migu&version=5.0.1&text=" + keyWord
                + "&pageNo=" + pageNo
                + "&pageSize=15" +
                "&searchSwitch={\"song\":1,\"album\":0,\"singer\":0,\"tagSong\":0,\"mvSong\":0,\"songlist\":0,\"bestShow\":1}";
        try {
            final String text = Jsoup.connect(url).ignoreContentType(true).get().text();
            jsonObject = JSONObject.parseObject(text);
            if (jsonObject.getString("code").equals("000000")) {
                final JSONObject songResultData = jsonObject.getJSONObject("songResultData");
                final String totalCount = songResultData.getString("totalCount");
                JSONObject data = new JSONObject();
                data.put("total", totalCount);
                JSONArray list = new JSONArray();
                final JSONArray result = songResultData.getJSONArray("result");
                for (int i = 0; i < result.size(); i++) {
                    final JSONObject object = result.getJSONObject(i);
                    final String name = object.getString("name");
                    final String lyricUrl = object.getString("lyricUrl");
                    final String singer = object.getJSONArray("singers").getJSONObject(0).getString("name");
                    System.out.println(singer + " -->> " + name);
                    final JSONArray newRateFormats = object.getJSONArray("newRateFormats");
                    final JSONObject o = newRateFormats.getJSONObject(newRateFormats.size() - 1);
                    String androidUrl = o.getString("androidUrl");
                    //String androidUrl = o.getString("iosUrl");
                    if (androidUrl == null || androidUrl.equals("null")) {
                        androidUrl = o.getString("url");
                    }
                    String songUrl = androidUrl.replace("ftp://218.200.160.122:21", "http://freetyst.nf.migu.cn");
                    System.out.println(songUrl);
                    JSONObject item = new JSONObject();
                    item.put("artist", singer);
                    item.put("name", name);
                    item.put("rid", songUrl);
                    item.put("lyricUrl", lyricUrl);
                    list.add(item);
                }
                data.put("list", list);
                res.put("data", data);
                res.put("code", 200);
            }
        } catch (Exception e) {
            res.put("code", 500);
            e.printStackTrace();
        }
        return res;
    }

    @GetMapping("/downLoad/{songUrl}")
    public ResponseEntity<Resource> downLoad(@PathVariable String songUrl) throws Exception {
        String suffix = songUrl.substring(songUrl.lastIndexOf("."),songUrl.length());
        String fileName="歌曲名"+suffix;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(fileName,"utf-8"))
                .body(new UrlResource(songUrl));
    }
    @GetMapping("/lrc/{lrcUrl}")
    public List<Lrc> lrc(@PathVariable String lrcUrl) {
        List<Lrc> lrclist=new ArrayList<>();
        String read;
        try {
            URL url = new URL(lrcUrl);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            urlCon.setConnectTimeout(5000);
            urlCon.setReadTimeout(5000);
            BufferedReader br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            while ((read = br.readLine()) != null) {
                String[] split = read.split("]");
                String time = split[0].replace("[","");
                float s= Float.parseFloat(time.split(":")[1]);
                int m=Integer.parseInt(time.split(":")[0])*60;
                float ms=m+s;
                System.out.println(time+"   -->>  "+ms);
                lrclist.add(new Lrc().setTime(ms).setLineLyric(split[1]));
            }
            br.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return lrclist;
    }


   public static void main(String[] args) {
        MiguController controller = new MiguController();
        controller.search(URLEncoder.encode("welcome to new york"), 1);
    }

}
