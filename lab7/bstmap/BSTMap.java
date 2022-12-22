package bstmap;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    public BSTMap() {
        clear();
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(key, root);
    }

    public boolean containsKey(K key, node<K, V> node) {
        if (node == null) {
            return false;
        }
        if (node.key == key) {
            return true;
        }
        if (key.compareTo(node.key) > 0)
            return containsKey(key, node.right);
        else
            return containsKey(key, node.left);
    }

    @Override
    public V get(K key) {
        return get(key, root);
    }

    public V get(K key, node<K, V> node) {
        if (node == null) {
            return null;
        }
        if (node.key == key) {
            return node.value;
        }
        if (key.compareTo(node.key) > 0)
            return get(key, node.right);
        else
            return get(key, node.left);
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        node<K, V> y = null;
        node<K, V> x = root;
        node<K, V> z = new node(key, value);
        while (x != null) {
            y = x;
            if (key.compareTo(x.key) > 0) {
                x = x.right;
            } else {
                x = x.left;
            }
        }
        z.parent = y;
        if (y == null) {
            root = z;
        } else if (key.compareTo(y.key) < 0) {
            y.left = z;
        } else {
            y.right = z;
        }
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public V remove(K key) {
        return remove(key,get(key));
    }

    @Override
    //algorithm from Introduction of algorithm
    public V remove(K key, V value) {
        if (containsKey(key) && get(key) == value) {
            node<K, V> n = root;
            while (n.key != key) {
                if (key.compareTo(n.key) > 0)
                    n = n.right;
                else
                    n = n.left;
            }
            if(n.left==null){
                transparent(n,n.right);
            } else if (n.right==null) {
                transparent(n,n.left);
            }
            else{
                node<K,V> y=minimum(n.right);
                if(y.parent!=n){
                    transparent(y,y.right);
                    y.right=n.right;
                    y.right.parent=y;
                }
                transparent(n,y);
                y.left=n.left;
                y.left.parent=y;
            }
            size--;
            return value;
        }
        return null;
    }
    private node<K,V> minimum(node<K,V> n){
        while(n.left!=null){
            n=n.left;
        }
        return n;
    }
    public void transparent(node<K, V> u, node<K, V> v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        if (v != null) {
            v.parent = u.parent;
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
        return keySet(s, root);
    }

    public Set<K> keySet(Set<K> s, node<K, V> n) {
        if (n == null) {
            return s;
        } else {
            s.add(n.key);
            keySet(s, n.left);
            keySet(s, n.right);
            return s;
        }
    }

    private class node<K, V> {
        public node(K k, V v) {
            this.key = k;
            this.value = v;
            left = right = parent = null;
        }

        public node<K, V> left;
        public node<K, V> right;
        public node<K, V> parent;
        private K key;
        private V value;
    }

    private node<K, V> root;
    private int size;
}
