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
package tera.gameserver.model.npc.interaction.dialogs;

import rlib.logging.Loggers;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 * @created 25.02.2012
 */
public enum DialogType
{
	SHOP_WINDOW(ShopDialog.class),
	MULTI_SHOP(MultiShopDialog.class),
	SKILL_SHOP(SkillShopDialog.class),
	PEGAS(PegasDialog.class),
	TELEPORT(TeleportDialog.class),
	PLAYER_BANK(PlayerBankDialog.class),
	GUILD_CREATE(CreateGuildDialog.class),
	GUILD_LOAD_ICON(LoadGuildIcon.class),
	GUILD_BANK(GuildBankDialog.class),;
	public static final DialogType[] ELEMENTS = values();
	private final FoldablePool<Dialog> pool;
	private final Class<? extends Dialog> type;
	
	/**
	 * Constructor for DialogType.
	 * @param type Class<? extends Dialog>
	 */
	private DialogType(Class<? extends Dialog> type)
	{
		this.type = type;
		pool = Pools.newConcurrentFoldablePool(type);
	}
	
	/**
	 * Method getPool.
	 * @return FoldablePool<Dialog>
	 */
	public final FoldablePool<Dialog> getPool()
	{
		return pool;
	}
	
	/**
	 * Method isInstance.
	 * @param dialog Dialog
	 * @return boolean
	 */
	public boolean isInstance(Dialog dialog)
	{
		return type.isInstance(dialog);
	}
	
	/**
	 * Method newInstance.
	 * @return Dialog
	 */
	public final Dialog newInstance()
	{
		Dialog dialog = pool.take();
		
		if (dialog == null)
		{
			try
			{
				dialog = type.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				Loggers.warning(this, e);
			}
		}
		
		return dialog;
	}
}