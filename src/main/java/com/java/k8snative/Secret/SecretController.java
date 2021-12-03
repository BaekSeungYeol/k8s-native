package com.java.k8snative.Secret;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@Slf4j
@Getter @Setter
public class SecretController {

    private final String NAME_PATH = "/etc/db/user.txt ";
    private final String PASS_PATH = "/etc/db/password.txt";

    @GetMapping("/user/1")
    public String user() {
        String nickname = findFilePropertyUsingPath(NAME_PATH);
        String pw = findFilePropertyUsingPath(PASS_PATH);
        //log.info("after = " + nickname + " " + pw);
        return "nickname: " + nickname + " " + "password:" + pw;
    }

    public String findFilePropertyUsingPath(String path) {
        StringBuilder sb = new StringBuilder();

        try(BufferedInputStream br = new BufferedInputStream(new FileInputStream(path))) {
            int ch = br.read();
          //  log.info("ch = " + ch);
            while (ch != -1) {
                sb.append((char) ch);
                ch = br.read();
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
        //log.info("sb = " + sb.toString());
        return sb.toString();
    }

}
