package authentication.bank.client.Helpers;

import authentication.bank.client.DAO.IUserDAO;
import authentication.bank.client.Entities.User;
import authentication.bank.client.Exceptions.UserNotFoundException;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class MessageConsumer {
    private Connection connection;
    private Channel channel;

    @Inject
    private IUserDAO userDAO;

    public MessageConsumer() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try {
            this.connection = connectionFactory.newConnection();
            this.channel = this.connection.createChannel();
            this.channel.queueDeclare("User_Sync", false, false, false, null);

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    public void asyncSyncronizeUser() {
        try {
            this.channel.basicConsume("User_Sync", true, (consumerTag, message) -> {
                Gson gson = new Gson();
                User user = gson.fromJson(new String(message.getBody(), StandardCharsets.UTF_8), User.class);
                System.out.println(user.getId());
                System.out.println(user.getNoCompte());
                System.out.println(user.getPassword());
                System.out.println(user.getSalt());

                try {
                    userDAO.findById(user.getId());
                    userDAO.update(user);
                } catch (UserNotFoundException e) {
                    userDAO.save(user);
                }

            }, consumerTag -> {});
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
