package contactmanagementsystem;

import java.util.Scanner;
 
public class ContactController {
 
    private static final int BACK = Integer.MIN_VALUE;
 
    private ContactList list;
    private ContactMenu menu;
    private Scanner     input;
    private int         lastPosition;
 
    public ContactController() {
        input  = new Scanner(System.in);
        list   = new ContactList();
        menu   = new ContactMenu(list);
    }
 
    public void start() {
        System.out.println("   ╔══════════════════════════════╗");
        System.out.println("   ║       CONTACT MANAGER        ║");
        System.out.println("   ╚══════════════════════════════╝");
        System.out.println("   " + KeyInput.getHint());
        loadFromFile(false);
        if (FileHandler.wasMigrated()) {
            saveToFile(false);
            System.out.println("   (Old file format: the IDs were renumbered per Telco and the file was updated.)");
        }
        System.out.println("   Press ENTER to start...");
        KeyInput.readLine(input);
        mainLoop();
    }
 
    public void mainLoop() {
        int choice;
 
        do {
            menu.showOptions();
            System.out.print("Choose: ");
            choice = readInt();
 
            if (choice == BACK) {
                System.out.println("You are already at the main menu. Choose 0 to exit.");
                continue;
            }
 
            switch (choice) {
                case 1:
                    addAtEnd();
                    break;
                case 2:
                    addAtPosition();
                    break;
                case 3:
                    menu.showContacts();
                    break;
                case 4:
                    deleteContact();
                    break;
                case 5:
                    searchContact();
                    break;
                case 6:
                    System.out.println("Size: " + list.getSize()
                            + ", Capacity: " + list.getCapacity());
 
                    for (int i = 0; i < TelecoProvider.ALL.length; i++) {
                        TelecoProvider p = TelecoProvider.ALL[i];
                        System.out.println("  " + p.getName() + " array -> Size: " + list.getSize(p)
                                + ", Capacity: " + list.getCapacity(p));
                    }
                    break;
                case 7:
                    saveToFile(true);
                    break;
                case 8:
                    if (FileHandler.exists()) {
                        list.clear();
                        loadFromFile(true);
                    } else {
                        System.out.println("No saved file yet (" + FileHandler.FILE_NAME + ").");
                    }
                    break;
                case 9:
                    editContact();
                    break;
                case 0:
                    saveToFile(false);
                    System.out.println("Your contacts are safely saved. Thanks for using our Contact Manager!");
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
                    break;
            }
        } while (choice != 0);
    }
 
    private void addAtEnd() {
        Contact c = askForContact(false);
        if (c == null) {
            System.out.println("Add contact cancelled.");
            return;
        }
        list.insertAtEnd(c);
        System.out.println("Contact added with ID " + c.getIdLabel() + ".");
 
        System.out.println("  ✔ " + c);
        System.out.println("  Index in " + c.getProvider().getName() + " array: "
                + (list.getSize(c.getProvider()) - 1));
        saveToFile(false);
    }
 
    private void addAtPosition() {
        Contact c = askForContact(true);
        if (c == null) {
            System.out.println("Insert cancelled.");
            return;
        }
 
        if (list.insertAtPosition(lastPosition - 1, c)) {
            System.out.println("Contact inserted at position " + lastPosition + " of the "
                    + c.getProvider().getName() + " array.");
            System.out.println("  ✔ " + c);
            System.out.println("  (The IDs of the " + c.getProvider().getName() + " contacts were renumbered.)");
            saveToFile(false);
        } else {
            System.out.println("Could not insert (bad position).");
        }
    }
 
    private void deleteContact() {
        menu.showContacts();
        if (list.getSize() == 0) {
            return;
        }
        System.out.print("Delete position: ");
        int position = readInt();
        if (position == BACK) {
            return;
        }
 
        if (position < 1 || position > list.getSize()) {
            System.out.println("Invalid position! Pick 1 to " + list.getSize() + ".");
            return;
        }
 
        Contact c = list.get(position - 1);
        String description = c.getIdLabel() + " " + c.getName() + " (" + c.getProvider().getName() + ")";
        TelecoProvider provider = c.getProvider();
 
        System.out.print("Delete " + description + "? (y/n): ");
        String answer = KeyInput.readLine(input);
        if (answer == null || !(answer.trim().equalsIgnoreCase("y") || answer.trim().equalsIgnoreCase("yes"))) {
            System.out.println("Delete cancelled.");
            return;
        }
 
        if (list.delete(position - 1)) {
            System.out.println("Deleted " + description + ".");
            System.out.println("  (The IDs of the " + provider.getName() + " contacts were renumbered.)");
            saveToFile(false);
        } else {
            System.out.println("Could not delete (bad position).");
        }
    }
 
