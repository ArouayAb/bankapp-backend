package account.bank.client.Web;

import account.bank.client.DAO.IUserDAO;
import account.bank.client.DAO.UserDAO;
import account.bank.client.Entities.Account;
import account.bank.client.DAO.IAccountDAO;
import account.bank.client.Entities.User;
import account.bank.client.Exceptions.UserNotFoundException;
import account.bank.client.Helpers.SecurityHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.ErrorCodeValidator;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtContext;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

// This Controller is responsible for account operations
@Path("/accounts")
public class AccountController {

    @Inject
    private IAccountDAO accountDAO;

    @Inject
    private IUserDAO userDAO;

    public AccountController(){

    }

    @GET
    @Path("get-infos")
    @Produces("application/json")
    public Response getInfos(@CookieParam("access_token") Cookie cookie) {
        try {
            if (cookie == null) throw new InvalidJwtException("Not provided", new ArrayList<ErrorCodeValidator.Error>(), new JwtContext(null, null));

            java.nio.file.Path pathAbsolute = Paths.get("/home/asus_/Documents/Project/Java/bankapp-backend/Security/RSAKeyPair.bin");

            FileInputStream fileInputStream = new FileInputStream(pathAbsolute.toString());
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            KeyPair rsaJsonWebKey = (KeyPair) objectInputStream.readObject();

            JwtClaims jwtClaims = SecurityHelper.processJwt(rsaJsonWebKey, cookie.getValue());
            Long id = (Long) jwtClaims.getClaimValue("id");

            List<Account> accounts = accountDAO.findByUserId(id.intValue());
            class Infos implements Serializable {
                String fullName;
                String phone;
                String cin;
                String birthDate;
                String email;
                String noCompte;
                String rib;
                double solde;

                public Infos(Account account) {
                    this.fullName = account.getUser().getFullName();
                    this.phone = account.getUser().getPhone();
                    this.cin = account.getUser().getCin();
                    this.birthDate = account.getUser().getBirthDate();
                    this.email = account.getUser().getEmail();
                    this.noCompte = account.getAccountNumber().toString();
                    this.rib = account.getRib();
                    this.solde = account.getBalance();
                }

                public String getFullName() {
                    return fullName;
                }

                public void setFullName(String fullName) {
                    this.fullName = fullName;
                }

                public String getPhone() {
                    return phone;
                }

                public void setPhone(String phone) {
                    this.phone = phone;
                }

                public String getCin() {
                    return cin;
                }

                public void setCin(String cin) {
                    this.cin = cin;
                }

                public String getBirthDate() {
                    return birthDate;
                }

                public void setBirthDate(String birthDate) {
                    this.birthDate = birthDate;
                }

                public String getEmail() {
                    return email;
                }

                public void setEmail(String email) {
                    this.email = email;
                }

                public String getNoCompte() {
                    return noCompte;
                }

                public void setNoCompte(String noCompte) {
                    this.noCompte = noCompte;
                }

                public String getRib() {
                    return rib;
                }

                public void setRib(String rib) {
                    this.rib = rib;
                }

                public double getSolde() {
                    return solde;
                }

                public void setSolde(double solde) {
                    this.solde = solde;
                }
            }
            Infos infos = new Infos(accounts.get(0));
            return Response.ok(infos).build();
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return Response.serverError().build();
        } catch (InvalidJwtException e) {
            if (e.hasExpired())
            return Response.status(420,"Token Expired").build();
            else return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}
