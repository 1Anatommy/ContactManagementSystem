package contactmanagementsystem;

public class SearchingAlgorithm {
    public static int linearSearch(DynamicArray<Contact> contacts,
                                   TelecoProvider provider,
                                   String correspondingNumber) {
        for (int i = 0; i < contacts.size(); i++) {
            Contact c = contacts.get(i);
            if (c.getProvider() == provider
                    && c.getCorrespondingNumber().equals(correspondingNumber)) {
                return i;
            }
        }
        return -1;
    }
}
