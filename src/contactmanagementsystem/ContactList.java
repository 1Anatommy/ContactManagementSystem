package contactmanagementsystem;


public class ContactList {

    private DynamicArray<Contact> smartList = new DynamicArray<>("Smart");
    private DynamicArray<Contact> globeList = new DynamicArray<>("Globe");
    private DynamicArray<Contact> ditoList  = new DynamicArray<>("DITO");

   
    private DynamicArray<Contact> arrayOf(TelecoProvider provider) {
        if (provider == TelecoProvider.SMART) return smartList;
        if (provider == TelecoProvider.GLOBE) return globeList;
        return ditoList;
    }

    
    public int getSize() {
        return smartList.size() + globeList.size() + ditoList.size();
    }

    
    public int getCapacity() {
        return smartList.getCapacity() + globeList.getCapacity() + ditoList.getCapacity();
    }

    
    public int getSize(TelecoProvider provider)     { return arrayOf(provider).size(); }
    public int getCapacity(TelecoProvider provider) { return arrayOf(provider).getCapacity(); }

    
    public void insertAtEnd(Contact c) {
        arrayOf(c.getProvider()).add(c);
    }

    
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

    
    public Contact get(TelecoProvider provider, int index) {
        DynamicArray<Contact> array = arrayOf(provider);
        if (index < 0 || index >= array.size()) {
            return null;
        }
        return array.get(index);
    }

    
    public boolean isSortedById() {
        for (int i = 0; i < TelecoProvider.ALL.length; i++) {
            if (!isSortedById(TelecoProvider.ALL[i])) {
                return false;
            }
        }
        return true;
    }

    
    public boolean isSortedById(TelecoProvider provider) {
        DynamicArray<Contact> array = arrayOf(provider);
        for (int i = 0; i < array.size() - 1; i++) {
            if (array.get(i).getId() > array.get(i + 1).getId()) {
                return false;
            }
        }
        return true;
    }

    
    public int linearSearch(TelecoProvider provider, String name) {
        return Searcher.linearSearch(arrayOf(provider), name);
    }

    
    public int binarySearch(TelecoProvider provider, int id) {
        return Searcher.binarySearch(arrayOf(provider), id);
    }

  
    public int recursiveBinarySearch(TelecoProvider provider, int id) {
        DynamicArray<Contact> array = arrayOf(provider);
        return Searcher.recursiveBinarySearch(array, id, 0, array.size() - 1);
    }

   
    public String nextCorrespondingNumber(TelecoProvider provider) {
        int nextNumber = arrayOf(provider).size() + 1;
        String number = String.valueOf(nextNumber);
        while (number.length() < provider.getCodeLength()) {   
            number = "0" + number;
        }
        return number;
    }

    
    public void clear() {
        smartList = new DynamicArray<>("Smart");
        globeList = new DynamicArray<>("Globe");
        ditoList  = new DynamicArray<>("DITO");
    }

  
    public boolean insertAtPosition(int index, Contact c) {

        return false;
    }

    public boolean delete(int index) {

        return false;
    }
}

