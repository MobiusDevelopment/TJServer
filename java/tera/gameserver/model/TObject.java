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
package tera.gameserver.model;

import tera.Config;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.ai.AI;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.resourse.ResourseInstance;
import tera.gameserver.model.territory.Territory;
import tera.gameserver.model.traps.Trap;
import tera.gameserver.network.serverpackets.DeleteCharacter;
import tera.gameserver.tables.ItemTable;
import tera.util.Location;

import rlib.gamemodel.GameObject;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 05.03.2012
 */
public abstract class TObject implements GameObject
{
	protected static final Logger log = Loggers.getLogger(TObject.class);
	protected static final float HEADINGS_IN_PI = 10430.378350470452724949566316381F;
	public static final TObject[] EMTY_OBJECTS = new TObject[0];
	public static final Array<TObject> EMPTY_ARRAY = Arrays.toArray(TObject.class, 0);
	protected int objectId;
	protected int heading;
	protected int continentId;
	protected float x;
	protected float y;
	protected float z;
	protected volatile boolean visible;
	protected volatile boolean deleted;
	protected WorldRegion currentRegion;
	
	/**
	 * Constructor for TObject.
	 * @param objectId int
	 */
	public TObject(int objectId)
	{
		this.objectId = objectId;
	}
	
	/**
	 * Method addMe.
	 * @param player Player
	 */
	public void addMe(Player player)
	{
	}
	
	/**
	 * Method compareTo.
	 * @param object GameObject
	 * @return int
	 */
	@Override
	public final int compareTo(GameObject object)
	{
		return objectId - object.getObjectId();
	}
	
	/**
	 * Method decayMe.
	 * @param type int
	 */
	public void decayMe(int type)
	{
		synchronized (this)
		{
			if (!isVisible())
			{
				return;
			}
			
			setVisible(false);
		}
		World.removeVisibleObject(this, type);
	}
	
	public void deleteMe()
	{
		if (isDeleted())
		{
			return;
		}
		
		deleteMe(DeleteCharacter.DISAPPEARS);
		setDeleted(true);
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyDelete(this);
	}
	
	/**
	 * Method deleteMe.
	 * @param type int
	 */
	public void deleteMe(int type)
	{
		decayMe(type);
	}
	
	/**
	 * Method getAI.
	 * @return AI
	 */
	public AI getAI()
	{
		return null;
	}
	
	/**
	 * Method getCharacter.
	 * @return Character
	 */
	public Character getCharacter()
	{
		return null;
	}
	
	/**
	 * Method getContinentId.
	 * @return int
	 */
	public final int getContinentId()
	{
		return continentId;
	}
	
	/**
	 * Method getCurrentRegion.
	 * @return WorldRegion
	 */
	public final WorldRegion getCurrentRegion()
	{
		return currentRegion;
	}
	
	/**
	 * Method getDistance.
	 * @param targetX float
	 * @param targetY float
	 * @return float
	 */
	public final float getDistance(float targetX, float targetY)
	{
		final float dx = targetX - x;
		final float dy = targetY - y;
		return (float) Math.sqrt((dx * dx) + (dy * dy));
	}
	
	/**
	 * Method getDistance.
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return float
	 */
	public final float getDistance(float targetX, float targetY, float targetZ)
	{
		final float dx = targetX - x;
		final float dy = targetY - y;
		final float dz = targetZ - z;
		return (float) Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
	}
	
	/**
	 * Method getDistance.
	 * @param object TObject
	 * @return float
	 */
	public final float getDistance(TObject object)
	{
		if (object == null)
		{
			return 0;
		}
		
		final float dx = object.x - x;
		final float dy = object.y - y;
		return (float) Math.sqrt((dx * dx) + (dy * dy));
	}
	
	/**
	 * Method getDistance3D.
	 * @param object TObject
	 * @return float
	 */
	public final float getDistance3D(TObject object)
	{
		if (object == null)
		{
			return 0;
		}
		
		final float dx = object.x - x;
		final float dy = object.y - y;
		final float dz = object.z - z;
		return (float) Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
	}
	
