package contactmanagementsystem;

public class Contact {
    private String name;
    private String phoneNumber;
    private TelecoProvider provider;
    private String correspondingNumber;

    public Contact(String name, String phoneNumber,
                   TelecoProvider provider, String correspondingNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.provider = provider;
        this.correspondingNumber = correspondingNumber;
    }

    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public TelecoProvider getProvider() { return provider; }
    public String getCorrespondingNumber() { return correspondingNumber; }

    @Override
    public String toString() {
        return "[" + provider.getName() + "] (" + correspondingNumber + ") "
                + name + " - " + phoneNumber;
    }
}
