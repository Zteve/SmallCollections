/**
 * JUnit-driven memory tests for {@link SmallSet}.
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

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * JUnit-driven memory tests for {@link SmallSet}.
 *
 * @author Zteve
 */
public class SmallSetMemTests {
    private static final int LARGE_NUM_SETS = 450000;
    private static final int NOT_SO_LARGE_NUM_SETS = 250000;

    private static HashSet<String> newHashSet() {
        return new HashSet<String>();
    }

    private static SmallSet<String> newSmallSet() {
        return new SmallSet<String>();
    }

    private final Set<?>[] arrOfSets = new Set<?>[LARGE_NUM_SETS];

    private MemSnapshot before;

    @Rule
    public final TestName name = new TestName();

    @BeforeClass
    public static void beforeMemTests() {
        // Shake up the free memory and garbage collector,
        // as well as jiggling the JIT at bit.
        Set<String> set1 = SmallSetMemTests.populate(
                new SmallSet<String>(), 100);
        for (int i = 0; i < 50; ++i) {
            Set<String> set2 = new SmallSet<String>();
            set2.addAll(set1);
            set2.remove("value" + 20);
            set2.clear();
        }
        set1.clear();
        Set<String> set3 = SmallSetMemTests.populate(
                new HashSet<String>(), 100);
        for (int i = 0; i < 50; ++i) {
            Set<String> set4 = new HashSet<String>();
            set4.addAll(set3);
            set4.remove("value" + 20);
            set4.clear();
        }
        set3.clear();
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
    public void estimateMemUsageOneSmallSet() throws Exception {
        populate(newSmallSet(), 1);
    }

    @Test
    public void estimateMemUsageOneHashSet() throws Exception {
        populate(newHashSet(), 1);
    }

    @Test
    public void estimateManyEmptySmallSets() throws Exception {
        for (int i = 0; i < LARGE_NUM_SETS; ++i) {
            arrOfSets[i] = newSmallSet();
        }
    }

    @Test
    public void estimateManyEmptyHashSets() throws Exception {
        for (int i = 0; i < LARGE_NUM_SETS; ++i) {
            arrOfSets[i] = newHashSet();
        }
    }

    @Test
    public void estimateManyOneElementSmallSets() throws Exception {
        for (int i = 0; i < LARGE_NUM_SETS; ++i) {
            arrOfSets[i] = populate(newSmallSet(), 1);
        }
    }

    @Test
    public void estimateManyOneElementHashSets() throws Exception {
        for (int i = 0; i < LARGE_NUM_SETS; ++i) {
            arrOfSets[i] = populate(newHashSet(), 1);
        }
    }

    @Test
    public void estimateManyTwoElementSmallSets() throws Exception {
        for (int i = 0; i < NOT_SO_LARGE_NUM_SETS; ++i) {
            arrOfSets[i] = populate(newSmallSet(), 2);
        }
    }

    @Test
    public void estimateManyTwoElementHashSets() throws Exception {
        for (int i = 0; i < NOT_SO_LARGE_NUM_SETS; ++i) {
            arrOfSets[i] = populate(newHashSet(), 2);
        }
    }

    private static Set<String> populate(Set<String> s, int num) {
        for (int i = 0; i < num; i++) {
            String value = "value" + i;
            s.add(value);
        }
        return s;
    }

    private static void compareMss(String hdr, MemSnapshot before,
            MemSnapshot after) {
        MemSnapshot.used(before, after).print(hdr);
    }
}
