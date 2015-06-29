/**
 * JUnit tests for {@link SmallMap}.
 * <p>
 * Copyright 2012 Steve Powell
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.zteve.smallcollections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

/**
 * JUnit tests for {@link SmallMap}.
 *
 * @author Zteve
 */
public class SmallMapTests {

    private static final String TEST_VALUE = "TestValue";
    private static final String TEST_VALUE_2 = "SecondTestValue";
    private static final String TEST_VALUE_3 = "ThirdTestValue";
    private static final String TEST_KEY = "TestKey";
    private static final String TEST_KEY_2 = "TestKey2";
    private static final String NOT_KEY = "NotAKey";
    private static final int POPULATION = 100;

    private final Map<String, String> sm;

    public SmallMapTests() {
        sm = new SmallMap<>();
    }

    @Test
    public void empty() throws Exception {
        assertEquals("SmallMap not empty", 0, this.sm.size());
        assertTrue("SmallMap not empty", this.sm.isEmpty());
    }

    @Test
    public void insert() throws Exception {
        assertFalse("Key found in empty SmallMap!",
                this.sm.containsKey(TEST_KEY));
        assertNull("Value returned not null", this.sm.put(TEST_KEY, TEST_VALUE));
        assertEquals("Not one inserted", 1, this.sm.size());
        assertTrue("Inserted entry key not found",
                this.sm.containsKey(TEST_KEY));
        assertEquals("Value of inserted key corrupted", TEST_VALUE,
                this.sm.get(TEST_KEY));

        assertFalse("Key found in singleton SmallMap!",
                this.sm.containsKey(TEST_KEY_2));
        assertNull("Value returned not null",
                this.sm.put(TEST_KEY_2, TEST_VALUE_2));
        assertEquals("Not two inserted", 2, this.sm.size());
        assertTrue("Inserted entry key not found",
                this.sm.containsKey(TEST_KEY_2));
        assertEquals("Value of inserted key corrupted", TEST_VALUE_2,
                this.sm.get(TEST_KEY_2));
    }

    @Test
    public void replace() throws Exception {
        this.sm.put(TEST_KEY, TEST_VALUE);
        this.sm.put(TEST_KEY_2, TEST_VALUE_2);
        assertEquals("Value returned not old value", TEST_VALUE,
                this.sm.put(TEST_KEY, TEST_VALUE_3));
        assertEquals("Not two inserted", 2, this.sm.size());
        assertTrue("Replaced key not found", this.sm.containsKey(TEST_KEY));
        assertEquals("Value of replaced key corrupted", TEST_VALUE_3,
                this.sm.get(TEST_KEY));
    }

    @Test
    public void removeNot() throws Exception {
        this.sm.put(TEST_KEY, TEST_VALUE);
        assertNull("Non-key value removed not null", this.sm.remove(NOT_KEY));
        assertEquals("SmallMap should have one element after non-removal", 1,
                this.sm.size());
        assertEquals("Value of other key corrupted", TEST_VALUE,
                this.sm.get(TEST_KEY));
    }

    @Test
    public void remove() throws Exception {
        this.sm.put(TEST_KEY, TEST_VALUE);
        this.sm.put(TEST_KEY_2, TEST_VALUE_2);
        assertEquals("Key value removed not original", TEST_VALUE,
                this.sm.remove(TEST_KEY));
        assertEquals("SmallMap should have one element after removal", 1,
                this.sm.size());
        assertEquals("Value of other key corrupted", TEST_VALUE_2,
                this.sm.get(TEST_KEY_2));

        this.sm.remove(TEST_KEY_2);
        assertTrue("SmallMap not empty after removal", this.sm.isEmpty());
    }

    @Test
    public void clear() throws Exception {
        populate(this.sm, POPULATION);
        this.sm.clear();
        assertTrue("SmallMap not cleared", this.sm.isEmpty());
    }

    @Test
    public void populate() throws Exception {
        Map<String, String> testMap = populate(this.sm, POPULATION);
        assertEquals("Wrong size of SmallMap", POPULATION, this.sm.size());
        assertEquals("SmallMap doesn't record Entrys correctly", testMap,
                this.sm);
    }

    @Test
    public void initWithSmallMap() throws Exception {
        Map<String, String> copyMap = populate(this.sm, POPULATION);
        Map<String, String> testMap = new HashMap<>(this.sm);
        assertEquals("SmallMap doesn't initialise HashMap correctly", copyMap,
                testMap);
    }

    @Test
    public void initSmallMapWithMap() throws Exception {
        Map<String, String> map = populate(POPULATION);
        SmallMap<String, String> testSmallMap = new SmallMap<>(
                map);
        assertEquals("HashMap doesn't initialise SmallMap correctly", map,
                testSmallMap);
    }

    @Test
    public void emptyEntrySet() throws Exception {
        Set<Entry<String, String>> es = this.sm.entrySet();
        assertTrue("EntrySet not empty", es.isEmpty());
    }

    @Test
    public void entrySet() throws Exception {
        populate(this.sm, POPULATION);
        Set<Entry<String, String>> es = this.sm.entrySet();
        assertEquals("EntrySet not correct size", POPULATION, es.size());
        for (Entry<String, String> e : es) {
            String k = e.getKey();
            assertTrue("EntrySet has key not in SmallMap",
                    this.sm.containsKey(k));
            assertEquals("Entry value not associated with key '" + k + "'",
                    e.getValue(), this.sm.get(k));
        }
    }

    private static Map<String, String> populate(int num) {
        return populate(null, num);
    }

    private static Map<String, String> populate(Map<String, String> m, int num) {
        Map<String, String> copyMap = new HashMap<>();
        for (int i = 0; i < num; i++) {
            String key = "key" + i;
            String value = "value" + i;
            if (m != null)
                m.put(key, value);
            copyMap.put(key, value);
        }
        return copyMap;
    }
}
