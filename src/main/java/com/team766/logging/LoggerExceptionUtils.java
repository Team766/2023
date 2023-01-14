package com.team766.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LoggerExceptionUtils {
	public static String exceptionToString(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.print("Uncaught exception: ");
		e.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}

	public static String logException(Throwable e) {
		String str = exceptionToString(e);
		try {
			Logger.get(Category.JAVA_EXCEPTION).logRaw(Severity.ERROR, str);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return str;
	}
}