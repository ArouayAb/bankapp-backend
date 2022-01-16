package account.bank.client.DAO;

import account.bank.client.Entities.Account;
import account.bank.client.Entities.Transaction;
import account.bank.client.Exceptions.AccountNotFoundException;
import account.bank.client.Exceptions.TransactionNotFoundException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

public class TransactionDAO implements ITransactionDAO{

    @Inject
    private EntityManager entityManager;

    @Override
    public void save(Transaction t) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            entityManager.persist(t);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public List<Transaction> findByAccount(Account account) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Transaction t = null;
        try{
            Query query = entityManager.createQuery("select t from Transaction t WHERE t.receiver = :a OR t.sender = :a");
            query.setParameter("a", account);
            transaction.commit();
            if (query.getResultList().size() == 0) {
                return null;
            }
            return query.getResultList();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
