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
 
    public int[] linearSearch(TelecoProvider provider, String name) {
        return Searcher.linearSearch(arrayOf(provider), name);
    }
 
    public int binarySearch(TelecoProvider provider, int id) {
        return Searcher.binarySearch(arrayOf(provider), id);
    }
 
    public int recursiveBinarySearch(TelecoProvider provider, int code) {
        DynamicArray<Contact> array = arrayOf(provider);
        return Searcher.recursiveBinarySearch(array, code, 0, array.size() - 1);
    }
 
    public int nextId(TelecoProvider provider) {
        DynamicArray<Contact> array = arrayOf(provider);
        int biggest = 0;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getId() > biggest) {
                biggest = array.get(i).getId();
            }
        }
        return biggest + 1;
    }
 
    public String formatCode(TelecoProvider provider, int number) {
        String text = String.valueOf(number);
        while (text.length() < provider.getCodeLength()) {
            text = "0" + text;
        }
        return text;
    }
 
    public void clear() {
        smartList = new DynamicArray<>("Smart");
        globeList = new DynamicArray<>("Globe");
        ditoList  = new DynamicArray<>("DITO");
    }
 
    public boolean insertAtPosition(int index, Contact c) {
 
        DynamicArray<Contact> array = arrayOf(c.getProvider());
        if (index < 0 || index > array.size()) {
            return false;
        }
        array.insert(index, c);
        renumber(c.getProvider());
        return true;
    }
 
    public boolean delete(int index) {
 
        if (index < 0) {
            return false;
        }
        for (int i = 0; i < TelecoProvider.ALL.length; i++) {
            TelecoProvider provider = TelecoProvider.ALL[i];
            DynamicArray<Contact> array = arrayOf(provider);
            if (index < array.size()) {
                array.remove(index);
                renumber(provider);
                return true;
            }
            index = index - array.size();
        }
        return false;
    }
 
    private void renumber(TelecoProvider provider) {
        DynamicArray<Contact> array = arrayOf(provider);
        for (int i = 0; i < array.size(); i++) {
            array.get(i).setId(i + 1);
            array.get(i).setCorrespondingNumber(formatCode(provider, i + 1));
        }
    }
}