package io.github.Puzzlots.Proximity.util;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FixedArray<T> implements Iterable<T> {
    public T[] items;
    public int size;
    public boolean ordered;

    public FixedArray() {
        this(true, 16);
    }

    public FixedArray(int capacity) {
        this(true, capacity);
    }

    public FixedArray(boolean ordered, int capacity) {
        this.ordered = ordered;
        this.items = (T[]) new Object[capacity];
    }

    public FixedArray(boolean ordered, int capacity, Class arrayType) {
        this.ordered = ordered;
        this.items = (T[]) Array.newInstance(arrayType, capacity);
    }

    public FixedArray(Class arrayType) {
        this(true, 16, arrayType);
    }

    public FixedArray(FixedArray<? extends T> array) {
        this(array.ordered, array.size, array.items.getClass().getComponentType());
        this.size = array.size;
        System.arraycopy(array.items, 0, this.items, 0, this.size);
    }

    public FixedArray(T[] array) {
        this(true, array, 0, array.length);
    }

    public FixedArray(boolean ordered, T[] array, int start, int count) {
        this(ordered, count, array.getClass().getComponentType());
        this.size = count;
        System.arraycopy(array, start, this.items, 0, this.size);
    }

    public void add(T value) {
        T[] items = this.items;
        if (this.size == items.length) {
            items = this.resize(Math.max(8, (int)((float)this.size * 1.75F)));
        }

        items[this.size++] = value;
    }

    public void add(T value1, T value2) {
        T[] items = this.items;
        if (this.size + 1 >= items.length) {
            items = this.resize(Math.max(8, (int)((float)this.size * 1.75F)));
        }

        items[this.size] = value1;
        items[this.size + 1] = value2;
        this.size += 2;
    }

    public void add(T value1, T value2, T value3) {
        T[] items = this.items;
        if (this.size + 2 >= items.length) {
            items = this.resize(Math.max(8, (int)((float)this.size * 1.75F)));
        }

        items[this.size] = value1;
        items[this.size + 1] = value2;
        items[this.size + 2] = value3;
        this.size += 3;
    }

    public void add(T value1, T value2, T value3, T value4) {
        T[] items = this.items;
        if (this.size + 3 >= items.length) {
            items = this.resize(Math.max(8, (int)((float)this.size * 1.8F)));
        }

        items[this.size] = value1;
        items[this.size + 1] = value2;
        items[this.size + 2] = value3;
        items[this.size + 3] = value4;
        this.size += 4;
    }

    public void addAll(FixedArray<? extends T> array) {
        this.addAll(array.items, 0, array.size);
    }

    public void addAll(FixedArray<? extends T> array, int start, int count) {
        if (start + count > array.size) {
            throw new IllegalArgumentException("start + count must be <= size: " + start + " + " + count + " <= " + array.size);
        } else {
            this.addAll(array.items, start, count);
        }
    }

    @SafeVarargs
    public final void addAll(T... array) {
        this.addAll(array, 0, array.length);
    }

    public void addAll(T[] array, int start, int count) {
        T[] items = this.items;
        int sizeNeeded = this.size + count;
        if (sizeNeeded > items.length) {
            items = this.resize(Math.max(Math.max(8, sizeNeeded), (int)((float)this.size * 1.75F)));
        }

        System.arraycopy(array, start, items, this.size, count);
        this.size = sizeNeeded;
    }

    public T get(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        } else {
            return this.items[index];
        }
    }

    public void set(int index, T value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        } else {
            this.items[index] = value;
        }
    }

    public void insert(int index, T value) {
        if (index > this.size) {
            throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + this.size);
        } else {
            T[] items = this.items;
            if (this.size == items.length) {
                items = this.resize(Math.max(8, (int)((float)this.size * 1.75F)));
            }

            if (this.ordered) {
                System.arraycopy(items, index, items, index + 1, this.size - index);
            } else {
                items[this.size] = items[index];
            }

            ++this.size;
            items[index] = value;
        }
    }

    public void insertRange(int index, int count) {
        if (index > this.size) {
            throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + this.size);
        } else {
            int sizeNeeded = this.size + count;
            if (sizeNeeded > this.items.length) {
                this.items = this.resize(Math.max(Math.max(8, sizeNeeded), (int)((float)this.size * 1.75F)));
            }

            System.arraycopy(this.items, index, this.items, index + count, this.size - index);
            this.size = sizeNeeded;
        }
    }

    public void swap(int first, int second) {
        if (first >= this.size) {
            throw new IndexOutOfBoundsException("first can't be >= size: " + first + " >= " + this.size);
        } else if (second >= this.size) {
            throw new IndexOutOfBoundsException("second can't be >= size: " + second + " >= " + this.size);
        } else {
            T[] items = this.items;
            T firstValue = items[first];
            items[first] = items[second];
            items[second] = firstValue;
        }
    }

    public boolean contains(@Nullable T value, boolean identity) {
        T[] items = this.items;
        int i = this.size - 1;
        if (!identity && value != null) {
            while(i >= 0) {
                if (value.equals(items[i--])) {
                    return true;
                }
            }
        } else {
            while(i >= 0) {
                if (items[i--] == value) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean containsAll(FixedArray<? extends T> values, boolean identity) {
        T[] items = values.items;
        int i = 0;

        for(int n = values.size; i < n; ++i) {
            if (!this.contains(items[i], identity)) {
                return false;
            }
        }

        return true;
    }

    public boolean containsAny(FixedArray<? extends T> values, boolean identity) {
        T[] items = values.items;
        int i = 0;

        for(int n = values.size; i < n; ++i) {
            if (this.contains(items[i], identity)) {
                return true;
            }
        }

        return false;
    }

    public int indexOf(@Nullable T value, boolean identity) {
        T[] items = this.items;
        int i;
        int n;
        if (!identity && value != null) {
            i = 0;

            for(n = this.size; i < n; ++i) {
                if (value.equals(items[i])) {
                    return i;
                }
            }
        } else {
            i = 0;

            for(n = this.size; i < n; ++i) {
                if (items[i] == value) {
                    return i;
                }
            }
        }

        return -1;
    }

    public int lastIndexOf(@Nullable T value, boolean identity) {
        T[] items = this.items;
        int i;
        if (!identity && value != null) {
            for(i = this.size - 1; i >= 0; --i) {
                if (value.equals(items[i])) {
                    return i;
                }
            }
        } else {
            for(i = this.size - 1; i >= 0; --i) {
                if (items[i] == value) {
                    return i;
                }
            }
        }

        return -1;
    }

    public boolean removeValue(@Nullable T value, boolean identity) {
        T[] items = this.items;
        int i;
        int n;
        if (!identity && value != null) {
            i = 0;

            for(n = this.size; i < n; ++i) {
                if (value.equals(items[i])) {
                    this.removeIndex(i);
                    return true;
                }
            }
        } else {
            i = 0;

            for(n = this.size; i < n; ++i) {
                if (items[i] == value) {
                    this.removeIndex(i);
                    return true;
                }
            }
        }

        return false;
    }

    public T removeIndex(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        } else {
            T[] items = this.items;
            T value = items[index];
            --this.size;
            if (this.ordered) {
                System.arraycopy(items, index + 1, items, index, this.size - index);
            } else {
                items[index] = items[this.size];
            }

            items[this.size] = null;
            return value;
        }
    }

    public void removeRange(int start, int end) {
        int n = this.size;
        if (end >= n) {
            throw new IndexOutOfBoundsException("end can't be >= size: " + end + " >= " + this.size);
        } else if (start > end) {
            throw new IndexOutOfBoundsException("start can't be > end: " + start + " > " + end);
        } else {
            T[] items = this.items;
            int count = end - start + 1;
            int lastIndex = n - count;
            int i;
            if (this.ordered) {
                System.arraycopy(items, start + count, items, start, n - (start + count));
            } else {
                i = Math.max(lastIndex, end + 1);
                System.arraycopy(items, i, items, start, n - i);
            }

            for(i = lastIndex; i < n; ++i) {
                items[i] = null;
            }

            this.size = n - count;
        }
    }

    public boolean removeAll(FixedArray<? extends T> array, boolean identity) {
        int size = this.size;
        int startSize = size;
        T[] items = this.items;
        int i;
        int n;
        Object item;
        int ii;
        if (identity) {
            i = 0;

            for(n = array.size; i < n; ++i) {
                item = array.get(i);

                for(ii = 0; ii < size; ++ii) {
                    if (item == items[ii]) {
                        this.removeIndex(ii);
                        --size;
                        break;
                    }
                }
            }
        } else {
            i = 0;

            for(n = array.size; i < n; ++i) {
                item = array.get(i);

                for(ii = 0; ii < size; ++ii) {
                    if (item.equals(items[ii])) {
                        this.removeIndex(ii);
                        --size;
                        break;
                    }
                }
            }
        }

        return size != startSize;
    }

    public T pop() {
        if (this.size == 0) {
            throw new IllegalStateException("Array is empty.");
        } else {
            --this.size;
            T item = this.items[this.size];
            this.items[this.size] = null;
            return item;
        }
    }

    public T peek() {
        if (this.size == 0) {
            throw new IllegalStateException("Array is empty.");
        } else {
            return this.items[this.size - 1];
        }
    }

    public T first() {
        if (this.size == 0) {
            throw new IllegalStateException("Array is empty.");
        } else {
            return this.items[0];
        }
    }

    public boolean notEmpty() {
        return this.size > 0;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void clear() {
        Arrays.fill(this.items, 0, this.size, (Object)null);
        this.size = 0;
    }

    public T[] shrink() {
        if (this.items.length != this.size) {
            this.resize(this.size);
        }

        return this.items;
    }

    public T[] ensureCapacity(int additionalCapacity) {
        if (additionalCapacity < 0) {
            throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
        } else {
            int sizeNeeded = this.size + additionalCapacity;
            if (sizeNeeded > this.items.length) {
                this.resize(Math.max(Math.max(8, sizeNeeded), (int)((float)this.size * 1.75F)));
            }

            return this.items;
        }
    }

    public T[] setSize(int newSize) {
        this.truncate(newSize);
        if (newSize > this.items.length) {
            this.resize(Math.max(8, newSize));
        }

        this.size = newSize;
        return this.items;
    }

    protected T[] resize(int newSize) {
        T[] items = this.items;
        T[] newItems = (T[]) Array.newInstance(items.getClass().getComponentType(), newSize);
        System.arraycopy(items, 0, newItems, 0, Math.min(this.size, newItems.length));
        this.items = newItems;
        return newItems;
    }

    public void reverse() {
        T[] items = this.items;
        int i = 0;
        int lastIndex = this.size - 1;

        for(int n = this.size / 2; i < n; ++i) {
            int ii = lastIndex - i;
            T temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }

    }

    public ArrayIterator<T> iterator() {
        return new ArrayIterator(this, true);
    }

    public void truncate(int newSize) {
        if (newSize < 0) {
            throw new IllegalArgumentException("newSize must be >= 0: " + newSize);
        } else if (this.size > newSize) {
            for(int i = newSize; i < this.size; ++i) {
                this.items[i] = null;
            }

            this.size = newSize;
        }
    }

    public int hashCode() {
        if (!this.ordered) {
            return super.hashCode();
        } else {
            Object[] items = this.items;
            int h = 1;
            int i = 0;

            for(int n = this.size; i < n; ++i) {
                h *= 31;
                Object item = items[i];
                if (item != null) {
                    h += item.hashCode();
                }
            }

            return h;
        }
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (!this.ordered) {
            return false;
        } else if (!(object instanceof FixedArray)) {
            return false;
        } else {
            FixedArray array = (FixedArray)object;
            if (!array.ordered) {
                return false;
            } else {
                int n = this.size;
                if (n != array.size) {
                    return false;
                } else {
                    Object[] items1 = this.items;
                    Object[] items2 = array.items;
                    int i = 0;

                    while(true) {
                        if (i >= n) {
                            return true;
                        }

                        Object o1 = items1[i];
                        Object o2 = items2[i];
                        if (o1 == null) {
                            if (o2 != null) {
                                break;
                            }
                        } else if (!o1.equals(o2)) {
                            break;
                        }

                        ++i;
                    }

                    return false;
                }
            }
        }
    }

    public boolean equalsIdentity(Object object) {
        if (object == this) {
            return true;
        } else if (!this.ordered) {
            return false;
        } else if (!(object instanceof FixedArray)) {
            return false;
        } else {
            FixedArray array = (FixedArray)object;
            if (!array.ordered) {
                return false;
            } else {
                int n = this.size;
                if (n != array.size) {
                    return false;
                } else {
                    Object[] items1 = this.items;
                    Object[] items2 = array.items;

                    for(int i = 0; i < n; ++i) {
                        if (items1[i] != items2[i]) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }
    }

    public String toString() {
        if (this.size == 0) {
            return "[]";
        } else {
            T[] items = this.items;
            StringBuilder buffer = new StringBuilder(32);
            buffer.append('[');
            buffer.append(items[0]);

            for(int i = 1; i < this.size; ++i) {
                buffer.append(", ");
                buffer.append(items[i]);
            }

            buffer.append(']');
            return buffer.toString();
        }
    }

    public String toString(String separator) {
        if (this.size == 0) {
            return "";
        } else {
            T[] items = this.items;
            StringBuilder buffer = new StringBuilder(32);
            buffer.append(items[0]);

            for(int i = 1; i < this.size; ++i) {
                buffer.append(separator);
                buffer.append(items[i]);
            }

            return buffer.toString();
        }
    }

    public static <T> FixedArray<T> of(Class<T> arrayType) {
        return new FixedArray(arrayType);
    }

    public static <T> FixedArray<T> of(boolean ordered, int capacity, Class<T> arrayType) {
        return new FixedArray(ordered, capacity, arrayType);
    }

    public static <T> FixedArray<T> with(T... array) {
        return new FixedArray(array);
    }

    public static class ArrayIterator<T> implements Iterator<T>, Iterable<T> {
        private final FixedArray<T> array;
        private final boolean allowRemove;
        int index;
        boolean valid;

        public ArrayIterator(FixedArray<T> array) {
            this(array, true);
        }

        public ArrayIterator(FixedArray<T> array, boolean allowRemove) {
            this.valid = true;
            this.array = array;
            this.allowRemove = allowRemove;
        }

        public boolean hasNext() {
            return this.index < this.array.size;
        }

        public T next() {
            if (this.index >= this.array.size) {
                throw new NoSuchElementException(String.valueOf(this.index));
            } else {
                return this.array.items[this.index++];
            }
        }

        public void remove() {
            if (!this.allowRemove) {
                throw new RuntimeException("Remove not allowed.");
            } else {
                --this.index;
                this.array.removeIndex(this.index);
            }
        }

        public void reset() {
            this.index = 0;
        }

        public ArrayIterator<T> iterator() {
            return this;
        }
    }

    public static class ArrayIterable<T> implements Iterable<T> {
        private final FixedArray<T> array;
        private final boolean allowRemove;
        private transient ArrayIterator<T> iterator1;
        private transient ArrayIterator<T> iterator2;

        public ArrayIterable(FixedArray<T> array) {
            this(array, true);
        }

        public ArrayIterable(FixedArray<T> array, boolean allowRemove) {
            this.array = array;
            this.allowRemove = allowRemove;
        }

        public ArrayIterator<T> iterator() {
            return new ArrayIterator(this.array, this.allowRemove);
        }
    }
}