/*
 * Created Apr 3, 2011
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

import junit.framework.TestCase;

/**
 * Exercises <code>WatchlistItem</code> functionality using JUnit. Some routines
 * require knowledge of database contents.
 * <p>
 * Contest version.
 */
public class WatchlistItemTest extends TestCase
{
	/**
	 * The test cost basis.
	 */
	public static final double COST_BASIS = 99.95;

	/**
	 * The test instrument symbol.
	 */
	public static final String INSTRUMENT = "TEST";

	/**
	 * The test notation.
	 */
	public static final String NOTATION = "This is the test notation.";

	/**
	 * The test quantity.
	 */
	public static final double QUANTITY = 99.0;

	/**
	 * The test <code>WatchlistItem</code> instance.
	 */
	static WatchlistItem testWatchlistItem = null;

	/**
	 * Sets up the nominal test <code>WatchlistItem</code> instance.
	 * 
	 * @return Returns the standard test instance.
	 */
	public static final WatchlistItem setUpTestItem()
	{
		Database.setUpForTesting();
		testWatchlistItem = new WatchlistItem();
		testWatchlistItem.setCostBasis(COST_BASIS);
		testWatchlistItem.setInstrument(INSTRUMENT);
		testWatchlistItem.setNotation(NOTATION);
		testWatchlistItem.setQuantity(QUANTITY);
		return testWatchlistItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		testWatchlistItem = setUpTestItem();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		testWatchlistItem = null;
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#delete()}.
	 */
	public final void testDelete()
	{
		assertFalse(testWatchlistItem.isStored());
		testWatchlistItem.store();
		assertTrue(testWatchlistItem.isStored());
		testWatchlistItem.delete();
		assertFalse(testWatchlistItem.isStored());
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#delete(java.lang.String)}.
	 */
	public final void testDeleteString()
	{
		assertFalse(testWatchlistItem.isStored());
		testWatchlistItem.store();
		assertTrue(testWatchlistItem.isStored());
		WatchlistItem.delete(testWatchlistItem.getInstrument());
		assertFalse(testWatchlistItem.isStored());
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#equals(java.lang.Object)}.
	 */
	public final void testEqualsObject()
	{
		WatchlistItem item = new WatchlistItem(testWatchlistItem);
		assertTrue(item.equals(testWatchlistItem));
		assertTrue(item.getInstrument().equals(testWatchlistItem.getInstrument()));
		assertTrue(item.getNotation().equals(testWatchlistItem.getNotation()));
		assertTrue(item.getCostBasis() == testWatchlistItem.getCostBasis());
		assertTrue(item.getQuantity() == testWatchlistItem.getQuantity());
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#getAll()}.
	 */
	public final void testGetAll()
	{
		ArrayList<WatchlistItem> result = null;
		result = WatchlistItem.getAll();
		assertNotNull(result);
		// MAGIC NUMBER: requires knowledge of database contents.
		assertTrue(result.size() == 4);
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#getCostBasis()}.
	 */
	public final void testGetCostBasis()
	{
		assertTrue(testWatchlistItem.getCostBasis() == COST_BASIS);
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#getInstrument()}.
	 */
	public final void testGetInstrument()
	{
		assertTrue(testWatchlistItem.getInstrument().equals(INSTRUMENT));
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#getNotation()}.
	 */
	public final void testGetNotation()
	{
		assertTrue(testWatchlistItem.getNotation().equals(NOTATION));
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#getQuantity()}.
	 */
	public final void testGetQuantity()
	{
		assertTrue(testWatchlistItem.getQuantity() == QUANTITY);
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#isStored()}.
	 */
	public final void testIsStored()
	{
		assertFalse(testWatchlistItem.isStored());
		testWatchlistItem.store();
		assertTrue(testWatchlistItem.isStored());
		testWatchlistItem.delete();
		assertFalse(testWatchlistItem.isStored());
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#isStored(java.lang.String)}.
	 */
	public final void testIsStoredString()
	{
		assertFalse(WatchlistItem.isStored(INSTRUMENT));
		testWatchlistItem.store();
		assertTrue(WatchlistItem.isStored(INSTRUMENT));
		testWatchlistItem.delete();
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#retrieve(java.lang.String)}.
	 */
	public final void testRetrieve()
	{
		testWatchlistItem.store();
		WatchlistItem item = null;
		item = WatchlistItem.retrieve(INSTRUMENT);
		assertTrue(item.equals(testWatchlistItem));
		testWatchlistItem.delete();
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#setCostBasis(double)}.
	 */
	public final void testSetCostBasis()
	{
		assertFalse(testWatchlistItem.getCostBasis() == 0.0);
		testWatchlistItem.setCostBasis(0.0);
		assertTrue(testWatchlistItem.getCostBasis() == 0.0);
	}

	/**
	 * Test method for
	 * {@link tkwatch.WatchlistItem#setInstrument(java.lang.String)}.
	 */
	public final void testSetInstrument()
	{
		assertFalse(testWatchlistItem.getInstrument().equals("asdf"));
		testWatchlistItem.setInstrument("asdf");
		assertTrue(testWatchlistItem.getInstrument().equals("asdf"));
	}

	/**
	 * Test method for
	 * {@link tkwatch.WatchlistItem#setNotation(java.lang.String)}.
	 */
	public final void testSetNotation()
	{
		assertFalse(testWatchlistItem.getNotation().equals("asdf"));
		testWatchlistItem.setNotation("asdf");
		assertTrue(testWatchlistItem.getNotation().equals("asdf"));
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#setQuantity(double)}.
	 */
	public final void testSetQuantity()
	{
		assertFalse(testWatchlistItem.getQuantity() == 0.0);
		testWatchlistItem.setQuantity(0.0);
		assertTrue(testWatchlistItem.getQuantity() == 0.0);
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#store()}.
	 */
	public final void testStore()
	{
		assertFalse(testWatchlistItem.isStored());
		testWatchlistItem.store();
		assertTrue(testWatchlistItem.isStored());
		testWatchlistItem.delete();
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#update(WatchlistItem)}.
	 */
	public final void testUpdate()
	{
		assertTrue(testWatchlistItem.store());
		testWatchlistItem.setCostBasis(111.11);
		testWatchlistItem.setNotation("Completely new notation");
		testWatchlistItem.setQuantity(222.22);
		assertTrue(WatchlistItem.update(testWatchlistItem));
		WatchlistItem referenceItem = WatchlistItem.retrieve(testWatchlistItem.getInstrument());
		assertTrue(referenceItem.equals(testWatchlistItem));
		assertTrue(testWatchlistItem.delete());
	}

	/**
	 * Test method for {@link tkwatch.WatchlistItem#WatchlistItem()}.
	 */
	public final void testWatchlistItem()
	{
		WatchlistItem newItem = null;
		newItem = new WatchlistItem();
		assertNotNull(newItem);
		assertTrue(newItem.getInstrument() == null);
		assertTrue(newItem.getNotation() == null);
		assertTrue(newItem.getCostBasis() == 0.0);
		assertTrue(newItem.getQuantity() == 0.0);
	}

	/**
	 * Test method for
	 * {@link tkwatch.WatchlistItem#WatchlistItem(double, java.lang.String, java.lang.String, double)}
	 * .
	 */
	public final void testWatchlistItemDoubleStringStringDouble()
	{
		WatchlistItem item = new WatchlistItem(COST_BASIS, INSTRUMENT, NOTATION, QUANTITY);
		assertNotNull(item);
		assertTrue(item.equals(testWatchlistItem));
	}

	/**
	 * Test method for
	 * {@link tkwatch.WatchlistItem#WatchlistItem(tkwatch.WatchlistItem)}.
	 */
	public final void testWatchlistItemWatchlistItem()
	{
		WatchlistItem item = new WatchlistItem(testWatchlistItem);
		assertTrue(item.equals(testWatchlistItem));
	}

}
