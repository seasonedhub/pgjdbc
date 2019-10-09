package org.postgresql.core.brushfire;

import org.postgresql.core.SqlInjectionException;

import java.lang.reflect.Method;

public class BrushfireUtils {

    public static boolean COMMENTS_ENABLED = false;
    public static Method GET_CURRENT_REQUEST_METHOD;

    private static final String CURRENT_REQUEST_DEFAULT_CLASS_PATH = "com.rbc.brushfire.framework.CurrentRequest";
    private static final CharSequence[] EVIL_CHARS = {"/", "*"};

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
        try {
            if (GET_CURRENT_REQUEST_METHOD != null) {
                Object requestObject = GET_CURRENT_REQUEST_METHOD.invoke(null);
                if (requestObject != null) {
                    for (Class clazz : requestObject.getClass().getInterfaces()) {
                        if (clazz.getName().equals(CurrentRequestHolder.class.getName())) {
                            return String.valueOf(requestObject.getClass().getMethod("getPostgresPrefix").invoke(requestObject));
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void validateComment(String sqlComment) {
        for (CharSequence c : EVIL_CHARS) {
            if (sqlComment.contains(c)) {
                throw new SqlInjectionException("Threat comment is detected! Somebody tried to make sql injection!");
            }
        }
    }

}
