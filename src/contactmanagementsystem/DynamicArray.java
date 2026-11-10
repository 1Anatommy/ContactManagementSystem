package contactmanagementsystem;

public class DynamicArray<T> {
    private T[] data;
    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public DynamicArray() {
        capacity = 2;
        data = (T[]) new Object[capacity];
        size = 0;
    }

    public void add(T element) {
        if (size == capacity) {
            resize();
        }
        data[size] = element;
        size++;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        capacity = capacity * 2;
        T[] newData = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
        System.out.println("  [Debug] Dynamic Array resized -> new capacity: " + capacity);
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return data[index];
    }

    public int size() { return size; }
    public int getCapacity() { return capacity; }
    public boolean isEmpty() { return size == 0; }
}
