/*
 * Created Apr 21, 2011
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Parent for all panels that interact with the TradeKing API. Establishes all
 * the necessary credentials for interaction from the file
 * <code>tradeking.properties</code> via the <code>static</code> section.
 * <p>
 * Contest version.
 */
public abstract class TradekingPanel extends JPanel
{

	/**
	 * TradeKing properties are set from the file
	 * <code>database.properties</code>.
	 */
	protected static Properties properties = new Properties();

	/**
	 * The TradeKing application key.
	 */
	protected static String appKey = new String();

	/**
	 * The TradeKing URL for making API calls.
	 */
	protected static String url = properties.getProperty("TRADEKING_URL");

	/**
	 * The TradeKing user key.
	 */
	protected static String userKey = new String();

	/**
	 * The TradeKing user secret key.
	 */
	protected static String userSecret = new String();

	static
	{
		appKey = properties.getProperty("TRADEKING_APP_KEY");
		url = properties.getProperty("TRADEKING_URL");
		userKey = properties.getProperty("TRADEKING_USER_KEY");
		userSecret = properties.getProperty("TRADEKING_USER_SECRET");

		properties = new Properties();
		FileInputStream in;
		try
		{
			in = new FileInputStream("tradeking.properties");
			properties.load(in);
			in.close();
			appKey = properties.getProperty("TRADEKING_APP_KEY");
			url = properties.getProperty("TRADEKING_URL");
			userKey = properties.getProperty("TRADEKING_USER_KEY");
			userSecret = properties.getProperty("TRADEKING_USER_SECRET");
		}
		catch (FileNotFoundException e)
		{
			TradekingException.handleException(e);
		}
		catch (IOException e)
		{
			TradekingException.handleException(e);
		}
	}

	/**
	 * Synchronizes the local watch list with the TradeKing watch list after
	 * each modification. This is called by each modification button (Add,
	 * Update, and Delete).
	 */
	public static final void synchWatchlistWithTk()
	{
		final Watchlist tkDefault = new Watchlist();
		final String result = Utilities.tradeKingRequest(url + "/user/watchlists", tkDefault.getUpdateRequestBody(),
				appKey, userKey, userSecret);
		final Document document;
		try
		{
			document = Utilities.xmlStringToDom(result);
		}
		catch (Exception e1)
		{
			TradekingException.handleException(e1);
			return;
		}
		final NodeList type = document.getElementsByTagName("type");
		if (type.getLength() > 0)
		{
			for (int i = 0; i < type.getLength(); i++)
			{
				if (type.item(i).getTextContent().equals("Error"))
				{
					Utilities.errorMessage("Error synchronizing watchlist with TradeKing");
					return;
				}
			}
		}
		// Silently proceed if everything is OK.
	}

}
