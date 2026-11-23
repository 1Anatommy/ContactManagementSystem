package contactmanagementsystem;

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

    
    public boolean isValidPrefix(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) return false;
        for (int i = 0; i < prefixes.length; i++) {
            if (phoneNumber.startsWith(prefixes[i])) {
                return true;
            }
        }
        return false;
    }

    
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

    
    public String getAllPrefixes() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < prefixes.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(prefixes[i]);
        }
        return sb.toString();
    }

   
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

