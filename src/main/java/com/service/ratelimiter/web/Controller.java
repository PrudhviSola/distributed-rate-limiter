package com.service.ratelimiter.web;

import io.micrometer.core.annotation.Timed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Controller {

    @GetMapping("/hello")
    @Timed(value = "api.hello", histogram = true)
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("OK");
    }
}
