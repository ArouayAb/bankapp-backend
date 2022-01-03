package account.bank.client.DAO;

import account.bank.client.Entities.User;
import account.bank.client.Exceptions.UserNotFoundException;

import java.util.List;

public interface IUserDAO {
    void save(User user);
    User findById(int id) throws UserNotFoundException;
    User findByEmail(String email) throws UserNotFoundException;
    void update(User user);
    void delete(int id);
    List<User> listAll();
}
