package com.team766.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {
	public static String exceptionToString(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}
}