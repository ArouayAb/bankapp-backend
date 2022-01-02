package account.bank.client.Web;

import account.bank.client.Entities.Account;
import account.bank.client.Resources.IAccountDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/accounts")
public class AccountController {

    @Inject
    private IAccountDAO accountResource;

    public AccountController(){

    }

    @GET
    @Path("hello")
    @Produces("text/plain")
    public String hello() {
        return "Hello, World!";
    }

    @POST
    @Path("create")
    @Consumes("application/json")
    @Produces("application/json")
    public Response createAccount(Account account) {
        accountResource.save(account);
        return Response.ok().build();
    }
}
