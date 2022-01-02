package authentication.bank.client.Resources;


import authentication.bank.client.Entities.RefreshToken;
import authentication.bank.client.Exceptions.TokenNotFoundException;
import authentication.bank.client.Exceptions.UserNotFoundException;

import java.sql.SQLException;

public interface IRefreshTokenResource {
    public void save(RefreshToken token) throws SQLException, ClassNotFoundException;
    public RefreshToken findById(int user_id) throws SQLException, ClassNotFoundException, UserNotFoundException, IllegalAccessException, InstantiationException, TokenNotFoundException;
    public void update(RefreshToken refreshToken) throws SQLException, ClassNotFoundException;
}
