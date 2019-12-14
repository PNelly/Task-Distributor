package com.itt.tds.logging;

import java.util.*;

public interface Logger {

    public void logWarn(String className, String methodName, String message);

    public void logWarn(String className, String methodName, String message, Exception ex);

    public void logError(String className, String methodName, String message);

    public void logError(String className, String methodName, String message, Exception ex);

    public void logInfo(String className, String methodName, String message);

    public void logInfo(String className, String methodName, String message, Exception ex);

    public void logDebug(String className, String methodName, String message);

    public void logDebug(String className, String methodName, String message, Exception ex);

}