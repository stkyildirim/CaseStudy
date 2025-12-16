package org.flightsearch.service.cache;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final CacheManager cacheManager;

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void clearAllCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            cacheManager.getCache(name).clear();
        });
    }

    public void evictCacheByKey(String key) {
        // logsByProvider cache'inden sil
        var cache = cacheManager.getCache("logsByProvider");
        if (cache != null) {
            cache.evict(key);
        }
    }
}
