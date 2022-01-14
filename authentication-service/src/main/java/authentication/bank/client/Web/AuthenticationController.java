package authentication.bank.client.Web;

import authentication.bank.client.Entities.RefreshToken;
import authentication.bank.client.Entities.User;
import authentication.bank.client.Exceptions.TokenNotFoundException;
import authentication.bank.client.Exceptions.UserNotFoundException;
import authentication.bank.client.Helpers.MessageConsumer;
import authentication.bank.client.Helpers.SecurityHelper;
import authentication.bank.client.DAO.IRefreshTokenDAO;
import authentication.bank.client.DAO.IUserDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

// AuthenticationController is responsible for authentication operations
@Path("/users")
public class AuthenticationController {

    // Dependency injection, to avoid tight coupling (Couplage fort)
    @Inject
    private IUserDAO userDAO;

    // Dependency injection, to avoid tight coupling (Couplage fort)
    @Inject
    private IRefreshTokenDAO tokenDAO;

    // Key pair used for jwt generation (Should be saved somewhere safe, stored in memory for the sake of simplicity)
    private final KeyPair rsaJsonWebKey = SecurityHelper.generateKeyPair("RSA");


    public AuthenticationController() throws NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            //all things objectmapper don't seem to be working atm
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("../../../../../../../../Security/RSAKeyPair.json");
            mapper.writeValue(file, rsaJsonWebKey);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MessageConsumer consumer = new MessageConsumer();
        consumer.asyncSyncronizeUser(userDAO);

    }

    // Authentication endpoint
    @POST
    @PermitAll
    @Path("authenticate")
    @Consumes("application/json")
    @Produces("application/json")
    public Response authenticateUser(User credential) throws
            JoseException,
            NoSuchAlgorithmException,
            InvalidKeySpecException
    {
        User user;
        try{
            // Finding user in question from database
            user = userDAO.findByNoCompte(credential.getNoCompte());

            // Check if password given match password (after unhashing) from database
            if (!user.getPassword().equals(Base64.getEncoder().encodeToString(SecurityHelper.generateHash(
                    credential.getPassword(),
                    Base64.getDecoder().decode(user.getSalt())))))
            {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            // Check if the user already have a token
            RefreshToken tokenFound = tokenDAO.findById(user.getId());

            // Generation a jwt pair (access token, refresh token)
            RefreshToken refreshToken = new RefreshToken(SecurityHelper.generateRefreshToken());
            String jwt = SecurityHelper.generateJwt(rsaJsonWebKey, user.getId());

            // Persisting the new refresh token in database
            if (tokenFound != null) {
                tokenDAO.update(refreshToken);
            } else {
                tokenDAO.save(refreshToken);
            }

            // Saving jwt pair in cookies, to be sent to the user
            NewCookie atCookie = new NewCookie("access_token", jwt, null, null, null, 1000, false, true);
            NewCookie rtCookie = new NewCookie("refresh_token", refreshToken.getToken(), null, null, null, 1000, false, true);

            return Response.ok().cookie(atCookie, rtCookie).build();
        }
        catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Refresh access token in case the token expires using the refresh token
    @POST
    @Path("refresh")
    @PermitAll
    @Produces("application/json")
    @Consumes("application/json")
    public Response refreshToken(@CookieParam("access_token") Cookie ratCookie, @CookieParam("refresh_token") Cookie rrtCookie) throws
            UserNotFoundException,
            JoseException
    {
        try {
            // Check if access token is invalid
            if (ratCookie != null) SecurityHelper.processJwt(rsaJsonWebKey, ratCookie.getValue());
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        catch (InvalidJwtException e) {
            // In case access token is invalid because of expiration
            if (e.hasExpired()) {
                // Find user in question
                Long userId = (Long) e.getJwtContext().getJwtClaims().getClaimValue("id");
                User user = userDAO.findById(userId.intValue());
                RefreshToken refreshToken = tokenDAO.findById(user.getId());

                // Check if refresh token given matches the refresh token in the database and generate a new jwt pair
                if (refreshToken.getToken().equals(rrtCookie.getValue())) {
                    RefreshToken newRefreshToken = new RefreshToken(SecurityHelper.generateRefreshToken(), user.getId());
                    String newJwt = SecurityHelper.generateJwt(rsaJsonWebKey, user.getId());
                    tokenDAO.update(newRefreshToken);

                    NewCookie atCookie = new NewCookie("access_token", newJwt, null, null, null, 1000, false, true);
                    NewCookie rtCookie = new NewCookie("refresh_token", newRefreshToken.getToken(), null, null, null, 1000, false, true);

                    return Response.ok().cookie(atCookie, rtCookie).build();
                }
            }
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    // listing users endpoint
    @GET
    @PermitAll
    @Path("list-all")
    @Produces("application/json")
    public Response listAllUsers(@CookieParam("access_token") Cookie cookie) {
        try {
            if(cookie == null) throw new TokenNotFoundException("Not logged in");
            // Checking if the jwt in the cookie is valid (ie. the user is authenticated)
            SecurityHelper.processJwt(rsaJsonWebKey, cookie.getValue());
            return Response.ok(userDAO.listAll()).build();
        } catch (InvalidJwtException e) {
            if (e.hasExpired()) {
                return Response.status(401, "Token Expired").build();
            }
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (TokenNotFoundException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    // Get specific users endpoint
    @GET
    @PermitAll
    @Path("get/{id}")
    @Produces("application/json")
    public Response getUser(@PathParam(value = "id") int id, @CookieParam("access_token") Cookie cookie) throws
            UserNotFoundException
    {
        try {
            // Checking if the jwt in the cookie is valid (ie. the user is authenticated)
            SecurityHelper.processJwt(rsaJsonWebKey, cookie.getValue());
            return Response.ok(userDAO.findById(id)).build();
        } catch (InvalidJwtException e) {
            if (e.hasExpired()) {
                return Response.status(401, "Token Expired").build();
            }
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    // updating user endpoint
    @POST
    @PermitAll
    @Path("update")
    @Consumes("application/json")
    public Response updateUser(User user, @CookieParam("access_token") Cookie cookie) {
        try {
            // Checking if the jwt in the cookie is valid (ie. the user is authenticated)
            SecurityHelper.processJwt(rsaJsonWebKey, cookie.getValue());
            userDAO.update(user);
            return Response.ok().build();
        } catch (InvalidJwtException e) {
            if (e.hasExpired()) {
                return Response.status(401, "Token Expired").build();
            }
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

    }

    // Deleting user endpoint
    @DELETE
    @PermitAll
    @Path("delete")
    @Consumes("application/json")
    public Response deleteUser(final User user, @CookieParam("access_token") Cookie cookie) {
        try {
            // Checking if the jwt in the cookie is valid (ie. the user is authenticated)
            SecurityHelper.processJwt(rsaJsonWebKey, cookie.getValue());
            userDAO.delete(user.getId());
            return Response.ok().build();
        } catch (InvalidJwtException e) {
            if (e.hasExpired()) {
                return Response.status(401, "Token Expired").build();
            }
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @GET
    @PermitAll
    @Path("test")
    @Consumes("application/json")
    @Produces("application/json")
    public Response test(@CookieParam("access_token") Cookie cookie) {
        try {
            // Checking if the jwt in the cookie is valid (ie. the user is authenticated)
            SecurityHelper.processJwt(rsaJsonWebKey, cookie.getValue());
            return Response.ok(userDAO.listAll()).build();
        } catch (InvalidJwtException e) {
            if (e.hasExpired()) {
                return Response.status(401, "Token Expired").build();
            }
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    //Temporary until the two services are synchronized when a new user is being created
    @POST
    @PermitAll
    @Path("tempCreate")
    @Consumes("application/json")
    @Produces("application/json")
    public Response createUser(User user) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException
    {
        try{
            // Check if the user already exist
            userDAO.findByNoCompte(user.getNoCompte());
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
            return Response.ok().build();
        }
    }
}
