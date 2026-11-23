package contactmanagementsystem;

public class FileHandler {
 
    public static final String FILE_NAME = "contacts.txt";
    private static final String HEADER = "CMS-CONTACTS-V2";
    private static final String OLD_HEADER = "CMS-CONTACTS-V1";
 
    private static int skippedLines = 0;      
    private static boolean migrated = false;  
 
    public static boolean exists() {
        return new java.io.File(FILE_NAME).exists();
    }
 
    public static int getSkippedLines() {
        return skippedLines;
    }
 
    public static boolean wasMigrated() {
        return migrated;
    }
 
    
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
 
    
    public static int load(ContactList list) {
        skippedLines = 0;
        migrated = false;
        java.io.File file = new java.io.File(FILE_NAME);
        if (!file.exists()) {
            return -1;
        }
 
        int loaded = 0;
        DynamicArray.debug = false;        
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(new java.io.FileInputStream(file), "UTF-8"))) {
 
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();    
                if (line.length() == 0) {
                    continue;
                }
                if (firstLine) {                              
                    firstLine = false;
                    if (line.equals(HEADER)) {
                        continue;
                    }
                    migrated = true;                          
                    if (line.equals(OLD_HEADER)) {
                        continue;
                    }
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

    private static boolean loadLine(ContactList list, String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 6) {
            return false;
        }
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
        if (provider == null) {
            return false;
        }
 
        
        if (Validator.checkName(name) != null
                || Validator.checkPhone(phone, provider) != null
                || Validator.checkEmailOptional(email) != null) {
            return false;
        }
 
        if (migrated) {
            id = list.nextId(provider);                      
        }
       
        list.insertAtEnd(new Contact(id, name, phone, email, provider, list.formatCode(provider, id)));
        return true;
    }
}