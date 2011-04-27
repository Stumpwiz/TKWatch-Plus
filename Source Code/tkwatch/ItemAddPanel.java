/*
 * Created Apr 11, 2011
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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * The basis for adding new watchlist items.
 * <p>
 * Contest version.
 */
public class ItemAddPanel extends TradekingPanel
{
	/**
	 * The <code>JButton</code> that adds an item to the watchlist.
	 */
	final JButton buttonAdd = new JButton("Add");
	
	/**
	 * The <code>JButton</code> that cancels a pending add operation.
	 */
	final JButton buttonCancel = new JButton("Cancel");
	
	/*
	 * The caption for the cost basis entry field.
	 */
	final private JLabel costBasisLabel = new JLabel("Cost Basis");
	
	/**
	 * The <code>JTextField</code> for entering the item's cost basis.
	 */
	final JTextField costBasisField = new JTextField(Constants.FIELD_WIDTH_NARROW);
	
	/*
	 * The caption for the instrument entry field.
	 */
	final private JLabel instrumentLabel = new JLabel("Instrument");
	
	/**
	 * The <code>JTextField</code> for entering the item's instrument (market symbol).
	 */
	final JTextField instrumentField = new JTextField(Constants.FIELD_WIDTH_NARROW);
	
	/*
	 * The caption for the item's quantity entry field.
	 */
	final private JLabel quantityLabel = new JLabel("Quantity");
	
	/**
	 * The <code>JTextField</code> for entering the item's quantity.
	 */
	final JTextField quantityField = new JTextField(Constants.FIELD_WIDTH_NARROW);
	
	/*
	 * The caption for the item's notation field.
	 */
	final private JLabel notationLabel = new JLabel("Notation");
	
	/**
	 * The <code>JEditorPane</code> for entering the item's notation.
	 */
	final JEditorPane notationField = new JEditorPane();
	
	/*
	 * Enables scrolling of over-long notations.
	 */
	final private JScrollPane notationFieldScrollPane = new JScrollPane(notationField);

	/**
	 * Constructor with argument. Needs the calling panel as an argument,
	 * because this will update the list of symbols on add or delete.
	 * 
	 * @param theTab
	 *            The panel that spawned this panel.
	 */
	public ItemAddPanel(final WatchlistPanel theTab)
	{
		// The panel for instrument, cost basis, and quantity.
		final JPanel topPanel = new JPanel(new GridBagLayout());
		topPanel.add(instrumentLabel, Utilities.getConstraints(0, 0, 1, 1, GridBagConstraints.CENTER));
		topPanel.add(instrumentField, Utilities.getConstraints(0, 1, 1, 1, GridBagConstraints.CENTER));
		topPanel.add(costBasisLabel, Utilities.getConstraints(1, 0, 1, 1, GridBagConstraints.CENTER));
		topPanel.add(costBasisField, Utilities.getConstraints(1, 1, 1, 1, GridBagConstraints.CENTER));
		topPanel.add(quantityLabel, Utilities.getConstraints(2, 0, 1, 1, GridBagConstraints.CENTER));
		topPanel.add(quantityField, Utilities.getConstraints(2, 1, 1, 1, GridBagConstraints.CENTER));

		// The panel for notation.
		JPanel notationPanel = new JPanel(new BorderLayout());
		notationPanel.add(notationLabel, BorderLayout.NORTH);
		notationPanel.add(notationFieldScrollPane, BorderLayout.CENTER);

		// The panel for the add and cancel buttons.
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				final String newInstrument = instrumentField.getText();
				if (newInstrument.isEmpty())
				{
					Utilities.errorMessage("Instrument can't be empty.");
					return;
				}
				final String newCostString = costBasisField.getText();
				if (newCostString.isEmpty())
				{
					Utilities.errorMessage("Cost basis can't be empty.");
					return;
				}
				final double newCostBasis = Double.parseDouble(newCostString);
				final String newNotation = notationField.getText();
				final String newQuantityString = quantityField.getText();
				if (newQuantityString.isEmpty())
				{
					Utilities.errorMessage("Quantity can't be empty.");
					return;
				}
				final double newQuantity = Double.parseDouble(newQuantityString);
				final WatchlistItem newItem = new WatchlistItem(newCostBasis, newInstrument, newNotation, newQuantity);
				final String newItemInstrument = newItem.getInstrument();
				if (newItem.isStored())
				{
					Utilities.warningMessage("You are already watching " + newItemInstrument);
					return;
				}
				if (!newItem.store())
				{
					Utilities.errorMessage("Could not store new item.");
				}

				theTab.instrumentListModel.addElement(newItemInstrument);
				if (theTab.tweet.isSelected())
				{
					Twitter twitter = new TwitterFactory().getInstance();
					final String update;
					try
					{
						update = "Now watching " + newItemInstrument + " on @TradeKing.";
						twitter.updateStatus(update);
					}
					catch (TwitterException e1)
					{
						Utilities.errorMessage(e1.getMessage());
					}
				}
				synchWatchlistWithTk();
				Utilities.closeFrame(buttonAdd);
			}
		});
		buttonPanel.add(buttonAdd, Utilities.getConstraints(0, 0, 1, 1, GridBagConstraints.CENTER));
		buttonCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				Utilities.closeFrame(buttonCancel);
			}
		});
		buttonPanel.add(buttonCancel, Utilities.getConstraints(1, 0, 1, 1, GridBagConstraints.CENTER));

		// The status panel.
		final JPanel bottomPanel = Utilities.getStatusPanel();

		// The panel to hold topPanel, notationPanel, and buttonPanel
		final JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(notationPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}
}
