package account.bank.client.Entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="accounts")
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // For clarity's sake
    private Long accountNumber;
    // Foreign key
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String holderName;
    private double balance;
    private double maxOverDraft;
    private double maxDebitAmount;

    public Account(Long accountNumber, String holderName, double balance, double maxOverDraft, double maxDebitAmount) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
        this.maxOverDraft = maxOverDraft;
        this.maxDebitAmount = maxDebitAmount;
    }

    public Account() {
        super();
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getMaxOverDraft() {
        return maxOverDraft;
    }

    public void setMaxOverDraft(double maxOverDraft) {
        this.maxOverDraft = maxOverDraft;
    }

    public double getMaxDebitAmount() {
        return maxDebitAmount;
    }

    public void setMaxDebitAmount(double maxDebitAmount) {
        this.maxDebitAmount = maxDebitAmount;
    }
}
