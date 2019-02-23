package com.exloki.foruxi.core.exceptions;

import com.exloki.foruxi.core.utils.Txt;
import com.exloki.foruxi.Msg;

public class LException extends Exception {
    /**
     * Mainly for tracking custom exceptions and catching generic instances
     */

    private static final long serialVersionUID = 5047924343912177877L;

    public LException(String message) {
        super(message.length() > 1 ? (message.charAt(0) == Txt.COLOUR_CODE_CHAR ? message : (message.charAt(0) == '&' ? message : Msg.ER_ERROR.withVars(message))) : "");
    }

    public LException(String message, Throwable throwable) {
        super(message.length() > 1 ? (message.charAt(0) == Txt.COLOUR_CODE_CHAR ? message : (message.charAt(0) == '&' ? message : Msg.ER_ERROR.withVars(message))) : "", throwable);
    }
}
