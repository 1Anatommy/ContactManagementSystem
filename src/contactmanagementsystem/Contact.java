package contactmanagementsystem;

public class Contact {

    private int    id;
    private String name;
    private String phone;
    private String email;
    private TelecoProvider provider;        
    private String correspondingNumber;     

    // [CHANGED] The constructor now also takes the provider and the corresponding number
    public Contact(int id, String name, String phone, String email,
                   TelecoProvider provider, String correspondingNumber) {
        this.id    = id;
        this.name  = name;
        this.phone = phone;
        this.email = email;
        this.provider = provider;
        this.correspondingNumber = correspondingNumber;
    }

    public int    getId()
    { return id; }

    public String getName()
    { return name; }

    public String getPhone()
    { return phone; }

    public String getEmail()
    { return email; }

    // [ADDED from Ethan]
    public TelecoProvider getProvider()
    { return provider; }

    // [ADDED from Ethan]
    public String getCorrespondingNumber()
    { return correspondingNumber; }

    // [ADDED from Ethan] (Ethan's toString, plus the email)
    @Override
    public String toString() {
        return "[" + provider.getName() + "] (" + correspondingNumber + ") "
                + name + " - " + phone + " - " + email;
    }
}

