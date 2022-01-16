package account.bank.client.Filters;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

@Provider
public class AuthorizationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();

        if (!method.isAnnotationPresent(PermitAll.class)) {
            if (method.isAnnotationPresent(DenyAll.class)) {
                containerRequestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Access blocked for all users").build());
                return;
            }

            final MultivaluedMap<String, String> headers = containerRequestContext.getHeaders();
            final List<String> authorization = headers.get("Authorization");

            if (authorization == null || authorization.isEmpty()) {
                containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Resource inaccessible").build());
                return;
            }

            boolean isGateway = false;
            for (String header : authorization) {
                if (header.equals("Gateway")) {
                    isGateway = true;
                    break;
                }
            }

            if (!isGateway) {
                containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Resource is not directly accessible").build());
            }

        }
    }
}