	/**
	 * Method getGeomDistance.
	 * @param targetX float
	 * @param targetY float
	 * @return float
	 */
	public float getGeomDistance(float targetX, float targetY)
	{
		final float dx = targetX - x;
		final float dy = targetY - y;
		return (float) Math.sqrt((dx * dx) + (dy * dy));
	}
	
	/**
	 * Method getHeading.
	 * @return int
	 * @see rlib.gamemodel.GameObject#getHeading()
	 */
	@Override
	public final int getHeading()
	{
		if (heading > 65536)
		{
			heading -= 65536;
		}
		
		return heading;
	}
	
	/**
	 * Method getHeadingTo.
	 * @param targetX float
	 * @param targetY float
	 * @return int
	 */
	public final int getHeadingTo(float targetX, float targetY)
	{
		final float dx = targetX - x;
		final float dy = targetY - y;
		int heading = (int) ((Math.atan2(-dy, -dx) * HEADINGS_IN_PI) + 32768);
		heading = getHeading() - heading;
		
		if (heading < 0)
		{
			heading = (heading + 1 + Integer.MAX_VALUE) & 0xFFFF;
		}
		else if (heading > 0xFFFF)
		{
			heading &= 0xFFFF;
		}
		
		return heading;
	}
	
	/**
	 * Method getHeadingTo.
	 * @param target TObject
	 * @param toChar boolean
	 * @return int
	 */
	public final int getHeadingTo(TObject target, boolean toChar)
	{
		if ((target == null) || (target == this))
		{
			return -1;
		}
		
		final float dx = target.x - x;
		final float dy = target.y - y;
		int heading = (int) ((Math.atan2(-dy, -dx) * HEADINGS_IN_PI) + 32768);
		heading = toChar ? target.getHeading() - heading : getHeading() - heading;
		
		if (heading < 0)
		{
			heading = (heading + 1 + Integer.MAX_VALUE) & 0xFFFF;
		}
		else if (heading > 0xFFFF)
		{
			heading &= 0xFFFF;
		}
		
		return heading;
	}
	
	/**
	 * Method getItem.
	 * @return ItemInstance
	 */
	public ItemInstance getItem()
	{
		return null;
	}
	
	/**
	 * Method getLoc.
	 * @return Location
	 */
	public final Location getLoc()
	{
		return getLoc(new Location(x, y, z, heading, continentId));
	}
	
	/**
	 * Method getLoc.
	 * @param loc Location
	 * @return Location
	 */
	public final Location getLoc(Location loc)
	{
		return loc.setXYZH(x, y, z, heading);
	}
	
	/**
	 * Method getName.
	 * @return String
	 */
	public String getName()
	{
		return getClass().getSimpleName() + ":" + objectId;
	}
	
	/**
	 * Method getNpc.
	 * @return Npc
	 */
	public Npc getNpc()
	{
		return null;
	}
	
	/**
	 * Method getObjectId.
	 * @return int
	 * @see rlib.gamemodel.GameObject#getObjectId()
	 */
	@Override
	public final int getObjectId()
	{
		return objectId;
	}
	
	/**
	 * Method getPlayer.
	 * @return Player
	 */
	public Player getPlayer()
	{
		return null;
	}
	
	/**
	 * Method getResourse.
	 * @return ResourseInstance
	 */
	public ResourseInstance getResourse()
	{
		return null;
	}
	
	/**
	 * Method getSquareDistance.
	 * @param targetX float
	 * @param targetY float
	 * @return float
	 */
	public final float getSquareDistance(float targetX, float targetY)
	{
		final float dx = targetX - x;
		final float dy = targetY - y;
		return (dx * dx) + (dy * dy);
	}
	
	/**
	 * Method getSquareDistance.
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return float
	 */
	public final float getSquareDistance(float targetX, float targetY, float targetZ)
	{
		final float dx = targetX - x;
		final float dy = targetY - y;
		final float dz = targetZ - z;
		return (dx * dx) + (dy * dy) + (dz * dz);
	}
	
