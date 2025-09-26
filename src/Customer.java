import java.math.BigDecimal;

public class Customer {
    private final int customerId;
    private String customerName;
    private final String cardNumber;
    private String email;
    private BigDecimal balance;

    // Constructor
    public Customer (int id, String name, String card, String email, BigDecimal balance){
        this.customerId = id;
        this.customerName = name;
        this.cardNumber = card;
        this.email = email;
        this.balance = balance;
    }

    // Getters & Setters
    public int getId() {return this.customerId;}

    public String getName() {return this.customerName;}
    public void setName(String name) {this.customerName = name;}

    public String getCardNumber() {return this.cardNumber;}

    public String getEmail() {return this.email;}
    public void setEmail(String email) {this.email = email;}

    public BigDecimal getBalance() {return this.balance;}
    public void setBalance(BigDecimal balance) {this.balance = balance;}
}
