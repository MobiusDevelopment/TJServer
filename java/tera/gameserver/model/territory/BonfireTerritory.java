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
package tera.gameserver.model.territory;

import java.util.concurrent.ScheduledFuture;

import org.w3c.dom.Node;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Bonfire;
import tera.gameserver.model.TObject;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.StatType;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.model.skillengine.funcs.stat.MathFunc;
import tera.gameserver.model.skillengine.lambdas.FloatMul;
import tera.gameserver.tables.BonfireTable;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class BonfireTerritory extends AbstractTerritory implements Runnable, Bonfire
{
	
	private static final Func func = new MathFunc(StatType.REGEN_HP, 0x30, null, new FloatMul(3));
	
	private volatile ScheduledFuture<BonfireTerritory> task;
	
	private float centerX;
	private float centerY;
	private float centerZ;
	
	/**
	 * Constructor for BonfireTerritory.
	 * @param node Node
	 * @param type TerritoryType
	 */
	public BonfireTerritory(Node node, TerritoryType type)
	{
		super(node, type);
		
		try
		{
			final VarTable vars = VarTable.newInstance(node);
			centerX = vars.getFloat("centerX", (minimumX + ((maximumX - maximumX) / 2)));
			centerY = vars.getFloat("centerY", (minimumY + ((maximumY - maximumY) / 2)));
			centerZ = vars.getFloat("centerZ", (minimumZ + ((maximumZ - maximumZ) / 2)));
			BonfireTable.addBonfire(this);
		}
		catch (Exception e)
		{
			log.warning(e);
			throw e;
		}
	}
	
	/**
	 * Method getCenterX.
	 * @return float
	 */
	public final float getCenterX()
	{
		return centerX;
	}
	
	/**
	 * Method getCenterY.
	 * @return float
	 */
	public final float getCenterY()
	{
		return centerY;
	}
	
	/**
	 * Method getCenterZ.
	 * @return float
	 */
	public final float getCenterZ()
	{
		return centerZ;
	}
	
	/**
	 * Method onEnter.
	 * @param object TObject
	 * @see tera.gameserver.model.territory.Territory#onEnter(TObject)
	 */
	@Override
	public void onEnter(TObject object)
	{
		super.onEnter(object);
		
		if (!object.isPlayer())
		{
			return;
		}
		
		final Player player = object.getPlayer();
		
		if (!player.addBonfire(this))
		{
			return;
		}
		
		func.addFuncTo(player);
		
		if (task == null)
		{
			synchronized (this)
			{
				if (task == null)
				{
					final ExecutorManager executor = ExecutorManager.getInstance();
					task = executor.scheduleGeneralAtFixedRate(this, 3000, 3000);
				}
			}
		}
	}
	
	/**
	 * Method onExit.
	 * @param object TObject
	 * @see tera.gameserver.model.territory.Territory#onExit(TObject)
	 */
	@Override
	public void onExit(TObject object)
	{
		super.onExit(object);
		
		if (!object.isPlayer())
		{
			return;
		}
		
		final Player player = object.getPlayer();
		func.removeFuncTo(player);
		player.removeBonfire(this);
		
		if (objects.isEmpty() && (task != null))
		{
			synchronized (this)
			{
				if (objects.isEmpty() && (task != null))
				{
					task.cancel(false);
					task = null;
				}
			}
		}
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		try
		{
			if (objects.isEmpty())
			{
				return;
			}
			
			objects.readLock();
			
			try
			{
				final TObject[] array = objects.array();
				
				for (int i = 0, length = objects.size(); i < length; i++)
				{
					final Player player = array[i].getPlayer();
					
					if (player != null)
					{
						player.addStamina();
					}
				}
			}
			
			finally
			{
				objects.readUnlock();
			}
		}
		catch (Exception e)
		{
			log.warning(this, e);
		}
	}
}
