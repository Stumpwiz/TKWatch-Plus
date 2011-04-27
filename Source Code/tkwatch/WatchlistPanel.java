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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * The panel for working with annotated watchlists.
 * <p>
 * Contest version.
 */
public class WatchlistPanel extends TradekingPanel
{
	/**
	 * The entry tab managed. Passed as an argument to spawned windows, to wit,
	 * the item add window.
	 */
	WatchlistPanel tabReference = null;

	/*
	 * Label for the list of instruments appearing in the watchlist. Spaces are
	 * to set the width of the symbol column.
	 */
	private final JLabel instrumentListLabel = new JLabel("   Symbol   ");

	/**
	 * List model for the list of stock market symbols in the watchlist.
	 */
	final DefaultListModel instrumentListModel = new DefaultListModel();

	/**
	 * The list of stock market symbols. This allows the user to select an
	 * instrument for display and then deletion or update.
	 */
	final JList instrumentList = new JList(instrumentListModel);

	/*
	 * Allows long instrument lists to scroll.
	 */
	private final JScrollPane instrumentListScrollPane = new JScrollPane(instrumentList);

	/*
	 * Label for the cost basis input/display field.
	 */
	final private JLabel costBasisLabel = new JLabel("Cost Basis");

	/*
	 * The cost basis input/display field.
	 */
	final private JTextField costBasisField = new JTextField(Constants.FIELD_WIDTH_NARROW);

	/*
	 * Label for the quantity input/display field.
	 */
	final private JLabel quantityLabel = new JLabel("Quantity");

	/*
	 * The quantity input/display field.
	 */
	final private JTextField quantityField = new JTextField(Constants.FIELD_WIDTH_NARROW);

	/*
	 * Label for the notation display/entry field.
	 */
	final private JLabel notationLabel = new JLabel("Notation");

	/*
	 * The field used to enter, display, and edit the notation the user adds to
	 * a watchlist item.
	 */
	final private JEditorPane notationField = new JEditorPane();

	/*
	 * Allows the notation field to scroll.
	 */
	final private JScrollPane notationFieldScrollPane = new JScrollPane(getNotationField());

	/*
	 * Clicked by the user to delete the selected instrument from the watch
	 * list.
	 */
	final private JButton buttonDelete = getButton("Delete", "Delete selected instrument from the watchlist.");

	/*
	 * Clicked by the user to open the window that supports adding a new
	 * instrument to the watchlist.
	 */
	final private JButton buttonAdd = getButton("Add", "Add an instrument to the watchlist.");

	/*
	 * Clicked by the user to save any changes made to cost basis, quantity, or
	 * notation to the database.
	 */
	final private JButton buttonUpdate = getButton("Update", "Record changes to local watchlist.");

	/*
	 * Labels the check box to enable tweeting.
	 */
	private final JLabel tweetLabel = new JLabel();

	/**
	 * Selects whether or not to tweet adds and deletes to/from the watchlist.
	 */
	final JCheckBox tweet = new JCheckBox();

