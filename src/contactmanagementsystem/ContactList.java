package contactmanagementsystem;

// [CHANGED] Matt's single Contact[] (+ its own resize()) was replaced by THREE Dynamic Arrays,
// one per Telco Provider (Smart / Globe / DITO). The resizing now lives inside DynamicArray.
// Methods that Matt already had keep their names so ContactController / ContactMenu still work.

public class ContactList {

    private DynamicArray<Contact> smartList = new DynamicArray<>("Smart");
    private DynamicArray<Contact> globeList = new DynamicArray<>("Globe");
    private DynamicArray<Contact> ditoList  = new DynamicArray<>("DITO");

    // [ADDED] Picks the Dynamic Array that belongs to a Telco
    private DynamicArray<Contact> arrayOf(TelecoProvider provider) {
        if (provider == TelecoProvider.SMART) return smartList;
        if (provider == TelecoProvider.GLOBE) return globeList;
        return ditoList;
    }

    // Total size of all three arrays
    public int getSize() {
        return smartList.size() + globeList.size() + ditoList.size();
    }

    // Total capacity of all three arrays
    public int getCapacity() {
        return smartList.getCapacity() + globeList.getCapacity() + ditoList.getCapacity();
    }

    // [ADDED] Size / capacity of ONE Telco's array
    public int getSize(TelecoProvider provider)     { return arrayOf(provider).size(); }
    public int getCapacity(TelecoProvider provider) { return arrayOf(provider).getCapacity(); }

    // [CHANGED] The contact goes into the array of its own Telco
    public void insertAtEnd(Contact c) {
        arrayOf(c.getProvider()).add(c);
    }

    // [CHANGED] Position over ALL contacts, in this order: Smart, then Globe, then DITO
    // (so ContactMenu.showContacts() still works the same way)
    public Contact get(int index) {
        if (index < 0) {
            return null;
        }
        for (int i = 0; i < TelecoProvider.ALL.length; i++) {
            DynamicArray<Contact> array = arrayOf(TelecoProvider.ALL[i]);
            if (index < array.size()) {
                return array.get(index);
            }
            index = index - array.size();
        }
        return null;
    }

    // [ADDED] Get a contact using its index inside ONE Telco's array
    public Contact get(TelecoProvider provider, int index) {
        DynamicArray<Contact> array = arrayOf(provider);
        if (index < 0 || index >= array.size()) {
            return null;
        }
        return array.get(index);
    }

    // [CHANGED] true only if EVERY Telco array is sorted by ID
    public boolean isSortedById() {
        for (int i = 0; i < TelecoProvider.ALL.length; i++) {
            if (!isSortedById(TelecoProvider.ALL[i])) {
                return false;
            }
        }
        return true;
    }

    // [ADDED] Is ONE Telco's array sorted by ID? (needed before using binary search on it)
    public boolean isSortedById(TelecoProvider provider) {
        DynamicArray<Contact> array = arrayOf(provider);
        for (int i = 0; i < array.size() - 1; i++) {
            if (array.get(i).getId() > array.get(i + 1).getId()) {
                return false;
            }
        }
        return true;
    }

    // [CHANGED] Linear search = by NAME, inside the chosen Telco's array
    public int linearSearch(TelecoProvider provider, String name) {
        return Searcher.linearSearch(arrayOf(provider), name);
    }

    // [CHANGED] Binary search = by ID, inside the chosen Telco's array
    public int binarySearch(TelecoProvider provider, int id) {
        return Searcher.binarySearch(arrayOf(provider), id);
    }

    // [CHANGED] Only gets the Telco's array now. Searcher.recursiveBinarySearch is still not written.
    public int recursiveBinarySearch(TelecoProvider provider, int id) {
        DynamicArray<Contact> array = arrayOf(provider);
        return Searcher.recursiveBinarySearch(array, id, 0, array.size() - 1);
    }

    // [ADDED from Ethan] Corresponding number of the NEXT contact of this Telco
    // (Ethan's generateCorrespondingNumber): Globe -> 01, 02 ...  Smart -> 001, 002 ...  DITO -> 1, 2 ...
    public String nextCorrespondingNumber(TelecoProvider provider) {
        int nextNumber = arrayOf(provider).size() + 1;
        String number = String.valueOf(nextNumber);
        while (number.length() < provider.getCodeLength()) {   // add leading zeros by hand
            number = "0" + number;
        }
        return number;
    }

    // [ADDED] Empties all three arrays (used when loading the file again)
    public void clear() {
        smartList = new DynamicArray<>("Smart");
        globeList = new DynamicArray<>("Globe");
        ditoList  = new DynamicArray<>("DITO");
    }

    // Matt's methods below are NOT changed (still not written yet)
    public boolean insertAtPosition(int index, Contact c) {

        return false;
    }

    public boolean delete(int index) {

        return false;
    }
}

