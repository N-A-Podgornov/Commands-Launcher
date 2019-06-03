package launcher;

import org.json.simple.JSONObject;
import spark.Request;
import spark.Route;

import java.util.Map;
import java.util.Objects;

class Middleware {

    private static final String login = "admin";
    private static final String password = "admin";

    private Middleware() {
    }


    static Route withAuthorization(Route route) {
        return (request, response) -> {
            if (isAuthorized(request)) {
                return route.handle(request, response);
            } else {
                response.status(401);
                final Map json = new JSONObject();
                json.put("error", "unauthorized");
                return json;
            }
        };
    }

    private static boolean isAuthorized(Request request) {
        return Objects.equals(request.queryParams("login"), login) && Objects.equals(request.queryParams("password"), password);
    }

}