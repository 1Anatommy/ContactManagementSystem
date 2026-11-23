package contactmanagementsystem;

public class Searcher {

    
    static int linearSearch(DynamicArray<Contact> array, String name) {

        String target = name.trim();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getName().equalsIgnoreCase(target)) {   
                return i;
            }
        }
        return -1;
    }

    
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

    
    static int recursiveBinarySearch(DynamicArray<Contact> array, int id, int low, int high) {

        return -1;
    }
}
