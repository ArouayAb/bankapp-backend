package account.bank.client.Helpers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@ApplicationScoped
public class EntityManagerProducer {

    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("PU_ACC");

    @Produces
    public EntityManager getEntityManager(){
        return entityManagerFactory.createEntityManager();
    }

}
