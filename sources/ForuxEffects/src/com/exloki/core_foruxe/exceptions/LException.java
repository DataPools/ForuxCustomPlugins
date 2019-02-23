package com.exloki.core_foruxe.exceptions;

import com.exloki.foruxe.TL;
import com.exloki.core_foruxe.utils.Txt;

public class LException extends Exception
{
	/**
	 * Mainly for tracking custom exceptions and catching generic instances
	 */
	
	private static final long serialVersionUID = 5047924343912177877L;

	public LException(String message)
	{
		super(message.length() > 1 ? (message.charAt(0) == Txt.COLOUR_CODE_CHAR ? message : (message.charAt(0) == '&' ? message : TL.ER_ERROR.withVars(message))) : "");
	}
	
	public LException(String message, Throwable throwable)
	{
		super(message.length() > 1 ? (message.charAt(0) == Txt.COLOUR_CODE_CHAR ? message : (message.charAt(0) == '&' ? message : TL.ER_ERROR.withVars(message))) : "", throwable);
	}
}
