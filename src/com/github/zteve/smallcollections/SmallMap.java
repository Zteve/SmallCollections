/**
 * Simple, lightweight, slow implementation of a map, with small memory footprint.
 *
 * @author Zteve.Powell
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
 */
public class SmallMap<K, V> extends AbstractMap<K, V> {

    private final ArrayList<K> keyArray = new ArrayList<K>(0);
    private final ArrayList<V> valueArray = new ArrayList<V>(0);

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
        putAllInternal(this.keyArray, this.valueArray, m);
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
        return putInternal(this.keyArray, this.valueArray, key, value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {

                    private int position = 0;

                    @Override
                    public boolean hasNext() {
                        return this.position < SmallMap.this.size();
                    }

                    @Override
                    public Entry<K, V> next() {
                        int pos = this.position++;
                        return new SimpleEntry<K, V>(
                                SmallMap.this.keyArray.get(pos),
                                SmallMap.this.valueArray.get(pos));
                    }

                    @Override
                    public void remove() {
                        int pos = --this.position;
                        SmallMap.this.keyArray.remove(pos);
                        SmallMap.this.valueArray.remove(pos);
                    }
                };
            }

            @Override
            public int size() {
                return SmallMap.this.keyArray.size();
            }

        };
    }

}
