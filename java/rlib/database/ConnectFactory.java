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
 *  com.jolbox.bonecp.BoneCPConfig
 */
package rlib.database;

import java.sql.Connection;
import java.sql.SQLException;

import com.jolbox.bonecp.BoneCPConfig;

public abstract class ConnectFactory
{
	public static ConnectFactory newBoneCPConnectFactory(BoneCPConfig config, String driver)
	{
		try
		{
			BoneCPConnectFactory connects = new BoneCPConnectFactory();
			connects.init(config, driver);
			return connects;
		}
		catch (SQLException e)
		{
			throw new IllegalArgumentException(e);
		}
	}
	
	public abstract Connection getConnection() throws SQLException;
}
