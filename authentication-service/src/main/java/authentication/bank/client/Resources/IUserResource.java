package authentication.bank.client.Resources;

import authentication.bank.client.Entities.User;
import authentication.bank.client.Exceptions.UserNotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface IUserResource {
    public void save(User user) throws SQLException, ClassNotFoundException;
    public User findById(int id) throws SQLException, ClassNotFoundException, UserNotFoundException, IllegalAccessException, InstantiationException;
    public User findByEmail(String email) throws SQLException, ClassNotFoundException, UserNotFoundException, IllegalAccessException, InstantiationException;
    public void update(User user) throws SQLException, ClassNotFoundException;
    public void delete(int id) throws SQLException, ClassNotFoundException;
    public List<User> listAll() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException;
}
