package contactmanagementsystem;
// [NEW] File Handling System - saves / loads the contacts to a text file (contacts.txt).
// Written in the same style as the professor's sample FileHandler: BufferedWriter / BufferedReader,
// one record per line, fields separated by '|', and fully-written java.io names (so no import lines).
//
// File format (one contact per line):
//   CMS-CONTACTS-V1
//   id|Telco|correspondingNumber|name|phone|email

public class FileHandler {

    public static final String FILE_NAME = "contacts.txt";
    private static final String HEADER = "CMS-CONTACTS-V1";

    private static int skippedLines = 0;      // how many bad lines the last load() ignored

    public static boolean exists() {
        return new java.io.File(FILE_NAME).exists();
    }

    public static int getSkippedLines() {
        return skippedLines;
    }

    // Writes ALL contacts (Smart, then Globe, then DITO) to the file. Returns true if it worked.
    public static boolean save(ContactList list) {
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                new java.io.OutputStreamWriter(new java.io.FileOutputStream(FILE_NAME), "UTF-8"))) {

            writer.write(HEADER);
            writer.newLine();

            for (int i = 0; i < list.getSize(); i++) {
                Contact c = list.get(i);
                String line = c.getId() + "|" + c.getProvider().getName() + "|" + c.getCorrespondingNumber()
                        + "|" + c.getName() + "|" + c.getPhone() + "|" + c.getEmail();
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (java.io.IOException e) {
            System.out.println("Error saving contacts to file: " + e.getMessage());
            return false;
        }
    }

    // Reads the file and adds every good line to the list (into the Dynamic Array of its Telco).
    // Returns how many contacts were loaded, or -1 if the file is missing / cannot be read.
    // Lines that are broken or fail the validations are skipped (see getSkippedLines()).
    public static int load(ContactList list) {
        skippedLines = 0;
        java.io.File file = new java.io.File(FILE_NAME);
        if (!file.exists()) {
            return -1;
        }

        int loaded = 0;
        DynamicArray.debug = false;        // do not print "resized" messages for every loaded contact
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(new java.io.FileInputStream(file), "UTF-8"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("﻿", "").trim();     // also ignores a BOM if an editor added one
                if (line.length() == 0 || line.equals(HEADER)) {
                    continue;
                }
                if (loadLine(list, line)) {
                    loaded++;
                } else {
                    skippedLines++;
                }
            }
            return loaded;
        } catch (java.io.IOException e) {
            System.out.println("Error loading contacts from file: " + e.getMessage());
            return -1;
        } finally {
            DynamicArray.debug = true;
        }
    }

    // Turns ONE line of the file into a Contact. Returns false if the line is not valid.
    private static boolean loadLine(ContactList list, String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 6) {
            return false;
        }
        // parts: 0=id  1=Telco  2=corresponding number  3=name  4=phone  5=email
        String name  = parts[3].trim();
        String phone = parts[4].trim();
        String email = parts[5].trim();

        int id;
        try {
            id = Integer.parseInt(parts[0].trim());
        } catch (NumberFormatException e) {
            return false;
        }
        if (id < 1) {
            return false;
        }

        TelecoProvider provider = TelecoProvider.fromName(parts[1]);
        String correspondingNumber = parts[2].trim();
        if (provider == null || correspondingNumber.length() == 0) {
            return false;
        }

        // Same validations as when typing a new contact
        if (Validator.checkName(name) != null
                || Validator.checkPhone(phone, provider) != null
                || Validator.checkEmail(email) != null) {
            return false;
        }

        list.insertAtEnd(new Contact(id, name, phone, email, provider, correspondingNumber));
        return true;
    }
}

