package com.project.ratelimit.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/v1/ratelimit/")
public interface RatelimitService {

    @PostMapping(path="/limitcheck")
    public ResponseEntity<?> checklimit(@RequestHeader (value ="userid" , required = false) Map<String,String> userid,
                                        @RequestHeader(value = "edition", required = false) String edition) throws Exception;

}
