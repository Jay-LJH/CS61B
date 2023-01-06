package deque;

import java.util.Arrays;
import java.util.Iterator;

import static java.lang.System.arraycopy;

public class ArrayDeque<T> implements Deque<T> {
    public ArrayDeque() {
        array = (T[]) new Object[8];
        start = end = size = 0;
        length = 8;
    }
    @Override
    public void addFirst(T item) {
        if (start == 0) {
            T[] temp = (T[]) new Object[length * 2];
            arraycopy(array, 0, temp, length, size);
            array = temp;
            start = length - 1;
            size++;
            end += length;
            length = array.length;
            array[start] = item;
        } else {
            start--;
            size++;
            array[start] = item;
        }
    }
    @Override
    public void addLast(T item) {
        if (end == length) {
            T[] temp = (T[]) new Object[length * 2];
            arraycopy(array, start, temp, start, size);
            array = temp;
            array[end] = item;
            size++;
            end++;
            length = array.length;
        } else {
            array[end] = item;
            end++;
            size++;
        }
    }
    @Override
    public T removeFirst() {
        if (size > 0) {
            T res = array[start];
            start++;
            size--;
            if (size < (length) / 4) {
                T[] temp = (T[]) new Object[length / 2];
                arraycopy(array, start, temp, start % ((length) / 4), size);
                array = temp;
                start = start % ((length) / 4);
                end = size + start;
                length = array.length;
            }
            return (T) res;
        }
        return null;
    }
    @Override
    public T removeLast() {
        if (size > 0) {
            T res = array[end - 1];
            end--;
            size--;
            if (size < (length) / 4) {
                T[] temp = (T[]) new Object[length / 2];
                arraycopy(array, start, temp, start % ((length) / 4), size);
                array = temp;
                start = start % ((length) / 4);
                end = size + start;
                length = array.length;
            }
            return (T) res;
        }
        return null;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(array[start + i] + " ");
        }
        System.out.print("\n");
    }
    @Override
    public T get(int index) {
        return array[start + index];
    }

    public Iterator<T> iterator() {
        T[] temp = (T[]) new Object[size];
        arraycopy(array, start, temp, 0, size);
        return Arrays.stream(temp).iterator();
    }
    public interface Iterable<T>{
        Iterator<T> iterator();
    }
    public boolean equals(Object o) {
        if (o instanceof ArrayDeque) {
            Iterator<T> iterator = this.iterator();
            Iterator<T> iterator1 = ((ArrayDeque) o).iterator();
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

    private T[] array;
    private int start;
    private int end;
    private int size;
    private int length;
}
