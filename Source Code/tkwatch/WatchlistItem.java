/*
 * Created Apr 2, 2011
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Encapsulates a watchlist item. This object comprises the fields implied by
 * the <i>Beta TradeKing API Documentation</i>, 03.25.2011, p. 20, augmented by
 * a notation field. The notation field is available for the user to store
 * whatever he or she wants to say about why the item is being watched.
 * <p>
 * Needs to have XML handling functions added.
 * <p>
 * Contest version.
 */
public class WatchlistItem
{
	/**
	 * Deletes a watchlist item from the database by instrument symbol.
	 * 
	 * @param theInstrument
	 *            The market symbol of the instrument.
	 * @return Returns <code>true</code> if the watchlist item is deleted ,
	 *         <code>false</code> otherwise.
	 */
	public static final boolean delete(final String theInstrument)
	{
		int result = 0;
		final String sql = "delete from Watchlist where instrument = '" + theInstrument + "'";
		try
		{
			result = Database.watchlistStatement.executeUpdate(sql);
		}
		catch (final Exception e)
		{
			TradekingException.handleException(e);
		}
		return (result == 1 ? true : false);
	}

	/**
	 * Returns all watchlist items in the database in an
	 * <code>ArrayList<WatchlistItem></code>, suitable for creating a TradeKing
	 * watchlist update request.
	 * 
	 * @return Returns a ArrayList of watchlist items.
	 */
	public static final ArrayList<WatchlistItem> getAll()
	{
		ResultSet itemResult = null;
		ArrayList<WatchlistItem> itemArrayList = new ArrayList<WatchlistItem>();
		final String sql = "Select * from Watchlist order by instrument";
		try
		{
			itemResult = Database.watchlistStatement.executeQuery(sql);
			while (itemResult.next())
			{
				itemArrayList.add(getItemFromResultSet(itemResult));
			}
		}
		catch (final SQLException e)
		{
			TradekingException.handleException(e);
		}
		return itemArrayList;
	}

	// TODO Add method that converts the watchlist item to/from TK XML format.
	/**
	 * Constructs a watchlist item from a result set.
	 */
	private static final WatchlistItem getItemFromResultSet(ResultSet resultSet)
	{
		double newCostBasis = 0.0;
		String newInstrument = null;
		String newNotation = null;
		double newQuantity = 0.0;
		try
		{
			newCostBasis = resultSet.getDouble(1);
			newInstrument = resultSet.getString(2);
			newNotation = resultSet.getString(3);
			newQuantity = resultSet.getDouble(4);
		}
		catch (final Exception e)
		{
			TradekingException.handleException(e);
		}
		return new WatchlistItem(newCostBasis, newInstrument, newNotation, newQuantity);
	}

	/**
	 * Determines whether a watchlist item with a given instrument symbol is
	 * stored in the database.
	 * 
	 * @param theInstrument
	 *            The symbol of the instrument in question.
	 * 
	 * @return Returns <code>true</code> if a watchlist item with the given
	 *         instrument symbol exists in the database, <code>false</code>
	 *         otherwise.
	 */
	public static final boolean isStored(final String theInstrument)
	{
		boolean result = false;
		ResultSet resultSet = null;
		final String sql = "select * from Watchlist where instrument = '" + theInstrument + "'";
		try
		{
			resultSet = Database.watchlistStatement.executeQuery(sql);
			result = resultSet.next();
		}
		catch (final SQLException e)
		{
			TradekingException.handleException(e);
		}
		return result;
	}

	/**
	 * Retrieves a watchlist item from the database by instrument symbol.
	 * 
	 * @param theInstrument
	 *            The market symbol of the instrument.
	 * @return Returns the desired watchlist item or <code>null</code> if it's
	 *         not stored.
	 */
	public static final WatchlistItem retrieve(final String theInstrument)
	{
		ResultSet resultSet = null;
		final String sql = "select * from Watchlist where instrument = '" + theInstrument + "'";
		try
		{
			resultSet = Database.watchlistStatement.executeQuery(sql);
			if (!resultSet.next())
				return null;
		}
		catch (final Exception e)
		{
			TradekingException.handleException(e);
		}
		return getItemFromResultSet(resultSet);
	}

