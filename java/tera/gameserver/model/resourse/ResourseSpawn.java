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
package tera.gameserver.model.resourse;

import java.util.concurrent.ScheduledFuture;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.templates.ResourseTemplate;
import tera.util.Location;

import rlib.geom.Coords;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Rnd;
import rlib.util.SafeTask;

/**
 * @author Ronn
 */
public final class ResourseSpawn extends SafeTask
{
	protected static final Logger log = Loggers.getLogger(ResourseSpawn.class);
	private ResourseTemplate template;
	private Location loc;
	private ResourseInstance spawned;
	private ResourseInstance waited;
	private ScheduledFuture<ResourseSpawn> schedule;
	private int respawn;
	private int randomRespawn;
	private int minRadius;
	private int maxRadius;
	private boolean stoped;
	
	/**
	 * Constructor for ResourseSpawn.
	 * @param template ResourseTemplate
	 * @param loc Location
	 * @param respawn int
	 * @param randomRespawn int
	 * @param minRadius int
	 * @param maxRadius int
	 */
	public ResourseSpawn(ResourseTemplate template, Location loc, int respawn, int randomRespawn, int minRadius, int maxRadius)
	{
		this.template = template;
		this.loc = loc;
		this.respawn = respawn;
		this.randomRespawn = randomRespawn;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		stoped = true;
	}
	
	protected synchronized void doRespawn()
	{
		if (isStoped())
		{
			return;
		}
		
		if (schedule != null)
		{
			log.warning(this, new Exception("found duplicate respawn"));
			return;
		}
		
		int delay = respawn;
		
		if (randomRespawn > 0)
		{
			delay += Rnd.nextInt(0, randomRespawn);
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, delay * 1000);
	}
	
	protected synchronized void doSpawn()
	{
		if (isStoped())
		{
			return;
		}
		
		schedule = null;
		ResourseInstance resourse = getWaited();
		
		if (resourse == null)
		{
			resourse = template.newInstance();
		}
		
		setSpawned(resourse);
		setWaited(null);
		resourse.setSpawn(this);
		
		if (maxRadius > 0)
		{
			resourse.spawnMe(Coords.randomCoords(new Location(), loc.getX(), loc.getY(), loc.getZ(), minRadius, maxRadius));
		}
		else
		{
			resourse.spawnMe(loc);
		}
	}
	
	/**
	 * Method getLoc.
	 * @return Location
	 */
	public final Location getLoc()
	{
		return loc;
	}
	
	/**
	 * Method getMaxRadius.
	 * @return int
	 */
	protected final int getMaxRadius()
	{
		return maxRadius;
	}
	
	/**
	 * Method getMinRadius.
	 * @return int
	 */
	protected final int getMinRadius()
	{
		return minRadius;
	}
	
	/**
	 * Method getRandomRespawn.
	 * @return int
	 */
	protected final int getRandomRespawn()
	{
		return randomRespawn;
	}
	
	/**
	 * Method getRespawn.
	 * @return int
	 */
	protected final int getRespawn()
	{
		return respawn;
	}
	
	/**
	 * Method getSpawned.
	 * @return ResourseInstance
	 */
	protected final ResourseInstance getSpawned()
	{
		return spawned;
	}
	
	/**
	 * Method getTemplate.
	 * @return ResourseTemplate
	 */
	public final ResourseTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Method getTemplateId.
	 * @return int
	 */
	public int getTemplateId()
	{
		return template.getId();
	}
	
	/**
	 * Method getWaited.
	 * @return ResourseInstance
	 */
	protected final ResourseInstance getWaited()
	{
		return waited;
	}
	
	/**
	 * Method isStoped.
	 * @return boolean
	 */
	protected final boolean isStoped()
	{
		return stoped;
	}
	
	/**
	 * Method onCollected.
	 * @param resourse ResourseInstance
	 */
	public synchronized void onCollected(ResourseInstance resourse)
	{
		if (isStoped())
		{
			return;
		}
		
		setSpawned(null);
		setWaited(waited);
		doRespawn();
	}
	
	@Override
	protected void runImpl()
	{
		doSpawn();
	}
	
	/**
	 * Method setLoc.
	 * @param loc Location
	 */
	protected final void setLoc(Location loc)
	{
		this.loc = loc;
	}
	
	/**
	 * Method setMaxRadius.
	 * @param maxRadius int
	 */
	protected final void setMaxRadius(int maxRadius)
	{
		this.maxRadius = maxRadius;
	}
	
	/**
	 * Method setMinRadius.
	 * @param minRadius int
	 */
	protected final void setMinRadius(int minRadius)
	{
		this.minRadius = minRadius;
	}
	
	/**
	 * Method setRandomRespawn.
	 * @param randomRespawn int
	 */
	protected final void setRandomRespawn(int randomRespawn)
	{
		this.randomRespawn = randomRespawn;
	}
	
	/**
	 * Method setRespawn.
	 * @param respawn int
	 */
	protected final void setRespawn(int respawn)
	{
		this.respawn = respawn;
	}
	
	/**
	 * Method setSpawned.
	 * @param spawned ResourseInstance
	 */
	protected final void setSpawned(ResourseInstance spawned)
	{
		this.spawned = spawned;
	}
	
	/**
	 * Method setStoped.
	 * @param stoped boolean
	 */
	protected final void setStoped(boolean stoped)
	{
		this.stoped = stoped;
	}
	
	/**
	 * Method setTemplate.
	 * @param template ResourseTemplate
	 */
	protected final void setTemplate(ResourseTemplate template)
	{
		this.template = template;
	}
	
	/**
	 * Method setWaited.
	 * @param waited ResourseInstance
	 */
	protected final void setWaited(ResourseInstance waited)
	{
		this.waited = waited;
	}
	
	public synchronized void start()
	{
		if (!isStoped())
		{
			return;
		}
		
		setStoped(false);
		doSpawn();
	}
	
	public synchronized void stop()
	{
		if (isStoped())
		{
			return;
		}
		
		setStoped(true);
		final ResourseInstance resourse = getSpawned();
		
		if (resourse != null)
		{
			resourse.deleteMe();
			setWaited(resourse);
		}
	}
}