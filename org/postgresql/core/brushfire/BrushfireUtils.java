package org.postgresql.core.brushfire;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BrushfireUtils {

    public static boolean COMMENTS_ENABLED = false;
    public static Method GET_CURRENT_REQUEST_METHOD;

    private static final String CURRENT_REQUEST_DEFAULT_CLASS_PATH = "com.rbc.brushfire.framework.CurrentRequest";

    static {
        String commentsEnabled = System.getProperty("COMMENTS_ENABLED");
        if (commentsEnabled != null) {
            COMMENTS_ENABLED = Boolean.valueOf(commentsEnabled);
        }
        String envPath = System.getProperty("CLASS_CURRENT_REQUEST");
        String classPath =  envPath != null ? envPath : CURRENT_REQUEST_DEFAULT_CLASS_PATH;

        Class<CurrentRequestHolder> clazz = null;
        try {
            clazz = (Class<CurrentRequestHolder>) Thread.currentThread().getContextClassLoader()
                    .loadClass(classPath);
            GET_CURRENT_REQUEST_METHOD = clazz.getMethod("get");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static String getPostgresPrefix() {
        Object requestObject = null;
        CurrentRequestHolder holder = null;
        if (GET_CURRENT_REQUEST_METHOD != null) {
            try {
                requestObject = GET_CURRENT_REQUEST_METHOD.invoke(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if (requestObject != null && requestObject.getClass().isAssignableFrom(CurrentRequestHolder.class)) {
                return ((CurrentRequestHolder) requestObject).getPostgresPrefix();
            }
        }
        return "";
    }
}
