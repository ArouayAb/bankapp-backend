package account.bank.client.DAO;

import account.bank.client.Entities.User;
import account.bank.client.Exceptions.UserNotFoundException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

public class UserDAO implements IUserDAO {

    @Inject
    private EntityManager entityManager;

    public void save(User user) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            entityManager.persist(user);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }

    }

    public User findById(int id) throws
            UserNotFoundException
    {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        User user = null;

        try{
            user = entityManager.find(User.class, id);
            transaction.commit();
            if (user == null) {
                throw new UserNotFoundException(Integer.toString(id));
            }
        } catch(UserNotFoundException ue){
            throw ue;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
        return user;
    }

    public User findByEmail(String email) throws
            UserNotFoundException
    {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try{
            Query query = entityManager.createQuery("select u from User u where u.email like :em");
            query.setParameter("em", email);
            transaction.commit();
            if (query.getResultList().size() == 0) {
                throw new UserNotFoundException(email);
            }
            return (User) query.getResultList().get(0);
        } catch (UserNotFoundException ue) {
            throw ue;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return null;
        }
    }

    public void update(User user) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try{
            entityManager.merge(user);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try{
            User user = entityManager.find(User.class, id);
            entityManager.remove(user);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public List<User> listAll()
    {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try{
            Query query = entityManager.createQuery("select u from User u");
            transaction.commit();
            return query.getResultList();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return null;
        }

    }
}