	/**
	 * Updates the entry with the selected instrument symbol in the database.
	 * 
	 * @param itemToUpdate
	 *            The symbol of the instrument to be updated.
	 * @return Returns <code>true</code> if the update succeeded,
	 *         <code>false</code> otherwise.
	 */
	public static boolean update(WatchlistItem itemToUpdate)
	{
		int result = 0;
		if (!itemToUpdate.isStored())
		{
			Utilities.errorMessage(itemToUpdate.instrument + " can't be updated; not stored.");
			return false;
		}
		final String sql = "Update Watchlist set costBasis = " + itemToUpdate.costBasis + ", notation = '"
				+ itemToUpdate.notation + "', quantity = " + itemToUpdate.quantity + " where instrument = '"
				+ itemToUpdate.instrument + "'";
		try
		{
			result = Database.watchlistStatement.executeUpdate(sql);
		}
		catch (SQLException e)
		{
			TradekingException.handleException(e);
		}
		return (result == Constants.UPDATE_SUCCESS ? true : false);
	}

	/**
	 * Not sure what this field is for. Used by TradeKing to
	 * "... assist in providing a portfolio value for the client."
	 */
	private double costBasis = 0.0;

	/**
	 * The market symbol of the item the user wants to watch.
	 */
	private String instrument = null;

	/**
	 * The notation added by the user to explain this watchlist item.
	 */
	private String notation = null;

	/**
	 * Not sure what this field is for. Used by TradeKing to
	 * "... assist in providing a portfolio value for the client."
	 */
	private double quantity = 0.0;

	/**
	 * Default constructor
	 */
	public WatchlistItem()
	{
		setCostBasis(0.0);
		setInstrument(null);
		setNotation(null);
		setQuantity(0.0);
	}

