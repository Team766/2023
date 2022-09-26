package com.team766.framework;

/**
 * This exception is thrown in the code running in a Context to indicate that
 * the Context has been terminated and that the code should immediately exit
 * (after doing any necessary cleanup).
 */
public class ContextStoppedException extends Error {
	private static final long serialVersionUID = 370773292108890929L;
}