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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 * Database implementation for the TradeKing enhanced watchlist application.
 * This database currently handles only a single watchlist, named DEFAULT.
 * <p>
 * The Microsoft Java Database Connectivity jar must be on the class path. This
 * class was developed and tested with SQL JDBC version 1.2. There is a <A
 * HREF="http://tinyurl.com/3sewc36">later version</A> (3.0) available.
 * <p>
 * Creates the empty database from the <code>main()</code>. This only needs to
 * be run once (or when an empty database needs to be re-created.
 * <p>
 * There is no JUnit test for this class, inasmuch as all the functionality will
 * be tested in other routines.
 * <p>
 * Contest version.
 */
final class Database
{
	/*
	 * Database properties are set from the file
	 * <code>database.properties</code>.
	 */
	private static Properties properties = new Properties();

	/*
	 * The name of the SQL Server watchlist database.
	 */
	private static String databaseName = new String();

	/*
	 * The name of the SQL Server database Java database connectivity driver.
	 */
	private static String databaseDriver = new String();

	/*
	 * The JDBC driver for SQL Server connectivity to the master database.
	 */
	private static String databaseMaster = new String();

	/*
	 * The user ID for database access.
	 */
	private static String uid = new String();

	/*
	 * The password for database access.
	 */
	private static String password = new String();

	/*
	 * Initialize database properties.
	 */
	static
	{
		properties = new Properties();
		FileInputStream in;
		try
		{
			in = new FileInputStream("./database.properties");
			properties.load(in);
			in.close();
			databaseName = properties.getProperty("DATABASE_NAME");
			databaseDriver = properties.getProperty("DATABASE_DRIVER");
			databaseMaster = properties.getProperty("DATABASE_MASTER");
			uid = properties.getProperty("UID");
			password = properties.getProperty("PASSWORD");
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
	 * The common <code>Connection</code> instance.
	 */
	public static Connection watchlistConnection = null;

	/**
	 * The common <code>Statement</code> instance.
	 */
	public static Statement watchlistStatement = null;

	/**
	 * Releases database objects.
	 */
	public static final void cleanUp()
	{
		try
		{
			if (watchlistStatement != null)
				watchlistStatement.close();
			if (watchlistConnection != null)
				watchlistConnection.close();
		}
		catch (SQLException e)
		{
			TradekingException.handleException(e);
		}

	}

	/**
	 * Initializes the connection to the duty database.
	 * 
	 * @throws ClassNotFoundException
	 * 
	 * @throws SQLException
	 */
	public static final void connectToDatabase() throws ClassNotFoundException, SQLException
	{
		Class.forName(databaseDriver);
		watchlistConnection = DriverManager.getConnection(databaseName, uid, password);
		watchlistStatement = watchlistConnection.createStatement();
	}

	/**
	 * Creates an empty database.
	 */
	public static final void initializeDatabase()
	{
		final int response = JOptionPane.showConfirmDialog(null,
				"Really initialize watchlist database (any existing data will be lost)?");
		if (!(response == JOptionPane.YES_OPTION))
			return;
		try
		{
			// TODO This is SQL Server specific code. Generalize for other
			// database options.
			String sql = null;
			Class.forName(databaseDriver);
			// See if there's even a watchlist database. If there is, drop it;
			// recreate it.
			Connection masterConnection = DriverManager.getConnection(databaseMaster, uid, password);
			final Statement masterStatement = masterConnection.createStatement();
			masterStatement.execute("if db_id('watchlist') is not null drop database watchlist");
			masterStatement.execute("create database watchlist");
			masterStatement.close();
			masterConnection.close();
			watchlistConnection = DriverManager.getConnection(databaseName, uid, password);
			watchlistStatement = watchlistConnection.createStatement();

			// Database creation DDL begins here.
			sql = "CREATE TABLE Watchlist(costBasis real, instrument varchar(" + Constants.MAX_INSTRUMENT_LENGTH
					+ ") not null, notation varchar(" + Constants.MAX_NOTATION_LENGTH + "), quantity real)";
			watchlistStatement.execute(sql);
			sql = "ALTER TABLE Watchlist ADD CONSTRAINT PK_WATCHLIST PRIMARY KEY(instrument)";
			watchlistStatement.execute(sql);
			// End database creation DDL.

			// OK to close if running main.
			cleanUp();
			Utilities.informationMessage("Database initialized.");
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
		}
		catch (final ClassNotFoundException e)
		{
			e.printStackTrace();
			TradekingException.handleException(e);
		}
	}

	/**
	 * Standard program entry point, in case we want to initialize the database
	 * from the command line or from some other routine. This drops the old
	 * database and re-creates the empty structure.
	 * 
	 * @param args
	 *            Not used.
	 */
	public static void main(String[] args)
	{
		initializeDatabase();
	}

	/**
	 * Sets up a database connection and statement for test purposes.
	 */
	public static final void setUpForTesting()
	{
		if (Database.watchlistConnection == null)
		{
			try
			{
				Database.connectToDatabase();
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
}
