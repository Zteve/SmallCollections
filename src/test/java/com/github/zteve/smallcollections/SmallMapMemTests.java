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

    private static HashMap<String, String> newHashMap() { return new HashMap<String, String>(); }
    private static SmallMap<String, String> newSmallMap() { return new SmallMap<String, String>(); }

    @SuppressWarnings("unused")
    private Map<String, String> holdingMap;
    private final Map<?,?>[] arrOfMaps = new Map<?,?>[LARGE_NUM_MAPS];

    private MemSnapshot before;
    private MemSnapshot after;

    @Rule
    public TestName name = new TestName();

    @Before
    public void beforeMss() {
        this.before = MemSnapshot.take();
    }

    @After
    public void afterMss() {
        this.after = MemSnapshot.take();
        compareMss(name.getMethodName(), this.before, this.after);
    }

    @Test
    public void estimateMemUsageOne() throws Exception {
        this.holdingMap = newSmallMap();
    }
    @Test
    public void estimateMemUsageHash() throws Exception {
        this.holdingMap = newHashMap();
    }

    @Test
    public void estimateManyEmptySmallMaps() throws Exception {
        for (int i=0; i<LARGE_NUM_MAPS; ++i) {
            arrOfMaps[i] = newSmallMap();
        }
    }
    @Test
    public void estimateManyEmptyHashMaps() throws Exception {
        for (int i=0; i<LARGE_NUM_MAPS; ++i) {
            arrOfMaps[i] = newHashMap();
        }
    }

    @Test
    public void estimateManyOneElementSmallMaps() throws Exception {
        for (int i=0; i<LARGE_NUM_MAPS; ++i) {
            arrOfMaps[i] = populate(newSmallMap(),1);
        }
    }
    @Test
    public void estimateManyOneElementHashMaps() throws Exception {
        for (int i=0; i<LARGE_NUM_MAPS; ++i) {
            arrOfMaps[i] = populate(newHashMap(),1);
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
        MemSnapshot.diff(before, after).print(hdr);
    }

    private static class MemSnapshot {
        private long freeMemory;

        private static Runtime rt = Runtime.getRuntime();

        private MemSnapshot() {
            this(rt.freeMemory());
        }

        private MemSnapshot(long freeMemory) {
            this.freeMemory = freeMemory;
        }

        public static MemSnapshot diff(MemSnapshot before, MemSnapshot after) {
            return new MemSnapshot(after.freeMemory - before.freeMemory);
        }

        public static MemSnapshot take() {
            System.gc();
            return new MemSnapshot();
        }

        public void print(String hdr) {
            System.out.println(String.format(
                    "%40s: %10d free", hdr, this.freeMemory));
        }
    }
}
