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
package tera.gameserver.model.npc.spawn;

import org.w3c.dom.Node;

import tera.gameserver.events.global.regionwars.Region;
import tera.gameserver.events.global.regionwars.RegionWarNpc;
import tera.gameserver.model.Guild;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAIClass;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.templates.NpcTemplate;
import tera.util.Location;

import rlib.geom.Coords;
import rlib.util.Rnd;
import rlib.util.Strings;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class RegionWarSpawn extends NpcSpawn
{
	
	private Guild owner;
	
	private Region region;
	
	private final String name;
	
	private final String chatLoc;
	
	/**
	 * Constructor for RegionWarSpawn.
	 * @param node Node
	 * @param vars VarTable
	 * @param template NpcTemplate
	 * @param location Location
	 * @param respawn int
	 * @param random int
	 * @param minRadius int
	 * @param maxRadius int
	 * @param config ConfigAI
	 * @param aiClass NpcAIClass
	 */
	public RegionWarSpawn(Node node, VarTable vars, NpcTemplate template, Location location, int respawn, int random, int minRadius, int maxRadius, ConfigAI config, NpcAIClass aiClass)
	{
		super(node, vars, template, location, respawn, random, minRadius, maxRadius, config, aiClass);
		name = vars.getString("name", Strings.EMPTY);
		final String regionId = vars.getString("regionId", Strings.EMPTY);
		chatLoc = getPointMessage(name, regionId, location.getX(), location.getY(), location.getZ());
	}
	
	/**
	 * Method setOwner.
	 * @param owner Guild
	 */
	public void setOwner(Guild owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Method setRegion.
	 * @param region Region
	 */
	public void setRegion(Region region)
	{
		this.region = region;
	}
	
	/**
	 * Method getOwner.
	 * @return Guild
	 */
	public Guild getOwner()
	{
		return owner;
	}
	
	/**
	 * Method getRegion.
	 * @return Region
	 */
	public Region getRegion()
	{
		return region;
	}
	
	@Override
	public synchronized void doSpawn()
	{
		if (isStoped())
		{
			return;
		}
		
		if (spawned != null)
		{
			log.warning(this, new Exception("found duplicate spawn"));
			return;
		}
		
		if (schedule != null)
		{
			schedule.cancel(false);
			schedule = null;
		}
		
		Npc newNpc = getDead();
		RegionWarNpc regionNpc = (RegionWarNpc) newNpc;
		final Location location = getLocation();
		
		if (newNpc == null)
		{
			newNpc = template.newInstance();
			regionNpc = (RegionWarNpc) newNpc;
			newNpc.setSpawn(this);
			newNpc.setAi(aiClass.newInstance(newNpc, config));
			regionNpc.setGuildOwner(owner);
			regionNpc.setRegion(region);
			Location spawnLoc = null;
			
			if (maxRadius > 0)
			{
				spawnLoc = Coords.randomCoords(new Location(), location.getX(), location.getY(), location.getZ(), location.getHeading() == -1 ? Rnd.nextInt(35000) : location.getHeading(), minRadius, maxRadius);
			}
			else
			{
				spawnLoc = new Location(location.getX(), location.getY(), location.getZ(), location.getHeading() == -1 ? Rnd.nextInt(0, 65000) : location.getHeading());
			}
			
			spawnLoc.setContinentId(location.getContinentId());
			newNpc.spawnMe(spawnLoc);
		}
		else
		{
			setDead(null);
			newNpc.reinit();
			regionNpc.setGuildOwner(owner);
			regionNpc.setRegion(region);
			Location spawnLoc = null;
			
			if (maxRadius > 0)
			{
				spawnLoc = Coords.randomCoords(newNpc.getSpawnLoc(), location.getX(), location.getY(), location.getZ(), location.getHeading() == -1 ? Rnd.nextInt(35000) : location.getHeading(), minRadius, maxRadius);
			}
			else
			{
				spawnLoc = newNpc.getSpawnLoc();
			}
			
			spawnLoc.setContinentId(location.getContinentId());
			newNpc.spawnMe(spawnLoc);
		}
		
		setSpawned(newNpc);
	}
	
	/**
	 * Method getName.
	 * @return String
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Method getPointMessage.
	 * @param name String
	 * @param regionId String
	 * @param x float
	 * @param y float
	 * @param z float
	 * @return String
	 */
	private String getPointMessage(String name, String regionId, float x, float y, float z)
	{
		final StringBuilder builder = new StringBuilder("<FONT FACE=\"$ChatFont\" SIZE=\"18\" COLOR=\"#FF0000\" KERNING=\"0\"> <A HREF=\"asfunction:chatLinkAction,3#####");
		builder.append(regionId);
		builder.append('@').append(x).append(',').append(y).append(',').append(z);
		builder.append("\">&lt;");
		builder.append(name);
		builder.append("&gt;</A></FONT>");
		return builder.toString();
	}
	
	/**
	 * Method getChatLoc.
	 * @return String
	 */
	public String getChatLoc()
	{
		return chatLoc;
	}
}
