package com.munchado.orderprocess.network.volley;

import java.io.Serializable;

public class APIException extends Exception implements Serializable {
    /** */
    private static final long serialVersionUID = 1L;
    /**
     * error code
     */
    private String mCode;

    private String mErrorType;

    /**
     * @param code
     * @param message
     */
    public APIException(String code, String message) {
        super(message);
        this.mCode = code;
    }

    /**
     * @param message
     */
    public APIException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public APIException(Throwable cause) {
        super("Caused by: " + cause.toString());
    }

    /**
     * @return the mErrorType
     */
    public String getmErrorType() {
        return mErrorType;
    }

    /**
     * @param mErrorType the mErrorType to set
     */
    public void setmErrorType(String mErrorType) {
        this.mErrorType = mErrorType;
    }

    /**
     * @return error code
     */
    public String getCode() {
        return mCode;
    }
}
