package account.bank.client.Web;

import account.bank.client.DAO.IAccountDAO;
import account.bank.client.DAO.IUserDAO;
import account.bank.client.Entities.Account;
import account.bank.client.Entities.User;
import account.bank.client.Exceptions.UserNotFoundException;
import account.bank.client.Helpers.MessageProducer;
import account.bank.client.Helpers.SecurityHelper;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

// This controller is responsible for creating modifying and deleting users
@Path("users")
public class UserController {

    @Inject
    private IUserDAO userDAO;

    @Inject
    private IAccountDAO accountDAO;

    public UserController() {

    }

    // User creation endpoint
    @POST
    @PermitAll
    @Path("create")
    @Consumes("application/json")
    @Produces("application/json")
    public Response createUser(User user) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException, UserNotFoundException {
        try{
            // Check if the user already exist
            userDAO.findByEmail(user.getEmail());
            return Response.status(Response.Status.CONFLICT).build();
        }
        catch (UserNotFoundException e) {

            // Hash user password for storage in database
            byte[] salt = SecurityHelper.generateSalt();
            user.setSalt(Base64.getEncoder().encodeToString(salt));
            user.setPassword(
                    Base64.getEncoder().encodeToString(SecurityHelper.generateHash(user.getPassword(),
                            Base64.getDecoder().decode(user.getSalt())))
            );

            // Persist user
            userDAO.save(user);
            accountDAO.save(new Account(null,user,"00000000001",user.getFullName(),0,4000,4000));

            User fullUser = userDAO.findByEmail(user.getEmail());
            Account fullAccount = accountDAO.findByUserId(fullUser.getId()).get(0);

            MessageProducer producer = new MessageProducer();
            producer.publishUserAuthInfo(fullUser, fullAccount);


            return Response.ok().build();
        }
    }
}
