`SmallCollections` is a response to a [Stack Overflow
question](http://stackoverflow.com/questions/8835928/which-implementation-of-mapk-v-should-i-use)
I asked a little while ago.

In failing to find a memory-lite implementation of `Map<K,V>` in Java I
eventually decided to write my own, which was a rewarding and
unexpectedly interesting experience.

This is the result.

Since I (needed to) include an implementation of `SmallSet`, I've
expanded the brief to include other collections. Of course, it now needs
work to finish the job.

## The Main Classes

The primary classes this project provides are `SmallMap<K,V>` and
`SmallSet<E>`. They implement the collection interfaces `Map<K,V>` and
`Set<E>` respectively and include constructors (approximately) of the
form:

 SmallMap(Map<K,V> map)

and

 SmallSet(Set<E> set)

to allow easy construction from other collections of the same form.

## The Basic Idea

The `SmallMap` is the prototypical case. It is implemented with
`ArrayList`s (two of them) which by default have zero allocation. One is
for the keys and the other for the corresponding values. The two lists
are always the same size.

Iteration is over-ridden so as to keep the two lists in-step (the
alternative solution with one list would have meant implementing an
Entry type which would have increased the memory overhead). Iteration is
performed over the key arraylist, and updates modify corresponding
elements in both lists.

Searches, inserts, deletions, and so on, all use a linear scan of the
arraylists and the implementation of the other `Map` methods is taken
from `AbstractMap<K,V>`.

There is no synchronisation, so this implementation is _not_
thread-safe.