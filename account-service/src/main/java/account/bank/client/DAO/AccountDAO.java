package account.bank.client.DAO;

import account.bank.client.Entities.Account;
import account.bank.client.Exceptions.AccountNotFoundException;

import javax.inject.Inject;
import javax.persistence.*;
import java.util.List;

public class AccountDAO implements IAccountDAO {

    @Inject
    private EntityManager entityManager;

    public AccountDAO(){

    }

    public void save(Account account){
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try{
            entityManager.persist(account);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public Account findById(Long id){
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Account account = null;

        try{
            account = entityManager.find(Account.class, id);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
        return account;
    }

    public List<Account> findByUserId(int id){
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Account account = null;
        try{
            Query query = entityManager.createQuery("select a from Account a WHERE a.user.id = :n");
            query.setParameter("n", id);
            transaction.commit();
            if (query.getResultList().size() == 0) {
                throw new AccountNotFoundException(Integer.toString(id));
            }
            return query.getResultList();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return null;
        }
    }

    public List<Account> listAll(){
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try{
            Query query = entityManager.createQuery("select a from Account a");
            transaction.commit();
            return query.getResultList();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return null;
        }
    }

    public void update(Account account){
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try{
            entityManager.merge(account);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Long id){
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try{
            Account account = entityManager.find(Account.class, id);
            entityManager.remove(account);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }
}
