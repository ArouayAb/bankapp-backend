package account.bank.client.DAO;

import account.bank.client.Entities.Account;
import account.bank.client.Entities.Transaction;

import java.util.List;

public interface ITransactionDAO {
    void save(Transaction t);
    List<Transaction> findByAccount(Account account);
}
