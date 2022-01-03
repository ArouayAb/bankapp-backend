package authentication.bank.client.DAO;

import authentication.bank.client.Entities.RefreshToken;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

// RefreshToken resource class managing users (Basic CRUD operations)
// N.B. We can use PreparedStatements for better results
public class RefreshTokenDAO implements IRefreshTokenDAO {

    @Inject
    private EntityManager entityManager;

    public void save(RefreshToken token) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            entityManager.persist(token);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public RefreshToken findById(int user_id) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        RefreshToken refreshToken = null;

        try{
            refreshToken = entityManager.find(RefreshToken.class, user_id);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
        return refreshToken;
    }

    public void update(RefreshToken refreshToken) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try{
            entityManager.merge(refreshToken);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

}
