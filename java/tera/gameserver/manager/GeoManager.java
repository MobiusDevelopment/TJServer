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
package tera.gameserver.manager;

import java.io.File;

import tera.Config;

import rlib.geoengine.GeoConfig;
import rlib.geoengine.GeoMap;
import rlib.geoengine.GeoMap3D;
import rlib.geoengine.GeoQuard;
import rlib.logging.ByteGameLogger;
import rlib.logging.GameLoggers;
import rlib.logging.Logger;
import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public final class GeoManager
{
	private static final Logger log = Loggers.getLogger(GeoManager.class);
	private static final ByteGameLogger geoLog = GameLoggers.getByteLogger("GeoManager");
	private static GeoManager instance;
	
	/**
	 * Method getInstance.
	 * @return GeoManager
	 */
	public static GeoManager getInstance()
	{
		if (instance == null)
		{
			instance = new GeoManager();
		}
		
		return instance;
	}
	
	/**
	 * Method write.
	 * @param continentId int
	 * @param x float
	 * @param y float
	 * @param z float
	 */
	public static void write(int continentId, float x, float y, float z)
	{
		if (!Config.DEVELOPER_GEO_LOGING)
		{
			return;
		}
		
		geoLog.lock();
		
		try
		{
			geoLog.writeByte(0);
			geoLog.writeByte(continentId);
			geoLog.writeFloat(x);
			geoLog.writeFloat(y);
			geoLog.writeFloat(z);
		}
		
		finally
		{
			geoLog.unlock();
		}
	}
	
	private final GeoMap[] geodata;
	
	private GeoManager()
	{
		geodata = new GeoMap[Config.WORLD_CONTINENT_COUNT];
		
		for (int i = 0, length = geodata.length; i < length; i++)
		{
			final GeoMap3D geoImpl = new GeoMap3D(Config.GEO_CONFIG);
			geodata[i] = geoImpl;
			final File file = new File(Config.SERVER_DIR + "/data/geodata_" + (i + 1) + ".dat");
			
			if (!file.exists())
			{
				continue;
			}
			
			geoImpl.importTo(file);
		}
		
		log.info("initialized.");
	}
	
	/**
	 * Method getHeight.
	 * @param continentId int
	 * @param x float
	 * @param y float
	 * @param z float
	 * @return float
	 */
	public float getHeight(int continentId, float x, float y, float z)
	{
		final GeoMap geo = geodata[continentId];
		final GeoQuard quard = geo.getGeoQuard(x, y, z);
		return quard == null ? z : quard.getHeight();
	}
	
	/**
	 * Method getQuards.
	 * @param continentId int
	 * @param x float
	 * @param y float
	 * @return GeoQuard[]
	 */
	public GeoQuard[] getQuards(int continentId, float x, float y)
	{
		final GeoMap geo = geodata[continentId];
		final GeoConfig config = Config.GEO_CONFIG;
		final int i = (int) ((x / config.getQuardSize()) + config.getOffsetX());
		final int j = (int) ((y / config.getQuardSize()) + config.getOffsetY());
		return ((GeoMap3D) geo).getQuards(i, j);
	}
}