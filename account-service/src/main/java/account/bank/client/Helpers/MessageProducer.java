package account.bank.client.Helpers;

import account.bank.client.Entities.Account;
import account.bank.client.Entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageProducer {
    private Connection connection;
    private Channel channel;

    public MessageProducer() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try {
            this.connection = connectionFactory.newConnection();
            this.channel = this.connection.createChannel();
            this.channel.queueDeclare("User_Sync", false, false, false, null);

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void publishUserAuthInfo(User user, Account account) {
        try {
            class UserAuth{
                public int id;
                public String noCompte;
                public String password;
                public String salt;

                public UserAuth(int id, String noCompte, String password, String salt) {
                    this.id = id;
                    this.noCompte = noCompte;
                    this.password = password;
                    this.salt = salt;
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

                public String getSalt() {
                    return salt;
                }

                public void setSalt(String salt) {
                    this.salt = salt;
                }
            }

            UserAuth userAuth = new UserAuth(
                    user.getId(),
                    account.getAccountNumber().toString(),
                    user.getPassword(),
                    user.getSalt()
            );

            ObjectMapper mapper = new ObjectMapper();
            String messageJson = mapper.writeValueAsString(userAuth);
            channel.basicPublish("", "User_Sync", false, null, messageJson.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