	/**
	 * Method getSubId.
	 * @return int
	 */
	public int getSubId()
	{
		return 0;
	}
	
	/**
	 * Method getTemplateId.
	 * @return int
	 */
	public int getTemplateId()
	{
		return -1;
	}
	
	/**
	 * Method getTemplateType.
	 * @return int
	 */
	public int getTemplateType()
	{
		return -1;
	}
	
	/**
	 * Method getTerritories.
	 * @return Array<Territory>
	 */
	public Array<Territory> getTerritories()
	{
		return null;
	}
	
	/**
	 * Method getTrap.
	 * @return Trap
	 */
	public Trap getTrap()
	{
		return null;
	}
	
	/**
	 * Method getX.
	 * @return float
	 * @see rlib.gamemodel.GameObject#getX()
	 */
	@Override
	public final float getX()
	{
		return x;
	}
	
	/**
	 * Method getY.
	 * @return float
	 * @see rlib.gamemodel.GameObject#getY()
	 */
	@Override
	public final float getY()
	{
		return y;
	}
	
	/**
	 * Method getZ.
	 * @return float
	 * @see rlib.gamemodel.GameObject#getZ()
	 */
	@Override
	public final float getZ()
	{
		return z;
	}
	
	/**
	 * Method hasAI.
	 * @return boolean
	 */
	public boolean hasAI()
	{
		return false;
	}
	
	/**
	 * Method hashCode.
	 * @return int
	 */
	@Override
	public final int hashCode()
	{
		return objectId;
	}
	
	/**
	 * Method isCharacter.
	 * @return boolean
	 */
	public boolean isCharacter()
	{
		return false;
	}
	
	/**
	 * Method isDeleted.
	 * @return boolean
	 */
	public final boolean isDeleted()
	{
		return deleted;
	}
	
	/**
	 * Method isHit.
	 * @param startX float
	 * @param startY float
	 * @param startZ float
	 * @param endX float
	 * @param endY float
	 * @param endZ float
	 * @param radius float
	 * @return boolean
	 */
	public boolean isHit(float startX, float startY, float startZ, float endX, float endY, float endZ, float radius)
	{
		return isHit(startX, startY, startZ, endX, endY, endZ, radius, true);
	}
	
	/**
	 * Method isHit.
	 * @param startX float
	 * @param startY float
	 * @param startZ float
	 * @param endX float
	 * @param endY float
	 * @param endZ float
	 * @param radius float
	 * @param checkHeight boolean
	 * @return boolean
	 */
	public boolean isHit(float startX, float startY, float startZ, float endX, float endY, float endZ, float radius, boolean checkHeight)
	{
		return false;
	}
	
	/**
	 * Method isInRange.
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param range float
	 * @return boolean
	 */
	public boolean isInRange(float targetX, float targetY, float targetZ, float range)
	{
		return getSquareDistance(targetX, targetY, targetZ) <= (range * range);
	}
	
	/**
	 * Method isInRange.
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param range int
	 * @return boolean
	 */
	public boolean isInRange(float targetX, float targetY, float targetZ, int range)
	{
		return getSquareDistance(targetX, targetY, targetZ) <= (range * range);
	}
	
	/**
	 * Method isInRange.
	 * @param targetX float
	 * @param targetY float
	 * @param range int
	 * @return boolean
	 */
	public final boolean isInRange(float targetX, float targetY, int range)
	{
		return getSquareDistance(targetX, targetY) <= (range * range);
	}
	
	/**
	 * Method isInRange.
	 * @param location Location
	 * @param range int
	 * @return boolean
	 */
	public final boolean isInRange(Location location, int range)
	{
		return getSquareDistance(location.getX(), location.getY()) <= (range * range);
	}
	
