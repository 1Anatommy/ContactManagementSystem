package contactmanagementsystem;

import java.util.Arrays;
import java.util.List;

/**
 * TelecoProvider - represents the three telecommunications providers
 * with their corresponding identification codes and allowed number prefixes.
 *
 * Codes:  Smart = 001, Globe = 01, DITO = 1
 */
public class TelecoProvider {

    public static final TelecoProvider SMART = new TelecoProvider("Smart", "001", Arrays.asList(
            "0813","0907","0908","0909","0910","0811","0912","0913","0914","0918","0919","0920","0921",
            "0928","0929","0930","0938","0939","0940","0946","0947","0948","0949","0950","0951","0961",
            "0963","0968","0969","0970","0981","0989","0992","0998","0999"
    ));

    public static final TelecoProvider GLOBE = new TelecoProvider("Globe", "01", Arrays.asList(
            "0817","0905","0906","0915","0916","0917","0926","0926","0927","0935","0936","0945","0955",
            "0956","0956","0965","0966","0967","0975","0976","0977","0995","0997"
    ));

    public static final TelecoProvider DITO = new TelecoProvider("DITO", "1", Arrays.asList(
            "0895","0896","0897","0898","0991","0992","0993","0994"
    ));

    private final String name;
    private final String code;
    private final List<String> prefixes;

    private TelecoProvider(String name, String code, List<String> prefixes) {
        this.name = name;
        this.code = code;
        this.prefixes = prefixes;
    }

    public String getName() { return name; }
    public String getCode() { return code; }
    public int getCodeLength() { return code.length(); }

    /**
     * Checks if the given phone number starts with any allowed prefix
     * for this provider.
     */
    public boolean isValidPrefix(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) return false;
        for (String prefix : prefixes) {
            if (phoneNumber.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /** Returns a short string listing prefixes for user feedback. */
    public String getPrefixesSample() {
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(3, prefixes.size());
        for (int i = 0; i < limit; i++) {
            if (i > 0) sb.append(", ");
            sb.append(prefixes.get(i));
        }
        if (prefixes.size() > limit) sb.append(", ...");
        return sb.toString();
    }

    @Override
    public String toString() { return name; }
}
