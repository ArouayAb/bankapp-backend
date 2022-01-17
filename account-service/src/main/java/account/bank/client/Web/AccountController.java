package account.bank.client.Web;

import account.bank.client.DAO.ITransactionDAO;
import account.bank.client.Entities.Account;
import account.bank.client.DAO.IAccountDAO;
import account.bank.client.Entities.Transaction;
import account.bank.client.Helpers.SecurityHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtContext;

import javax.annotation.security.PermitAll;
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
    private ITransactionDAO transactionDAO;

    public AccountController(){

    }

    @GET
    @Path("get-infos")
    @Produces("application/json")
    public Response getInfos(@CookieParam("access_token") Cookie cookie) {
        try {
            if (cookie == null) throw new InvalidJwtException("Not provided", new ArrayList<>(), new JwtContext(null, null));

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

    @GET
    @PermitAll
    @Path("contact")
    @Produces("application/json")
    public Response contact() {

        class Contact implements Serializable {
            String email;
            String phone;

            public Contact() {
                this.email = "support@gmail.com";
                this.phone = "0610203040";
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }
        }

        Contact contact = new Contact();
        return Response.ok(contact).build();
    }

    @POST
    @Path("virement")
    @Consumes("application/json")
    public Response verser(String json, @CookieParam("access_token") Cookie cookie) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode object = objectMapper.readTree(json);

            Long accountnumber = Long.parseLong(object.get("noCompte").textValue());

            if (cookie == null)
                throw new InvalidJwtException("Not provided", new ArrayList<>(), new JwtContext(null, null));

            java.nio.file.Path pathAbsolute = Paths.get("/home/asus_/Documents/Project/Java/bankapp-backend/Security/RSAKeyPair.bin");

            FileInputStream fileInputStream = new FileInputStream(pathAbsolute.toString());
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            KeyPair rsaJsonWebKey = (KeyPair) objectInputStream.readObject();

            JwtClaims jwtClaims = SecurityHelper.processJwt(rsaJsonWebKey, cookie.getValue());
            Long id = (Long) jwtClaims.getClaimValue("id");

            List<Account> accounts = accountDAO.findByUserId(id.intValue());

            Account sender = accounts.get(0);
            Account receiver = accountDAO.findById(accountnumber);
            double montant = Double.parseDouble(object.get("montant").textValue());
            if ((sender.getBalance() >= montant) && (montant <= sender.getBalance() + sender.getMaxOverDraft()) &&
                    (montant <= receiver.getMaxOverDraft())) {

                sender.setBalance(sender.getBalance() - montant);
                receiver.setBalance(receiver.getBalance() + montant);

                Transaction transaction = new Transaction("virement", sender, receiver, montant);
                transactionDAO.save(transaction);
                accountDAO.update(sender);
                accountDAO.update(receiver);
                return Response.ok().build();
            }
            else {
                return Response.status(Response.Status.EXPECTATION_FAILED).build();
            }
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return Response.serverError().build();
        } catch (InvalidJwtException e) {
            if (e.hasExpired())
                return Response.status(420,"Token Expired").build();
            else return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @GET
    @Path("list-transactions")
    @Produces("application/json")
    public Response transactions(@CookieParam("access_token") Cookie cookie) {
        try {
            if (cookie == null)
                throw new InvalidJwtException("Not provided", new ArrayList<>(), new JwtContext(null, null));

            java.nio.file.Path pathAbsolute = Paths.get("/home/asus_/Documents/Project/Java/bankapp-backend/Security/RSAKeyPair.bin");

            FileInputStream fileInputStream = new FileInputStream(pathAbsolute.toString());
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            KeyPair rsaJsonWebKey = (KeyPair) objectInputStream.readObject();

            JwtClaims jwtClaims = SecurityHelper.processJwt(rsaJsonWebKey, cookie.getValue());
            Long id = (Long) jwtClaims.getClaimValue("id");

            List<Account> accounts = accountDAO.findByUserId(id.intValue());

            Account account = accounts.get(0);
            List<Transaction> transactions = transactionDAO.findByAccount(account);
            class TransInfos implements Serializable {
                String type;
                double montant;
                String donneur;
                String beneficiaire;

                public TransInfos() {
                }

                public TransInfos(String type, double montant, String donneur, String beneficiaire) {
                    this.type = type;
                    this.montant = montant;
                    this.donneur = donneur;
                    this.beneficiaire = beneficiaire;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public double getMontant() {
                    return montant;
                }

                public void setMontant(double montant) {
                    this.montant = montant;
                }

                public String getDonneur() {
                    return donneur;
                }

                public void setDonneur(String donneur) {
                    this.donneur = donneur;
                }

                public String getBeneficiaire() {
                    return beneficiaire;
                }

                public void setBeneficiaire(String beneficiaire) {
                    this.beneficiaire = beneficiaire;
                }
            }
            List<TransInfos> infos = new ArrayList<>();
            for (Transaction t: transactions) {
                infos.add(new TransInfos(t.getType(), t.getAmmount(), t.getSender().getHolderName(),t.getReceiver().getHolderName()));
            }
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
