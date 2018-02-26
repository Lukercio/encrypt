package com.adyen.encrypter.exception;

/**
 * Created by andrei on 8/8/16. -  For Android
 * Converted to Java by ldlopes on 02/23/2018
 *
 * It's not recommended to encrypt data in backend
 */

public class EncrypterException extends Exception {

    private static final long serialVersionUID = 2699577096011945291L;

    /**
     * Wrapping exception for all JCE encryption related exceptions
     * @param message
     * @param cause
     */
    public EncrypterException(String message, Throwable cause) {
        super(message, cause);
    }

}
