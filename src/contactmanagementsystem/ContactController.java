package contactmanagementsystem;

import java.util.Scanner;    // Matt's original import (Scanner is allowed)

// [CHANGED] Typed input still comes from Matt's Scanner, but it is now read through our KeyInput class
// (KeyInput.readLine(input)), which lets ESC / Backspace go back one step.

public class ContactController {

    // [ADDED] readInt() returns this when the user pressed ESC / Backspace ("go back")
    private static final int BACK = Integer.MIN_VALUE;

    private ContactList list;
    private ContactMenu menu;
    private Scanner     input;      // Matt's Scanner (kept)
    private int         nextId;


    public ContactController() {
        input  = new Scanner(System.in);
        list   = new ContactList();
        menu   = new ContactMenu(list);
        nextId = 1;
    }


    public void start() {
        System.out.println("   ╔══════════════════════════════╗");
        System.out.println("   ║       CONTACT MANAGER        ║");
        System.out.println("   ╚══════════════════════════════╝");
        System.out.println("   " + KeyInput.getHint());          // [ADDED] tells which keys go back
        loadFromFile(false);                                      // [ADDED] File Handling: load saved contacts
        System.out.println("   Press ENTER to start...");
        KeyInput.readLine(input);                                 // [CHANGED] was input.nextLine()
        mainLoop();
    }


