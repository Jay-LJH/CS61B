package hashmap;

import java.util.*;

import static java.lang.Math.abs;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int initialSize;
    private int size;
    private double loadFactor;

    /**
     * Constructors
     */
    public MyHashMap() {
        initialSize = 16;
        loadFactor = 0.75;
        size = 0;
        buckets = createTable(initialSize);
    }

    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        loadFactor = 0.75;
        size = 0;
        buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.loadFactor = maxLoad;
        size = 0;
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] n =new Collection[tableSize];
        for (int i = 0; i < tableSize; i++)
            n[i] = createBucket();
        return n;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        buckets = createTable(initialSize);
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        int n = hash(key);
        Collection<Node> bucket = buckets[n];
        Iterator<Node> i = bucket.iterator();
        while (i.hasNext()) {
            Node temp = i.next();
            if (temp.key == key)
                return true;
        }
        return false;
    }

    @Override
    public V get(K key) {
        int n = hash(key);
        Collection<Node> bucket = buckets[n];
        Iterator<Node> i = bucket.iterator();
        while (i.hasNext()) {
            Node temp = i.next();
            if (temp.key == key)
                return temp.value;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int n = hash(key);
        Collection<Node> bucket = buckets[n];
        for (Node node : bucket) {
            if (node.key == key) {
                node.value = value;
                return;
            }
        }
        bucket.add(new Node(key, value));
        size++;
        if (size > initialSize * loadFactor) {
            MyHashMap<K,V> temp=new MyHashMap<>(initialSize*2,loadFactor);
            Iterator<K> i=this.iterator();
            while(i.hasNext()){
                K k=i.next();
                temp.put(k,get(k));
            }
            this.initialSize=temp.initialSize;
            this.size=temp.size;
            this.buckets=temp.buckets;
        }
    }

    @Override
    public Iterator<K> iterator() {
        Set<K> s = keySet();
        return s.iterator();
    }

    @Override
    public Set<K> keySet() {
        Set<K> s = new HashSet<>();
        for (int i = 0; i < initialSize; i++) {
            for (Node node : buckets[i]) {
                s.add(node.key);
            }
        }
        return s;
    }

    @Override
    public V remove(K key) {
        int n = hash(key);
        Collection<Node> bucket = buckets[n];
        for(Node node:bucket){
            if(node.key==key){
                bucket.remove(node);
                return node.value;
            }
        }
        return null;
    }
    @Override
    public V remove(K key,V value) {
        int n = hash(key);
        Collection<Node> bucket = buckets[n];
        for(Node node:bucket){
            if(node.key==key && node.value==value){
                bucket.remove(node);
                return node.value;
            }
        }
        return null;
    }
    private int hash(K key) {
        return abs(key.hashCode()) % initialSize;
    }
}
