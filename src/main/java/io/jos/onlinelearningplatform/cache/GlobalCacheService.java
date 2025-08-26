package io.jos.onlinelearningplatform.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GlobalCacheService {
    private static final Logger logger = LoggerFactory.getLogger(GlobalCacheService.class);

    // Single global cache that applies only for users and lessons
    private final LRUCacheAlgo<String, Object> globalCache;

    public GlobalCacheService() {
        this.globalCache = new LRUCacheAlgo<>(50); // Global cache with capacity of 5
        logger.info("Initialized global cache with capacity: 5 for users and lessons only");
    }

    // Cache user data
    public void putUser(String key, Object user) {
        globalCache.put("user_" + key, user);
        logger.debug("Cached user with key: user_{}", key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getUser(String key, Class<T> type) {
        Object value = globalCache.get("user_" + key);
        if (value != null && type.isInstance(value)) {
            logger.debug("Cache hit for user key: user_{}", key);
            return (T) value;
        }
        logger.debug("Cache miss for user key: user_{}", key);
        return null;
    }

    // Cache lesson data
    public void putLesson(String key, Object lesson) {
        globalCache.put("lesson_" + key, lesson);
        logger.debug("Cached lesson with key: lesson_{}", key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getLesson(String key, Class<T> type) {
        Object value = globalCache.get("lesson_" + key);
        if (value != null && type.isInstance(value)) {
            logger.debug("Cache hit for lesson key: lesson_{}", key);
            return (T) value;
        }
        logger.debug("Cache miss for lesson key: lesson_{}", key);
        return null;
    }

    public int size() {
        return globalCache.size();
    }

    public void logCacheStats() {
        logger.info("Global cache size: {}/5 (users and lessons only)", globalCache.size());
    }
}
