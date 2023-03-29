package com.project.ratelimit.serviceImpl;

import com.project.ratelimit.business.RatelimitBusinessImpl;
import com.project.ratelimit.exception.RatelimitException;
import com.project.ratelimit.service.RatelimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin
public class RatelimitServiceImpl implements RatelimitService {

    @Value("${userId.name}")
    public String USER_ID;

    @Value("${time.window.count}")
    public long NUMBER_OF_TIME_WINDOWS;

    @Value("${request.time.limit}")
    public long TIME_LIMIT;

    @Value("${request.counter}")
    public long NO_OF_REQ_ALLOWED;

    @Value("${time.window.count.community}")
    public long NUMBER_OF_TIME_WINDOWS_COMMUNITY;

    @Value("${request.time.limit.community}")
    public long TIME_LIMIT_COMMUNITY;

    @Value("${request.counter.community}")
    public long NO_OF_REQ_ALLOWED_COMMUNITY;

    @Value("${time.window.count.enterprise}")
    public long NUMBER_OF_TIME_WINDOWS_ENTERPRISE;

    @Value("${request.time.limit.enterprise}")
    public long TIME_LIMIT_ENTERPRISE;

    @Value("${request.counter.enterprise}")
    public long NO_OF_REQ_ALLOWED_ENTERPRISE;



    @Autowired
    private RatelimitBusinessImpl ratelimitbusinessimpl;

    @Override
    public ResponseEntity<?> checklimit(@RequestHeader(value ="userid") Map<String,String> userid,
                                        @RequestHeader(value = "edition", required = false) String edition) throws RatelimitException{
        try {
            if (userid == null || !userid.containsKey(USER_ID)) {
                throw new NullPointerException("Please provide a valid UserID");
            }

            boolean isLimitExceeded;
            if (Objects.equals(edition, "Enterprise")) {
                isLimitExceeded = ratelimitbusinessimpl.limitcheck(userid.get(USER_ID), NUMBER_OF_TIME_WINDOWS_ENTERPRISE, TIME_LIMIT_ENTERPRISE, NO_OF_REQ_ALLOWED_ENTERPRISE);
            } else if (Objects.equals(edition, "Community")) {
                isLimitExceeded = ratelimitbusinessimpl.limitcheck(userid.get(USER_ID), NUMBER_OF_TIME_WINDOWS_COMMUNITY, TIME_LIMIT_COMMUNITY, NO_OF_REQ_ALLOWED_COMMUNITY);
            } else {
                isLimitExceeded = ratelimitbusinessimpl.limitcheck(userid.get(USER_ID), NUMBER_OF_TIME_WINDOWS, TIME_LIMIT, NO_OF_REQ_ALLOWED);
            }

            return ResponseEntity.ok(isLimitExceeded);

        } catch (RatelimitException ex) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.getMessage());
        }
        catch (NullPointerException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
