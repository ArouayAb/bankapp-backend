package gateway.bank.api_gateway.Routes;

import java.util.HashMap;
import java.util.Map;

public class Routes {
    public Map<String, String> authenticationService;
    public Map<String, String> accountService;

    public Routes() {
        this.authenticationService = new HashMap<>();
        this.accountService = new HashMap<>();
    }

}
