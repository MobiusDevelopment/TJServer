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
package tera.gameserver.model.inventory;

import tera.Config;
import tera.gameserver.model.Guild;
import tera.gameserver.model.items.ItemLocation;

import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public class GuildBank extends AbstractBank<Guild>
{
	private static final FoldablePool<GuildBank> pool = Pools.newConcurrentFoldablePool(GuildBank.class);
	
	/**
	 * Method newInstance.
	 * @param owner Guild
	 * @return GuildBank
	 */
	public static GuildBank newInstance(Guild owner)
	{
		GuildBank bank = pool.take();
		
		if (bank == null)
		{
			bank = new GuildBank();
		}
		
		bank.owner = owner;
		return bank;
	}
	
	/**
	 * Method fold.
	 * @see tera.gameserver.model.inventory.Bank#fold()
	 */
	@Override
	public void fold()
	{
		pool.put(this);
	}
	
	/**
	 * Method getLocation.
	 * @return ItemLocation
	 * @see tera.gameserver.model.inventory.Bank#getLocation()
	 */
	@Override
	public ItemLocation getLocation()
	{
		return ItemLocation.GUILD_BANK;
	}
	
	/**
	 * Method getMaxSize.
	 * @return int
	 * @see tera.gameserver.model.inventory.Bank#getMaxSize()
	 */
	@Override
	public int getMaxSize()
	{
		return Config.WORLD_GUILD_BANK_MAX_SIZE;
	}
	
	/**
	 * Method getOwnerId.
	 * @return int
	 */
	@Override
	protected int getOwnerId()
	{
		return owner.getObjectId();
	}
}