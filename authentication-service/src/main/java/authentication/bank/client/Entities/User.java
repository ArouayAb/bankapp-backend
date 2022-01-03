package authentication.bank.client.Entities;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;

// User bean
@Entity
@Table(name="users")
public class User implements Serializable {
    @Id
    private int id;
    private String noCompte;
    private String password;
    private String salt;

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoCompte() {
        return noCompte;
    }

    public void setNoCompte(String noCompte) {
        this.noCompte = noCompte;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
