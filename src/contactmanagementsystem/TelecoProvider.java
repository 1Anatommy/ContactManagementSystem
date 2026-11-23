package contactmanagementsystem;

// [ADDED from Ethan]
// [CHANGED] Removed "import java.util.Arrays/List" -> prefixes are stored in plain String[] arrays.
// [CHANGED] Duplicate prefixes inside one provider list (Globe 0926 and 0956 were listed twice) were removed.
// NOTE: 0992 is in BOTH the Smart list and the DITO list in the prefixes you gave DeepSeek. It was kept
//       in both, so a 0992 number is accepted for either provider.

/**
 * TelecoProvider - the three telecommunications providers with their corresponding
 * identification codes and allowed number prefixes.
 *
 * Codes:  Smart = 001, Globe = 01, DITO = 1
 */
public class TelecoProvider {

    public static final TelecoProvider SMART = new TelecoProvider("Smart", "001", new String[] {
            "0813","0907","0908","0909","0910","0811","0912","0913","0914","0918","0919","0920","0921",
            "0928","0929","0930","0938","0939","0940","0946","0947","0948","0949","0950","0951","0961",
            "0963","0968","0969","0970","0981","0989","0992","0998","0999"
    });

    public static final TelecoProvider GLOBE = new TelecoProvider("Globe", "01", new String[] {
            "0817","0905","0906","0915","0916","0917","0926","0927","0935","0936","0945","0955",
            "0956","0965","0966","0967","0975","0976","0977","0995","0997"
    });

    public static final TelecoProvider DITO = new TelecoProvider("DITO", "1", new String[] {
            "0895","0896","0897","0898","0991","0992","0993","0994"
    });

    // [ADDED] All providers in one array (Smart, Globe, DITO) so other classes can loop over them
    public static final TelecoProvider[] ALL = { SMART, GLOBE, DITO };

    private final String name;
    private final String code;
    private final String[] prefixes;

    private TelecoProvider(String name, String code, String[] prefixes) {
        this.name = name;
        this.code = code;
        this.prefixes = prefixes;
    }

    public String getName() { return name; }
    public String getCode() { return code; }
    public int getCodeLength() { return code.length(); }

    /** Checks if the given phone number starts with any allowed prefix for this provider. */
    public boolean isValidPrefix(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) return false;
        for (int i = 0; i < prefixes.length; i++) {
            if (phoneNumber.startsWith(prefixes[i])) {
                return true;
            }
        }
        return false;
    }

    /** Returns a short string listing a few prefixes for user feedback. */
    public String getPrefixesSample() {
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(3, prefixes.length);
        for (int i = 0; i < limit; i++) {
            if (i > 0) sb.append(", ");
            sb.append(prefixes[i]);
        }
        if (prefixes.length > limit) sb.append(", ...");
        return sb.toString();
    }

    /** [ADDED] Returns every allowed prefix (used in the validation error message). */
    public String getAllPrefixes() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < prefixes.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(prefixes[i]);
        }
        return sb.toString();
    }

    /** [ADDED] Finds a provider by its name (used when loading the file). Returns null if not found. */
    public static TelecoProvider fromName(String name) {
        if (name == null) return null;
        for (int i = 0; i < ALL.length; i++) {
            if (ALL[i].name.equalsIgnoreCase(name.trim())) {
                return ALL[i];
            }
        }
        return null;
    }

    @Override
    public String toString() { return name; }
}

