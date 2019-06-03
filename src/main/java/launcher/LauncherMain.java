package launcher;

import launcher.execution.Execution;
import launcher.executions.CommandsExecutions;
import launcher.executions.StatusContainer;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static spark.Spark.*;

public class LauncherMain {

    public static void main(String[] args) {

        new CommandLineExtractor(args).setUpPort();


        CommandsExecutions executions = new CommandsExecutions();


        post("/execute", Middleware.withAuthorization((request, response) -> new Response<>(() -> {
                    final Map json = new JSONObject();
                    json.put("id", executions.start(request.queryParams("command")));
                    return json;
                }))
        );

        get("/executions", Middleware.withAuthorization((request, response) -> new Response<>(() -> {
                    Map<Execution.Stage, List<StatusContainer>> statuses = executions.getStatuses();
                    final Map json = new JSONObject();
                    json.put("executions", statuses);
                    return json;
                }))
        );

        get("/execution/:id", Middleware.withAuthorization((request, response) -> new Response<>(() -> {
                    UUID id = UUID.fromString(request.params(":id"));
                    return executions.getStatus(id);
                }))
        );


        get("/execution/:id/output", Middleware.withAuthorization((request, response) -> new Response<>(() -> {
                    UUID id = UUID.fromString(request.params(":id"));
                    int length = Integer.parseInt(request.queryParams("length"));

                    final Map json = new JSONObject();
                    json.put("id", id);
                    json.put("output", executions.getOutput(id, length));
                    return json;
                }))
        );

        get("/execution/:id/error", Middleware.withAuthorization((request, response) -> new Response<>(() -> {
                    UUID id = UUID.fromString(request.params(":id"));
                    int length = Integer.parseInt(request.queryParams("length"));

                    final Map json = new JSONObject();
                    json.put("id", id);
                    json.put("error", executions.getErrors(id, length));
                    return json;
                }))
        );


        delete("/execution/:id", Middleware.withAuthorization((request, response) -> new Response<>(() -> {
                    UUID id = UUID.fromString(request.params(":id"));

                    final Map json = new JSONObject();
                    if (!executions.deleteFinished(id)) {
                        executions.stop(id);
                        json.put("Info", "Execution was interrupted");
                        return json;
                    }
                    json.put("Info", "Info about finished execution was deleted");
                    return json;
                }))
        );

    }

}