package contactmanagementsystem;

public class Searcher {
 
    static int[] linearSearch(DynamicArray<Contact> array, String name) {
 
        String[] words = name.trim().toLowerCase().split("\\s+");
 
        int[] temp = new int[array.size()];
        int count = 0;
 
        
        for (int i = 0; i < array.size(); i++) {
            String fullName = array.get(i).getName().toLowerCase();
 
            boolean allWordsFound = true;
            for (int w = 0; w < words.length; w++) {
                if (words[w].length() > 0 && fullName.indexOf(words[w]) == -1) {
                    allWordsFound = false;
                    break;
                }
            }
            if (allWordsFound) {
                temp[count] = i;
                count++;
            }
        }
 
        int[] matches = new int[count];
        for (int i = 0; i < count; i++) {
            matches[i] = temp[i];
        }
        return matches;
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
 
    
    static int recursiveBinarySearch(DynamicArray<Contact> array, int code, int low, int high) {
 
        if (low > high) {                                   
            return -1;
        }
 
        int mid = low + (high - low) / 2;
        int midCode = Integer.parseInt(array.get(mid).getCorrespondingNumber());   
 
        if (midCode == code) {                             
            return mid;
        } else if (midCode < code) {
            return recursiveBinarySearch(array, code, mid + 1, high);   
        } else {
            return recursiveBinarySearch(array, code, low, mid - 1);    
        }
    }
}
