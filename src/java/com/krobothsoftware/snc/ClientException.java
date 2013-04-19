package com.krobothsoftware.snc;

/**
 * Signals an exception with client.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public class ClientException extends Exception {
	private static final long serialVersionUID = 6769353379782248202L;

	/**
	 * @param message
	 * @since SNC 1.0
	 */
	public ClientException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 * @since SNC 1.0
	 */
	public ClientException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @since SNC 1.0
	 */
	public ClientException(String message, Throwable cause) {
		super(message, cause);
	}

}
