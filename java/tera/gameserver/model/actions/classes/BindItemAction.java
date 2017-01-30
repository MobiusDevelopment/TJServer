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
package tera.gameserver.model.actions.classes;

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.EmotionType;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.actions.ActionType;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.AppledAction;
import tera.gameserver.templates.ItemTemplate;

/**
 * @author Ronn
 * @created 06.03.2012
 */
public class BindItemAction extends AbstractAction<ItemInstance>
{
	/**
	 * Method assent.
	 * @param player Player
	 * @see tera.gameserver.model.actions.Action#assent(Player)
	 */
	@Override
	public void assent(Player player)
	{
		final Player actor = getActor();
		final ItemInstance target = getTarget();
		super.assent(player);
		
		if (!test(actor, target))
		{
			return;
		}
		
		synchronized (target)
		{
			if (target.isBinded())
			{
				return;
			}
			
			target.setOwnerName(actor.getName());
			final DataBaseManager dbManager = DataBaseManager.getInstance();
			dbManager.updateDataItem(target);
			PacketManager.showEmotion(actor, EmotionType.BOASTING);
			PacketManager.updateInventory(actor);
		}
	}
	
	/**
	 * Method cancel.
	 * @param player Player
	 * @see tera.gameserver.model.actions.Action#cancel(Player)
	 */
	@Override
	public synchronized void cancel(Player player)
	{
		final Player actor = getActor();
		
		if (actor == null)
		{
			log.warning(this, new Exception("not found actor"));
			return;
		}
		
		actor.setLastAction(null);
		super.cancel(player);
	}
	
	/**
	 * Method getType.
	 * @return ActionType
	 * @see tera.gameserver.model.actions.Action#getType()
	 */
	@Override
	public ActionType getType()
	{
		return ActionType.BIND_ITEM;
	}
	
	/**
	 * Method init.
	 * @param actor Player
	 * @param name String
	 * @see tera.gameserver.model.actions.Action#init(Player, String)
	 */
	@Override
	public void init(Player actor, String name)
	{
		this.actor = actor;
		final int objectId = Integer.parseInt(name);
		final Inventory inventory = actor.getInventory();
		
		if (inventory == null)
		{
			log.warning(this, new Exception("not found inventory"));
			return;
		}
		
		final ItemInstance item = inventory.getItemForObjectId(objectId);
		
		if (item == null)
		{
			log.warning(this, new Exception("not found item " + target));
			return;
		}
		
		target = item;
	}
	
	/**
	 * Method invite.
	 * @see tera.gameserver.model.actions.Action#invite()
	 */
	@Override
	public synchronized void invite()
	{
		final Player actor = getActor();
		
		if ((actor == null) || (target == null) || target.isBinded() || actor.isOnMount() || actor.isFlyingPegas())
		{
			return;
		}
		
		actor.setLastAction(this);
		final ActionType type = getType();
		actor.sendPacket(AppledAction.newInstance(actor, null, type.ordinal(), objectId), true);
		PacketManager.showEmotion(actor, EmotionType.CAST);
		final ExecutorManager executor = ExecutorManager.getInstance();
		setSchedule(executor.scheduleGeneral(this, 5000));
	}
	
	@Override
	protected final void runImpl()
	{
		assent(null);
	}
	
	/**
	 * Method test.
	 * @param actor Player
	 * @param target ItemInstance
	 * @return boolean
	 */
	@Override
	public boolean test(Player actor, ItemInstance target)
	{
		if ((target == null) || (actor == null) || actor.isOnMount() || actor.isFlyingPegas())
		{
			return false;
		}
		
		if (target.isBinded())
		{
			actor.sendMessage(MessageType.THAT_ITEM_IS_SOULBOUND);
			return false;
		}
		
		final ItemTemplate template = target.getTemplate();
		
		if (!template.checkClass(actor))
		{
			actor.sendMessage(MessageType.THAT_ITEM_IS_UNAVAILABLE_TO_YOUR_CLASS);
			return false;
		}
		
		if (template.getRequiredLevel() > actor.getLevel())
		{
			actor.sendMessage(MessageType.YOU_MUST_BE_A_HIGHER_LEVEL_TO_USE_THAT);
			return false;
		}
		
		return true;
	}
}