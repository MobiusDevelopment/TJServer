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
package tera.gameserver.model.actions;

import tera.gameserver.model.actions.classes.BindItemAction;
import tera.gameserver.model.actions.classes.DuelStartAction;
import tera.gameserver.model.actions.classes.EnchantItemAction;
import tera.gameserver.model.actions.classes.GuildCreateAction;
import tera.gameserver.model.actions.classes.GuildInviteAction;
import tera.gameserver.model.actions.classes.PartyInviteAction;
import tera.gameserver.model.actions.classes.TradeStartAction;
import tera.gameserver.model.playable.Player;

import rlib.logging.Loggers;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 * @created 07.03.2012
 */
public enum ActionType
{
	NONE(null),
	NONE1(null),
	NONE2(null),
	TRADE(TradeStartAction.class),
	PARTY(PartyInviteAction.class),
	JOIN_PARTY(null),
	NONE6(null),
	NONE7(null),
	NONE8(null),
	NONE9(null),
	CREATE_GUILD(GuildCreateAction.class),
	INVITE_GUILD(GuildInviteAction.class),
	DUEL(DuelStartAction.class),
	NONE13(null),
	NONE14(null),
	NONE15(null),
	NONE16(null),
	NONE17(null),
	NONE18(null),
	NONE19(null),
	NONE20(null),
	NONE21(null),
	NONE22(null),
	NONE23(null),
	NONE24(null),
	NONE25(null),
	NONE26(null),
	NONE27(null),
	NONE28(null),
	NONE29(null),
	NONE30(null),
	NONE31(null),
	BIND_ITEM(BindItemAction.class),
	NONE32(null),
	ENCHANT_ITEM(EnchantItemAction.class),;
	public static final ActionType[] ELEMENTS = values();
	
	/**
	 * Method valueOf.
	 * @param index int
	 * @return ActionType
	 */
	public static ActionType valueOf(int index)
	{
		if ((index < 0) || (index >= ELEMENTS.length))
		{
			return ActionType.NONE;
		}
		
		return ELEMENTS[index];
	}
	
	private FoldablePool<Action> pool;
	private Class<? extends Action> type;
	
	/**
	 * Constructor for ActionType.
	 * @param type Class<? extends Action>
	 */
	private ActionType(Class<? extends Action> type)
	{
		this.type = type;
		pool = Pools.newConcurrentFoldablePool(Action.class);
	}
	
	/**
	 * Method getPool.
	 * @return FoldablePool<Action>
	 */
	public final FoldablePool<Action> getPool()
	{
		return pool;
	}
	
	/**
	 * Method isImplemented.
	 * @return boolean
	 */
	public boolean isImplemented()
	{
		return type != null;
	}
	
	/**
	 * Method newInstance.
	 * @param actor Player
	 * @param name String
	 * @return Action
	 */
	public Action newInstance(Player actor, String name)
	{
		Action action = pool.take();
		
		if (action == null)
		{
			try
			{
				action = type.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				Loggers.warning(this, e);
			}
		}
		
		if (action != null)
		{
			action.init(actor, name);
		}
		return action;
	}
}