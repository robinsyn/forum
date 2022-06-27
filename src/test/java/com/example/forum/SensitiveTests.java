package com.example.forum;

import com.example.forum.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitive(){
        String text="这里可以赌博，可以嫖娼，可以吸毒，可以玩耍。";
        /*text = sensitiveFilter.filter(text);
        System.out.println(text);
*/
        text="这里可以※赌★博★，可以★嫖★娼★，可以★吸&毒，可以玩耍。";
        text=sensitiveFilter.filter(text);
        System.out.println(text);

    }
}
