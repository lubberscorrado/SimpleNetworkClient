package com.krobothsoftware.snc;

/**
 * When a Token encounters an error like token expired.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public class TokenException extends Exception {
	private static final long serialVersionUID = 8266203313746429100L;

	/**
	 * Create new exception with text.
	 * 
	 * <pre>
	 * Token expired
	 * </pre>
	 * 
	 * @since SNC 1.0
	 */
	public TokenException() {
		super("Token expired");
	}

	/**
	 * @param message
	 * @since SNC 1.0
	 */
	public TokenException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 * @since SNC 1.0
	 */
	public TokenException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @since SNC 1.0
	 */
	public TokenException(String message, Throwable cause) {
		super(message, cause);
	}

}
