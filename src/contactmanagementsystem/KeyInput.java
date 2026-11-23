package contactmanagementsystem;
// [NEW] Keyboard input with "go back" keys. Matt's Scanner is still used for normal typing (mode 3 below).
//
// readLine() returns the text the user typed, or NULL when the user wants to go BACK:
//   - ESC key
//   - Backspace while the field is empty
//   - the ` (backtick) key while the field is empty
//
// Plain Java cannot read a single key press from the console by itself, so there are 3 modes
// (it picks the best one automatically):
//   1. Mac / Linux terminal : switches the terminal to "raw" mode with the stty command  -> real key presses
//   2. Windows console      : starts a tiny PowerShell helper that reads the keys         -> real key presses
//   3. Anything else (IDE console such as NetBeans / IntelliJ / Eclipse, or input from a file):
//      the normal Scanner "type a line then press ENTER" mode. To go back here, type ESC or ` and then press ENTER.
// To force mode 3, run the program with:  java -Dcms.simple=true ContactManagementSystem_2

import java.io.InputStream;
import java.util.Scanner;

public class KeyInput {

    private static final int KEY_EOF    = -1;    // no more input
    private static final int KEY_IGNORE = -2;    // a key we do not use (arrow keys, etc.)
    private static final int KEY_ESC    = 27;

    private static final int MODE_NOT_STARTED = 0;
    private static final int MODE_LINE        = 1;   // normal typing + ENTER
    private static final int MODE_UNIX        = 2;   // raw terminal (stty)
    private static final int MODE_WINDOWS     = 3;   // PowerShell key reader

    private static int mode = MODE_NOT_STARTED;
    private static String savedTerminalSettings = null;   // Mac/Linux: to put the terminal back as it was
    private static Process windowsHelper = null;          // Windows: the PowerShell helper
    private static InputStream windowsHelperOut = null;

    // PowerShell script: prints READY once it can read keys, then one line "K,<character code>" per key press.
    private static final String WINDOWS_SCRIPT =
            "try{[void][Console]::KeyAvailable;[Console]::Out.WriteLine('READY');"
          + "while($true){$k=[Console]::ReadKey($true);[Console]::Out.WriteLine('K,'+[int]$k.KeyChar)}}"
          + "catch{exit 1}";

    // ------------------------------------------------------------------
    // Public methods
    // ------------------------------------------------------------------

    // Returns the typed line, or null if the user pressed a "go back" key.
    // 'input' is Matt's Scanner; it is only used in the normal line mode.
    public static String readLine(Scanner input) {
        if (mode == MODE_NOT_STARTED) {
            start();
        }
        if (mode == MODE_LINE) {
            return readLineSimple(input);
        }
        return readLineKeys();
    }

    // One-line tip shown when the program starts (tells the user which keys go back)
    public static String getHint() {
        if (mode == MODE_NOT_STARTED) {
            start();
        }
        if (mode == MODE_LINE) {
            return "Tip: to go back one step, type ESC (or the ` key) and press ENTER.";
        }
        return "Tip: press ESC (or Backspace on an empty field) to go back one step.";
    }

    // ------------------------------------------------------------------
    // Choosing the mode
    // ------------------------------------------------------------------
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
            // Always put the terminal back to normal when the program ends (even with Ctrl+C)
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    restoreTerminal();
                }
            });
        }
    }

    // Mac / Linux ---------------------------------------------------------
    private static boolean startUnixRawMode() {
        try {
            String saved = runStty("-g");                    // remember the current settings
            if (saved == null || saved.trim().length() == 0) {
                return false;                                // not a real terminal
            }
            // no line buffering, no automatic echo: we get every key press right away
            if (runStty("-icanon -echo min 1 time 0") == null) {
                return false;
            }
            savedTerminalSettings = saved.trim();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Runs "stty <args>" on the real terminal. Returns its output, or null if it failed.
    private static String runStty(String args) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", "stty " + args + " 2>/dev/null");
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);   // stty must look at OUR terminal
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

    // Windows -------------------------------------------------------------
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
                if (line == null) {                    // helper ended -> keys cannot be read here (IDE console)
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

    // ------------------------------------------------------------------
    // Mode 1: normal line input (typing + ENTER)
    // ------------------------------------------------------------------
    private static String readLineSimple(Scanner input) {
        try {
            String line = input.nextLine();                       // Matt's Scanner

            // "go back" keys in this mode: ESC anywhere in the line, Backspace first, or just a `
            if (line.indexOf((char) KEY_ESC) != -1) return null;
            if (line.length() > 0 && (line.charAt(0) == 8 || line.charAt(0) == 127)) return null;
            if (line.trim().equals("`")) return null;
            return line;
        } catch (Exception e) {                                   // no more input (NoSuchElementException)
            endOfInput();
            return null;
        }
    }

    // ------------------------------------------------------------------
    // Modes 2 and 3 (Mac/Linux, Windows): real key presses
    // ------------------------------------------------------------------
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
            if (key == '\n' || key == '\r') {                      // ENTER
                System.out.println();
                return typed.toString();
            }
            if (key == KEY_ESC) {                                  // ESC -> go back
                System.out.println("(back)");
                return null;
            }
            if (key == 8 || key == 127) {                          // Backspace
                if (typed.length() == 0) {                         // empty field -> go back
                    System.out.println("(back)");
                    return null;
                }
                typed.setLength(typed.length() - 1);               // otherwise delete one character
                System.out.print("\b \b");
                System.out.flush();
                continue;
            }
            if (key == '`' && typed.length() == 0) {               // ` key on an empty field -> go back
                System.out.println("(back)");
                return null;
            }
            if (key >= 32 && typed.length() < 200) {               // a normal character
                typed.append((char) key);
                System.out.print((char) key);
                System.out.flush();
            }
        }
    }

    // Reads ONE key press. Returns the character code, KEY_ESC, KEY_IGNORE or KEY_EOF.
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
                if (code == 0) {                  // arrow keys, F-keys ... have no character
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
            // A lone ESC press sends only the byte 27.
            // Arrow keys etc. send 27 followed by more bytes ("ESC [ A") -> read them and ignore them.
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

        // Letters like ñ / Ñ come as 2 or 3 bytes (UTF-8): read the rest and turn them into one character
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
        if (b >= 0xF0) {                          // 4-byte characters (emoji): skip them
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

    // Input stopped (Ctrl+D, or the input file ended): leave the program
    private static void endOfInput() {
        System.out.println();
        System.out.println("No more input. Exiting...");
        System.exit(0);
    }
}

