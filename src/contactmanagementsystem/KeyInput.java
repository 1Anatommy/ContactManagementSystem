package contactmanagementsystem;

import java.io.InputStream;
import java.util.Scanner;
 
public class KeyInput {
 
    private static final int KEY_EOF    = -1;   
    private static final int KEY_IGNORE = -2;    
    private static final int KEY_ESC    = 27;
 
    private static final int MODE_NOT_STARTED = 0;
    private static final int MODE_LINE        = 1;   
    private static final int MODE_UNIX        = 2;   
    private static final int MODE_WINDOWS     = 3;   
 
    private static int mode = MODE_NOT_STARTED;
    private static String savedTerminalSettings = null;   
    private static Process windowsHelper = null;          
    private static InputStream windowsHelperOut = null;
 

    private static final String WINDOWS_SCRIPT =
            "try{[void][Console]::KeyAvailable;[Console]::Out.WriteLine('READY');"
          + "while($true){$k=[Console]::ReadKey($true);[Console]::Out.WriteLine('K,'+[int]$k.KeyChar)}}"
          + "catch{exit 1}";
 
    
    public static String readLine(Scanner input) {
        if (mode == MODE_NOT_STARTED) {
            start();
        }
        if (mode == MODE_LINE) {
            return readLineSimple(input);
        }
        return readLineKeys();
    }
 
    
    public static String getHint() {
        if (mode == MODE_NOT_STARTED) {
            start();
        }
        if (mode == MODE_LINE) {
            return "Tip: to go back one step, type ESC (or the ` key) and press ENTER.";
        }
        return "Tip: press ESC (or Backspace on an empty field) to go back one step.";
    }
 
    
    private static void start() {
        mode = MODE_LINE;
        if (System.getProperty("cms.simple") != null) {
            return;
        }
 
        String os = System.getProperty("os.name");
        if (os != null && os.toLowerCase().startsWith("windows")) {
            if (startWindowsHelper()) {
                mode = MODE_WINDOWS;
            }
        } else {
            if (startUnixRawMode()) {
                mode = MODE_UNIX;
            }
        }
 
        if (mode != MODE_LINE) {
            
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    restoreTerminal();
                }
            });
        }
    }
 
   
    private static boolean startUnixRawMode() {
        try {
            String saved = runStty("-g");                    
            if (saved == null || saved.trim().length() == 0) {
                return false;                                
            }
           
            if (runStty("-icanon -echo min 1 time 0") == null) {
                return false;
            }
            savedTerminalSettings = saved.trim();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
 
    
    private static String runStty(String args) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", "stty " + args + " 2>/dev/null");
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);   
        Process p = pb.start();
        InputStream out = p.getInputStream();
        StringBuilder sb = new StringBuilder();
        int b;
        while ((b = out.read()) != -1) {
            sb.append((char) b);
        }
        if (p.waitFor() != 0) {
            return null;
        }
        return sb.toString();
    }
 
    
    private static boolean startWindowsHelper() {
        try {
            ProcessBuilder pb = new ProcessBuilder("powershell", "-NoProfile", "-NonInteractive",
                    "-ExecutionPolicy", "Bypass", "-Command", WINDOWS_SCRIPT);
            pb.redirectInput(ProcessBuilder.Redirect.INHERIT);   // the helper reads OUR console
            pb.redirectErrorStream(true);
            windowsHelper = pb.start();
            windowsHelperOut = windowsHelper.getInputStream();
 
            while (true) {
                String line = readHelperLine();
                if (line == null) {                    
                    windowsHelper.destroy();
                    windowsHelper = null;
                    return false;
                }
                if (line.equals("READY")) {
                    return true;
                }
            }
        } catch (Exception e) {
            if (windowsHelper != null) {
                windowsHelper.destroy();
                windowsHelper = null;
            }
            return false;
        }
    }
 
    private static String readHelperLine() throws Exception {
        StringBuilder sb = new StringBuilder();
        int b;
        while ((b = windowsHelperOut.read()) != -1 && b != '\n') {
            if (b != '\r') {
                sb.append((char) b);
            }
        }
        if (b == -1 && sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }
 
    private static void restoreTerminal() {
        try {
            if (savedTerminalSettings != null) {
                runStty(savedTerminalSettings);
                savedTerminalSettings = null;
            }
            if (windowsHelper != null) {
                windowsHelper.destroy();
                windowsHelper = null;
            }
        } catch (Exception e) {
            // nothing else we can do
        }
    }
 
    
    private static String readLineSimple(Scanner input) {
        try {
            String line = input.nextLine();                       
 
            
            if (line.indexOf((char) KEY_ESC) != -1) return null;
            if (line.length() > 0 && (line.charAt(0) == 8 || line.charAt(0) == 127)) return null;
            if (line.trim().equals("`")) return null;
            return line;
        } catch (Exception e) {                                   
            endOfInput();
            return null;
        }
    }
 
   
    private static String readLineKeys() {
        StringBuilder typed = new StringBuilder();
 
        while (true) {
            int key = nextKey();
 
            if (key == KEY_EOF) {
                endOfInput();
            }
            if (key == KEY_IGNORE) {
                continue;
            }
            if (key == '\n' || key == '\r') {                      
                System.out.println();
                return typed.toString();
            }
            if (key == KEY_ESC) {                                  
                System.out.println("(back)");
                return null;
            }
            if (key == 8 || key == 127) {                          
                if (typed.length() == 0) {                         
                    System.out.println("(back)");
                    return null;
                }
                typed.setLength(typed.length() - 1);               
                System.out.print("\b \b");
                System.out.flush();
                continue;
            }
            if (key == '`' && typed.length() == 0) {               
                System.out.println("(back)");
                return null;
            }
            if (key >= 32 && typed.length() < 200) {               
                typed.append((char) key);
                System.out.print((char) key);
                System.out.flush();
            }
        }
    }
 
   
    private static int nextKey() {
        try {
            if (mode == MODE_WINDOWS) {
                return nextKeyWindows();
            }
            return nextKeyUnix();
        } catch (Exception e) {
            return KEY_EOF;
        }
    }
 
    private static int nextKeyWindows() throws Exception {
        while (true) {
            String line = readHelperLine();
            if (line == null) {
                return KEY_EOF;
            }
            if (line.startsWith("K,")) {
                int code = 0;
                for (int i = 2; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (c < '0' || c > '9') {
                        return KEY_IGNORE;
                    }
                    code = code * 10 + (c - '0');
                }
                if (code == 0) {                  
                    return KEY_IGNORE;
                }
                return code;
            }
        }
    }
 
    private static int nextKeyUnix() throws Exception {
        int b = System.in.read();
        if (b == -1) {
            return KEY_EOF;
        }
 
        if (b == 27) {
            
            if (System.in.available() > 0) {
                int next = System.in.read();
                if (next == '[') {
                    int c;
                    do {
                        c = System.in.read();
                    } while (c != -1 && !(c >= 0x40 && c <= 0x7E));
                } else if (next == 'O') {
                    System.in.read();
                }
                return KEY_IGNORE;
            }
            return KEY_ESC;
        }
 
        
        if (b >= 0xC2 && b < 0xF0) {
            int extra = (b < 0xE0) ? 1 : 2;
            byte[] bytes = new byte[extra + 1];
            bytes[0] = (byte) b;
            for (int i = 1; i <= extra; i++) {
                int more = System.in.read();
                if (more == -1) {
                    return KEY_EOF;
                }
                bytes[i] = (byte) more;
            }
            return new String(bytes, "UTF-8").charAt(0);
        }
        if (b >= 0xF0) {                        
            System.in.read();
            System.in.read();
            System.in.read();
            return KEY_IGNORE;
        }
        if (b >= 0x80) {
            return KEY_IGNORE;
        }
        return b;
    }
 
    
    private static void endOfInput() {
        System.out.println();
        System.out.println("No more input. Exiting...");
        System.exit(0);
    }
}

