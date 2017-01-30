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
package tera.gameserver.model.actions.dialogs;

import rlib.logging.Loggers;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public enum ActionDialogType
{
	NULL(null),
	NULL1(null),
	NULL2(null),
	
	TRADE_DIALOG(TradeDialog.class),
	
	ENCHANT_ITEM_DIALOG(EnchantItemDialog.class);
	
	private final FoldablePool<ActionDialog> pool;
	
	private final Class<? extends ActionDialog> type;
	
	/**
	 * Constructor for ActionDialogType.
	 * @param type Class<? extends ActionDialog>
	 */
	private ActionDialogType(Class<? extends ActionDialog> type)
	{
		pool = Pools.newConcurrentFoldablePool(ActionDialog.class);
		this.type = type;
	}
	
	/**
	 * Method getPool.
	 * @return FoldablePool<ActionDialog>
	 */
	public FoldablePool<ActionDialog> getPool()
	{
		return pool;
	}
	
	/**
	 * Method newInstance.
	 * @return ActionDialog
	 */
	public ActionDialog newInstance()
	{
		ActionDialog dialog = pool.take();
		
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
