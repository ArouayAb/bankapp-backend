package authentication.bank.client.Resources;

import authentication.bank.client.Entities.User;
import authentication.bank.client.Exceptions.UserNotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface IUserDAO {
    void save(User user);
    User findById(int id) throws UserNotFoundException;
    User findByEmail(String email) throws UserNotFoundException;
    void update(User user);
    void delete(int id);
    List<User> listAll();
}
