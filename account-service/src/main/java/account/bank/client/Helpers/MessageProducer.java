package account.bank.client.Helpers;

import account.bank.client.Entities.Account;
import account.bank.client.Entities.User;
import com.google.gson.Gson;
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
            this.channel.queueDeclare("User_Sync", false, true, false, null);

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void asyncPublishUserAuthInfo(User user, Account account) {
        try {
            class UserAuth{
                private int id;
                private String noCompte;
                private String password;
                private String salt;

                public UserAuth(int id, String noCompte, String password, String salt) {
                    this.id = id;
                    this.noCompte = noCompte;
                    this.password = password;
                    this.salt = salt;
                }

            }

            UserAuth userAuth = new UserAuth(
                    user.getId(),
                    account.getAccountNumber().toString(),
                    user.getPassword(),
                    user.getSalt()
            );

            Gson gson = new Gson();
            String messageJson = gson.toJson(userAuth);
            channel.basicPublish("", "User_Sync", false, null, messageJson.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
