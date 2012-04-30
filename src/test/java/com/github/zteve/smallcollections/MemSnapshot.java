/**
 * Memory snapshot for tests.
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

class MemSnapshot {
    private long freeMemory;

    private final static Runtime rt = Runtime.getRuntime();

    private MemSnapshot() {
        this(rt.freeMemory());
    }

    private MemSnapshot(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public static MemSnapshot used(MemSnapshot before, MemSnapshot after) {
        return new MemSnapshot(before.freeMemory - after.freeMemory);
    }

    public static MemSnapshot take() {
        rt.gc();
        rt.gc();  // and again!
        return new MemSnapshot();
    }

    public void retake() {
        rt.gc();
        rt.gc();  // and again!
        this.freeMemory = rt.freeMemory();
    }

    public void print(String hdr) {
        System.out.println(String.format("%40s: %10d free", hdr,
                this.freeMemory));
    }

    public long getFree() {
        return freeMemory;
    }
}