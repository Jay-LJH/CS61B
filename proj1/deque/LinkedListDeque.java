package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> {
    public LinkedListDeque() {
        start = end = null;
        size = 0;
    }

    public LinkedListDeque(T i) {
        start = end = new Node<>(i, null, null);
        size = 1;
    }

    @Override
    public void addFirst(T i) {
        if (size == 0) {
            start = end = new Node<>(i, null, null);
            size += 1;
            return;
        }
        start.before = new Node<>(i, start, null);
        start = start.before;
        size += 1;
    }

    @Override
    public void addLast(T i) {
        if (size == 0) {
            start = end = new Node<>(i, null, null);
            size += 1;
            return;
        }
        end.next = new Node<>(i, null, end);
        end = end.next;
        size += 1;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        if (size > 0) {
            Node p = start;
            System.out.print(p.item + " ");
            while (p.next != null) {
                p = p.next;
                System.out.print(p.item + " ");
            }
        }
        System.out.print("\n");
    }

    @Override
    public T removeFirst() {
        if (size > 1) {
            T i = start.item;
            start = start.next;
            size -= 1;
            return (T) i;
        }
        if (size == 1) {
            T i = start.item;
            start = end = null;
            size -= 1;
            return (T) i;
        }
        return null;
    }

    @Override
    public T removeLast() {
        if (size > 1) {
            T i = end.item;
            end = end.before;
            size -= 1;
            return (T) i;
        }
        if (size == 1) {
            T i = start.item;
            start = end = null;
            size -= 1;
            return (T) i;
        }
        return null;
    }

    public interface Iterator<T> {
        T next();

        boolean hasNext();

        T index(int n);
    }

    public Iterator<T> iterator() {
        return new IteratorImpl<>(start);
    }

    public class IteratorImpl<T> implements Iterator<T> {
        public IteratorImpl(Node n) {
            p = n;
            index = 0;
        }

        public T next() {
            T temp = p.item;
            p = p.next;
            index++;
            return temp;
        }

        ;

        public boolean hasNext() {
            return p.next != null;
        }

        public T index(int n) {
            while (n > index && this.hasNext()) {
                this.next();
            }
            if (n == index) {
                return this.next();
            } else
                return null;
        }

        private Node<T> p;
        private int index;
    }

    @Override
    public T get(int index) {
        Iterator<T> iterator = this.iterator();
        return iterator.index(index);
    }

    public T getRecursive(int index) {
        Node<T> p = start;
        while (index > 0 && p != null) {
            index--;
            p = p.next;
        }
        if (p != null) {
            return p.item;
        }
        return null;
    }

    public boolean equals(Object o) {
        if (o instanceof LinkedListDeque) {
            Iterator<T> iterator = this.iterator();
            Iterator<T> iterator1 = ((LinkedListDeque<T>) o).iterator();
            while (iterator1.hasNext() && iterator.hasNext()) {
                if (iterator1.next() != iterator.next()) {
                    return false;
                }
            }
            if (!iterator1.hasNext() && !iterator.hasNext()) {
                return true;
            }
        }
        return false;
    }
    public interface Iterable<T>{
        java.util.Iterator<T> iterator();
    }
    private class Node<T> {
        public Node(T i, Node n, Node b) {
            item = i;
            next = n;
            before = b;
        }

        private Node<T> next;
        private Node before;
        private T item;
    }

    private Node<T> start;
    private Node<T> end;
    private int size;
}
