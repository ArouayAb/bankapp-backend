package authentication.bank.client.Entities;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;

import javax.persistence.*;
import java.io.Serializable;

// RefreshToken bean
@Entity
@Table(name="refresh_tokens")
public class RefreshToken implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;
    private String token;

    public RefreshToken(){
        super();
    }

    public RefreshToken(String token){
        this.token = token;
    }

    public RefreshToken(String token, int user_id){
        this.token = token;
        this.user_id = user_id;
    }

    public void setId(int user_id){
        this.user_id = user_id;
    }

    public void setToken(String token){
        this.token = token;
    }

    public int getId(){
        return this.user_id;
    }

    public String getToken(){
        return this.token;
    }
}
