package com.github.zteve.smallcollections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Unit tests for {@link SmallSet} implementation.
 *
 * @author zteve.powell
 */
public class SmallSetTests {

    private static final String TEST_VALUE = "TestValue";
    private static final String TEST_VALUE_2 = "SecondTestValue";
    private static final String NOT_VALUE = "Not in the Set";
    private static final int POPULATION = 100;
    private final Set<String> ss = new SmallSet<String>();

    @Test
    public void empty() throws Exception {
        assertEquals("SmallSet not empty", 0, this.ss.size());
        assertTrue("SmallSet not empty", this.ss.isEmpty());
    }

    @Test
    public void insert() throws Exception {
        assertFalse("Value found in empty SmallSet!",
                this.ss.contains(TEST_VALUE));
        assertTrue("Boolean returned not true", this.ss.add(TEST_VALUE));
        assertEquals("Not one inserted", 1, this.ss.size());
        assertTrue("Inserted value not found", this.ss.contains(TEST_VALUE));

        assertFalse("Value not acknowledged on second insert",
                this.ss.add(TEST_VALUE));

        assertFalse("Value found in singleton SmallSet!",
                this.ss.contains(TEST_VALUE_2));
        assertTrue("Boolean returned not true", this.ss.add(TEST_VALUE_2));
        assertEquals("Not two inserted", 2, this.ss.size());
        assertTrue("Inserted value not found", this.ss.contains(TEST_VALUE_2));
    }

    @Test
    public void removeNot() throws Exception {
        this.ss.add(TEST_VALUE);
        assertFalse("Non-value removed not false", this.ss.remove(NOT_VALUE));
        assertEquals("SmallSet should have one element after non-removal", 1,
                this.ss.size());
        assertTrue("Value corrupted", this.ss.contains(TEST_VALUE));
    }

    @Test
    public void remove() throws Exception {
        this.ss.add(TEST_VALUE);
        this.ss.add(TEST_VALUE_2);
        assertTrue("Value not removed", this.ss.remove(TEST_VALUE));
        assertEquals("SmallSet should have one element after removal", 1,
                this.ss.size());
        assertTrue("Other value removed!", this.ss.contains(TEST_VALUE_2));

        this.ss.remove(TEST_VALUE_2);
        assertTrue("SmallSet not empty after final removal", this.ss.isEmpty());
    }

    @Test
    public void clear() throws Exception {
        populate(this.ss, POPULATION);
        this.ss.clear();
        assertTrue("SmallSet not cleared", this.ss.isEmpty());
    }

    @Test
    public void populate() throws Exception {
        Set<String> testSet = populate(this.ss, POPULATION);
        assertEquals("Wrong size of SmallSet", POPULATION, this.ss.size());
        assertEquals("SmallSet doesn't record elements correctly", testSet,
                this.ss);
    }

    @Test
    public void initWithSmallSet() throws Exception {
        Set<String> copySet = populate(this.ss, POPULATION);
        Set<String> testSet = new HashSet<String>(this.ss);
        assertEquals("SmallSet doesn't initialise HashSet correctly", copySet,
                testSet);
    }

    @Test
    public void initSmallSetWithSet() throws Exception {
        Set<String> set = populate(POPULATION);
        SmallSet<String> testSmallSet = new SmallSet<String>(set);
        assertEquals("HashSet doesn't initialise SmallSet correctly", set,
                testSmallSet);
    }

    private static Set<String> populate(int num) {
        return populate(null, num);
    }

    private static Set<String> populate(Set<String> s, int num) {
        Set<String> copySet = new HashSet<String>();
        for (int i = 0; i < num; i++) {
            String value = "value" + i;
            if (s != null)
                s.add(value);
            copySet.add(value);
        }
        return copySet;
    }

}
