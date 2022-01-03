package authentication.bank.client.DAO;

import authentication.bank.client.Entities.User;
import authentication.bank.client.Exceptions.UserNotFoundException;

import java.util.List;

public interface IUserDAO {
    void save(User user);
    User findById(int id) throws UserNotFoundException;
    User findByNoCompte(String noCompte) throws UserNotFoundException;
    void update(User user);
    void delete(int id);
    List<User> listAll();
}
