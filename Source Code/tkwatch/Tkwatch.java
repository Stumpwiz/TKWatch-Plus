/*
 * Created Mar 31, 2011.
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Java Swing GUI shell for the <strong>TKWatch+</strong> program.
 * <p>
 * Contest version.
 */
public class Tkwatch extends JFrame implements ItemListener
{
	/*
	 * The main GUI frame.
	 */
	private static final JFrame frame = new JFrame();

	/*
	 * Displays user-interface options in the program.
	 */
	private static JPanel guiPanel = null;

	/**
	 * Creates and shows the graphical user interface display.
	 */
	static final void createAndShowGUI()
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setTitle(Constants.PROGRAM_TITLE);
		BufferedImage image = null;
		try
		{
			image = ImageIO.read(frame.getClass().getResource(Constants.ICON_IMAGE));
		}
		catch (IOException e)
		{
			TradekingException.handleException(e);
		}
		frame.setIconImage(image);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiPanel = createGuiPanel();
		frame.getContentPane().add(guiPanel);
		frame.pack();
		frame.setSize(Constants.INITIAL_FRAME_WIDTH, Constants.INITIAL_FRAME_HEIGHT);
		Utilities.centerWindow(frame);
		SwingUtilities.updateComponentTreeUI(frame);
		frame.setVisible(true);
	}

	/*
	 * Creates the user interface panel.
	 * 
	 * @return The user interface panel.
	 */
	private static final JPanel createGuiPanel()
	{
		// The panel itself
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(Constants.GAP, Constants.GAP));

		// The tabbed panel
		final JPanel watchListPanel = new WatchlistPanel("Manage watchlists.");
		mainPanel.add(watchListPanel, BorderLayout.CENTER);

		// The status sub-panel
		final JPanel statusPanel = Utilities.getStatusPanel();
		mainPanel.add(statusPanel, BorderLayout.SOUTH);
		return mainPanel;
	}

	/**
	 * Standard entry point.
	 * 
	 * @param args
	 *            Not used.
	 */
	public static void main(String[] args)
	{
		// Make it look Windows-like.
		try
		{
			// TODO allow for other OS look and feel.
			UIManager.setLookAndFeel(Constants.LOOK_AND_FEEL);
		}
		catch (final Exception e)
		{
			TradekingException.handleException(e);
		}
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				createAndShowGUI();
			}
		});
	}

	/**
	 * Inherited from <code>ItemListener</code>. Not used.
	 * 
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent arg0)
	{
		// Not currently used.

	}

	/**
	 * Clean up and exit the program.
	 */
	public void quit()
	{
		Database.cleanUp();
		Tkwatch.this.dispose();
		System.exit(0);
	}

}
