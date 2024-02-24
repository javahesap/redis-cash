package com.haydikodlayalim.redisapp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haydikodlayalim.redisapp.service.RedisCacheService;
import com.haydikodlayalim.redisapp.service.UserRateLimitService;

@RestController
@RequestMapping("/mest")
public class RediscacherequestController {

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private UserRateLimitService userRateLimitService;

    @GetMapping
    public String cacheControl(@RequestParam String userId) throws InterruptedException {
        if (userRateLimitService.isUserBlocked(userId)) {
            return "Kullanıcı bloklanmış durumda. Lütfen bekleyin...";
        }

        userRateLimitService.checkAndIncrementRequestCount(userId);

        return redisCacheService.longRunnigMethod();
    }
}
