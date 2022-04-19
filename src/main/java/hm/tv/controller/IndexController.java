package hm.tv.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class IndexController {

    @GetMapping("redirect")
    public void redirect(@RequestParam String url, HttpServletResponse response) throws IOException {
        System.out.println(url);
        response.sendRedirect(url);
    }
}
