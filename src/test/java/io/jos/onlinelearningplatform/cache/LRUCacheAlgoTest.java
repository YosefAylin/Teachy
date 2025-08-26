package io.jos.onlinelearningplatform.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class LRUCacheAlgoTest {

    private static final Logger logger = LoggerFactory.getLogger(LRUCacheAlgoTest.class);

    private LRUCacheAlgo<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new LRUCacheAlgo<>(3); // Create cache with capacity of 3
        logger.info("LRUCacheAlgoTest setup completed - created cache with capacity 3");
    }

    @Test
    @DisplayName("Should create empty cache with correct capacity")
    void testInitialState() {
        logger.info("Testing initial cache state");

        assertEquals(0, cache.size());
        assertFalse(cache.containsKey("any"));
        assertNull(cache.get("any"));

        logger.info("Initial state test passed - cache is empty as expected");
    }

    @Test
    @DisplayName("Should put and get single item")
    void testPutAndGetSingleItem() {
        logger.info("Testing put and get single item");

        cache.put("key1", "value1");
        logger.debug("Added key1=value1 to cache");

        assertEquals(1, cache.size());
        assertTrue(cache.containsKey("key1"));
        assertEquals("value1", cache.get("key1"));

        logger.info("Single item test passed - cache size: {}, contains key1: {}",
                cache.size(), cache.containsKey("key1"));
    }

    @Test
    @DisplayName("Should put and get multiple items within capacity")
    void testPutAndGetMultipleItems() {
        logger.info("Testing put and get multiple items within capacity");

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        logger.debug("Added 3 items to cache - key1, key2, key3");

        assertEquals(3, cache.size());
        assertEquals("value1", cache.get("key1"));
        assertEquals("value2", cache.get("key2"));
        assertEquals("value3", cache.get("key3"));

        logger.info("Multiple items test passed - cache size: {}, all items retrieved correctly", cache.size());
    }

    @Test
    @DisplayName("Should evict least recently used item when capacity exceeded")
    void testLRUEviction() {
        logger.info("Testing LRU eviction when capacity exceeded");

        // Fill cache to capacity
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        logger.debug("Filled cache to capacity with key1, key2, key3");

        // Add one more item - should evict key1 (least recently used)
        cache.put("key4", "value4");
        logger.debug("Added key4 - should evict key1 (LRU)");

        assertEquals(3, cache.size()); // Size should remain at capacity
        assertFalse(cache.containsKey("key1")); // key1 should be evicted
        assertTrue(cache.containsKey("key2"));
        assertTrue(cache.containsKey("key3"));
        assertTrue(cache.containsKey("key4"));

        assertNull(cache.get("key1"));
        assertEquals("value2", cache.get("key2"));
        assertEquals("value3", cache.get("key3"));
        assertEquals("value4", cache.get("key4"));

        logger.info("LRU eviction test passed - key1 evicted, key2/key3/key4 remain, cache size: {}", cache.size());
    }

    @Test
    @DisplayName("Should update access order when getting items")
    void testAccessOrderUpdate() {
        logger.info("Testing access order update when getting items");

        // Fill cache
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        logger.debug("Filled cache with key1, key2, key3");

        // Access key1 to make it most recently used
        cache.get("key1");
        logger.debug("Accessed key1 to update its position in LRU order");

        // Add new item - should evict key2 (now least recently used)
        cache.put("key4", "value4");
        logger.debug("Added key4 - should evict key2 (now LRU)");

        assertEquals(3, cache.size());
        assertTrue(cache.containsKey("key1")); // key1 should still be there
        assertFalse(cache.containsKey("key2")); // key2 should be evicted
        assertTrue(cache.containsKey("key3"));
        assertTrue(cache.containsKey("key4"));

        logger.info("Access order update test passed - key2 evicted after key1 access, cache size: {}", cache.size());
    }

    @Test
    @DisplayName("Should update value for existing key without changing size")
    void testUpdateExistingKey() {
        logger.info("Testing update value for existing key");

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        logger.debug("Added key1=value1 and key2=value2");

        // Update existing key
        cache.put("key1", "updatedValue1");
        logger.debug("Updated key1 to updatedValue1");

        assertEquals(2, cache.size()); // Size should remain the same
        assertEquals("updatedValue1", cache.get("key1"));
        assertEquals("value2", cache.get("key2"));

        logger.info("Update existing key test passed - key1 updated, size unchanged: {}", cache.size());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        logger.info("Testing null values handling");

        cache.put("key1", null);
        logger.debug("Added key1=null to cache");

        assertEquals(1, cache.size());
        assertTrue(cache.containsKey("key1"));
        assertNull(cache.get("key1"));

        logger.info("Null values test passed - cache handles null values correctly, size: {}", cache.size());
    }

    @Test
    @DisplayName("Should handle complex LRU scenario")
    void testComplexLRUScenario() {
        logger.info("Testing complex LRU scenario with multiple access patterns");

        // Fill cache
        cache.put("A", "1");
        cache.put("B", "2");
        cache.put("C", "3");
        logger.debug("Initial fill: A, B, C");

        // Access A and B to update their order
        cache.get("A");
        cache.get("B");
        logger.debug("Accessed A and B to update LRU order");

        // Add D - should evict C (least recently used)
        cache.put("D", "4");
        logger.debug("Added D - should evict C");

        assertTrue(cache.containsKey("A"));
        assertTrue(cache.containsKey("B"));
        assertFalse(cache.containsKey("C")); // C should be evicted
        assertTrue(cache.containsKey("D"));
        logger.debug("After adding D: A, B, D remain; C evicted");

        // Access A again
        cache.get("A");
        logger.debug("Accessed A again");

        // Add E - should evict D (now least recently used)
        cache.put("E", "5");
        logger.debug("Added E - should evict B");

        assertTrue(cache.containsKey("A"));
        assertFalse(cache.containsKey("B"));
        assertTrue(cache.containsKey("D")); // D should be evicted
        assertTrue(cache.containsKey("E"));

        logger.info("Complex LRU scenario test passed - final state: A, B, E; cache size: {}", cache.size());
    }

    @Test
    @DisplayName("Should work with different key-value types")
    void testDifferentTypes() {
        logger.info("Testing cache with different key-value types (Integer, String)");

        LRUCacheAlgo<Integer, String> intCache = new LRUCacheAlgo<>(2);
        logger.debug("Created integer key cache with capacity 2");

        intCache.put(1, "one");
        intCache.put(2, "two");
        logger.debug("Added 1=one, 2=two");

        assertEquals("one", intCache.get(1));
        assertEquals("two", intCache.get(2));
        assertEquals(2, intCache.size());

        // Test eviction with integer keys
        intCache.put(3, "three");
        logger.debug("Added 3=three - should evict 1");

        assertFalse(intCache.containsKey(1)); // 1 should be evicted
        assertTrue(intCache.containsKey(2));
        assertTrue(intCache.containsKey(3));

        logger.info("Different types test passed - integer keys work correctly, final size: {}", intCache.size());
    }

    @Test
    @DisplayName("Should handle cache with capacity of 1")
    void testCapacityOne() {
        logger.info("Testing cache with capacity of 1");

        LRUCacheAlgo<String, String> smallCache = new LRUCacheAlgo<>(1);
        logger.debug("Created cache with capacity 1");

        smallCache.put("key1", "value1");
        logger.debug("Added key1=value1");
        assertEquals(1, smallCache.size());
        assertEquals("value1", smallCache.get("key1"));

        // Add second item - should evict first
        smallCache.put("key2", "value2");
        logger.debug("Added key2=value2 - should evict key1");

        assertEquals(1, smallCache.size());
        assertFalse(smallCache.containsKey("key1"));
        assertTrue(smallCache.containsKey("key2"));
        assertEquals("value2", smallCache.get("key2"));

        logger.info("Capacity 1 test passed - cache maintains size 1, evicts correctly");
    }

    @Test
    @DisplayName("Should maintain consistent state after multiple operations")
    void testConsistentState() {
        logger.info("Testing consistent state after multiple operations");

        // Perform multiple operations
        for (int i = 0; i < 10; i++) {
            cache.put("key" + i, "value" + i);
            logger.debug("Added key{} = value{}, cache size: {}", i, i, cache.size());

            // Cache should never exceed capacity
            assertTrue(cache.size() <= 3);

            // Recently added item should always be present
            assertTrue(cache.containsKey("key" + i));
            assertEquals("value" + i, cache.get("key" + i));
        }

        // Final state should have last 3 items
        assertEquals(3, cache.size());
        assertTrue(cache.containsKey("key7"));
        assertTrue(cache.containsKey("key8"));
        assertTrue(cache.containsKey("key9"));

        // Earlier items should be evicted
        assertFalse(cache.containsKey("key0"));
        assertFalse(cache.containsKey("key1"));
        assertFalse(cache.containsKey("key6"));

        logger.info("Consistent state test passed - final cache contains key7, key8, key9; size: {}", cache.size());
    }
}
