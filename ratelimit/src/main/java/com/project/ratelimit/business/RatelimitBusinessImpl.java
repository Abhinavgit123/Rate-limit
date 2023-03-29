package com.project.ratelimit.business;

import com.project.ratelimit.exception.RatelimitException;
import com.project.ratelimit.service.RatelimitService;
import com.project.ratelimit.serviceImpl.RatelimitServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@Data
public class RatelimitBusinessImpl {

    private Map<String, Map<Long, AtomicLong>> timestamp = new ConcurrentHashMap<>();

    public boolean limitcheck(String username,Long NUMBER_OF_TIME_WINDOWS,Long TIME_LIMIT,Long NO_OF_REQ_ALLOWED) throws RatelimitException {
        long currentTime = Instant.now().getEpochSecond() / NUMBER_OF_TIME_WINDOWS;
        Map<Long, AtomicLong> userRecords;

        // Case 1: A new user
        if (!timestamp.containsKey(username)) {
            userRecords = new HashMap<>();
            userRecords.put(currentTime, new AtomicLong(1L));
            timestamp.put(username, userRecords);
            log.debug("User record is created and rate-limit accepted");
            return true;
        }

        // Case 2: User already exists, Time Window for the user may or may not exist in the map
        else {
            userRecords = timestamp.get(username);
            return isRatelimitAllowed(username, currentTime, userRecords,NUMBER_OF_TIME_WINDOWS,TIME_LIMIT,NO_OF_REQ_ALLOWED);
        }
    }

    public boolean isRatelimitAllowed(String username, long currentTimeWindow, Map<Long, AtomicLong> userRecords,Long NUMBER_OF_TIME_WINDOWS,Long TIME_LIMIT,Long NO_OF_REQ_ALLOWED) throws RatelimitException {
        Long timeWindowCount = deletePrevEntries(username, currentTimeWindow, userRecords,NUMBER_OF_TIME_WINDOWS,TIME_LIMIT);

        if (timeWindowCount < NO_OF_REQ_ALLOWED) {

            //Manage newly defined time periods.
            Long newCount = userRecords.getOrDefault(currentTimeWindow, new AtomicLong(0)).longValue() + 1;
            userRecords.put(currentTimeWindow, new AtomicLong(newCount));
            log.debug("Request count for User " + username + " within limit. Count:"+timeWindowCount);
            return true;
        }
             log.debug("Request limit exceeded for User " + username + ". Count:"+timeWindowCount);
            throw new RatelimitException("Request limit exceeded for User ID " + username +".Please try again in sometime.");
    }

    // Delete the outdated entries associated with the user and provide the current total number of requests made by the user.
    public long deletePrevEntries(String username, long currentTimeWindow, Map<Long, AtomicLong> userRecords,Long NUMBER_OF_TIME_WINDOWS,Long TIME_LIMIT)
    {
        List<Long> oldEntriesToBeDeleted=new ArrayList<>();
        long overallCount=0L;
        for (Long loggedtime : userRecords.keySet()) {

            //Identify entries that fall outside the valid time window within the time limit and mark them for deletion as outdated.
            if ((currentTimeWindow - loggedtime) >= TIME_LIMIT / NUMBER_OF_TIME_WINDOWS) {
                oldEntriesToBeDeleted.add(loggedtime);
            } else {
                overallCount += userRecords.get(loggedtime).longValue();
            }
        }
        oldEntriesToBeDeleted.forEach(userRecords.keySet()::remove);
        return overallCount;
    }

}
