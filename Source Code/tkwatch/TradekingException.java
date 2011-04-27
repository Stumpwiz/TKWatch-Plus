/*
 * Created Mar 31, 2011
 * Copyright (c) 2011, Mike Radovich (maradovich@loyola.edu).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice, 
 *       this list of conditions and the following disclaimer.
 * 
 *     * Redistributions in binary form must reproduce the above copyright 
 *       notice, this list of conditions and the following disclaimer in the 
 *       documentation and/or other materials provided with the distribution.
 * 
 *     * Neither the name of George Wright nor the name of Loyola College
 *       may be used to endorse or promote products derived from this software 
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * To learn more about open source licenses, please visit: 
 * http://opensource.org/index.php
 */

package tkwatch;

/**
 * Standard, all-purpose exception for <code>tkwatch</code> package routines.
 * Displays a dialog box at the console.
 * <p>
 * Contest version.
 */
public class TradekingException extends Throwable
{
	/**
	 * Ultimate handler for all exceptions. Displays a dialog box.
	 * 
	 * @param theException
	 *            Exception
	 */
	static void handleException(final Throwable theException)
	{
		// Uncomment stack trace printer for development.
		// theException.printStackTrace();
		Utilities.errorMessage(theException.getMessage());
	}

	/**
	 * Default constructor.
	 */
	TradekingException()
	{
		super();
	}

	/**
	 * Constructor with single argument.
	 * 
	 * @param message
	 *            The dialog box message.
	 */
	TradekingException(final String message)
	{
		super(message);
	}

	/**
	 * Constructor with two arguments.
	 * 
	 * @param message
	 *            java.lang.String
	 * @param throwable
	 *            java.lang.Throwable
	 */
	TradekingException(final String message, final Throwable throwable)
	{
		super(message, throwable);
	}

	/**
	 * Indicates whether some other object is "equal to" this one. Expected to
	 * be used mainly for testing.
	 * 
	 * @param obj
	 *            Object
	 * @return whether or not the argument equals the caller
	 */
	@Override
	public final boolean equals(final Object obj)
	{
		if (obj == null)
			return false;
		if (obj.getClass() != this.getClass())
			return false;
		final TradekingException newNetworkException = (TradekingException) obj;
		if (!this.getMessage().equals(newNetworkException.getMessage()))
			return false;
		if (this.getCause() != null && newNetworkException.getCause() != null)
		{
			if (!this.getCause().equals(newNetworkException.getCause()))
				return false;
		}
		if (this.getCause() == null && newNetworkException.getCause() == null)
			return true;
		return true;
	}
}
