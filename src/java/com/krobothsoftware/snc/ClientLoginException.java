package com.krobothsoftware.snc;

/**
 * Login credentials incorrect or login process encountered a problem.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public class ClientLoginException extends ClientException {
	private static final long serialVersionUID = 5352380979938655622L;

	/**
	 * @param message
	 * @since SNC 1.0
	 */
	public ClientLoginException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 * @since SNC 1.0
	 */
	public ClientLoginException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @since SNC 1.0
	 */
	public ClientLoginException(String message, Throwable cause) {
		super(message, cause);
	}

}
