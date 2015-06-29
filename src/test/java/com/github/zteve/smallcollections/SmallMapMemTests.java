/**
 * JUnit-driven memory tests for {@link SmallMap}.
 *
 * Copyright 2012 Steve Powell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.zteve.smallcollections;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * JUnit-driven memory tests for {@link SmallMap}.
 *
 * @author Zteve
 */
public class SmallMapMemTests {
    private static final int LARGE_NUM_MAPS = 450000;
    private static final int NOT_SO_LARGE_NUM_MAPS = 250000;

    private static HashMap<String, String> newHashMap() {
        return new HashMap<String, String>();
    }

    private static SmallMap<String, String> newSmallMap() {
        return new SmallMap<String, String>();
    }

    private final Map<?, ?>[] arrOfMaps = new Map<?, ?>[LARGE_NUM_MAPS];

    private MemSnapshot before;

    @Rule
    public final TestName name = new TestName();

    @BeforeClass
    public static void beforeMemTests() {
        // Shake up the free memory and garbage collector,
        // as well as jiggling the JIT at bit.
        Map<String, String> map1 = SmallMapMemTests.populate(
                new SmallMap<String, String>(), 100);
        for (int i = 0; i < 50; ++i) {
            Map<String, String> map2 = new SmallMap<String, String>();
            map2.putAll(map1);
            map2.remove("key" + 20);
            map2.clear();
        }
        map1.clear();
        Map<String, String> map3 = SmallMapMemTests.populate(
                new HashMap<String, String>(), 100);
        for (int i = 0; i < 50; ++i) {
            Map<String, String> map4 = new HashMap<String, String>();
            map4.putAll(map3);
            map4.remove("key" + 20);
            map4.clear();
        }
        map3.clear();
        System.gc();
    }

    @Before
    public void beforeMss() {
        this.before = MemSnapshot.take();
    }

    @After
    public void afterMss() {
        compareMss(name.getMethodName(), this.before, MemSnapshot.take());
    }

    @Test
    public void estimateMemUsageOneSmallMap() throws Exception {
        populate(newSmallMap(), 1);
    }

    @Test
    public void estimateMemUsageOneHashMap() throws Exception {
        populate(newHashMap(), 1);
    }

    @Test
    public void estimateManyEmptySmallMaps() throws Exception {
        for (int i = 0; i < LARGE_NUM_MAPS; ++i) {
            arrOfMaps[i] = newSmallMap();
        }
    }

    @Test
    public void estimateManyEmptyHashMaps() throws Exception {
        for (int i = 0; i < LARGE_NUM_MAPS; ++i) {
            arrOfMaps[i] = newHashMap();
        }
    }

    @Test
    public void estimateManyOneElementSmallMaps() throws Exception {
        for (int i = 0; i < LARGE_NUM_MAPS; ++i) {
            arrOfMaps[i] = populate(newSmallMap(), 1);
        }
    }

    @Test
    public void estimateManyOneElementHashMaps() throws Exception {
        for (int i = 0; i < LARGE_NUM_MAPS; ++i) {
            arrOfMaps[i] = populate(newHashMap(), 1);
        }
    }

    @Test
    public void estimateManyTwoElementSmallMaps() throws Exception {
        for (int i = 0; i < NOT_SO_LARGE_NUM_MAPS; ++i) {
            arrOfMaps[i] = populate(newSmallMap(), 2);
        }
    }

    @Test
    public void estimateManyTwoElementHashMaps() throws Exception {
        for (int i = 0; i < NOT_SO_LARGE_NUM_MAPS; ++i) {
            arrOfMaps[i] = populate(newHashMap(), 2);
        }
    }

    private static Map<String, String> populate(Map<String, String> m, int num) {
        for (int i = 0; i < num; i++) {
            String key = "key" + i;
            String value = "value" + i;
            m.put(key, value);
        }
        return m;
    }

    private static void compareMss(String hdr, MemSnapshot before,
            MemSnapshot after) {
        MemSnapshot.used(before, after).print(hdr);
    }
}