    private void editContact() {
        if (list.getSize() == 0) {
            menu.showContacts();
            return;
        }
 
        int step = 0;
        int position = 0;
        Contact c = null;
        int field = 0;
        String newName = null;
        String newPhone = null;
        String newEmail = null;
 
        while (step < 3) {
            if (step == 0) {
                menu.showContacts();
                System.out.print("Edit position: ");
                position = readInt();
                if (position == BACK) {
                    return;
                }
                if (position < 1 || position > list.getSize()) {
                    System.out.println("  ✘ Invalid position! Pick 1 to " + list.getSize() + ".");
                    continue;
                }
                c = list.get(position - 1);
                step = 1;
 
            } else if (step == 1) {
                newName = null;
                newPhone = null;
                newEmail = null;
                System.out.println("Editing:");
                menu.showContact(position, c);
                System.out.println("What do you want to edit?");
                System.out.println("  1. Name");
                System.out.println("  2. Phone");
                System.out.println("  3. Email");
                System.out.println("  4. All of them");
                System.out.print("Choose: ");
                field = readInt();
                if (field == BACK) {
                    step = 0;
                    continue;
                }
                if (field < 1 || field > 4) {
                    System.out.println("  ✘ Invalid choice.");
                    continue;
                }
                step = 2;
 
            } else {
                int[] order;
                if (field == 4) {
                    order = new int[] { 1, 2, 3 };
                } else {
                    order = new int[] { field };
                }
 
                int k = 0;
                while (k >= 0 && k < order.length) {
                    if (order[k] == 1) {
                        System.out.print("New name (current: " + c.getName() + "): ");
                        String typed = KeyInput.readLine(input);
                        if (typed == null) {
                            k--;
                            continue;
                        }
                        typed = typed.trim();
                        String error = Validator.checkName(typed);
                        if (error != null) {
                            System.out.println("  ✘ " + error);
                            continue;
                        }
                        newName = typed;
                        k++;
 
                    } else if (order[k] == 2) {
                        System.out.print("New phone (must start with a valid " + c.getProvider().getName()
                                + " prefix, e.g. " + c.getProvider().getPrefixesSample()
                                + ") (current: " + c.getPhone() + "): ");
                        String typed = KeyInput.readLine(input);
                        if (typed == null) {
                            k--;
                            continue;
                        }
                        typed = typed.trim();
                        String error = Validator.checkPhone(typed, c.getProvider());
                        if (error != null) {
                            System.out.println("  ✘ " + error);
                            continue;
                        }
                        newPhone = typed;
                        k++;
 
                    } else {
                        System.out.print("New email (current: " + c.getEmailDisplay()
                                + ") (ENTER with nothing = remove the email): ");
                        String typed = KeyInput.readLine(input);
                        if (typed == null) {
                            k--;
                            continue;
                        }
                        typed = typed.trim();
                        String error = Validator.checkEmailOptional(typed);
                        if (error != null) {
                            System.out.println("  ✘ " + error);
                            continue;
                        }
                        newEmail = typed;
                        k++;
                    }
                }
 
                if (k < 0) {
                    step = 1;
                    continue;
                }
                step = 3;
            }
        }
 
        if (list.editContact(position - 1, newName, newPhone, newEmail)) {
            System.out.println("Contact updated:");
            menu.showContact(position, c);
            saveToFile(false);
        } else {
            System.out.println("Could not edit (bad position).");
        }
    }
 
