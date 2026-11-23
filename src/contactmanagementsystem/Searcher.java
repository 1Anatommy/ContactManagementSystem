package contactmanagementsystem;

// [CHANGED] The methods now receive a DynamicArray<Contact> (one Telco's array) instead of
// a Contact[] + size, because every Telco now has its own Dynamic Array.

public class Searcher {

    // [CHANGED] Linear search now searches by NAME (was by id). Returns the index in that Telco's array, or -1.
    // Uses Ethan's linear-search logic: check one element at a time from the start.
    static int linearSearch(DynamicArray<Contact> array, String name) {

        String target = name.trim();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getName().equalsIgnoreCase(target)) {   // not case-sensitive
                return i;
            }
        }
        return -1;
    }

    // [ADDED] Binary search by contact ID. The array must be sorted by ID (it is, because IDs only go up).
    static int binarySearch(DynamicArray<Contact> array, int id) {

        int low  = 0;
        int high = array.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int midId = array.get(mid).getId();

            if (midId == id) {
                return mid;
            } else if (midId < id) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    // [CHANGED] Only the parameter type changed (DynamicArray instead of Contact[]). Still not written yet.
    static int recursiveBinarySearch(DynamicArray<Contact> array, int id, int low, int high) {

        return -1;
    }
}
