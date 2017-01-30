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
 */
package rlib.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

public abstract class CleaningManager
{
	private static final Logger log = Loggers.getLogger(CleaningManager.class);
	public static final Array<CleaningQuery> query = Arrays.toArray(CleaningQuery.class);
	
	public static void addQuery(String name, String squery)
	{
		query.add(new CleaningQuery(name, squery));
	}
	
	public static void cleaning(ConnectFactory connects)
	{
		Connection con = null;
		Statement statement = null;
		try
		{
			try
			{
				con = connects.getConnection();
				statement = con.createStatement();
				for (CleaningQuery clean : query)
				{
					log.info(String.valueOf(clean.getName().replace("{count}", String.valueOf(statement.executeUpdate(clean.getQuery())))) + ".");
				}
			}
			catch (SQLException e)
			{
				log.warning(e);
				DBUtils.closeDatabaseCS(con, statement);
			}
		}
		finally
		{
			DBUtils.closeDatabaseCS(con, statement);
		}
	}
}
