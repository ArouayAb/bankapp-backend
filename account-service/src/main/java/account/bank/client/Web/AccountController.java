package account.bank.client.Web;

import account.bank.client.Entities.Account;
import account.bank.client.DAO.IAccountDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

// This Controller is responsible for account operations
@Path("/accounts")
public class AccountController {

    @Inject
    private IAccountDAO accountDAO;

    public AccountController(){

    }

    @POST
    @Path("create")
    @Consumes("application/json")
    @Produces("application/json")
    public Response createAccount(Account account) {
        accountDAO.save(account);
        return Response.ok().build();
    }
}
