/*
 * Created Apr 23, 2011
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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * The Twitter <code>OAuth</code> authorization process requires access tokens.
 * Twitter4j stores them in <code>twitter4j.properties</code>. Use this routine
 * to obtain them; then add the appropriate lines in the properties file.
 * Documentation for <code>twitter4j.properties</code> is available <a
 * href="http://twitter4j.org/en/configuration.html">here</a>.
 * <p>
 * Adapted from Yusuke Yamamoto, <a
 * href="http://twitter4j.org/en/code-examples.html"><code>OAuth</code>
 * support</a>, April 18, 2011.
 * <p>
 * Contest version.
 */
public class GetAccessTokens
{

	public static void main(String args[]) throws Exception
	{
		// The factory instance is re-usable and thread safe.
		Twitter twitter = new TwitterFactory().getInstance();
		// Assumes consumer key and consumer secret are already stored in
		// properties file.
		// twitter.setOAuthConsumer(Constants.CONSUMER_KEY,
		// Constants.CONSUMER_SECRET);
		RequestToken requestToken = twitter.getOAuthRequestToken();
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken)
		{
			System.out.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
			String pin = br.readLine();
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
		// Print out the access tokens for future reference.
		System.out.println("Access tokens for " + twitter.verifyCredentials().getId());
		System.out.println("oauth.accessToken=" + accessToken.getToken());
		System.out.println("oauth.accessTokenSecret=" + accessToken.getTokenSecret());
	}
}