    private void searchContact() {
        int type = 0;
        TelecoProvider provider = null;
        String name = null;
        int number = 0;
        int step = 0;
 
        while (step < 3) {
            if (step == 0) {
                System.out.println("Search using:");
                System.out.println("  1. Linear search (by Name)");
                System.out.println("  2. Binary search (by ID)");
                System.out.println("  3. Recursive binary search (by Code: 001 / 01 / 1)");
                System.out.print("Choose: ");
                type = readInt();
                if (type == BACK) {
                    return;
                }
                if (type < 1 || type > 3) {
                    System.out.println("Invalid search type.");
                    return;
                }
                step = 1;
            } else if (step == 1) {
                provider = chooseProvider();
                if (provider == null) {
                    step = 0;
                    continue;
                }
                step = 2;
            } else {
                if (type == 1) {
                    System.out.print("Name to find (full name or just a part, e.g. Ethan or Macatangay): ");
                    String typed = KeyInput.readLine(input);
                    if (typed == null) {
                        step = 1;
                        continue;
                    }
                    name = typed.trim();
                    if (name.length() == 0) {
                        System.out.println("Please type a name.");
                        continue;
                    }
                } else if (type == 2) {
                    number = readNumber("ID to find (e.g. 1 or 1" + provider.getInitial() + "): ", provider, true);
                    if (number == BACK) {
                        step = 1;
                        continue;
                    }
                } else {
                    number = readNumber("Code to find (e.g. " + provider.getCode() + "): ", provider, false);
                    if (number == BACK) {
                        step = 1;
                        continue;
                    }
                }
                step = 3;
            }
        }
 
        if (type == 1) {
            int[] matches = list.linearSearch(provider, name);
            if (matches.length == 0) {
                System.out.println("Not found.");
            } else if (matches.length == 1) {
                System.out.println("Found in the " + provider.getName() + " array at index " + matches[0] + ":");
                menu.showContact(matches[0] + 1, list.get(provider, matches[0]));
            } else {
                System.out.println("Found " + matches.length + " contacts in the " + provider.getName()
                        + " array (number shown = index + 1):");
                for (int i = 0; i < matches.length; i++) {
                    menu.showContact(matches[i] + 1, list.get(provider, matches[i]));
                }
            }
            return;
        }
 
        if (!list.isSortedById(provider)) {
            System.out.println("The list is NOT sorted by ID, so binary search cannot be used.");
            return;
        }
 
        int result = -1;
        if (type == 2) {
            result = list.binarySearch(provider, number);
        } else {
            result = list.recursiveBinarySearch(provider, number);
        }
 
        if (result != -1) {
            System.out.println("Found in the " + provider.getName() + " array at index " + result + ":");
            menu.showContact(result + 1, list.get(provider, result));
        } else {
            System.out.println("Not found.");
        }
    }
 
    private Contact askForContact(boolean askPosition) {
        TelecoProvider provider = null;
        String name = null;
        String phone = null;
        String email = null;
        int step = 0;
 
        while (step < 5) {
            if (step == 0) {
                provider = chooseProvider();
                if (provider == null) {
                    return null;
                }
                step = askPosition ? 1 : 2;
 
            } else if (step == 1) {
                menu.showContacts(provider);
                int last = list.getSize(provider) + 1;
                System.out.print("Insert at position (1 = first, " + last + " = last) in the "
                        + provider.getName() + " array: ");
                int position = readInt();
                if (position == BACK) {
                    step = 0;
                    continue;
                }
                if (position < 1 || position > last) {
                    System.out.println("  ✘ Invalid position! Pick 1 to " + last + ".");
                    continue;
                }
                lastPosition = position;
                step = 2;
 
            } else if (step == 2) {
                System.out.print("Name: ");
                String typed = KeyInput.readLine(input);
                if (typed == null) {
                    step = askPosition ? 1 : 0;
                    continue;
                }
                typed = typed.trim();
                String error = Validator.checkName(typed);
                if (error != null) {
                    System.out.println("  ✘ " + error);
                    continue;
                }
                name = typed;
                step = 3;
 
            } else if (step == 3) {
                System.out.print("Phone (must start with a valid " + provider.getName() + " prefix, e.g. "
                        + provider.getPrefixesSample() + "): ");
                String typed = KeyInput.readLine(input);
                if (typed == null) {
                    step = 2;
                    continue;
                }
                typed = typed.trim();
                String error = Validator.checkPhone(typed, provider);
                if (error != null) {
                    System.out.println("  ✘ " + error);
                    continue;
                }
                phone = typed;
                step = 4;
 
            } else {
                System.out.print("Email (optional - just press ENTER to skip): ");
                String typed = KeyInput.readLine(input);
                if (typed == null) {
                    step = 3;
                    continue;
                }
                typed = typed.trim();
                String error = Validator.checkEmailOptional(typed);
                if (error != null) {
                    System.out.println("  ✘ " + error);
                    continue;
                }
                email = typed;
                step = 5;
            }
        }
 
        int id = list.nextId(provider);
        return new Contact(id, name, phone, email, provider, list.formatCode(provider, id));
    }
 