	/**
	 * Method isInRange.
	 * @param object TObject
	 * @param range int
	 * @return boolean
	 */
	public final boolean isInRange(TObject object, int range)
	{
		if (object == null)
		{
			return false;
		}
		
		final float dx = Math.abs(object.x - x);
		
		if (dx > range)
		{
			return false;
		}
		
		final float dy = Math.abs(object.y - y);
		
		if (dy > range)
		{
			return false;
		}
		
		final float dz = Math.abs(object.z - z);
		return (dz <= 1500) && (((dx * dx) + (dy * dy)) <= (range * range));
	}
	
	/**
	 * Method isInRangeZ.
	 * @param location Location
	 * @param range int
	 * @return boolean
	 */
	public final boolean isInRangeZ(Location location, int range)
	{
		return getSquareDistance(location.getX(), location.getY(), location.getZ()) <= (range * range);
	}
	
	/**
	 * Method isInRangeZ.
	 * @param object TObject
	 * @param range int
	 * @return boolean
	 */
	public final boolean isInRangeZ(TObject object, int range)
	{
		if (object == null)
		{
			return false;
		}
		
		final float dx = Math.abs(object.x - x);
		
		if (dx > range)
		{
			return false;
		}
		
		final float dy = Math.abs(object.y - y);
		
		if (dy > range)
		{
			return false;
		}
		
		final float dz = Math.abs(object.z - z);
		return (dz <= range) && (((dx * dx) + (dy * dy) + (dz * dz)) <= (range * range));
	}
	
	/**
	 * Method isInvisible.
	 * @return boolean
	 */
	public final boolean isInvisible()
	{
		return !visible;
	}
	
	/**
	 * Method isInWorld.
	 * @return boolean
	 */
	public final boolean isInWorld()
	{
		return currentRegion != null;
	}
	
	/**
	 * Method isItem.
	 * @return boolean
	 */
	public boolean isItem()
	{
		return false;
	}
	
	/**
	 * Method isNpc.
	 * @return boolean
	 */
	public boolean isNpc()
	{
		return false;
	}
	
	/**
	 * Method isPlayer.
	 * @return boolean
	 */
	public boolean isPlayer()
	{
		return false;
	}
	
	/**
	 * Method isResourse.
	 * @return boolean
	 */
	public boolean isResourse()
	{
		return false;
	}
	
	/**
	 * Method isSummon.
	 * @return boolean
	 */
	public boolean isSummon()
	{
		return false;
	}
	
	/**
	 * Method isTrap.
	 * @return boolean
	 */
	public boolean isTrap()
	{
		return false;
	}
	
	/**
	 * Method isVisible.
	 * @return boolean
	 * @see rlib.gamemodel.GameObject#isVisible()
	 */
	@Override
	public final boolean isVisible()
	{
		return visible;
	}
	
	/**
	 * Method isWorldObject.
	 * @return boolean
	 */
	public boolean isWorldObject()
	{
		return false;
	}
	
	/**
	 * Method pickUpMe.
	 * @param target TObject
	 * @return boolean
	 */
	public boolean pickUpMe(TObject target)
	{
		return false;
	}
	
	/**
	 * Method removeMe.
	 * @param player Player
	 * @param type int
	 */
	public void removeMe(Player player, int type)
	{
	}
	
	/**
	 * Method setContinentId.
	 * @param continentId int
	 */
	public final void setContinentId(int continentId)
	{
		this.continentId = continentId;
	}
	
	/**
	 * Method setCurrentRegion.
	 * @param region WorldRegion
	 */
	public final void setCurrentRegion(WorldRegion region)
	{
		currentRegion = region;
	}
	
	/**
	 * Method setDeleted.
	 * @param deleted boolean
	 */
	protected final void setDeleted(boolean deleted)
	{
		this.deleted = deleted;
	}
	
	/**
	 * Method setHeading.
	 * @param heading int
	 */
	public final void setHeading(int heading)
	{
		this.heading = heading;
	}
	
	/**
	 * Method setLoc.
	 * @param location Location
	 */
	public void setLoc(Location location)
	{
		setContinentId(location.getContinentId());
		setXYZ(location.getX(), location.getY(), location.getZ());
	}
	
