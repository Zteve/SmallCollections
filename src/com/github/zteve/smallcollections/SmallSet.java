/**
 * Simple, lightweight, slow implementation of a set, with small memory footprint.
 *
 * @author Zteve.Powell
 */
package com.github.zteve.smallcollections;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * A {@link Set} implementation that has a small memory footprint. It is slow,
 * <em>unsynchronised</em>, and unoptimised, but if you want a lot of small
 * sets, which individually have little use, this fits the bill.
 */
public class SmallSet<E> extends AbstractSet<E> {

    private final ArrayList<E> arrList = new ArrayList<E>(0);

    /**
     * Constructs a new, empty, <code>SmallSet</code>.
     */
    public SmallSet() {
    }

    /**
     * Constructs a new <code>SmallSet</code> with the same elements as the
     * specified <code>Set</code>.
     *
     * @param s
     *            the set whose elements are to be placed in this set
     * @throws NullPointerException
     *             if the specified set is null
     */
    public SmallSet(Set<? extends E> s) {
        putAllInternal(this.arrList, s);
    }

    private static <E> void putAllInternal(ArrayList<E> al, Set<? extends E> s) {
        for (Iterator<? extends E> i = s.iterator(); i.hasNext();) {
            putInternal(al, i.next());
        }
    }

    private static <E> boolean putInternal(ArrayList<E> al, E value) {
        if (al.contains(value))
            return false;
        al.add(value);
        return true;
    }

    @Override
    public boolean add(E value) {
        return putInternal(this.arrList, value);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private int position = 0;

            @Override
            public boolean hasNext() {
                return this.position < SmallSet.this.size();
            }

            @Override
            public E next() {
                int pos = this.position++;
                return SmallSet.this.arrList.get(pos);
            }

            @Override
            public void remove() {
                int pos = --this.position;
                SmallSet.this.arrList.remove(pos);
            }
        };
    }

    @Override
    public int size() {
        return this.arrList.size();
    }

}
