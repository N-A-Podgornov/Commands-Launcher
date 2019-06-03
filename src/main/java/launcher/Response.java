package launcher;

import launcher.executions.StatusContainer;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class Response<Data> {

    private Data data;
    private String error;

    Response(final Callable<Data> callable) {
        try {
            data = callable.call();
        } catch (Exception e) {
            error = e.getMessage();
        }
    }

    @Override
    public String toString() {
        final Map json = new JSONObject();
        if (data != null) {
            json.put("data", encodeJson(data));
        }
        if (error != null) {
            json.put("error", error);
        }
        return ((JSONObject) json).toJSONString();
    }

    private Object encodeJson(Object o) {
        if (o instanceof Map) {
            final Map result = new HashMap();
            for (Object key : ((Map) o).keySet()) {
                result.put(key.toString(), encodeJson(((Map) o).get(key)));
            }
            return result;
        }
        if (o instanceof List) {
            final List result = new ArrayList();
            for (Object e : ((List) o)) {
                result.add(encodeJson(e));
            }
            return result;
        }
        if (o instanceof StatusContainer) {
            final StatusContainer status = (StatusContainer) o;
            final Map json = new HashMap();
            json.put("id", status.getId());
            json.put("command", status.getCommand());
            json.put("stage", status.getStage());
            json.put("exitCode", status.getExitCode());
            json.put("started", status.getStartTime());
            json.put("finished", status.getFinishTime());
            return json;
        }
        return o;
    }

}