	/**
	 * Constructor with arguments.
	 * 
	 * @param newCostBasis
	 *            The new cost basis, used by TradeKing to
	 *            "... assist in providing a portfolio value for the client."
	 * @param newInstrument
	 *            The market symbol of the instrument the user wants to watch.
	 * @param newNotation
	 *            The free text entered by the user to explain why this element
	 *            is being watched.
	 * @param newQuantity
	 *            The new quantity, used by TradeKing to
	 *            "... assist in providing a portfolio value for the client."
	 */
	public WatchlistItem(double newCostBasis, String newInstrument, String newNotation, double newQuantity)
	{
		setCostBasis(newCostBasis);
		setInstrument(newInstrument);
		setNotation(newNotation);
		setQuantity(newQuantity);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param oldWatchlistItem
	 *            The watchlist item to copy.
	 */
	public WatchlistItem(WatchlistItem oldWatchlistItem)
	{
		this.setCostBasis(oldWatchlistItem.costBasis);
		this.setInstrument(oldWatchlistItem.instrument);
		this.setNotation(oldWatchlistItem.notation);
		this.setQuantity(oldWatchlistItem.quantity);
	}

	/**
	 * Enables a watchlist item to delete itself from the database. If this
	 * method returns <code>true</code>, the caller is assured that no item with
	 * the same market symbol exists in the database.
	 * <p>
	 * 
	 * @return Returns <code>true</code> if the item is deleted or not stored in
	 *         the first place, <code>false</code> if the delete fails.
	 */
	public final boolean delete()
	{
		if (!isStored())
			return true;
		int result = 0;
		final String sql = "delete from Watchlist where instrument = '" + instrument + "'";
		try
		{
			result = Database.watchlistStatement.executeUpdate(sql);
		}
		catch (final SQLException e)
		{
			TradekingException.handleException(e);
		}
		return (result == Constants.UPDATE_SUCCESS ? true : false);
	}

	/**
	 * Tests two watchlist items for equality.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof WatchlistItem))
			return false;
		WatchlistItem other = (WatchlistItem) obj;
		if (Utilities.round2(costBasis) != Utilities.round2(other.costBasis))
			return false;
		if (instrument == null)
		{
			if (other.instrument != null)
				return false;
		}
		else if (!instrument.equals(other.instrument))
			return false;
		if (notation == null)
		{
			if (other.notation != null)
				return false;
		}
		else if (!notation.equals(other.notation))
			return false;
		if (Utilities.round2(quantity) != Utilities.round2(other.quantity))
			return false;
		return true;
	}

	/**
	 * Getter for <code>costBasis</code>.
	 * 
	 * @return the costBasis
	 */
	public final double getCostBasis()
	{
		return costBasis;
	}

	/**
	 * Getter for <code>instrument</code>.
	 * 
	 * @return the instrument
	 */
	public final String getInstrument()
	{
		return instrument;
	}

	/**
	 * Getter for <code>notation</code>.
	 * 
	 * @return the notation
	 */
	public final String getNotation()
	{
		return notation;
	}

	/**
	 * Getter for <code>quantity</code>.
	 * 
	 * @return the quantity
	 */
	public final double getQuantity()
	{
		return quantity;
	}

	/**
	 * Generates a hash for this watchlist item.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(costBasis);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((instrument == null) ? 0 : instrument.hashCode());
		result = prime * result + ((notation == null) ? 0 : notation.hashCode());
		temp = Double.doubleToLongBits(quantity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * Enables a watchlist item to find if it is already stored in the database.
	 * 
	 * @return Returns <code>true</code> if a watchlist item of the same
	 *         instrument symbol is stored in the database, <code>false</code>
	 *         otherwise.
	 */
	public final boolean isStored()
	{
		boolean result = false;
		final String sql = "select * from Watchlist where instrument = '" + instrument + "'";
		ResultSet resultSet = null;
		try
		{
			resultSet = Database.watchlistStatement.executeQuery(sql);
			result = resultSet.next();
		}
		catch (final SQLException e)
		{
			TradekingException.handleException(e);
		}
		return result;
	}

	/**
	 * Setter for <code>costBasis</code>.
	 * 
	 * @param newCostBasis
	 *            the costBasis to set
	 */
	public final void setCostBasis(double newCostBasis)
	{
		if (newCostBasis < Constants.MIN_COST_BASIS || newCostBasis > Constants.MAX_COST_BASIS)
		{
			Utilities.errorMessage("New cost basis must be between " + Constants.MIN_COST_BASIS + " and "
					+ Constants.MAX_COST_BASIS + ".");
			return;
		}
		this.costBasis = Utilities.round2(newCostBasis);
	}

	/**
	 * Setter for <code>instrument</code>.
	 * 
	 * @param newInstrument
	 *            the instrument to set
	 */
	public final void setInstrument(String newInstrument)
	{
		if (newInstrument == null)
		{
			this.instrument = null;
			return;
		}
		if (newInstrument.length() < Constants.MIN_INSTRUMENT_LENGTH
				|| newInstrument.length() > Constants.MAX_INSTRUMENT_LENGTH)
		{
			Utilities.errorMessage("New instrument length must be greater than " + Constants.MIN_INSTRUMENT_LENGTH
					+ " and " + Constants.MAX_INSTRUMENT_LENGTH + ".");
			return;
		}
		this.instrument = newInstrument;
	}

	/**
	 * Setter for <code>notation</code>.
	 * 
	 * @param newNotation
	 *            the notation to set
	 */
	public final void setNotation(String newNotation)
	{
		if (newNotation == null)
		{
			this.notation = null;
			return;
		}
		if (newNotation.length() > Constants.MAX_NOTATION_LENGTH)
		{
			Utilities.errorMessage("Notation must be fewer than " + Constants.MAX_NOTATION_LENGTH + " characters.");
			return;
		}
		this.notation = newNotation;
	}

	/**
	 * Setter for <code>quantity</code>.
	 * 
	 * @param newQuantity
	 *            the quantity to set
	 */
	public final void setQuantity(double newQuantity)
	{
		if (newQuantity < Constants.MIN_QUANTITY || newQuantity > Constants.MAX_QUANTITY)
		{
			Utilities.errorMessage("Quantity must be between " + Constants.MIN_QUANTITY + " and "
					+ Constants.MAX_QUANTITY + ".");
			return;
		}
		this.quantity = Utilities.round2(newQuantity);
	}

	/**
	 * Enables a watchlist item to store itself in the database, overwriting
	 * anything that may already be stored.
	 * 
	 * @return Returns <code>true</code> if the entity successfully stores
	 *         itself, <code>false</code> if the store fails.
	 */
	public final boolean store()
	{
		int result = 0;
		if (isStored())
			delete();
		final String sql = "insert into Watchlist values(" + costBasis + ", '" + instrument + "', '"
				+ Utilities.prep(notation) + "', " + quantity + ")";
		try
		{
			result = Database.watchlistStatement.executeUpdate(sql);
		}
		catch (SQLException e)
		{
			TradekingException.handleException(e);
		}
		return (result == Constants.UPDATE_SUCCESS ? true : false);
	}

	/**
	 * Converts a watchlist to a string representation for display at the
	 * console.
	 * <p>
	 * This function currently emulates the output of an array
	 * <code>toString()</code>. It should eventually be converted to produce an
	 * XML output.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		// TODO Add conversion to XML, a la TK.
		return "WatchlistItem [costBasis=" + costBasis + ", instrument=" + instrument + ", notation=" + notation
				+ ", quantity=" + quantity + "]";
	}
}
