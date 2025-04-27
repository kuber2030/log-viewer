package com.example.logviewer.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 16:28
 */
@SpringBootTest
class SysPropsTest {

    @Autowired
    private SysProps sysProps;
    @Test
    void getIncludesPattern() {
        System.out.println(sysProps);
        String patternStr = sysProps.getIncludesPattern().get(0);
//        "space-\\w+-[test|prod]-\\w+-\\w+\\.log"
        Pattern compile = Pattern.compile(patternStr);
        Matcher matcher = compile.matcher("space-api-test-794b8cdd56-gz499.log");
        System.out.println(matcher.matches());
        Assertions.assertTrue(matcher.matches());
    }
}