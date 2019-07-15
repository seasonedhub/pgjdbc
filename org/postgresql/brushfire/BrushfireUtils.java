package org.postgresql.brushfire;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BrushfireUtils {

    public static boolean COMMENTS_ENABLED = false;
    public static Method GET_CURRENT_REQUEST_METHOD;

    private static final String CURRENT_REQUEST_DEFAULT_CLASS_PATH = "com.rbc.brushfire.framework.CurrentRequest";
    private static final String AMAZON_TEXT_FORMAT = " /* X-Amzn-Trace-Id: %s */";
    private static final String THREAD_ID_TEXT_FORMAT = " /* Thread id: %s */";

    static {
        String commentsEnabled = System.getProperty("COMMENTS_ENABLED");
        if (commentsEnabled != null) {
            COMMENTS_ENABLED = Boolean.valueOf(commentsEnabled);
        }
        String envPath = System.getProperty("CLASS_CURRENT_REQUEST");
        String classPath =  envPath != null ? envPath : CURRENT_REQUEST_DEFAULT_CLASS_PATH;

        Class<CurrentRequestHolder> clazz = null;
        try {
            clazz = (Class<CurrentRequestHolder>) BrushfireUtils.class.getClassLoader()
                    .loadClass(classPath);
            GET_CURRENT_REQUEST_METHOD = clazz.getMethod("get");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static String getPostgresPrefix() {
        CurrentRequestHolder holder = null;
        try {
            holder = (CurrentRequestHolder) GET_CURRENT_REQUEST_METHOD.invoke(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return generateComment(holder);
    }

    private static String generateComment(CurrentRequestHolder holder) {
        if (holder != null && holder.getAmazonTraceId() != null) {
            return String.format(AMAZON_TEXT_FORMAT, holder.getAmazonTraceId());
        }
        return String.format(THREAD_ID_TEXT_FORMAT, Thread.currentThread().getId());
    }

}
