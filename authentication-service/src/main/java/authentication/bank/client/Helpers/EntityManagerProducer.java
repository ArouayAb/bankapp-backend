package authentication.bank.client.Helpers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@ApplicationScoped
public class EntityManagerProducer {

    private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("PU_AUTH");

    @Produces
    public EntityManager getEntityManager(){
        return entityManagerFactory.createEntityManager();
    }

}
