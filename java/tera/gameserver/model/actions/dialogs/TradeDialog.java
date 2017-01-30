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

import tera.gameserver.IdFactory;
import tera.gameserver.manager.GameLogManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.TradeItem;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.ServerPacket;
import tera.gameserver.network.serverpackets.ShowTrade;
import tera.gameserver.network.serverpackets.SystemMessage;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public class TradeDialog extends AbstractActionDialog
{
	
	public static final int MAX_ITEMS = 18;
	
	/**
	 * Method newInstance.
	 * @param actor Player
	 * @param enemy Player
	 * @return TradeDialog
	 */
	public static final TradeDialog newInstance(Player actor, Player enemy)
	{
		final TradeDialog dialog = (TradeDialog) ActionDialogType.TRADE_DIALOG.newInstance();
		final IdFactory idFactory = IdFactory.getInstance();
		dialog.actor = actor;
		dialog.enemy = enemy;
		dialog.objectId = idFactory.getNextActionId();
		return dialog;
	}
	
	private Array<TradeItem> actorItems;
	
	private final Array<TradeItem> enemyItems;
	
	private long actorMoney;
	
	private long enemyMoney;
	
	private boolean actorLock;
	
	private boolean enemyLock;
	
	private boolean done;
	
	public TradeDialog()
	{
		actorItems = Arrays.toArray(TradeItem.class, MAX_ITEMS);
		enemyItems = Arrays.toArray(TradeItem.class, MAX_ITEMS);
	}
	
	/**
	 * Method addItem.
	 * @param player Player
	 * @param count int
	 * @param index int
	 */
	public synchronized void addItem(Player player, int count, int index)
	{
		if (isActorLock() || isEnemyLock())
		{
			return;
		}
		
		Array<TradeItem> items;
		final Inventory inventory = player.getInventory();
		
		if (player == getActor())
		{
			items = getActorItems();
		}
		else
		{
			items = getEnemyItems();
		}
		
		inventory.lock();
		
		try
		{
			final Cell cell = inventory.getCell(index);
			
			if ((cell == null) || cell.isEmpty())
			{
				return;
			}
			
			final ItemInstance item = cell.getItem();
			
			if (!item.isTradable())
			{
				final SystemMessage packet = SystemMessage.getInstance(MessageType.YOU_CANT_TRADE);
				packet.addItemName(item.getItemId());
				player.sendPacket(packet, true);
				return;
			}
			
			if (count > item.getItemCount())
			{
				return;
			}
			
			final int order = items.indexOf(item);
			
			if (order > -1)
			{
				final TradeItem tradeItem = items.get(order);
				
				if ((tradeItem.getCount() + count) > item.getItemCount())
				{
					return;
				}
				
				tradeItem.addCount(count);
				updateDialog();
			}
			else if (items.size() < MAX_ITEMS)
			{
				items.add(TradeItem.newInstance(item, count));
				updateDialog();
			}
		}
		
		finally
		{
			inventory.unlock();
		}
	}
	
	/**
	 * Method addMoney.
	 * @param player Player
	 * @param money long
	 */
	public synchronized void addMoney(Player player, long money)
	{
		if (isActorLock() || isEnemyLock())
		{
			return;
		}
		
		final Inventory inventory = player.getInventory();
		final boolean isActor = player == getActor();
		
		if (isActor && ((money + actorMoney) <= inventory.getMoney()))
		{
			actorMoney += money;
			updateDialog();
		}
		else if ((money + enemyMoney) <= inventory.getMoney())
		{
			enemyMoney += money;
			updateDialog();
		}
	}
	
	/**
	 * Method apply.
	 * @return boolean
	 * @see tera.gameserver.model.actions.dialogs.ActionDialog#apply()
	 */
	@Override
	public synchronized boolean apply()
	{
		if (isDone())
		{
			return false;
		}
		
		setDone(true);
		final Player actor = getActor();
		final Player enemy = getEnemy();
		
		if ((actor == null) || (enemy == null))
		{
			log.warning(this, new Exception("not found actor or enemy,"));
			return false;
		}
		
		final Inventory actorInventory = actor.getInventory();
		final Inventory enemyInventory = enemy.getInventory();
		
		if ((actorInventory == null) || (enemyInventory == null))
		{
			return false;
		}
		
		actorInventory.lock();
		
		try
		{
			enemyInventory.lock();
			
			try
			{
				if (actorMoney > actorInventory.getMoney())
				{
					actorMoney = 0;
				}
				
				TradeItem[] array = actorItems.array();
				
				for (int i = 0, length = actorItems.size(); i < length; i++)
				{
					final TradeItem tradeItem = array[i];
					final ItemInstance item = actorInventory.getItemForObjectId(tradeItem.getObjectId());
					
					if ((item == null) || (item.getItemCount() < tradeItem.getCount()))
					{
						actorItems.fastRemove(i--);
						length--;
					}
				}
				
				if (enemyMoney > enemyInventory.getMoney())
				{
					enemyMoney = 0;
				}
				
				array = enemyItems.array();
				
				for (int i = 0, length = enemyItems.size(); i < length; i++)
				{
					final TradeItem tradeItem = array[i];
					final ItemInstance item = enemyInventory.getItemForObjectId(tradeItem.getObjectId());
					
					if ((item == null) || (item.getItemCount() < tradeItem.getCount()))
					{
						enemyItems.fastRemove(i--);
						length--;
					}
				}
				
				actorInventory.addMoney(enemyMoney);
				actorInventory.subMoney(actorMoney);
				enemyInventory.addMoney(actorMoney);
				enemyInventory.subMoney(enemyMoney);
				final GameLogManager gameLogger = GameLogManager.getInstance();
				gameLogger.writeItemLog(actor.getName() + " add " + actorMoney + " money to " + enemy.getName());
				gameLogger.writeItemLog(enemy.getName() + " add " + enemyMoney + " money to " + actor.getName());
				array = actorItems.array();
				
				for (int i = 0, length = actorItems.size(); i < length; i++)
				{
					final TradeItem trade = array[i];
					final long count = trade.getCount();
					final ItemInstance item = trade.getItem();
					
					if (!trade.isStackable())
					{
						enemyInventory.moveItem(item, actorInventory);
					}
					else
					{
						if (enemyInventory.addItem(trade.getItemId(), trade.getCount(), actor.getName()))
						{
							actorInventory.removeItem(trade.getItemId(), trade.getCount());
						}
					}
					
					gameLogger.writeItemLog(actor.getName() + " trade item [id = " + item.getItemId() + ", count = " + count + ", name = " + item.getName() + "] to " + enemy.getName());
				}
				
				array = enemyItems.array();
				
				for (int i = 0, length = enemyItems.size(); i < length; i++)
				{
					final TradeItem trade = array[i];
					final long count = trade.getCount();
					final ItemInstance item = trade.getItem();
					
					if (!trade.isStackable())
					{
						actorInventory.moveItem(trade.getItem(), enemyInventory);
					}
					else
					{
						if (actorInventory.addItem(trade.getItemId(), trade.getCount(), actor.getName()))
						{
							enemyInventory.removeItem(trade.getItemId(), trade.getCount());
						}
					}
					
					gameLogger.writeItemLog(enemy.getName() + " trade item [id = " + item.getItemId() + ", count = " + count + ", name = " + item.getName() + "] to " + actor.getName());
				}
				
				final ObjectEventManager eventManager = ObjectEventManager.getInstance();
				eventManager.notifyInventoryChanged(actor);
				eventManager.notifyInventoryChanged(enemy);
			}
			
			finally
			{
				enemyInventory.unlock();
			}
		}
		
		finally
		{
			actorInventory.unlock();
		}
		actor.sendMessage(MessageType.TRADE_COMPLETED);
		enemy.sendMessage(MessageType.TRADE_COMPLETED);
		return true;
	}
	
	/**
	 * Method cancel.
	 * @param player Player
	 * @see tera.gameserver.model.actions.dialogs.ActionDialog#cancel(Player)
	 */
	@Override
	public synchronized void cancel(Player player)
	{
		if (player != null)
		{
			final Player actor = getActor();
			final Player enemy = getEnemy();
			
			if (player == actor)
			{
				actor.sendMessage(MessageType.TRADE_CANCELED);
				enemy.sendPacket(SystemMessage.getInstance(MessageType.OPPONENT_CANCELED_THE_TRADE).addOpponent(actor.getName()), true);
			}
			else if (player == enemy)
			{
				enemy.sendMessage(MessageType.TRADE_CANCELED);
				actor.sendPacket(SystemMessage.getInstance(MessageType.OPPONENT_CANCELED_THE_TRADE).addOpponent(enemy.getName()), true);
			}
		}
		
		super.cancel(player);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		TradeItem[] array = actorItems.array();
		
		for (int i = 0, length = actorItems.size(); i < length; i++)
		{
			array[i].fold();
		}
		
		actorItems.clear();
		array = enemyItems.array();
		
		for (int i = 0, length = enemyItems.size(); i < length; i++)
		{
			array[i].fold();
		}
		
		enemyItems.clear();
		actorMoney = 0;
		enemyMoney = 0;
		actorLock = false;
		enemyLock = false;
		done = false;
		super.finalyze();
	}
	
	/**
	 * Method getActorItems.
	 * @return Array<TradeItem>
	 */
	protected final Array<TradeItem> getActorItems()
	{
		return actorItems;
	}
	
	/**
	 * Method getActorMoney.
	 * @return long
	 */
	protected final long getActorMoney()
	{
		return actorMoney;
	}
	
	/**
	 * Method getEnemyItems.
	 * @return Array<TradeItem>
	 */
	protected Array<TradeItem> getEnemyItems()
	{
		return enemyItems;
	}
	
	/**
	 * Method getEnemyMoney.
	 * @return long
	 */
	protected final long getEnemyMoney()
	{
		return enemyMoney;
	}
	
	/**
	 * Method getItemCount.
	 * @param player Player
	 * @return int
	 */
	public int getItemCount(Player player)
	{
		if (player == actor)
		{
			return actorItems.size();
		}
		else if (player == enemy)
		{
			return enemyItems.size();
		}
		
		return 0;
	}
	
	/**
	 * Method getItems.
	 * @param player Player
	 * @return Array<TradeItem>
	 */
	public Array<TradeItem> getItems(Player player)
	{
		if (player == actor)
		{
			return actorItems;
		}
		else if (player == enemy)
		{
			return enemyItems;
		}
		
		return null;
	}
	
	/**
	 * Method getMoney.
	 * @param player Player
	 * @return long
	 */
	public long getMoney(Player player)
	{
		if (player == actor)
		{
			return getActorMoney();
		}
		else if (player == enemy)
		{
			return getEnemyMoney();
		}
		
		return 0;
	}
	
	/**
	 * Method getType.
	 * @return ActionDialogType
	 * @see tera.gameserver.model.actions.dialogs.ActionDialog#getType()
	 */
	@Override
	public ActionDialogType getType()
	{
		return ActionDialogType.TRADE_DIALOG;
	}
	
	/**
	 * Method init.
	 * @return boolean
	 * @see tera.gameserver.model.actions.dialogs.ActionDialog#init()
	 */
	@Override
	public synchronized boolean init()
	{
		if (super.init())
		{
			final Player actor = getActor();
			final Player enemy = getEnemy();
			PacketManager.updateInventory(actor);
			PacketManager.updateInventory(enemy);
			updateDialog();
			actor.sendMessage(MessageType.TRADE_HAS_BEGUN);
			enemy.sendMessage(MessageType.TRADE_HAS_BEGUN);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Method isActorLock.
	 * @return boolean
	 */
	protected final boolean isActorLock()
	{
		return actorLock;
	}
	
	/**
	 * Method isDone.
	 * @return boolean
	 */
	protected final boolean isDone()
	{
		return done;
	}
	
	/**
	 * Method isEnemyLock.
	 * @return boolean
	 */
	protected final boolean isEnemyLock()
	{
		return enemyLock;
	}
	
	/**
	 * Method isLock.
	 * @param player Player
	 * @return boolean
	 */
	public boolean isLock(Player player)
	{
		if (player == actor)
		{
			return isActorLock();
		}
		else if (player == enemy)
		{
			return isEnemyLock();
		}
		
		return false;
	}
	
	/**
	 * Method lock.
	 * @param player Player
	 */
	public synchronized void lock(Player player)
	{
		if (actor == player)
		{
			setActorLock(true);
		}
		else if (enemy == player)
		{
			setEnemyLock(true);
		}
		
		if (isActorLock() && isEnemyLock())
		{
			apply();
			cancel(null);
			return;
		}
		
		updateDialog();
	}
	
	/**
	 * Method setActorItems.
	 * @param actorItems Array<TradeItem>
	 */
	protected final void setActorItems(Array<TradeItem> actorItems)
	{
		this.actorItems = actorItems;
	}
	
	/**
	 * Method setActorLock.
	 * @param actorLock boolean
	 */
	protected final void setActorLock(boolean actorLock)
	{
		this.actorLock = actorLock;
	}
	
	/**
	 * Method setDone.
	 * @param done boolean
	 */
	protected final void setDone(boolean done)
	{
		this.done = done;
	}
	
	/**
	 * Method setEnemyLock.
	 * @param enemyLock boolean
	 */
	protected final void setEnemyLock(boolean enemyLock)
	{
		this.enemyLock = enemyLock;
	}
	
	protected void updateDialog()
	{
		final Player actor = getActor();
		final Player enemy = getEnemy();
		
		if ((actor == null) || (enemy == null))
		{
			return;
		}
		
		final ServerPacket packet = ShowTrade.getInstance(actor, enemy, objectId, this);
		packet.increaseSends();
		packet.increaseSends();
		actor.sendPacket(packet, false);
		enemy.sendPacket(packet, false);
	}
}
