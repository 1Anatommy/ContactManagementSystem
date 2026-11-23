package contactmanagementsystem;

// [ADDED from Ethan] Custom Dynamic Array (no ArrayList). Each Telco Provider gets its own one.
public class DynamicArray<T> {
    private T[] data;
    private int size;
    private int capacity;
    private String label;                       // [ADDED] name of the array (e.g. "Smart") for the debug message

    // [ADDED] When false, the "resized" debug message is hidden (used while loading the file)
    public static boolean debug = true;

    @SuppressWarnings("unchecked")
    public DynamicArray(String label) {
        this.label = label;
        capacity = 4;                           // [CHANGED] starts at 4, same as Matt's original ContactList capacity
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
        if (debug) {
            System.out.println("  [Debug] " + label + " array resized -> new capacity: " + capacity);
        }
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

