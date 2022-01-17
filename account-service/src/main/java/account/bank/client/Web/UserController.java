package account.bank.client.Web;

import account.bank.client.DAO.IAccountDAO;
import account.bank.client.DAO.IUserDAO;
import account.bank.client.Entities.Account;
import account.bank.client.Entities.User;
import account.bank.client.Exceptions.UserNotFoundException;
import account.bank.client.Helpers.MessageProducer;
import account.bank.client.Helpers.SecurityHelper;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.ErrorCodeValidator;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtContext;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
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

    @PUT
    @PermitAll
    @Path("edit-infos")
    @Consumes("application/json")
    @Produces("application/json")
    public Response editInfos (User user, @CookieParam("access_token") Cookie cookie) {
        try {
            if (cookie == null) throw new InvalidJwtException("Not provided", new ArrayList<ErrorCodeValidator.Error>(), new JwtContext(null, null));

            java.nio.file.Path pathAbsolute = Paths.get("E:/Backup/Documents/CI2/Other/bankapp-backend/Security/RSAKeyPair.bin");

            FileInputStream fileInputStream = new FileInputStream(pathAbsolute.toString());
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            KeyPair rsaJsonWebKey = (KeyPair) objectInputStream.readObject();

            JwtClaims jwtClaims = SecurityHelper.processJwt(rsaJsonWebKey, cookie.getValue());
            Long id = (Long) jwtClaims.getClaimValue("id");

            User u = userDAO.findById(id.intValue());

            u.setPhone(user.getPhone());
            u.setEmail(user.getEmail());
            byte[] salt = SecurityHelper.generateSalt();
            u.setSalt(Base64.getEncoder().encodeToString(salt));
            u.setPassword(
                    Base64.getEncoder().encodeToString(SecurityHelper.generateHash(user.getPassword(),
                            Base64.getDecoder().decode(u.getSalt())))
            );

            userDAO.save(u);

            return Response.ok().build();

        } catch(UserNotFoundException e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }  catch (InvalidJwtException e) {
            if (e.hasExpired())
                return Response.status(420,"Token Expired").build();
            else return Response.status(Response.Status.UNAUTHORIZED).build();
        }

    }

}
