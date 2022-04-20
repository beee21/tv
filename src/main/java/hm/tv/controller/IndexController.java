package hm.tv.controller;

import hm.tv.beans.Item;
import hm.tv.beans.PlayItem;
import hm.tv.beans.User;
import hm.tv.repository.UserRepository;
import hm.tv.utils.IPUtil;
import hm.tv.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

@RestController
public class IndexController {

/*
    @GetMapping("redirect")
    public void redirect(@RequestParam String url, HttpServletResponse response) throws IOException {
        System.out.println(url);
        response.sendRedirect(url);
    }
*/

    @GetMapping("item")
    public List<PlayItem> item(@RequestParam String tid) {
        System.out.println(tid);
        return RequestUtil.home(tid);
    }

    @Autowired
    private UserRepository userRepository;

    @GetMapping("play")
    public Item play(@RequestParam String attr, HttpServletResponse response, HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        new Thread(() -> userRepository.save(new User().setUa(agent).setIp(IPUtil.getIpAddr(request)))).start();

        System.out.println(agent);
        System.out.println(attr);
        return RequestUtil.getPlayUrl(attr);
        /*if (Objects.nonNull(playUrl)) {
            try {
                if (agent.toLowerCase().contains("windows"))
                    response.sendRedirect(playUrl);
                else
                    response.sendRedirect("play.html?url=" + URLEncoder.encode(playUrl, "utf-8"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }*/

    }
}
