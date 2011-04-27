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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.Vector;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.xerces.parsers.SAXParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Contains some common routines used by the <code>tkwatch</code> package. Some
 * of these were used only during development. Others appear for potential
 * future use.
 * <p>
 * Contest version.
 */
final class Utilities
{
	/**
	 * Centers a frame on the screen; from Murach and Steelman, <i>Murach's Java
	 * SE 6</i>, p. 487.
	 * 
	 * @param window
	 *            The window to center.
	 */
	public static final void centerWindow(final Window window)
	{
		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		final Dimension dimension = toolkit.getScreenSize();
		window.setLocation((dimension.width - window.getWidth()) / 2, (dimension.height - window.getHeight()) / 2);
	}

	/**
	 * Called by a button to close the containing window when actions are done.
	 * 
	 * @param thisButton
	 *            The button that closes its containing window.
	 */
	static final void closeFrame(JButton thisButton)
	{
		((JFrame) thisButton.getTopLevelAncestor()).dispose();
	}

	/**
	 * Shows an error message and waits for user to clear it.
	 * 
	 * @param messageText
	 *            The message to be displayed.
	 */
	public static final void errorMessage(String messageText)
	{
		JOptionPane.showMessageDialog(null, messageText, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Generates the signature necessary to make a TradeKing API call. Adapted
	 * from the <i>TradeKing API Reference Guide</i>, 03.25.2011, p. 51.
	 * 
	 * @param data
	 *            Concatenation of the API request body and a timestamp.
	 * @param key
	 *            The user's secret key.
	 * @return Returns the necessary signature.
	 * @throws java.security.SignatureException
	 *             Thrown if the md5 hash-based message authentication code
	 *             can't be generated.
	 */
	public static final String generateSignature(final String data, final String key)
			throws java.security.SignatureException
	{
		String result;
		try
		{
			byte[] encodedData = Base64.encodeBase64(data.getBytes());
			// Get an hmac_md5 key from the raw key bytes
			byte[] keyBytes = key.getBytes();
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacMD5");
			// Get an hmac_md5 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance("HmacMD5");
			mac.init(signingKey);
			// Compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(encodedData);
			// Convert raw bytes to Hex
			byte[] hexBytes = new Hex().encode(rawHmac);
			// Covert array of Hex bytes to a String
			result = new String(hexBytes, "ISO-8859-1");
		}
		catch (Exception e)
		{
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}

	/**
	 * The Twitter OAuth authorization process requires access tokens. Twitter4j
	 * stores them in <code>twitter4j.properties</code>. Invoke this routine to
	 * obtain them; then add the appropriate lines in the properties file. (You
	 * will need to write your own <code>main()</code> to do this. This routine
	 * isn't actually called from the <strong>TKWatch+</strong> routine.
	 * Documentation for <code>twitter4j.properties</code> is available <a
	 * href="http://twitter4j.org/en/configuration.html">here</a>.
	 * <p>
	 * Adapted from Yusuke Yamamoto, <a
	 * href="http://twitter4j.org/en/code-examples.html">OAuth support</a>,
	 * April 18, 2011.
	 */
	@SuppressWarnings("null")
	public static final void getAccessTokens()
	{
		// The factory instance is re-usable and thread safe.
		Twitter twitter = new TwitterFactory().getInstance();
		// Assumes consumer key and consumer secret are already stored in
		// properties file.
		// twitter.setOAuthConsumer(Constants.CONSUMER_KEY,
		// Constants.CONSUMER_SECRET);
		RequestToken requestToken = null;
		try
		{
			requestToken = twitter.getOAuthRequestToken();
		}
		catch (TwitterException e1)
		{
			e1.printStackTrace();
		}
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken)
		{
			System.out.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			System.out.print("Cut and paste the pin:");
			String pin = "";
			try
			{
				pin = br.readLine();
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			try
			{
				if (pin.length() > 0)
				{
					accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				}
				else
				{
					accessToken = twitter.getOAuthAccessToken();
				}
			}
			catch (TwitterException te)
			{
				if (401 == te.getStatusCode())
				{
					System.out.println("Unable to get the access token.");
				}
				else
				{
					te.printStackTrace();
				}
			}
		}
		// Print out the access tokens for future reference. The ones we got are
		// in the
		// <code>twitter4j.properties</code> file.
		try
		{
			System.out.println("Access tokens for " + twitter.verifyCredentials().getId());
		}
		catch (TwitterException e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Store in twitter4j.properties as oauth.accessToken=" + accessToken.getToken());
		System.out.println("Store in twitter4j.properties as oauth.accessTokenSecret=" + accessToken.getTokenSecret());
	}

	/**
	 * Method for setting grid bag constraints; adapted from Murach and
	 * Steelman's <i>Java SE 6</i>, p. 542.
	 * 
	 * @param gridx
	 *            The leftmost cell that the component occupies.
	 * @param gridy
	 *            The topmost cell that the component occupies.
	 * @param gridwidth
	 *            The number of horizontal cells that a component occupies.
	 * @param gridheight
	 *            The number of vertical cells that a component occupies.
	 * @param anchor
	 *            Specifies the alignment of the component within the cell.
	 */
	public static final GridBagConstraints getConstraints(final int gridx, final int gridy, final int gridwidth,
			final int gridheight, int anchor)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(Constants.INSET, Constants.INSET, Constants.INSET, Constants.INSET);
		c.ipadx = Constants.PADDING;
		c.ipady = Constants.PADDING;
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = gridwidth;
		c.gridheight = gridheight;
		c.anchor = anchor;
		return c;
	}

	/**
	 * Gets a working instance of a document builder.
	 * 
	 * @return The document builder instance.
	 * @throws ParserConfigurationException
	 */
	public static final DocumentBuilder getDocumentBuilder() throws ParserConfigurationException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setIgnoringComments(false);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setNamespaceAware(true);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		builder.setEntityResolver(new NullResolver());
		return builder;
	}

	/**
	 * Returns the content of a URL as a <code>String</code> instance. This was
	 * used only during development. Retained for completeness.
	 * 
	 * @param newUrlName
	 *            The name of the URL to visit.
	 * @return The contents of the URL as a <code>String</code> or
	 *         <code>null</code> if there's a problem.
	 */
	public static final String getHtmlContent(String newUrlName)
	{
		int urlContentLength = 0;
		byte[] inBuffer = null;
		try
		{
			URL url = new URL(newUrlName);
			URLConnection urlConnection = url.openConnection();
			urlContentLength = urlConnection.getContentLength();
			if (urlContentLength == Constants.UNKNOWN_LENGTH)
				inBuffer = new byte[Constants.MAX_URL_CONTENT];
			else
				inBuffer = new byte[urlContentLength];
			DataInputStream in = new DataInputStream(new BufferedInputStream(urlConnection.getInputStream()));
			in.readFully(inBuffer);
			in.close();
			System.out.println("Utilities.getHtmlContent() reports inBuffer length is " + inBuffer.length);
		}
		catch (MalformedURLException mue)
		{
			return null;
		}
		catch (IOException ioe)
		{
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Utilities.errorMessage(e.getMessage());
			return null;
		}
		String resultString = new String(inBuffer);
		System.out.println(resultString.toString());
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < resultString.length(); i++)
		{
			char c = resultString.charAt(i);
			// Only interested in ASCII
			if (c < Constants.LF || c > Constants.DEL)
				continue;
			if (c == '"')
			{
				// Assumes the first character won't be "
				char previous = resultString.charAt(i - 1);
				// Escape the \ in \" to deal with "\""
				if (previous == '\\')
					result.append('\\');
				// Escape double quotes to allow sane handling of Java strings.
				result.append('\\');
			}
			// Eliminate single quotes to avoid problems storing strings in SQL.
			// Eliminate | to allow for alternate string delimiter.
			if (c != '\'' && c != '|')
				result.append(c);
		}
		return result.toString();
	}

	/**
	 * Returns a quit button.
	 * 
	 * @return A button that cleans up and exits.
	 */
	public static final JButton getQuitButton()
	{
		final JButton button = new JButton("Quit");
		button.setToolTipText("Exit the application.");
		button.setSize(Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				System.out.flush();
				System.exit(0);
			}
		});
		return button;
	}

	/**
	 * Returns a standard panel for the bottom (SOUTH) of all windows.
	 */
	public static final JPanel getStatusPanel()
	{
		ImageIcon poweredByT4j = new ImageIcon("poweredByT4j.gif");
		// TODO Messageline currently used for Twitter4J banner. Could be used
		// for expected status messages, currently implemented as dialog boxes.
		final JLabel messageLine = new JLabel();
		messageLine.setIcon(poweredByT4j);
		final JPanel buttonPanel = new JPanel(new GridLayout(Constants.ONE_ROW, Constants.THREE_COLUMNS));
		buttonPanel.add(getQuitButton());
		final JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.add(messageLine, BorderLayout.WEST);
		statusPanel.add(buttonPanel, BorderLayout.EAST);
		statusPanel.updateUI();
		return statusPanel;
	}

	/**
	 * Finds the value of the named element, if any, in an XML string. Adapted
	 * from Vohra and Vohra, <i>Pro XML Development with Java Technology</i>, p.
	 * 47.
	 * 
	 * @param desiredElementName
	 *            The name of the element to search for in the XML string.
	 * @param xmlString
	 *            The XML string to search for an account number.
	 * 
	 * @return Returns the element value(s) as formatted in the incoming string
	 *         (if found) as a <code>Vector<String></code>, <code>null</code>
	 *         otherwise.
	 */
	public static final Vector<String> getValueFromXml(final String desiredElementName, final String xmlString)
	{
		Vector<String> elementValue = new Vector<String>();
		String elementValueText = null;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		try
		{
			String elementName = "";
			StringReader stringReader = new StringReader(xmlString);
			XMLStreamReader reader = inputFactory.createXMLStreamReader(stringReader);
			while (reader.hasNext())
			{
				int eventType = reader.getEventType();
				switch (eventType)
				{
				case XMLStreamConstants.START_ELEMENT:
					elementName = reader.getLocalName();
					if (elementName.equals(desiredElementName))
					{
						elementValueText = reader.getAttributeValue(0);
						System.out.println("START_ELEMENT case, element name is " + elementName + ", element value is "
								+ elementValueText);
					}
					break;
				case XMLStreamConstants.ATTRIBUTE:
					elementName = reader.getLocalName();
					if (elementName.equals(desiredElementName))
					{
						elementValueText = reader.getElementText();
						System.out.println("ATTRIBUTE case, element name is " + elementName + ", element value is "
								+ elementValueText);
						elementValue.add(elementValueText);
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					elementName = reader.getLocalName();
					if (elementName.equals(desiredElementName))
					{
						System.out.println("END_ELEMENT case, element name is " + elementName + ", element value is "
								+ elementValueText);
					}
					break;
				default:
					elementName = reader.getLocalName();
					if (elementName != null)
					{
						if (elementName.equals(desiredElementName))
						{
							System.out.println("default case, element name is " + elementName);
						}
					}
					break;
				}
				reader.next();
			}
		}
		catch (XMLStreamException e)
		{
			TradekingException.handleException(e);
		}
		return (elementValue.isEmpty() ? null : elementValue);
	}

	/**
	 * Shows an informational message and waits for user to clear it.
	 * 
	 * @param messageText
	 *            The message to be displayed.
	 */
	public static final void informationMessage(final String messageText)
	{
		JOptionPane.showMessageDialog(null, messageText, "Message", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Prepares a string to be stored in an SQL statement. That means that
	 * single quoted (apostrophes) are doubled.
	 * 
	 * @param inputString
	 *            The string to prepared for SQL handling.
	 * @return The prepared string.
	 */
	static final String prep(final String inputString)
	{
		char c = '\0';
		final StringBuffer inputBuffer = new StringBuffer(inputString);
		final StringBuffer outputBuffer = new StringBuffer();
		for (int i = 0; i < inputBuffer.length(); i++)
		{
			c = inputBuffer.charAt(i);
			if (c == '\'')
				outputBuffer.append('\'');
			outputBuffer.append(c);
		}
		return outputBuffer.toString();
	}

	/**
	 * Returns the input argument, rounded to two decimal places.
	 * 
	 * @param arg
	 *            The argument to round.
	 * @return The argument rounded to two decimal places.
	 */
	static final double round2(final double arg)
	{
		double d = arg * 100.0;
		final long longArg = Math.round(d);
		return longArg / 100.0;
	}

	/**
	 * Issues a TradeKing API request. Adapted from the <i>TradeKing API
	 * Reference Guide</i>, 03.25.2011, p. 51.
	 * 
	 * @param resourceUrl
	 *            The URL to which API requests must be made.
	 * @param body
	 *            The body of the API request.
	 * @param appKey
	 *            The user's application key.
	 * @param userKey
	 *            The user's key.
	 * @param userSecret
	 *            The user's secret key.
	 * @return Returns the result of the API request.
	 */
	public static final String tradeKingRequest(final String resourceUrl, final String body, final String appKey,
			final String userKey, final String userSecret)
	{
		String response = new String();
		try
		{
			String timestamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
			String request_data = body + timestamp;
			String signature = generateSignature(request_data, userSecret);
			URL url = new URL(resourceUrl);
			URLConnection conn = url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/xml");
			conn.setRequestProperty("Accept", "application/xml");
			conn.setRequestProperty("TKI_TIMESTAMP", timestamp);
			conn.setRequestProperty("TKI_SIGNATURE", signature);
			conn.setRequestProperty("TKI_USERKEY", userKey);
			conn.setRequestProperty("TKI_APPKEY", appKey);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(body);
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String temp;
			while ((temp = in.readLine()) != null)
			{
				response += temp + "\n";
			}
			in.close();
			return response;
		}
		catch (java.security.SignatureException e)
		{
			errorMessage(e.getMessage());
			return "";
		}
		catch (java.io.IOException e)
		{
			errorMessage(e.getMessage());
			return "";
		}
	}

	/**
	 * Prepares a retrieved string to be displayed without SQL-necessary doubled
	 * single quotes (apostrophes).
	 * 
	 * @param inputString
	 *            The string to be stripped of double apostrophes.
	 * @return The string ready for display.
	 */
	static final String unPrep(final String inputString)
	{
		char c = '\0';
		final StringBuffer inputBuffer = new StringBuffer(inputString);
		final StringBuffer outputBuffer = new StringBuffer();
		for (int i = 0; i < inputBuffer.length(); i++)
		{
			c = inputBuffer.charAt(i);
			// Delete doubles apostrophes
			if (i > 0 && inputBuffer.charAt(i - 1) == '\'' && inputBuffer.charAt(i) == '\'')
				continue;
			outputBuffer.append(c);
		}
		return outputBuffer.toString();
	}

	/**
	 * Recursively travels through a DOM tree. Adapted from Vohra and Vohra,
	 * <i>Pro XML Development with Java Technology</i>, p. 47. This was used
	 * during development and is not called by <strong>TKWatch+</strong>.
	 * Retained for completeness.
	 * 
	 * @param previousNode
	 * @param visitNode
	 */
	public static void visitNode(Element previousNode, Element visitNode)
	{
		if (previousNode != null)
		{
			System.out.println("Element " + previousNode.getTagName() + " has element:");
		}
		System.out.println("Element Name: " + visitNode.getTagName());
		if (visitNode.hasAttributes())
		{
			System.out.println("Element " + visitNode.getTagName() + " has attributes: ");
			NamedNodeMap attributes = visitNode.getAttributes();
			for (int j = 0; j < attributes.getLength(); j++)
			{
				Attr attribute = (Attr) (attributes.item(j));
				System.out.println("Attribute:" + attribute.getName() + " with value " + attribute.getValue());
			}
		}

		// Obtain a NodeList of nodes in an Element node.
		NodeList nodeList = visitNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;
				visitNode(visitNode, element);
			}
			else if (node.getNodeType() == Node.TEXT_NODE)
			{
				String str = node.getNodeValue().trim();
				if (str.length() > 0)
				{
					System.out.println("Element Text: " + str);
				}
			}
		}
	}

	/**
	 * Shows a warning message and waits for user to clear it.
	 * 
	 * @param messageText
	 *            The message to be displayed.
	 */
	public static final void warningMessage(final String messageText)
	{
		JOptionPane.showMessageDialog(null, messageText, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Create a DOM from a file.
	 * 
	 * @param fileName
	 *            The input file to convert.
	 * @return Returns the DOM document from the XML.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static final Document xmlFileToDom(final String fileName) throws SAXException, IOException,
			ParserConfigurationException
	{
		DocumentBuilder db = getDocumentBuilder();
		return db.parse(new File(fileName));
	}

	public static final void xmlParse(final String xmlString)
	{

		XMLReader parser = new SAXParser();
		try
		{
			parser.parse(xmlString);
		}
		catch (IOException e)
		{
			TradekingException.handleException(e);
		}
		catch (SAXException e)
		{
			TradekingException.handleException(e);
		}
	}

	/**
	 * Create a DOM from an input stream. This is originally from the Jakarta
	 * Commons Modeler.
	 * 
	 * @param is
	 *            An input stream.
	 * @return Returns the DOM document from the input stream.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static final Document xmlStreamToDom(final InputStream is) throws SAXException, IOException,
			ParserConfigurationException
	{
		DocumentBuilder db = getDocumentBuilder();
		return db.parse(is);
	}

	/**
	 * Converts a string to an DOM document.
	 * 
	 * @param xml
	 *            The string to convert.
	 * @return Returns the DOM document from the XML string.
	 * @throws Exception
	 */
	public static final Document xmlStringToDom(final String xml) throws Exception
	{
		DocumentBuilder builder = getDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(xml)));
	}

}
