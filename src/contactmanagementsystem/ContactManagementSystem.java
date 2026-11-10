package contactmanagementsystem;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * ============================================================================
 * CONTACT MANAGEMENT SYSTEM
 * ============================================================================
 * Flow for adding a contact:
 *   1. Choose Teleco Provider (Smart / Globe / DITO)
 *   2. Enter phone number → validated against the provider's prefixes
 *   3. Enter name → contact saved
 * ============================================================================
 */
public class ContactManagementSystem {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ContactList contactList = new ContactList();
        boolean running = true;

        System.out.println("============================================");
        System.out.println("       CONTACT MANAGEMENT SYSTEM");
        System.out.println("     (Dynamic Array + Linear Search)");
        System.out.println("============================================");

        while (running) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Add Contact");
            System.out.println("2. Search Contact");
            System.out.println("3. Display All Contacts");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            try {
                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        addContactUI(sc, contactList);
                        break;
                    case 2:
                        searchContactUI(sc, contactList);
                        break;
                    case 3:
                        contactList.displayAll();
                        break;
                    case 4:
                        running = false;
                        System.out.println("\nExiting... Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();
            }
        }
        sc.close();
    }

    // ------------------------------------------------------------------
    // Add Contact UI – Teleco FIRST, then validated phone, then name
    // ------------------------------------------------------------------
    private static void addContactUI(Scanner sc, ContactList contactList) {
        System.out.println("\n--- ADD CONTACT ---");

        // STEP 1: Choose provider first
        TelecoProvider provider = chooseProvider(sc);
        if (provider == null) return;

        // STEP 2: Ask for phone number and validate prefix
        String phone = null;
        while (true) {
            System.out.print("Enter phone number (must start with a valid "
                    + provider.getName() + " prefix): ");
            phone = sc.nextLine().trim();

            if (provider.isValidPrefix(phone)) {
                break; // valid – exit loop
            } else {
                System.out.println("  ✘ Invalid prefix for " + provider.getName() + ".");
                System.out.println("    Allowed prefixes: " + provider.getPrefixesSample());
                System.out.print("    Try again? (y/n): ");
                String retry = sc.nextLine().trim().toLowerCase();
                if (!retry.equals("y")) {
                    System.out.println("  Contact not added.");
                    return;
                }
            }
        }

        // STEP 3: Ask for name
        System.out.print("Enter name: ");
        String name = sc.nextLine();

        // STEP 4: Save contact
        contactList.addContact(name, phone, provider);
    }

    // ------------------------------------------------------------------
    // Search Contact UI
    // ------------------------------------------------------------------
    private static void searchContactUI(Scanner sc, ContactList contactList) {
        System.out.println("\n--- SEARCH CONTACT ---");
        TelecoProvider provider = chooseProvider(sc);
        if (provider == null) return;

        String example = getExampleNumber(provider);
        System.out.print("Enter corresponding number (e.g., " + example + "): ");
        String correspondingNumber = sc.nextLine().trim();

        contactList.searchContact(provider, correspondingNumber);
    }

    // ------------------------------------------------------------------
    // Helper: choose provider
    // ------------------------------------------------------------------
    private static TelecoProvider chooseProvider(Scanner sc) {
        System.out.println("Choose Teleco Provider:");
        System.out.println("  1. Smart (code: 001)");
        System.out.println("  2. Globe (code: 01)");
        System.out.println("  3. DITO  (code: 1)");
        System.out.print("Enter choice: ");

        int choice = sc.nextInt();
        sc.nextLine(); // consume newline

        switch (choice) {
            case 1: return TelecoProvider.SMART;
            case 2: return TelecoProvider.GLOBE;
            case 3: return TelecoProvider.DITO;
            default:
                System.out.println("Invalid provider choice.");
                return null;
        }
    }

    private static String getExampleNumber(TelecoProvider provider) {
        if (provider == TelecoProvider.SMART) return "001";
        if (provider == TelecoProvider.GLOBE) return "01";
        return "1";
    }
}
