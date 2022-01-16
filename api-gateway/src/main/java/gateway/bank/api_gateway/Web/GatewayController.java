package gateway.bank.api_gateway.Web;

import gateway.bank.api_gateway.Routes.Routes;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.net.ConnectException;

@Path("api")
public class GatewayController {

    Routes routes;

    public GatewayController() {
        routes = new Routes();

        this.routes.accountService.put("target", "http://localhost:8079/account_service/");
        this.routes.accountService.put("user path", "users");
        this.routes.accountService.put("creation path", "create");
        this.routes.accountService.put("account path", "accounts");
        this.routes.accountService.put("get-infos path", "get-infos");

        this.routes.authenticationService.put("target", "http://localhost:8081/authentication_service/");
        this.routes.authenticationService.put("user path", "users");
        this.routes.authenticationService.put("authentication path", "authenticate");
        this.routes.authenticationService.put("refresh path", "refresh");

    }

    @POST
    @Path("signup")
    @Consumes("application/json")
    public Response createUser(String payload) {

        Response response;
        try{
            Client client = ClientBuilder.newClient();
            WebTarget webTarget = client.target(this.routes.accountService.get("target"))
                    .path(this.routes.accountService.get("user path"))
                    .path(this.routes.accountService.get("creation path"));
            Invocation.Builder invocationBuilder = webTarget.request("application/json");
            response = invocationBuilder.post(Entity.entity(payload, "application/json"));

            return response;
        } catch (ProcessingException e) {
           //e.printStackTrace();
           if (e.getCause() instanceof ConnectException) {
               return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
           } else {
               return Response.serverError().build();
           }
        }
    }

    @POST
    @Path("login")
    @Consumes("application/json")
    public Response authenticate(String payload) {
        Response response;
        try{
            Client client = ClientBuilder.newClient();
            WebTarget webTarget = client.target(this.routes.authenticationService.get("target"))
                    .path(this.routes.authenticationService.get("user path"))
                    .path(this.routes.authenticationService.get("authentication path"));
            Invocation.Builder invocationBuilder = webTarget.request("application/json");
            response = invocationBuilder.post(Entity.entity(payload, "application/json"));

            return response;
        } catch (ProcessingException e) {
            //e.printStackTrace();
            if (e.getCause() instanceof ConnectException) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            } else {
                return Response.serverError().build();
            }
        }
    }

    @GET
    @Path("refresh-login")
    @Consumes("application/json")
    public Response refreshLogin(@CookieParam("access_token") Cookie ratCookie, @CookieParam("refresh_token") Cookie rrtCookie) {
        Response response;
        try{
            Client client = ClientBuilder.newClient();
            WebTarget webTarget = client.target(this.routes.authenticationService.get("target"))
                    .path(this.routes.authenticationService.get("user path"))
                    .path(this.routes.authenticationService.get("refresh path"));
            Invocation.Builder invocationBuilder = webTarget.request("application/json");
            response = invocationBuilder.cookie(ratCookie).cookie(rrtCookie).get();

            return response;
        } catch (ProcessingException e) {
            //e.printStackTrace();
            if (e.getCause() instanceof ConnectException) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            } else {
                return Response.serverError().build();
            }
        }
    }

    @GET
    @Path("get-infos")
    @Produces("application/json")
    public Response getInfos(@CookieParam("access_token") Cookie cookie) {
        Response response;
        try{
            Client client = ClientBuilder.newClient();
            WebTarget webTarget = client.target(this.routes.accountService.get("target"))
                    .path(this.routes.accountService.get("account path"))
                    .path(this.routes.accountService.get("get-infos path"));
            Invocation.Builder invocationBuilder = webTarget.request("application/json");
            response = invocationBuilder.cookie(cookie).get();

            return response;
        } catch (ProcessingException e) {
            //e.printStackTrace();
            if (e.getCause() instanceof ConnectException) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            } else {
                return Response.serverError().build();
            }
        }
    }
}
