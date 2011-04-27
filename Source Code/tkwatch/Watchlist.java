/*
 * Created Apr 12, 2011
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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Prototype for encapsulating a TradeKing watchlist. Should eventually provide
 * functionality to convert among <code>Watchlist</code> object,
 * <code>DOM</code> document, and <code>XML</code> representation. Right now its
 * main function is to generate a watchlist update request body.
 * <p>
 * Contest version.
 */
public class Watchlist
{
	/*
	 * The name of the watchlist in the TradeKing account. This is currently
	 * limited to DEFAULT.
	 */
	private static final String name = "DEFAULT";

	/**
	 * Returns the name of the watchlist.
	 * 
	 * @return the name
	 */
	public static final String getName()
	{
		return name;
	}

	/*
	 * The array list of watchlist items.
	 */
	private ArrayList<WatchlistItem> item = null;

	/**
	 * Default constructor.
	 */
	public Watchlist()
	{
		super();
		setItem(WatchlistItem.getAll());
	}

	/**
	 * Constructor with argument.
	 * 
	 * @param newItem
	 */
	public Watchlist(ArrayList<WatchlistItem> newItem)
	{
		super();
		this.item = newItem;
	}

	/**
	 * Returns the array list of watchlist items.
	 * 
	 * @return the item
	 */
	public final ArrayList<WatchlistItem> getItem()
	{
		return item;
	}

	/**
	 * Returns a string to serve as the body for a TK API watchlist update
	 * request. This is the request that synchronizes a watchlist stored in the
	 * local database with the DEFAULT watchlist in the TK account.
	 */
	public String getUpdateRequestBody()
	{
		// TODO Eventually convert this to XML handling.
		StringBuffer result = new StringBuffer("<request><watchlist action=\"update\"><id>DEFAULT</id>");
		Iterator<WatchlistItem> iterator = getItem().iterator();
		while (iterator.hasNext())
		{
			WatchlistItem currentItem = iterator.next();
			result.append("<watchlistItem><costBasis>");
			result.append(currentItem.getCostBasis());
			result.append("</costBasis><qty>");
			result.append(currentItem.getQuantity());
			result.append("</qty><instrument><sym>");
			result.append(currentItem.getInstrument());
			result.append("</sym></instrument></watchlistItem>");
		}
		result.append("</watchlist></request>");
		return result.toString();
	}

	/**
	 * Sets the array list of watchlist items.
	 * 
	 * @param newItem
	 *            The new array list of items.
	 */
	public final void setItem(ArrayList<WatchlistItem> newItem)
	{
		this.item = newItem;
	}
}