    private TelecoProvider chooseProvider() {
        while (true) {
            System.out.println("Choose Teleco Provider:");
            System.out.println("  1. Smart (code: " + TelecoProvider.SMART.getCode() + ")");
            System.out.println("  2. Globe (code: " + TelecoProvider.GLOBE.getCode() + ")");
            System.out.println("  3. DITO  (code: " + TelecoProvider.DITO.getCode() + ")");
            System.out.print("Enter choice: ");
            int choice = readInt();
 
            if (choice == BACK) {
                return null;
            }
            if (choice == 1) return TelecoProvider.SMART;
            if (choice == 2) return TelecoProvider.GLOBE;
            if (choice == 3) return TelecoProvider.DITO;
            System.out.println("Invalid provider choice.");
        }
    }
 
    private void saveToFile(boolean showMessage) {
        if (FileHandler.save(list)) {
            if (showMessage) {
                System.out.println("Saved " + list.getSize() + " contact(s) to " + FileHandler.FILE_NAME + ".");
            }
        } else {
            System.out.println("  ✘ Could not save to " + FileHandler.FILE_NAME + ".");
        }
    }
 
    private void loadFromFile(boolean showMessage) {
        int loaded = FileHandler.load(list);
        if (loaded == -1) {
            if (FileHandler.exists()) {
                System.out.println("  ✘ Could not read " + FileHandler.FILE_NAME + ".");
            } else if (showMessage) {
                System.out.println("No saved file yet (" + FileHandler.FILE_NAME + ").");
            }
            return;
        }
 
        System.out.println("   Loaded " + loaded + " contact(s) from " + FileHandler.FILE_NAME + ".");
        if (FileHandler.getSkippedLines() > 0) {
            System.out.println("   (" + FileHandler.getSkippedLines() + " invalid line(s) in the file were skipped.)");
        }
    }
 
    private int readNumber(String prompt, TelecoProvider provider, boolean allowInitial) {
        while (true) {
            System.out.print(prompt);
            String typed = KeyInput.readLine(input);
            if (typed == null) {
                return BACK;
            }
            typed = typed.trim();
 
            if (allowInitial && typed.length() > 1 && Character.isLetter(typed.charAt(typed.length() - 1))) {
                String letter = typed.substring(typed.length() - 1).toUpperCase();
                if (!letter.equals(provider.getInitial())) {
                    System.out.println("  ✘ " + typed.toUpperCase() + " is not a " + provider.getName()
                            + " ID (" + provider.getName() + " IDs end with " + provider.getInitial() + ").");
                    continue;
                }
                typed = typed.substring(0, typed.length() - 1);
            }
 
            boolean isNumber = typed.length() > 0 && typed.length() <= 9;
            int number = 0;
            for (int i = 0; i < typed.length() && isNumber; i++) {
                char c = typed.charAt(i);
                if (c < '0' || c > '9') {
                    isNumber = false;
                } else {
                    number = number * 10 + (c - '0');
                }
            }
            if (isNumber) {
                return number;
            }
            System.out.println("  ✘ Numbers only (example: 1).");
        }
    }
 
    private int readInt() {
        while (true) {
            String typed = KeyInput.readLine(input);
            if (typed == null) {
                return BACK;
            }
            typed = typed.trim();
 
            boolean isNumber = typed.length() > 0 && typed.length() <= 9;
            int number = 0;
            for (int i = 0; i < typed.length() && isNumber; i++) {
                char c = typed.charAt(i);
                if (c < '0' || c > '9') {
                    isNumber = false;
                } else {
                    number = number * 10 + (c - '0');
                }
            }
 
            if (isNumber) {
                return number;
            }
            System.out.print("Numbers only, try again: ");
        }
    }
}