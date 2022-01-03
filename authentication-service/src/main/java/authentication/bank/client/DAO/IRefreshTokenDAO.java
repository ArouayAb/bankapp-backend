package authentication.bank.client.DAO;


import authentication.bank.client.Entities.RefreshToken;


public interface IRefreshTokenDAO {
    void save(RefreshToken token);
    RefreshToken findById(int user_id);
    void update(RefreshToken refreshToken);
}
