/*
 * This file is part of TJServer.
 * 
 * TJServer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * TJServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Could not load the following classes:
 *  com.jolbox.bonecp.BoneCP
 *  com.jolbox.bonecp.BoneCPConfig
 */
package rlib.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import rlib.logging.Logger;
import rlib.logging.Loggers;

public final class DataBaseFactory
{
	private static final Logger log = Loggers.getLogger("DataBaseFactory");
	private static BoneCP source;
	
	public static void closeConnection(Connection connection)
	{
		if (connection == null)
		{
			return;
		}
		try
		{
			connection.close();
		}
		catch (SQLException e)
		{
			log.warning(e);
		}
	}
	
	public static void closeDatabaseCS(Connection connection, Statement statement)
	{
		DataBaseFactory.closeStatement(statement);
		DataBaseFactory.closeConnection(connection);
	}
	
	public static void closeDatabaseCSR(Connection connection, Statement statement, ResultSet rset)
	{
		DataBaseFactory.closeResultSet(rset);
		DataBaseFactory.closeStatement(statement);
		DataBaseFactory.closeConnection(connection);
	}
	
	public static void closeDatabaseSR(Statement statement, ResultSet rset)
	{
		DataBaseFactory.closeResultSet(rset);
		DataBaseFactory.closeStatement(statement);
	}
	
	public static void closeResultSet(ResultSet rset)
	{
		if (rset == null)
		{
			return;
		}
		try
		{
			rset.close();
		}
		catch (SQLException e)
		{
			log.warning(e);
		}
	}
	
	public static void closeStatement(Statement statement)
	{
		if (statement == null)
		{
			return;
		}
		try
		{
			statement.close();
		}
		catch (SQLException e)
		{
			log.warning(e);
		}
	}
	
	public static int generateKey()
	{
		return Thread.currentThread().hashCode();
	}
	
	public static Connection getConnection() throws SQLException
	{
		return source.getConnection();
	}
	
	public static synchronized void start(BoneCPConfig config, String driver) throws SQLException
	{
		try
		{
			Class.forName(driver).newInstance();
			source = new BoneCP(config);
			source.getConnection().close();
		}
		catch (Exception e)
		{
			throw new SQLException("could not init DB connection:" + e);
		}
	}
	
	public static synchronized void stop()
	{
		source.close();
		source.shutdown();
	}
}
