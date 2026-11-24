package contactmanagementsystem;

public class ContactMenu {
 
    private ContactList list;
 
    public ContactMenu(ContactList list) {
        this.list = list;
    }
 
    public void showOptions() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println(boxLine("           CONTACT MANAGER"));
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println(boxLine(optionText(1, "Add a new contact")));
        System.out.println(boxLine(optionText(2, "Add a contact at a chosen place")));
        System.out.println(boxLine(optionText(3, "Show all contacts")));
        System.out.println(boxLine(optionText(4, "Delete a contact")));
        System.out.println(boxLine(optionText(5, "Search for a contact")));
        System.out.println(boxLine(optionText(6, "How many contacts are saved?")));
        System.out.println(boxLine(optionText(7, "Save contacts to the file")));
        System.out.println(boxLine(optionText(8, "Load contacts from the file")));
        System.out.println(boxLine(optionText(9, "Edit a contact")));
        System.out.println(boxLine(optionText(10, "Swap or move contacts")));
        System.out.println(boxLine(optionText(0, "Exit")));
        System.out.println("╚══════════════════════════════════════╝");
    }
 
    private String optionText(int number, String text) {
        String prefix = (number < 10) ? "  " : " ";
        return prefix + number + ". " + text;
    }
 
    private String boxLine(String text) {
        String padded = text;
        while (padded.length() < 38) {
            padded = padded + " ";
        }
        return "║" + padded + "║";
    }
 
    public void showContacts() {
        System.out.println("════════════════ CONTACTS ════════════════");
        if (list.getSize() == 0) {
            System.out.println("  You have no contacts yet. Choose 1 in the menu to add your first contact.");
        }
        for (int i = 0; i < list.getSize(); i++) {
            showContact(i + 1, list.get(i));
        }
        System.out.println("═══════════════════════════════════════════");
    }
 
    public void showContacts(TelecoProvider provider) {
        System.out.println("══════════ " + provider.getName().toUpperCase() + " CONTACTS ══════════");
        if (list.getSize(provider) == 0) {
            System.out.println("  You have no " + provider.getName() + " contacts yet.");
        }
        for (int i = 0; i < list.getSize(provider); i++) {
            showContact(i + 1, list.get(provider, i));
        }
        System.out.println("═══════════════════════════════════════════");
    }
 
    public void showContact(int position, Contact c) {
        System.out.println("  " + position + ". ID " + c.getIdLabel() + " | " + c.getName()
                + " | " + c.getPhone() + " | " + c.getEmailDisplay()
                + " | " + c.getProvider().getName() + " (" + c.getCorrespondingNumber() + ")");
    }
}
 
 


