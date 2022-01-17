package account.bank.client.Entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="transactions")
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String type;
    @ManyToOne
    @JoinColumn(name = "sender_account_nbr")
    private Account sender;
    @ManyToOne
    @JoinColumn(name = "receiver_account_nbr")
    private Account receiver;
    private double ammount;

    public Transaction() {
    }

    public Transaction(String type, Account sender, Account receiver, double ammount) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.ammount = ammount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public Account getReceiver() {
        return receiver;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    public double getAmmount() {
        return ammount;
    }

    public void setAmmount(double ammount) {
        this.ammount = ammount;
    }
}
