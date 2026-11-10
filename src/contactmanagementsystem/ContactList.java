package contactmanagementsystem;

public class ContactList {
    private DynamicArray<Contact> contacts;

    public ContactList() {
        contacts = new DynamicArray<>();
    }

    public void addContact(String name, String phoneNumber, TelecoProvider provider) {
        String correspondingNumber = generateCorrespondingNumber(provider);
        Contact contact = new Contact(name, phoneNumber, provider, correspondingNumber);
        contacts.add(contact);

        System.out.println("\n  ✔ Contact added successfully!");
        System.out.println("  " + contact);
        System.out.println("  Index in system: " + (contacts.size() - 1));
    }

    private String generateCorrespondingNumber(TelecoProvider provider) {
        int count = 0;
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getProvider() == provider) {
                count++;
            }
        }
        int nextNumber = count + 1;
        int codeLength = provider.getCodeLength();

        if (codeLength == 1) {
            return String.valueOf(nextNumber);
        } else if (codeLength == 2) {
            return String.format("%02d", nextNumber);
        } else {
            return String.format("%03d", nextNumber);
        }
    }

    public void searchContact(TelecoProvider provider, String correspondingNumber) {
        String normalized = normalizeNumber(correspondingNumber, provider);
        int index = SearchingAlgorithm.linearSearch(contacts, provider, normalized);

        System.out.println();
        if (index != -1) {
            System.out.println("  ✔ Contact FOUND at index " + index + ":");
            System.out.println("  " + contacts.get(index));
        } else {
            System.out.println("  ✘ Contact NOT FOUND.");
            System.out.println("  (No " + provider.getName()
                    + " contact with corresponding number: " + correspondingNumber + ")");
        }
    }

    private String normalizeNumber(String input, TelecoProvider provider) {
        try {
            int value = Integer.parseInt(input.trim());
            int codeLength = provider.getCodeLength();
            return String.format("%0" + codeLength + "d", value);
        } catch (NumberFormatException e) {
            return input.trim();
        }
    }

    public void displayAll() {
        if (contacts.isEmpty()) {
            System.out.println("\n  No contacts saved yet.");
            return;
        }
        System.out.println("\n  --- All Contacts ("
                + contacts.size() + " total, capacity: "
                + contacts.getCapacity() + ") ---");
        for (int i = 0; i < contacts.size(); i++) {
            System.out.println("  [" + i + "] " + contacts.get(i));
        }
    }

    public int size() { return contacts.size(); }
}
