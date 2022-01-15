package account.bank.client.DAO;

import account.bank.client.Entities.Account;

import java.util.List;

public interface IAccountDAO {
    public void save(Account account);
    public Account findById(Long id);
    public List<Account>findByUserId(int id);
    public List<Account> listAll();
    public void update(Account account);
    public void delete(Long id);
}