    public void mainLoop() {
        int choice;

        do {
            menu.showOptions();
            System.out.print("Choose: ");
            choice = readInt();

            // [ADDED] ESC at the main menu: there is nothing to go back to
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
                    // [ADDED] every Telco has its own Dynamic Array, so show each one
                    for (int i = 0; i < TelecoProvider.ALL.length; i++) {
                        TelecoProvider p = TelecoProvider.ALL[i];
                        System.out.println("  " + p.getName() + " array -> Size: " + list.getSize(p)
                                + ", Capacity: " + list.getCapacity(p));
                    }
                    break;
                case 7:                                           // [ADDED] File Handling
                    saveToFile(true);
                    break;
                case 8:                                           // [ADDED] File Handling
                    if (FileHandler.exists()) {
                        list.clear();
                        loadFromFile(true);
                    } else {
                        System.out.println("No saved file yet (" + FileHandler.FILE_NAME + ").");
                    }
                    break;
                case 0:
                    saveToFile(false);                            // [ADDED] save before leaving
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
                    break;
            }
        } while (choice != 0);
    }


    private void addAtEnd() {
        Contact c = askForContact();
        if (c == null) {                                          // [ADDED] ESC on the first step
            System.out.println("Add contact cancelled.");
            return;
        }
        list.insertAtEnd(c);
        nextId++;
        System.out.println("Contact added with ID " + c.getId() + ".");
        // [ADDED from Ethan] show the saved contact and where it is inside its Telco's array
        System.out.println("  ✔ " + c);
        System.out.println("  Index in " + c.getProvider().getName() + " array: "
                + (list.getSize(c.getProvider()) - 1));
        saveToFile(false);                                        // [ADDED] auto-save after adding
    }


    private void addAtPosition() {
        menu.showContacts();
        System.out.print("Insert at position (1 = first): ");
        int position = readInt();
        if (position == BACK) {                                   // [ADDED] ESC -> back to the menu
            return;
        }


        if (position < 1 || position > list.getSize() + 1) {
            System.out.println("Invalid position! Pick 1 to " + (list.getSize() + 1) + ".");
            return;
        }

        Contact c = askForContact();
        if (c == null) {                                          // [ADDED] ESC on the first step
            System.out.println("Insert cancelled.");
            return;
        }

        if (list.insertAtPosition(position - 1, c)) {
            nextId++;
            System.out.println("Contact inserted.");
            saveToFile(false);                                    // [ADDED] auto-save
        } else {
            System.out.println("Could not insert (insertAtPosition() not written yet?).");
        }
    }


    private void deleteContact() {
        menu.showContacts();
        System.out.print("Delete position: ");
        int position = readInt();
        if (position == BACK) {                                   // [ADDED] ESC -> back to the menu
            return;
        }

        if (list.delete(position - 1)) {
            System.out.println("Deleted.");
            saveToFile(false);                                    // [ADDED] auto-save
        } else {
            System.out.println("Could not delete (bad position, or delete() not written yet).");
        }
    }


    // [CHANGED] Search flow: search type -> Telco -> what to look for.
    // ESC goes back one step (value -> Telco -> search type -> main menu).
    // Linear search looks for a NAME, binary search looks for an ID.
    private void searchContact() {
        int type = 0;
        TelecoProvider provider = null;
        String name = null;
        int id = 0;
        int step = 0;

        while (step < 3) {
            if (step == 0) {
                System.out.println("Search using:");
                System.out.println("  1. Linear search (by Name)");
                System.out.println("  2. Binary search (by ID)");
                System.out.println("  3. Recursive binary search (by ID)");
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
                provider = chooseProvider();                      // [ADDED from Ethan] search inside ONE Telco's array
                if (provider == null) {
                    step = 0;                                     // ESC -> back to the search type
                    continue;
                }
                step = 2;
            } else {
                if (type == 1) {
                    System.out.print("Name to find: ");
                    String typed = KeyInput.readLine(input);
                    if (typed == null) {                          // ESC -> back to the Telco
                        step = 1;
                        continue;
                    }
                    name = typed.trim();
                    if (name.length() == 0) {
                        System.out.println("Please type a name.");
                        continue;
                    }
                } else {
                    System.out.print("ID to find: ");
                    id = readInt();
                    if (id == BACK) {                             // ESC -> back to the Telco
                        step = 1;
                        continue;
                    }
                }
                step = 3;
            }
        }

        int result = -1;

        switch (type) {
            case 1:
                result = list.linearSearch(provider, name);
                break;
            case 2:

                if (!list.isSortedById(provider)) {
                    System.out.println("The list is NOT sorted by ID, so binary search cannot be used.");
                    return;
                }
                result = list.binarySearch(provider, id);
                break;
            case 3:
                if (!list.isSortedById(provider)) {
                    System.out.println("The list is NOT sorted by ID, so binary search cannot be used.");
                    return;
                }
                result = list.recursiveBinarySearch(provider, id);
                break;
        }

        if (result != -1) {
            System.out.println("Found in the " + provider.getName() + " array at index " + result + ":");
            menu.showContact(result + 1, list.get(provider, result));
        } else {
            System.out.println("Not found.");
        }
    }


    // [CHANGED] Now follows Ethan's flow: Telco first, then Name, Phone (checked against the Telco's
    // prefixes) and Email. Every answer is validated. ESC / Backspace goes back to the PREVIOUS step;
    // ESC on the very first step cancels and returns null.
    private Contact askForContact() {
        TelecoProvider provider = null;
        String name = null;
        String phone = null;
        String email = null;
        int step = 0;

        while (step < 4) {
            if (step == 0) {
                provider = chooseProvider();
                if (provider == null) {
                    return null;
                }
                step = 1;

            } else if (step == 1) {
                System.out.print("Name: ");
                String typed = KeyInput.readLine(input);
                if (typed == null) {
                    step = 0;
                    continue;
                }
                typed = typed.trim();
                String error = Validator.checkName(typed);
                if (error != null) {
                    System.out.println("  ✘ " + error);
                    continue;
                }
                name = typed;
                step = 2;

            } else if (step == 2) {
                System.out.print("Phone (must start with a valid " + provider.getName() + " prefix, e.g. "
                        + provider.getPrefixesSample() + "): ");
                String typed = KeyInput.readLine(input);
                if (typed == null) {
                    step = 1;
                    continue;
                }
                typed = typed.trim();
                String error = Validator.checkPhone(typed, provider);
                if (error != null) {
                    System.out.println("  ✘ " + error);
                    continue;
                }
                phone = typed;
                step = 3;

            } else {
                System.out.print("Email: ");
                String typed = KeyInput.readLine(input);
                if (typed == null) {
                    step = 2;
                    continue;
                }
                typed = typed.trim();
                String error = Validator.checkEmail(typed);
                if (error != null) {
                    System.out.println("  ✘ " + error);
                    continue;
                }
                email = typed;
                step = 4;
            }
        }
        // [CHANGED] also gives the Telco and its corresponding number (Ethan's idea)
        return new Contact(nextId, name, phone, email, provider, list.nextCorrespondingNumber(provider));
    }


    // [ADDED from Ethan] Choose the Telco Provider. Returns null if the user pressed ESC / Backspace.
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


    // [ADDED] File Handling: save everything to contacts.txt
    private void saveToFile(boolean showMessage) {
        if (FileHandler.save(list)) {
            if (showMessage) {
                System.out.println("Saved " + list.getSize() + " contact(s) to " + FileHandler.FILE_NAME + ".");
            }
        } else {
            System.out.println("  ✘ Could not save to " + FileHandler.FILE_NAME + ".");
        }
    }

    // [ADDED] File Handling: load contacts.txt, then continue the IDs after the biggest saved ID
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

        int biggestId = 0;
        for (int i = 0; i < list.getSize(); i++) {
            if (list.get(i).getId() > biggestId) {
                biggestId = list.get(i).getId();
            }
        }
        nextId = biggestId + 1;

        System.out.println("   Loaded " + loaded + " contact(s) from " + FileHandler.FILE_NAME + ".");
        if (FileHandler.getSkippedLines() > 0) {
            System.out.println("   (" + FileHandler.getSkippedLines() + " invalid line(s) in the file were skipped.)");
        }
    }


    // [CHANGED] Same idea as Matt's readInt (keep asking until a number is typed), but it reads
    // through KeyInput (which uses Matt's Scanner), and returns BACK when ESC / Backspace is pressed.
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

