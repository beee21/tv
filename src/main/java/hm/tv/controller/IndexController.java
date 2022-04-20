package hm.tv.controller;

import hm.tv.beans.PlayItem;
import hm.tv.utils.RequestUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

@RestController
public class IndexController {

    @GetMapping("redirect")
    public void redirect(@RequestParam String url, HttpServletResponse response) throws IOException {
        System.out.println(url);
        response.sendRedirect(url);
    }

    @GetMapping("item")
    public List<PlayItem> item(@RequestParam String tid) {
        System.out.println(tid);
        return RequestUtil.home(tid);
    }

    @GetMapping("play")
    public void play(@RequestParam String attr, HttpServletResponse response) {
        System.out.println(attr);
        String playUrl = RequestUtil.getPlayUrl(attr);
        if(Objects.nonNull(playUrl)){
            try {
                response.sendRedirect("play.html?url="+ URLEncoder.encode(playUrl,"utf-8"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