	/**
	 * Method setObjectId.
	 * @param objectId int
	 */
	public void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}
	
	/**
	 * Method setTerritories.
	 * @param territories Array<Territory>
	 */
	public void setTerritories(Array<Territory> territories)
	{
		log.warning(this, new Exception("unsupported method"));
	}
	
	/**
	 * Method setVisible.
	 * @param visible boolean
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	/**
	 * Method setX.
	 * @param x float
	 */
	public void setX(float x)
	{
		this.x = x;
	}
	
	/**
	 * Method setXYZ.
	 * @param x float
	 * @param y float
	 * @param z float
	 */
	public void setXYZ(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		
		if ((isSummon() || isPlayer()) && Config.DEVELOPER_DEBUG_MOVING_PLAYER)
		{
			ItemTable.createItem(125, 1L).spawnMe(getLoc());
		}
		else if (isNpc() && Config.DEVELOPER_DEBUG_MOVING_NPC)
		{
			ItemTable.createItem(125, 1L).spawnMe(getLoc());
		}
		
		World.addVisibleObject(this);
	}
	
	/**
	 * Method setXYZInvisible.
	 * @param x float
	 * @param y float
	 * @param z float
	 */
	public void setXYZInvisible(float x, float y, float z)
	{
		if (x > World.MAP_MAX_X)
		{
			x = World.MAP_MAX_X;
		}
		
		if (x < World.MAP_MIN_X)
		{
			x = World.MAP_MIN_X;
		}
		
		if (y > World.MAP_MAX_Y)
		{
			y = World.MAP_MAX_Y;
		}
		
		if (y < World.MAP_MIN_Y)
		{
			y = World.MAP_MIN_Y;
		}
		
		if (z < World.MAP_MIN_Z)
		{
			z = World.MAP_MIN_Z;
		}
		
		if (z > World.MAP_MAX_Z)
		{
			z = World.MAP_MAX_Z;
		}
		
		this.x = x;
		this.y = y;
		this.z = z;
		setVisible(false);
	}
	
	/**
	 * Method setY.
	 * @param y float
	 */
	public void setY(float y)
	{
		this.y = y;
	}
	
	/**
	 * Method setZ.
	 * @param z float
	 */
	public void setZ(float z)
	{
		this.z = z;
	}
	
	public void spawnMe()
	{
		synchronized (this)
		{
			setDeleted(false);
			setVisible(true);
			World.addVisibleObject(this);
		}
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifySpawn(this);
	}
	
	/**
	 * Method spawnMe.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param heading int
	 * @see rlib.gamemodel.GameObject#spawnMe(float, float, float, int)
	 */
	@Override
	public void spawnMe(float x, float y, float z, int heading)
	{
		if (x > World.MAP_MAX_X)
		{
			x = World.MAP_MAX_X - 5000;
		}
		
		if (x < World.MAP_MIN_X)
		{
			x = World.MAP_MIN_X + 5000;
		}
		
		if (y > World.MAP_MAX_Y)
		{
			y = World.MAP_MAX_Y - 5000;
		}
		
		if (y < World.MAP_MIN_Y)
		{
			y = World.MAP_MIN_Y + 5000;
		}
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		if (heading > 0)
		{
			setHeading(heading);
		}
		
		spawnMe();
	}
	
	/**
	 * Method spawnMe.
	 * @param location Location
	 */
	public void spawnMe(Location location)
	{
		setContinentId(location.getContinentId());
		spawnMe(location.getX(), location.getY(), location.getZ(), location.getHeading());
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " objectId = " + objectId + ", x = " + x + ", y = " + y + ", z = " + z + ", visible = " + visible + ", heading = " + heading + ", currentRegion = " + currentRegion;
	}
	
	public void updateTerritories()
	{
		log.warning(this, new Exception("unsupported method"));
	}
	
	public void updateZoneId()
	{
	}
}