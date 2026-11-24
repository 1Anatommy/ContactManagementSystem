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
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║          WELCOME TO THE CONTACT MANAGER            ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  HOW TO USE THIS PROGRAM");
        System.out.println("   1. A menu with numbered choices will appear.");
        System.out.println("      Type the number of what you want to do, then press ENTER.");
        System.out.println("   2. " + KeyInput.getHint());
        System.out.println("   3. Your contacts are saved automatically in the file " + FileHandler.FILE_NAME + ".");
        System.out.println("   4. Phone numbers are typed like this: 09*********  (11 digits, no spaces)");
        System.out.println();
        loadFromFile(false);
        if (FileHandler.wasMigrated()) {
            saveToFile(false);
            System.out.println("   Note: your contact file was made by an older version. It was updated,");
            System.out.println("   and the ID numbers were renumbered for each network (Smart, Globe, DITO).");
        }
        System.out.println();
        System.out.print("   Press ENTER to start...");
        KeyInput.readLine(input);
        mainLoop();
    }
 
    public void mainLoop() {
        int choice;
 
        do {
            menu.showOptions();
            System.out.print("Type the number of your choice and press ENTER: ");
            choice = readInt();
 
            if (choice == BACK) {
                System.out.println("You are already at the main menu. To close the program, type 0.");
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
                    showCounts();
                    break;
                case 7:
                    saveToFile(true);
                    break;
                case 8:
                    if (FileHandler.exists()) {
                        list.clear();
                        loadFromFile(true);
                    } else {
                        System.out.println("There is no saved file yet. Add a contact first, or choose 7 to save.");
                    }
                    break;
                case 9:
                    editContact();
                    break;
                case 10:
                    swapOrMoveContacts();
                    break;
                case 0:
                    saveToFile(false);
                    System.out.println("Your contacts are saved. Goodbye!");
                    break;
                default:
                    System.out.println("That choice is not in the menu. Please type a number from 0 to 10.");
                    break;
            }
        } while (choice != 0);
    }
 
    private void showCounts() {
        System.out.println("You have " + list.getSize() + " contact(s) in total.");
        for (int i = 0; i < TelecoProvider.ALL.length; i++) {
            TelecoProvider p = TelecoProvider.ALL[i];
            System.out.println("  " + p.getName() + ": " + list.getSize(p) + " contact(s)"
                    + "   (storage space: " + list.getCapacity(p) + ")");
        }
        System.out.println("  Storage space grows by itself when it gets full.");
    }
 
    private void showBackTip() {
        System.out.println("  (" + KeyInput.getHint() + ")");
    }
 
    private void addAtEnd() {
        System.out.println();
        System.out.println("── ADD A NEW CONTACT ──");
        showBackTip();
        Contact c = askForContact(false);
        if (c == null) {
            System.out.println("Nothing was added.");
            return;
        }
        list.insertAtEnd(c);
        System.out.println();
        System.out.println("✔ The contact was added. Its ID is " + c.getIdLabel() + ".");
        System.out.println("  " + c);
        System.out.println("  It is number " + list.getSize(c.getProvider()) + " in the "
                + c.getProvider().getName() + " list.");
        saveToFile(false);
    }
 
    private void addAtPosition() {
        System.out.println();
        System.out.println("── ADD A CONTACT AT A CHOSEN PLACE ──");
        showBackTip();
        Contact c = askForContact(true);
        if (c == null) {
            System.out.println("Nothing was added.");
            return;
        }
 
        if (list.insertAtPosition(lastPosition - 1, c)) {
            System.out.println();
            System.out.println("✔ The contact was added as number " + lastPosition + " in the "
                    + c.getProvider().getName() + " list.");
            System.out.println("  " + c);
            System.out.println("  (The IDs of the " + c.getProvider().getName()
                    + " contacts were renumbered to follow the new order.)");
            saveToFile(false);
        } else {
            System.out.println("✘ Sorry, the contact could not be added at that place. Please try again.");
        }
    }
 
    private void deleteContact() {
        System.out.println();
        System.out.println("── DELETE A CONTACT ──");
        if (list.getSize() == 0) {
            System.out.println("You have no contacts to delete yet.");
            return;
        }
        showBackTip();
        menu.showContacts();
        System.out.print("Type the number of the contact you want to delete: ");
        int position = readInt();
        if (position == BACK) {
            return;
        }
 
        if (position < 1 || position > list.getSize()) {
            System.out.println("✘ There is no contact with that number. Please choose a number from 1 to "
                    + list.getSize() + ".");
            return;
        }
 
        Contact c = list.get(position - 1);
        String description = c.getName() + " (" + c.getIdLabel() + ", " + c.getProvider().getName() + ")";
        TelecoProvider provider = c.getProvider();
 
        System.out.print("Are you sure you want to delete " + description + "? Type y for yes or n for no: ");
        String answer = KeyInput.readLine(input);
        if (answer == null || !(answer.trim().equalsIgnoreCase("y") || answer.trim().equalsIgnoreCase("yes"))) {
            System.out.println("Okay, nothing was deleted.");
            return;
        }
 
        if (list.delete(position - 1)) {
            System.out.println("✔ " + description + " was deleted.");
            System.out.println("  (The IDs of the " + provider.getName() + " contacts were renumbered.)");
            saveToFile(false);
        } else {
            System.out.println("✘ Sorry, that contact could not be deleted. Please try again.");
        }
    }
 
    private void editContact() {
        System.out.println();
        System.out.println("── EDIT A CONTACT ──");
        if (list.getSize() == 0) {
            System.out.println("You have no contacts to edit yet.");
            return;
        }
        showBackTip();
 
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
                System.out.print("Type the number of the contact you want to edit: ");
                position = readInt();
                if (position == BACK) {
                    return;
                }
                if (position < 1 || position > list.getSize()) {
                    System.out.println("  ✘ There is no contact with that number. Please choose a number from 1 to "
                            + list.getSize() + ".");
                    continue;
                }
                c = list.get(position - 1);
                step = 1;
 
            } else if (step == 1) {
                newName = null;
                newPhone = null;
                newEmail = null;
                System.out.println();
                System.out.println("You are editing:");
                menu.showContact(position, c);
                System.out.println("What do you want to change?");
                System.out.println("  1. The name");
                System.out.println("  2. The phone number");
                System.out.println("  3. The email");
                System.out.println("  4. Everything (name, phone number and email)");
                System.out.print("Type the number of your choice and press ENTER: ");
                field = readInt();
                if (field == BACK) {
                    step = 0;
                    continue;
                }
                if (field < 1 || field > 4) {
                    System.out.println("  ✘ Please type a number from 1 to 4.");
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
                        System.out.print("New name (now: " + c.getName() + "): ");
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
                        System.out.print("New phone number (now: " + c.getPhone() + ", example: 09*********): ");
                        String typed = KeyInput.readLine(input);
                        if (typed == null) {
                            k--;
                            continue;
                        }
                        typed = typed.trim();
                        String error = Validator.checkPhoneFormat(typed);
                        if (error == null && !c.getProvider().isValidPrefix(typed)) {
                            error = "This contact is saved under " + c.getProvider().getName()
                                  + ", but the number you typed starts with " + typed.substring(0, 4)
                                  + ", which is not a " + c.getProvider().getName() + " number."
                                  + " If the contact changed networks, please delete it and add it again.";
                        }
                        if (error != null) {
                            System.out.println("  ✘ " + error);
                            continue;
                        }
                        newPhone = typed;
                        k++;
 
                    } else {
                        System.out.print("New email (now: " + c.getEmailDisplay()
                                + "). Press ENTER with nothing typed to remove the email: ");
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
            System.out.println();
            System.out.println("✔ The contact was updated:");
            menu.showContact(position, c);
            saveToFile(false);
        } else {
            System.out.println("✘ Sorry, that contact could not be changed. Please try again.");
        }
    }
 
    private void swapOrMoveContacts() {
        System.out.println();
        System.out.println("── SWAP OR MOVE CONTACTS ──");
        if (list.getSize() < 2) {
            System.out.println("You need at least 2 contacts to use this. Add more contacts first.");
            return;
        }
        showBackTip();
 
        int step = 0;
        int mode = 0;
        int first = 0;
        int second = 0;
 
        while (true) {
            if (step == 0) {
                System.out.println();
                System.out.println("What would you like to do?");
                System.out.println("  1. Swap two contacts (they trade places)");
                System.out.println("  2. Move one contact to a new place (the others move over to make room)");
                System.out.print("Type the number of your choice and press ENTER: ");
                mode = readInt();
                if (mode == BACK) {
                    return;
                }
                if (mode != 1 && mode != 2) {
                    System.out.println("  ✘ Please type 1 or 2.");
                    continue;
                }
                step = 1;
 
            } else if (step == 1) {
                System.out.println();
                menu.showContacts();
                showNetworkRanges();
                if (mode == 1) {
                    System.out.print("Number of the FIRST contact: ");
                } else {
                    System.out.print("Number of the contact you want to MOVE: ");
                }
                first = readInt();
                if (first == BACK) {
                    step = 0;
                    continue;
                }
                if (first < 1 || first > list.getSize()) {
                    System.out.println("  ✘ There is no contact with that number. Please choose a number from 1 to "
                            + list.getSize() + ".");
                    continue;
                }
                step = 2;
 
            } else {
                if (mode == 1) {
                    System.out.print("Number of the SECOND contact (it will trade places with "
                            + list.get(first - 1).getName() + "): ");
                } else {
                    System.out.print("Move " + list.get(first - 1).getName() + " to which number? ");
                }
                second = readInt();
                if (second == BACK) {
                    step = 1;
                    continue;
                }
                if (second < 1 || second > list.getSize()) {
                    System.out.println("  ✘ There is no contact with that number. Please choose a number from 1 to "
                            + list.getSize() + ".");
                    continue;
                }
 
                String firstName = list.get(first - 1).getName();
                String secondName = list.get(second - 1).getName();
                TelecoProvider provider = list.providerAt(first - 1);
 
                int result;
                if (mode == 1) {
                    result = list.swapContacts(first - 1, second - 1);
                } else {
                    result = list.moveContact(first - 1, second - 1);
                }
 
                if (result == 2) {
                    System.out.println("  ✘ These two contacts are not in the same network, so they cannot trade places.");
                    System.out.println("    Each network (Smart, Globe, DITO) keeps its own list. Please pick numbers");
                    System.out.println("    from the same network.");
                    showNetworkRanges();
                    continue;
                }
                if (result == 3) {
                    System.out.println("  ✘ That is the same contact. Please choose a different number.");
                    continue;
                }
                if (result != 0) {
                    System.out.println("  ✘ Sorry, that did not work. Please try again.");
                    continue;
                }
 
                System.out.println();
                if (mode == 1) {
                    System.out.println("✔ Done! " + firstName + " and " + secondName + " traded places.");
                } else {
                    System.out.println("✔ Done! " + firstName + " is now number " + second + ".");
                }
                System.out.println("  (The IDs of the " + provider.getName()
                        + " contacts were renumbered to follow the new order.)");
                menu.showContacts();
                saveToFile(false);
                return;
            }
        }
    }
 
    private void showNetworkRanges() {
        System.out.println("  Contacts can only trade places inside their own network:");
        for (int i = 0; i < TelecoProvider.ALL.length; i++) {
            TelecoProvider p = TelecoProvider.ALL[i];
            int count = list.getSize(p);
            if (count == 0) {
                continue;
            }
            int start = list.startPosition(p);
            if (count == 1) {
                System.out.println("    " + p.getName() + ": number " + start + " only");
            } else {
                System.out.println("    " + p.getName() + ": numbers " + start + " to " + (start + count - 1));
            }
        }
    }
 
    private void searchContact() {
        System.out.println();
        System.out.println("── SEARCH FOR A CONTACT ──");
        if (list.getSize() == 0) {
            System.out.println("You have no contacts yet, so there is nothing to search.");
            return;
        }
        showBackTip();
 
        int type = 0;
        int step = 0;
 
        while (true) {
            if (step == 0) {
                System.out.println();
                System.out.println("How do you want to search?");
                System.out.println("  1. By name          (Linear search)  - you can type just a part of the name");
                System.out.println("  2. By ID            (Binary search)  - for example 2S, 1G or 3D");
                System.out.println("  3. By code number   (Recursive binary search) - 001 = Smart, 01 = Globe, 1 = DITO");
                System.out.print("Type the number of your choice and press ENTER: ");
                type = readInt();
                if (type == BACK) {
                    return;
                }
                if (type < 1 || type > 3) {
                    System.out.println("  ✘ Please type 1, 2 or 3.");
                    continue;
                }
                step = 1;
 
            } else {
                boolean finished;
                if (type == 1) {
                    finished = searchByName();
                } else if (type == 2) {
                    finished = searchById();
                } else {
                    finished = searchByCode();
                }
                if (finished) {
                    return;
                }
                step = 0;
            }
        }
    }
 
    private boolean searchByName() {
        while (true) {
            System.out.print("Type the name to look for (the whole name or just a part, like Christian): ");
            String typed = KeyInput.readLine(input);
            if (typed == null) {
                return false;
            }
            String name = typed.trim();
            if (name.length() == 0) {
                System.out.println("  ✘ Please type a name, or a part of a name.");
                continue;
            }
 
            int count = 0;
            System.out.println();
            for (int i = 0; i < TelecoProvider.ALL.length; i++) {
                TelecoProvider p = TelecoProvider.ALL[i];
                int[] matches = list.linearSearch(p, name);
                for (int j = 0; j < matches.length; j++) {
                    if (count == 0) {
                        System.out.println("Search results:");
                    }
                    menu.showContact(list.startPosition(p) + matches[j], list.get(p, matches[j]));
                    count++;
                }
            }
            if (count == 0) {
                System.out.println("No contact was found with \"" + name + "\" in the name. Check the spelling or try a shorter part of the name.");
            } else {
                System.out.println("Found " + count + " contact(s).");
            }
            return true;
        }
    }
 
    private boolean searchById() {
        while (true) {
            System.out.print("Type the ID to look for (for example 2S, 1G or 3D; or just a number like 2): ");
            String typed = KeyInput.readLine(input);
            if (typed == null) {
                return false;
            }
            typed = typed.trim();
 
            TelecoProvider only = null;
            boolean hasLetter = false;
            if (typed.length() > 1 && Character.isLetter(typed.charAt(typed.length() - 1))) {
                hasLetter = true;
                String letter = typed.substring(typed.length() - 1).toUpperCase();
                for (int i = 0; i < TelecoProvider.ALL.length; i++) {
                    if (TelecoProvider.ALL[i].getInitial().equals(letter)) {
                        only = TelecoProvider.ALL[i];
                    }
                }
                if (only == null) {
                    System.out.println("  ✘ An ID ends with S (Smart), G (Globe) or D (DITO), like 2S. Please try again.");
                    continue;
                }
                typed = typed.substring(0, typed.length() - 1);
            }
 
            int number = parseNumber(typed);
            if (number < 0) {
                System.out.println("  ✘ Please type the ID number, like 2S or just 2.");
                continue;
            }
 
            int count = 0;
            System.out.println();
            for (int i = 0; i < TelecoProvider.ALL.length; i++) {
                TelecoProvider p = TelecoProvider.ALL[i];
                if (only != null && only != p) {
                    continue;
                }
                if (!list.isSortedById(p)) {
                    continue;
                }
                int result = list.binarySearch(p, number);
                if (result != -1) {
                    if (count == 0) {
                        System.out.println("Search results:");
                    }
                    menu.showContact(list.startPosition(p) + result, list.get(p, result));
                    count++;
                }
            }
            if (count == 0) {
                System.out.println("No contact has that ID.");
            } else if (!hasLetter && count > 1) {
                System.out.println("(Tip: add S, G or D to look in one network only, like " + number + "G.)");
            }
            return true;
        }
    }
 
    private boolean searchByCode() {
        while (true) {
            System.out.print("Type the code to look for (001 for Smart, 01 for Globe, 1 for DITO): ");
            String typed = KeyInput.readLine(input);
            if (typed == null) {
                return false;
            }
            typed = typed.trim();
            int number = parseNumber(typed);
            if (number < 0) {
                System.out.println("  ✘ Please type the code using numbers only, like 001, 01 or 1.");
                continue;
            }
 
            int exact = 0;
            int sameNumberOnly = 0;
            System.out.println();
            for (int i = 0; i < TelecoProvider.ALL.length; i++) {
                TelecoProvider p = TelecoProvider.ALL[i];
                if (!list.isSortedById(p)) {
                    continue;
                }
                int result = list.recursiveBinarySearch(p, number);
                if (result == -1) {
                    continue;
                }
                Contact found = list.get(p, result);
                if (found.getCorrespondingNumber().equals(typed)) {
                    if (exact == 0) {
                        System.out.println("Search results:");
                    }
                    menu.showContact(list.startPosition(p) + result, found);
                    exact++;
                } else {
                    sameNumberOnly++;
                }
            }
            if (exact == 0) {
                if (sameNumberOnly > 0) {
                    System.out.println("No contact has the code " + typed + ". Smart codes look like 001, Globe codes like 01 and DITO codes like 1.");
                } else {
                    System.out.println("No contact has that code.");
                }
            }
            return true;
        }
    }
 
    private Contact askForContact(boolean askPosition) {
        TelecoProvider provider = null;
        TelecoProvider[] options = null;
        String name = null;
        String phone = null;
        String email = null;
        int total = askPosition ? 4 : 3;
        int step = 0;
 
        while (step < 5) {
            if (step == 0) {
                System.out.print("Step 1 of " + total + " - Phone number (example: 09*********): ");
                String typed = KeyInput.readLine(input);
                if (typed == null) {
                    return null;
                }
                typed = typed.trim();
                String error = Validator.checkPhoneFormat(typed);
                if (error != null) {
                    System.out.println("  ✘ " + error);
                    continue;
                }
                options = TelecoProvider.findProviders(typed);
                if (options.length == 0) {
                    System.out.println("  ✘ We could not tell which network this number belongs to (it starts with "
                            + typed.substring(0, 4) + "). Please check the number and try again.");
                    continue;
                }
                phone = typed;
                if (options.length == 1) {
                    provider = options[0];
                    System.out.println("  ✔ This is a " + provider.getName() + " number.");
                    step = askPosition ? 2 : 3;
                } else {
                    step = 1;
                }
 
            } else if (step == 1) {
                System.out.println("  Numbers starting with " + phone.substring(0, 4) + " are used by both "
                        + options[0].getName() + " and " + options[1].getName() + ".");
                System.out.println("  Which network is this number from?");
                System.out.println("    1. " + options[0].getName());
                System.out.println("    2. " + options[1].getName());
                System.out.print("  Type 1 or 2 and press ENTER: ");
                int choice = readInt();
                if (choice == BACK) {
                    step = 0;
                    continue;
                }
                if (choice < 1 || choice > options.length) {
                    System.out.println("  ✘ Please type 1 or 2.");
                    continue;
                }
                provider = options[choice - 1];
                System.out.println("  ✔ Okay, " + provider.getName() + ".");
                step = askPosition ? 2 : 3;
 
            } else if (step == 2) {
                menu.showContacts(provider);
                int last = list.getSize(provider) + 1;
                System.out.println("Step 2 of " + total + " - Where should the new contact go in the "
                        + provider.getName() + " list?");
                if (last == 1) {
                    System.out.print("  The list is empty, so type 1 and press ENTER: ");
                } else {
                    System.out.print("  Type 1 to put it first, " + last + " to put it last, or any number in between: ");
                }
                int position = readInt();
                if (position == BACK) {
                    step = (options.length == 2) ? 1 : 0;
                    continue;
                }
                if (position < 1 || position > last) {
                    System.out.println("  ✘ Please choose a number from 1 to " + last + ".");
                    continue;
                }
                lastPosition = position;
                step = 3;
 
            } else if (step == 3) {
                System.out.print("Step " + (askPosition ? 3 : 2) + " of " + total + " - Name of the contact: ");
                String typed = KeyInput.readLine(input);
                if (typed == null) {
                    if (askPosition) {
                        step = 2;
                    } else {
                        step = (options.length == 2) ? 1 : 0;
                    }
                    continue;
                }
                typed = typed.trim();
                String error = Validator.checkName(typed);
                if (error != null) {
                    System.out.println("  ✘ " + error);
                    continue;
                }
                name = typed;
                step = 4;
 
            } else {
                System.out.print("Step " + total + " of " + total
                        + " - Email (optional, just press ENTER to skip): ");
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
 
    private void saveToFile(boolean showMessage) {
        if (FileHandler.save(list)) {
            if (showMessage) {
                System.out.println("✔ Your " + list.getSize() + " contact(s) were saved in the file "
                        + FileHandler.FILE_NAME + ".");
            }
        } else {
            System.out.println("  ✘ We could not save your contacts to the file " + FileHandler.FILE_NAME
                    + ". Please make sure the folder is not locked or read-only.");
        }
    }
 
    private void loadFromFile(boolean showMessage) {
        int loaded = FileHandler.load(list);
        if (loaded == -1) {
            if (FileHandler.exists()) {
                System.out.println("  ✘ We could not read the file " + FileHandler.FILE_NAME
                        + ". It may be damaged or open in another program.");
            } else if (showMessage) {
                System.out.println("There is no saved file yet. Add a contact first, or choose 7 to save.");
            }
            return;
        }
 
        System.out.println("   ✔ " + loaded + " contact(s) were loaded from the file " + FileHandler.FILE_NAME + ".");
        if (FileHandler.getSkippedLines() > 0) {
            System.out.println("   (" + FileHandler.getSkippedLines()
                    + " line(s) in the file could not be understood and were skipped.)");
        }
    }
 
    private int parseNumber(String typed) {
        if (typed.length() == 0 || typed.length() > 9) {
            return -1;
        }
        int number = 0;
        for (int i = 0; i < typed.length(); i++) {
            char c = typed.charAt(i);
            if (c < '0' || c > '9') {
                return -1;
            }
            number = number * 10 + (c - '0');
        }
        return number;
    }
 
    private int readInt() {
        while (true) {
            String typed = KeyInput.readLine(input);
            if (typed == null) {
                return BACK;
            }
            int number = parseNumber(typed.trim());
            if (number >= 0) {
                return number;
            }
            System.out.print("Please type a number only (for example: 1). Try again: ");
        }
    }
}