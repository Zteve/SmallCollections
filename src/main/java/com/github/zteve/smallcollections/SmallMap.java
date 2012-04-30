/**
 * Simple, lightweight, slow implementation of a map, with small memory footprint.
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
 *
 */
package com.github.zteve.smallcollections;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A {@link Map} implementation that has a small memory footprint. It is slow,
 * <em>unsynchronised</em>, and unoptimised, but if you want a lot of small
 * maps, which individually have little use, this fits the bill.
 *
 * @author Zteve
 */
public class SmallMap<K, V> extends AbstractMap<K, V> {

    private ArrayList<K> keyArray = null;
    private ArrayList<V> valueArray = null;

    private ArrayList<K> keyArray() {
        if (this.keyArray == null)
            this.keyArray = new ArrayList<K>(0);
        return this.keyArray;
    }
    private ArrayList<V> valueArray() {
        if (this.valueArray == null)
            this.valueArray = new ArrayList<V>(0);
        return this.valueArray;
    }
    /**
     * Constructs a new, empty, <code>SmallMap</code>.
     */
    public SmallMap() {
    }

    /**
     * Constructs a new <code>SmallMap</code> with the same mappings as the
     * specified <code>Map</code>.
     *
     * @param m
     *            the map whose mappings are to be placed in this map
     * @throws NullPointerException
     *             if the specified map is null
     */
    public SmallMap(Map<? extends K, ? extends V> m) {
        putAllInternal(this.keyArray(), this.valueArray(), m);
    }

    private static <K, V> void putAllInternal(ArrayList<K> ka, ArrayList<V> va,
            Map<? extends K, ? extends V> m) {
        for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m
                .entrySet().iterator(); i.hasNext();) {
            Map.Entry<? extends K, ? extends V> e = i.next();
            putInternal(ka, va, e.getKey(), e.getValue());
        }
    }

    private static <K, V> V putInternal(ArrayList<K> ka, ArrayList<V> va,
            K key, V value) {
        int ind = ka.indexOf(key);
        if (-1 == ind) {
            ka.add(key);
            va.add(value);
            return null;
        } else {
            return va.set(ind, value);
        }
    }

    @Override
    public V put(K key, V value) {
        return putInternal(this.keyArray(), this.valueArray(), key, value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {

                    private int posNext = 0;

                    @Override
                    public boolean hasNext() {
                        return this.posNext < SmallMap.this.size();
                    }

                    @Override
                    public Entry<K, V> next() {
                        int pos = this.posNext++;
                        return new SimpleEntry<K, V>(
                                SmallMap.this.keyArray.get(pos),
                                SmallMap.this.valueArray.get(pos));
                    }

                    @Override
                    public void remove() {
                        int pos = --this.posNext;
                        SmallMap.this.keyArray.remove(pos);
                        SmallMap.this.valueArray.remove(pos);
                    }
                };
            }

            @Override
            public int size() {
                return (SmallMap.this.keyArray == null) ? 0 : SmallMap.this.keyArray.size();
            }

        };
    }

}
