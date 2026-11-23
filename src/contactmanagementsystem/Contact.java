package contactmanagementsystem;

public class Contact {
 
    private int    id;                      
    private String name;
    private String phone;
    private String email;                   
    private TelecoProvider provider;        
    private String correspondingNumber;     
 
    
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
 
    
    public String getIdLabel()
    { return id + provider.getInitial(); }
 
    public String getName()
    { return name; }
 
    public String getPhone()
    { return phone; }
 
    public String getEmail()
    { return email; }
 
  
    public String getEmailDisplay()
    { return email.length() == 0 ? "(no email)" : email; }
 
    public TelecoProvider getProvider()
    { return provider; }
 

    public String getCorrespondingNumber()
    { return correspondingNumber; }
 
    @Override
    public String toString() {
        return "[" + provider.getName() + "] ID " + getIdLabel() + " (" + correspondingNumber + ") "
                + name + " - " + phone + " - " + getEmailDisplay();
    }
}

