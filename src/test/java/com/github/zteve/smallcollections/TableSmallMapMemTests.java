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

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit-driven memory tests for {@link SmallMap}.
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
public class TableSmallMapMemTests {

    private static final int MAX_NUM_MAPS = 100000;
    private static final int NUM_INTERVAL = 1000;
    private static final int NUM_IN_TABLE = MAX_NUM_MAPS / NUM_INTERVAL;
    private static final String TAB_FILE_PATH = "build/output/MapMemTable.csv";
    private static final File tabOut = new File(TAB_FILE_PATH);

    private static final int NUM_TESTS = 6;
    private static final String[] tableHdrs = new String[NUM_TESTS + 1];
    private static final long[][] table = new long[NUM_IN_TABLE][NUM_TESTS + 1];
    static {
        tableHdrs[0] = "Number of maps";
        for (int i = 0; i < NUM_IN_TABLE; ++i) {
            table[i][0] = (long) i * NUM_INTERVAL; // x-axis
        }
    }

    private interface MapFactory {
        Map<String, String> generate();
    }

    private final static MapFactory smallMapFactory = new MapFactory() {
        public Map<String, String> generate() {
            return new SmallMap<String, String>();
        }
    };

    private final static MapFactory hashMapFactory = new MapFactory() {
        public Map<String, String> generate() {
            return new HashMap<String, String>();
        }
    };

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

        for (int i = 0; i < table.length; ++i) {
            StringBuilder sb = new StringBuilder().append(table[i][0]);
            for (int j = 1; j <= NUM_TESTS; ++j) {
                sb.append(',').append(table[i][j]);
            }
            fos.write(sb.append('\n').toString().getBytes("US-ASCII"));
        }
        fos.close();
    }

    @Test
    public void generateMemTableEmptySmallMaps() throws Exception {
        generateMemTable(smallMapFactory, 0, 1, "EmptySmallMaps");
    }

    @Test
    public void generateMemTableEmptyHashMaps() throws Exception {
        generateMemTable(hashMapFactory, 0, 2, "EmptyHashMaps");
    }

    @Test
    public void generateMemTableSingletonSmallMaps() throws Exception {
        generateMemTable(smallMapFactory, 1, 3, "OneSmallMaps");
    }

    @Test
    public void generateMemTableSingletonHashMaps() throws Exception {
        generateMemTable(hashMapFactory, 1, 4, "OneHashMaps");
    }

    @Test
    public void generateMemTablePairSmallMaps() throws Exception {
        generateMemTable(smallMapFactory, 2, 5, "PairSmallMaps");
    }

    @Test
    public void generateMemTablePairHashMaps() throws Exception {
        generateMemTable(hashMapFactory, 2, 6, "PairHashMaps");
    }

    private final static void generateMemTable(MapFactory factory, int setSize,
            int testNum, String hdr) throws Exception {
        tableHdrs[testNum] = hdr;
        for (int i = 0; i < NUM_IN_TABLE; i++) {
            int tabSize = i * NUM_INTERVAL;
            Map<?,?>[] arrOfMaps = new Map<?,?>[tabSize];
            before.retake();
            fillArrayMaps(arrOfMaps, tabSize, setSize, factory);
            after.retake();
            table[i][testNum] = before.getFree() - after.getFree();
        }
    }

    private final static void fillArrayMaps(Map<?,?>[] arr, int num, int size,
            MapFactory mapFactory) {
        for (int i = 0; i < num; ++i) {
            arr[i] = populate(mapFactory.generate(), size);
        }
    }

    private final static Map<String, String> populate(Map<String,String> m, int num) {
        for (int i = 0; i < num; i++) {
            String key = "key" + i;
            String value = "value" + i;
            m.put(key, value);
        }
        return m;
    }
}