	/**
	 * Constructor with title argument.
	 * 
	 * @param string
	 *            The title of the watchlist panel.
	 */
	public WatchlistPanel(String string)
	{
		tabReference = this;
		try
		{
			Database.connectToDatabase();
		}
		catch (ClassNotFoundException e)
		{
			TradekingException.handleException(e);
		}
		catch (SQLException e)
		{
			TradekingException.handleException(e);
		}
		final ArrayList<WatchlistItem> items = getItemsArrayList();
		final Iterator<WatchlistItem> itemIterator = items.iterator();
		while (itemIterator.hasNext())
		{
			instrumentListModel.addElement(itemIterator.next().getInstrument());
		}
		instrumentList.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (instrumentList.getSelectedValue() != null)
				{
					final String symbol = instrumentList.getSelectedValue().toString();
					final WatchlistItem item = WatchlistItem.retrieve(symbol);
					getCostBasisField().setText(Double.toString(item.getCostBasis()));
					getQuantityField().setText(Double.toString(item.getQuantity()));
					getNotationField().setText(Utilities.unPrep(item.getNotation()));
				}
			}

		});
		instrumentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		instrumentList.setLayoutOrientation(JList.VERTICAL);
		instrumentList.setFont(Constants.DEFAULT_FONT);
		instrumentList.setFixedCellWidth(Constants.FIELD_WIDTH_NARROW);
		if (instrumentList.getComponentCount() >= 0)
			instrumentList.setSelectedIndex(0);

		JPanel costQuantityPanel = new JPanel(new GridBagLayout());
		costQuantityPanel.add(costBasisLabel, Utilities.getConstraints(0, 0, 1, 1, GridBagConstraints.SOUTH));
		costQuantityPanel.add(quantityLabel, Utilities.getConstraints(1, 0, 1, 1, GridBagConstraints.SOUTH));
		costQuantityPanel.add(getCostBasisField(), Utilities.getConstraints(0, 1, 1, 1, GridBagConstraints.CENTER));
		costQuantityPanel.add(getQuantityField(), Utilities.getConstraints(1, 1, 1, 1, GridBagConstraints.CENTER));
		costQuantityPanel.add(notationLabel, Utilities.getConstraints(0, 2, 2, 1, GridBagConstraints.SOUTH));

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				// TODO Currently opens a new window; use a panel in the main frame instead.
				final ItemAddPanel addWindow = new ItemAddPanel(tabReference);
				final JFrame frame = new JFrame();
				frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(addWindow);
				frame.pack();
				frame.setSize(Constants.INITIAL_FRAME_WIDTH, Constants.INITIAL_FRAME_HEIGHT);
				Utilities.centerWindow(frame);
				frame.setVisible(true);
			}
		});
		buttonPanel.add(buttonAdd, Utilities.getConstraints(0, 0, 1, 1, GridBagConstraints.CENTER));
		buttonUpdate.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				final String instrumentSelected = (String) instrumentList.getSelectedValue();
				final int response = JOptionPane.showConfirmDialog(null, "Really update " + instrumentSelected
						+ " in the database?");
				if (response != JOptionPane.YES_OPTION)
					return;
				final WatchlistItem itemToUpdate = WatchlistItem.retrieve(instrumentSelected);
				itemToUpdate.setCostBasis(Double.parseDouble(getCostBasisField().getText()));
				itemToUpdate.setNotation(Utilities.prep(getNotationField().getText()));
				itemToUpdate.setQuantity(Double.parseDouble(getQuantityField().getText()));
				if (!WatchlistItem.update(itemToUpdate))
				{
					Utilities.errorMessage("Couldn't update " + instrumentSelected);
					return;
				}
				synchWatchlistWithTk();
			}
		});
		buttonPanel.add(buttonUpdate, Utilities.getConstraints(1, 0, 1, 1, GridBagConstraints.CENTER));
		buttonDelete.addActionListener(new ActionListener()
		{
			// TODO deleting all instruments leaves last instrument's data in
			// cost basis, quantity and notation windows.
			public void actionPerformed(final ActionEvent e)
			{
				final String symbolToDelete = (String) instrumentList.getSelectedValue();
				final int response = JOptionPane.showConfirmDialog(null, "Really delete " + symbolToDelete
						+ " from the watchlist?");
				if (response != JOptionPane.YES_OPTION)
					return;
				if (!WatchlistItem.delete(symbolToDelete))
				{
					Utilities.errorMessage("Couldn't delete item " + symbolToDelete);
					return;
				}
				instrumentListModel.removeElementAt(instrumentList.getSelectedIndex());
				if (tweet.isSelected())
				{
					final Twitter twitter = new TwitterFactory().getInstance();
					final String update;
					try
					{
						update = "Stopped watching " + symbolToDelete + " on @TradeKing.";
						twitter.updateStatus(update);
					}
					catch (TwitterException e1)
					{
						Utilities.errorMessage(e1.getMessage());
					}
				}
				if (instrumentList.getComponentCount() >= 0)
					instrumentList.setSelectedIndex(0);
				synchWatchlistWithTk();
			}
		});
		buttonPanel.add(buttonDelete, Utilities.getConstraints(2, 0, 1, 1, GridBagConstraints.CENTER));

		final JPanel tweetPanel = new JPanel(new FlowLayout());
		final ImageIcon iconColor = new ImageIcon("twitterColor.gif");
		final ImageIcon iconGray = new ImageIcon("twitterGray.gif");
		getTweetLabel().setIcon(iconColor);
		tweet.setSelected(true);
		tweet.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (tweet.isSelected())
				{
					getTweetLabel().setIcon(iconColor);
					// to tweet or not to tweet is decided by tweet.isSelected()
				}
				else
				{
					getTweetLabel().setIcon(iconGray);
				}
			}
		});
		tweetPanel.add(tweet);
		tweetPanel.add(getTweetLabel());

		final JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(buttonPanel, BorderLayout.NORTH);
		bottomPanel.add(tweetPanel, BorderLayout.SOUTH);

		final JPanel managementPanel = new JPanel(new BorderLayout());
		managementPanel.add(costQuantityPanel, BorderLayout.NORTH);
		managementPanel.add(notationFieldScrollPane, BorderLayout.CENTER);
		managementPanel.add(bottomPanel, BorderLayout.SOUTH);

		final JPanel symbolPanel = new JPanel(new BorderLayout());
		symbolPanel.add(instrumentListLabel, BorderLayout.NORTH);
		symbolPanel.add(instrumentListScrollPane, BorderLayout.CENTER);

		this.setLayout(new BorderLayout(Constants.GAP, Constants.GAP));
		this.add(symbolPanel, BorderLayout.WEST);
		this.add(managementPanel, BorderLayout.CENTER);
	}

	/**
	 * Returns a standard-size button.
	 * 
	 * @param label
	 *            The button label.
	 * @param toolTip
	 *            The tool tip for the button.
	 */
	private final JButton getButton(final String label, final String toolTip)
	{
		final JButton result = new JButton(label);
		result.setSize(Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
		result.setToolTipText(toolTip);
		return result;
	}

	/**
	 * Gets the cost basis.
	 * 
	 * @return the costBasisField
	 */
	public final JTextField getCostBasisField()
	{
		return costBasisField;
	}

	/**
	 * Sets up the ArrayList of watchlist items. (If for nothing else, this is
	 * needed for testing.)
	 * 
	 * @return Returns the ArrayList, populated with all the items in the watch
	 *         list database.
	 */
	public final ArrayList<WatchlistItem> getItemsArrayList()
	{
		final ArrayList<WatchlistItem> result = WatchlistItem.getAll();
		return result;
	}

	/**
	 * Gets the notation.
	 * 
	 * @return the notationField
	 */
	public final JEditorPane getNotationField()
	{
		return notationField;
	}

	/**
	 * Gets the quantity.
	 * 
	 * @return the quantityField
	 */
	public final JTextField getQuantityField()
	{
		return quantityField;
	}

	/**
	 * Getter for the (graphic) caption of the tweet check box. This becomes
	 * colored icon image when checked, a grayed-out icon image when unchecked.
	 * 
	 * @return the tweetLabel
	 */
	public JLabel getTweetLabel()
	{
		return tweetLabel;
	}

	/**
	 * Refreshes the list of instrument symbols from the database.
	 * 
	 * @return Returns the list.
	 */
	final JList refreshInstrumentList()
	{
		final ArrayList<WatchlistItem> items = getItemsArrayList();
		final Vector<String> symbols = new Vector<String>();
		final Iterator<WatchlistItem> itemIterator = items.iterator();
		while (itemIterator.hasNext())
		{
			symbols.add(itemIterator.next().getInstrument());
		}
		final JList result = new JList(symbols);
		result.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent arg0)
			{
				final String symbol = result.getSelectedValue().toString();
				final WatchlistItem item = WatchlistItem.retrieve(symbol);
				getCostBasisField().setText(Double.toString(item.getCostBasis()));
				getQuantityField().setText(Double.toString(item.getQuantity()));
				getNotationField().setText(Utilities.unPrep(item.getNotation()));
			}
		});
		result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		result.setLayoutOrientation(JList.VERTICAL);
		result.setFont(Constants.DEFAULT_FONT);
		result.setFixedCellWidth(Constants.FIELD_WIDTH_NARROW);
		if (result.getComponentCount() >= 0)
			result.setSelectedIndex(0);
		return result;
	}
}
