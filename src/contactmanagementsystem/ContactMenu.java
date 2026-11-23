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
        System.out.println("║  1. Add contact (at the end)         ║ "); //Meron nato
        System.out.println("║  2. Add contact (at a position)      ║ "); //wala pa
        System.out.println("║  3. Show all contacts                ║ "); //meron na
        System.out.println("║  4. Delete contact (by position)     ║ "); //wala pa
        System.out.println("║  5. Search contact (name / ID)       ║ "); // [CHANGED] text only: Linear = name, Binary = ID
        System.out.println("║  6. Show size and capacity           ║ "); //Meron na
        System.out.println("║  7. Save contacts to file            ║ "); // [ADDED] File Handling
        System.out.println("║  8. Load contacts from file          ║ "); // [ADDED] File Handling
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
        System.out.println("  " + position + ". ID " + c.getId() + " | " + c.getName()
                + " | " + c.getPhone() + " | " + c.getEmail()
                + " | " + c.getProvider().getName() + " (" + c.getCorrespondingNumber() + ")"); // [CHANGED] added Telco + corresponding number
    }
}

