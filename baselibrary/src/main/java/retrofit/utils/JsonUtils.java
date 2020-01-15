package retrofit.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by aojiaoqiang on 2018/1/30.
 */

public class JsonUtils {

    public static  <T> T fromJson(String json, Class<T> cls) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, cls);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toJson(Object obj) {
        try {
            Gson gson = new Gson();
            return gson.toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
