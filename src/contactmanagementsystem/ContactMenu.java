package contactmanagementsystem;

public class ContactMenu {
 
    private ContactList list;
 
    public ContactMenu(ContactList list) {
        this.list = list;
    }
 
    public void showOptions() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║           CONTACT MANAGER            ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1. Add contact (at the end)         ║ "); 
        System.out.println("║  2. Add contact (at a position)      ║ "); 
        System.out.println("║  3. Show all contacts                ║ "); 
        System.out.println("║  4. Delete contact (by position)     ║ "); 
        System.out.println("║  5. Search contact (name/ID/code)    ║ "); 
        System.out.println("║  6. Show size and capacity           ║ "); 
        System.out.println("║  7. Save contacts to file            ║ "); 
        System.out.println("║  8. Load contacts from file          ║ "); 
        System.out.println("║  0. Exit                             ║");
        System.out.println("╚══════════════════════════════════════╝");
    }
 
    public void showContacts() {
        System.out.println("════════════════ CONTACTS ════════════════");
        if (list.getSize() == 0) {
            System.out.println("  No contacts yet.");
        }
        for (int i = 0; i < list.getSize(); i++) {
            showContact(i + 1, list.get(i));
        }
        System.out.println("═══════════════════════════════════════════");
    }
 
    public void showContact(int position, Contact c) {
        System.out.println("  " + position + ". ID " + c.getIdLabel() + " | " + c.getName()
                + " | " + c.getPhone() + " | " + c.getEmailDisplay()
                + " | " + c.getProvider().getName() + " (" + c.getCorrespondingNumber() + ")"); 
    }
}

