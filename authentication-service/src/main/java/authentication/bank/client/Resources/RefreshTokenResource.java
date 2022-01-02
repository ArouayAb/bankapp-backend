package authentication.bank.client.Resources;

import authentication.bank.client.Entities.RefreshToken;
import authentication.bank.client.Exceptions.TokenNotFoundException;
import authentication.bank.client.Helpers.DataProviderHelper;

import java.sql.SQLException;
import java.util.List;

// RefreshToken resource class managing users (Basic CRUD operations)
// N.B. We can use PreparedStatements for better results
public class RefreshTokenResource extends DataProviderHelper<RefreshToken> implements IRefreshTokenResource {

    public RefreshTokenResource() {
        super(RefreshToken.class);
    }

    private String sqlQuery;

    public void save(RefreshToken token) throws
            SQLException,
            ClassNotFoundException
    {
        sqlQuery = "INSERT INTO RefreshToken(user_id, token) " +
                "VALUES('" + token.getId() + "', '" + token.getToken() + "');";
        executeUpdateQuery(sqlQuery);
    }

    public RefreshToken findById(int user_id) throws
            SQLException,
            ClassNotFoundException,
            IllegalAccessException,
            InstantiationException,
            TokenNotFoundException
    {
        sqlQuery = "SELECT * FROM RefreshToken WHERE user_id = " + user_id + ";";
        List<RefreshToken> tokens = executeQuery(sqlQuery);
        if (tokens.size() == 0) {
            throw new TokenNotFoundException(String.valueOf(user_id));
        }
        return tokens.get(0);
    }

    public void update(RefreshToken refreshToken) throws
            SQLException,
            ClassNotFoundException
    {
        sqlQuery = "UPDATE RefreshToken SET " +
                "token = COALESCE(NULLIF('" + refreshToken.getToken() + "', 'null'), token) " +
                "WHERE user_id = " + refreshToken.getId() + ";";
        executeUpdateQuery(sqlQuery);
    }

}
