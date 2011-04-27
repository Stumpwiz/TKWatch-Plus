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

import java.awt.Font;

/**
 * Various manifest constants for the <code>tkwatch</code> package. Allows
 * parameterization of code. Important items are defined once here and only
 * referenced (perhaps multiple times) elsewhere.
 * <p>
 * Contest version.
 */
public interface Constants
{
	/**
	 * Preferred height of Swing buttons.
	 */
	final int BUTTON_HEIGHT = 25;

	/**
	 * Preferred width of Swing buttons.
	 */
	final int BUTTON_WIDTH = 163;

	/**
	 * Manifest constant for ASCII DEL character.
	 */
	final int DEL = 0x007f;

	/**
	 * The default font.
	 */
	final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, Constants.FONT_SIZE);

	/**
	 * Default font point-size.
	 */
	final int FONT_SIZE = 12;

	/**
	 * Standard gap-size for Swing graphic displays.
	 */
	final int GAP = 2;

	/**
	 * The name of the image file to use for the frame icon.
	 */
	final String ICON_IMAGE = "/tkwIcon.jpg";

	/**
	 * Initial height of the GUI frame.
	 */
	final int INITIAL_FRAME_HEIGHT = 475;

	/**
	 * Initial width of the GUI frame.
	 */
	final int INITIAL_FRAME_WIDTH = 510;

	/**
	 * External padding for grid bag constraints.
	 */
	final int INSET = 5;

	/**
	 * ASCII LF character.
	 */
	final int LF = 0x000a;

	/**
	 * Programmable look-and-feel setting for Windows format.
	 */
	final String LOOK_AND_FEEL = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

	/**
	 * Manifest constant indicating one row.
	 */
	final int ONE_ROW = 1;

	/**
	 * The working title of the main program.
	 */
	final String PROGRAM_TITLE = "TKWatch+";

	/**
	 * Internal padding for grid bag constraints.
	 */
	final int PADDING = 5;

	/**
	 * Maximum cost basis for a watchlist item.
	 */
	final double MAX_COST_BASIS = 10000.0;

	/**
	 * Maximum quantity for a watchlist item.
	 */
	final double MAX_QUANTITY = 1000.0;

	/**
	 * Maximum character count in an HTML page,
	 */
	final int MAX_URL_CONTENT = 300000;

	/**
	 * Minimum cost basis for a watchlist item.
	 */
	final double MIN_COST_BASIS = 0.0;

	/**
	 * Minimum instrument length;
	 */
	final int MIN_INSTRUMENT_LENGTH = 1;

	/**
	 * Minimum quantity for a watchlist item.
	 */
	final double MIN_QUANTITY = 0.0;

	/**
	 * Maximum length for the watchlist item instrument (stock symbol) length.
	 */
	final int MAX_INSTRUMENT_LENGTH = 6;

	/**
	 * Maximum length of the watchlist notation, the free text entered by the
	 * user to explain or otherwise comment on why the instrument is being
	 * watched.
	 */
	final int MAX_NOTATION_LENGTH = 4096;

	/**
	 * Default width of medium-size input fields, such as keys.
	 */
	final int FIELD_WIDTH_NARROW = 8;

	/**
	 * Default width of medium-size input fields, such as keys.
	 */
	final int FIELD_WIDTH_WIDE = 40;

	/**
	 * Manifest constant indicating three columns.
	 */
	final int THREE_COLUMNS = 3;

	/**
	 * Manifest constant for unknown URL content length.
	 */
	final int UNKNOWN_LENGTH = -1;

	/**
	 * Manifest constant indicating a successful database update of one row.
	 */
	final int UPDATE_SUCCESS = 1;
}
