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

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit-driven memory tests for {@link SmallSet}.
 * <p>
 * Each unit test generates a (numbered) column of the table <code>table</code>.
 * Column zero is the number of collections created in each test. The unit test
 * class outputs the tables in csv format into
 * <code>build/output/MemTable.csv</code>.
 * </p>
 * <p>
 * In <code>src/test/data</code> there is a spreadsheet file which imports the
 * <code>csv</code> file and displays a chart of the various memory requirements
 * for each unit test.
 * </p>
 *
 * @author Zteve
 */
public class TableSmallSetMemTests {

    private static final int MAX_NUM_SETS = 100000;
    private static final int NUM_INTERVAL = 1000;
    private static final int NUM_IN_TABLE = MAX_NUM_SETS / NUM_INTERVAL;
    private static final String TAB_FILE_PATH = "build/output/SetMemTable.csv";
    private static final File tabOut = new File(TAB_FILE_PATH);

    private static final int NUM_TESTS = 6;
    private static final String[] tableHdrs = new String[NUM_TESTS + 1];
    private static final long[][] table = new long[NUM_IN_TABLE][NUM_TESTS + 1];
    static {
        tableHdrs[0] = "Number of sets";
        for (int i = 0; i < NUM_IN_TABLE; ++i) {
            table[i][0] = (long) i * NUM_INTERVAL; // x-axis
        }
    }

    private interface SetFactory {
        Set<String> generate();
    }

    private final static SetFactory smallSetFactory = SmallSet::new;

    private final static SetFactory hashSetFactory = HashSet::new;

    private final static MemSnapshot before = MemSnapshot.take();
    private final static MemSnapshot after = MemSnapshot.take();

    @BeforeClass
    public static void beforeMemTests() {
        // delete the csv file and call the garbage collector (twice)
        tabOut.delete();
        before.retake();
        after.retake();
    }

    @AfterClass
    public static void outputTableFile() throws Exception {
        FileOutputStream fos = new FileOutputStream(tabOut, true /* append */);

        {
            StringBuilder sb = new StringBuilder().append('\"')
                    .append(tableHdrs[0]).append('\"');
            for (int j = 1; j <= NUM_TESTS; ++j) {
                sb.append(',').append('\"').append(tableHdrs[j]).append('\"');
            }
            fos.write(sb.append('\n').toString().getBytes("US-ASCII"));
        }

        for (long[] aTable : table) {
            StringBuilder sb = new StringBuilder().append(aTable[0]);
            for (int j = 1; j <= NUM_TESTS; ++j) {
                sb.append(',').append(aTable[j]);
            }
            fos.write(sb.append('\n').toString().getBytes("US-ASCII"));
        }
        fos.close();
    }

    @Test
    public void generateMemTableEmptySmallSets() throws Exception {
        generateMemTable(smallSetFactory, 0, 1, "EmptySmallSets");
    }

    @Test
    public void generateMemTableEmptyHashSets() throws Exception {
        generateMemTable(hashSetFactory, 0, 2, "EmptyHashSets");
    }

    @Test
    public void generateMemTableSingletonSmallSets() throws Exception {
        generateMemTable(smallSetFactory, 1, 3, "OneSmallSets");
    }

    @Test
    public void generateMemTableSingletonHashSets() throws Exception {
        generateMemTable(hashSetFactory, 1, 4, "OneHashSets");
    }

    @Test
    public void generateMemTablePairSmallSets() throws Exception {
        generateMemTable(smallSetFactory, 2, 5, "PairSmallSets");
    }

    @Test
    public void generateMemTablePairHashSets() throws Exception {
        generateMemTable(hashSetFactory, 2, 6, "PairHashSets");
    }

    private static void generateMemTable(SetFactory factory, int setSize,
            int testNum, String hdr) throws Exception {
        tableHdrs[testNum] = hdr;
        for (int i = 0; i < NUM_IN_TABLE; i++) {
            int tabSize = i * NUM_INTERVAL;
            Set<?>[] arrOfSets = new Set<?>[tabSize];
            before.retake();
            fillArraySets(arrOfSets, tabSize, setSize, factory);
            after.retake();
            table[i][testNum] = before.getFree() - after.getFree();
        }
    }

    private static void fillArraySets(Set<?>[] arr, int num, int size,
            SetFactory setFactory) {
        for (int i = 0; i < num; ++i) {
            arr[i] = populate(setFactory.generate(), size);
        }
    }

    private static Set<String> populate(Set<String> s, int num) {
        for (int i = 0; i < num; i++) {
            String value = "value" + i;
            s.add(value);
        }
        return s;
    }
}
