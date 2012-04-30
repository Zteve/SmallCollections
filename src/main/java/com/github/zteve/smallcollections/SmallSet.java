/**
 * Simple, lightweight, slow implementation of a set, with small memory footprint.
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

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * A {@link Set} implementation that has a small memory footprint. It is slow,
 * <em>unsynchronised</em>, and unoptimised, but if you want a lot of small
 * sets, which individually have little use, this fits the bill.
 *
 * @author Zteve
 */
public class SmallSet<E> extends AbstractSet<E> {

    private ArrayList<E> arrList = null;

    private ArrayList<E> arrList() {
        if (this.arrList == null)
            this.arrList = new ArrayList<E>(0);
        return this.arrList;
    }
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
        putAllInternal(this.arrList(), s);
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
        return putInternal(this.arrList(), value);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private int posNext = 0;

            @Override
            public boolean hasNext() {
                return this.posNext < SmallSet.this.size();
            }

            @Override
            public E next() {
                int pos = this.posNext++;
                return SmallSet.this.arrList.get(pos);
            }

            @Override
            public void remove() {
                int pos = --this.posNext;
                SmallSet.this.arrList.remove(pos);
            }
        };
    }

    @Override
    public int size() {
        return (this.arrList == null) ? 0 : this.arrList.size();
    }

}
