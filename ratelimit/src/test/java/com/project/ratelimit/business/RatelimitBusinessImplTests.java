package com.project.ratelimit.business;

import com.project.ratelimit.business.RatelimitBusinessImpl;
import com.project.ratelimit.exception.RatelimitException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

//Used TestNG for test cases

@SpringBootTest
public class RatelimitBusinessImplTests {

    @Test
    public void checkForNewUser() throws RatelimitException {
        RatelimitBusinessImpl rateLimiter = new RatelimitBusinessImpl();
        assertThat(rateLimiter.limitcheck("Messi", 6L,60L,10L)).isEqualTo(true);
    }


    @Test
    public void existingUsers() throws Exception {
        RatelimitBusinessImpl rateLimiter = new RatelimitBusinessImpl();
        for (int i=0;i<10;i++)
            assertThat(rateLimiter.limitcheck("Neymar",6L,60L,10L)).isEqualTo(true);
    }

    @Test
    public void existingUsersEnterprise() throws Exception {
        RatelimitBusinessImpl rateLimiter = new RatelimitBusinessImpl();
        for (int i=0;i<600;i++)
            assertThat(rateLimiter.limitcheck("cristiano",600L,3660L,600L)).isEqualTo(true);
    }



    @Test(expectedExceptions = RatelimitException.class)
    public void RateLimitExceededScenario() throws Exception {
        RatelimitBusinessImpl rateLimiter = new RatelimitBusinessImpl();
        for (int i=0;i<10;i++) {
            assertThat(rateLimiter.limitcheck("Pedri", 6L, 30L, 10L)).isEqualTo(true);
        }
        rateLimiter.limitcheck("Pedri",6L,30L,10L);
    }


    @Test(expectedExceptions = RatelimitException.class)
    public void ifRatelimitExceededForUserOthersNotImpacted() throws Exception {
        RatelimitBusinessImpl rateLimiter = new RatelimitBusinessImpl();
        for (int i=0;i<10;i++)
            assertThat(rateLimiter.limitcheck("Barca",6L,60L,10L)).isEqualTo(true);

        rateLimiter.limitcheck("Barca",6L,30L,10L);
        assertThat(rateLimiter.limitcheck("Ronaldo",6L,60L,10L)).isEqualTo(true);
    }

}
