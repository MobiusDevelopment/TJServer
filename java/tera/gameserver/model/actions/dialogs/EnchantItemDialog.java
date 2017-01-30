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

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.DialogPanel;
import tera.gameserver.network.serverpackets.DialogPanel.PanelType;
import tera.gameserver.network.serverpackets.EnchantResult;
import tera.gameserver.network.serverpackets.EnchatItemInfo;

import rlib.util.array.Arrays;
import rlib.util.random.Random;
import rlib.util.random.Randoms;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public class EnchantItemDialog extends AbstractActionDialog
{
	
	private static final int MAX_ENCHANT_LEVEL = 12;
	
	public static final int ITEM_COUNTER = 2;
	
	private static final int ALKAHEST_ITEM_INDEX = 2;
	private static final int CONSUME_ITEM_INDEX = 1;
	private static final int SOURCE_ITEM_INDEX = 0;
	
	private static final int[][] CHANE_TABLE =
	{
		
		{
			70,
			80,
			90,
			99
		},
		{
			70,
			80,
			90,
			99
		},
		{
			70,
			80,
			90,
			99
		},
		
		{
			20,
			30,
			50,
			66
		},
		{
			20,
			30,
			50,
			66
		},
		{
			20,
			30,
			50,
			66
		},
		
		{
			5,
			15,
			25,
			40
		},
		{
			5,
			15,
			25,
			40
		},
		{
			5,
			15,
			25,
			40
		},
		
		{
			1,
			5,
			15,
			20
		},
		{
			1,
			5,
			15,
			20
		},
		{
			1,
			5,
			15,
			20
		},
	};
	
	private static final Table<IntKey, int[]> ALKAHEST_TABLE;
	
	static
	{
		
		ALKAHEST_TABLE = Tables.newIntegerTable();
		
		ALKAHEST_TABLE.put(15, Arrays.toIntegerArray(0, 6));
		ALKAHEST_TABLE.put(446, Arrays.toIntegerArray(6, 9));
		ALKAHEST_TABLE.put(448, Arrays.toIntegerArray(6, 9));
		ALKAHEST_TABLE.put(447, Arrays.toIntegerArray(9, 12));
	}
	
	/**
	 * Method newInstance.
	 * @param player Player
	 * @return EnchantItemDialog
	 */
	public static EnchantItemDialog newInstance(Player player)
	{
		EnchantItemDialog dialog = (EnchantItemDialog) ActionDialogType.ENCHANT_ITEM_DIALOG.newInstance();
		
		if (dialog == null)
		{
			dialog = new EnchantItemDialog();
		}
		
		dialog.actor = player;
		dialog.enemy = player;
		return dialog;
	}
	
	private final Random random;
	
	private ItemInstance consume;
	
	private ItemInstance alkahest;
	
	private ItemInstance source;
	
	public EnchantItemDialog()
	{
		random = Randoms.newRealRandom();
	}
	
	/**
	 * Method apply.
	 * @return boolean
	 * @see tera.gameserver.model.actions.dialogs.ActionDialog#apply()
	 */
	@Override
	public boolean apply()
	{
		try
		{
			final Player actor = getActor();
			
			if (actor == null)
			{
				return false;
			}
			
			final Inventory inventory = actor.getInventory();
			
			if (inventory == null)
			{
				return false;
			}
			
			inventory.lock();
			
			try
			{
				ItemInstance target = getSource();
				final ItemInstance source = inventory.getItemForObjectId(target.getObjectId());
				
				if (source == null)
				{
					return false;
				}
				
				target = getConsume();
				final ItemInstance consume = inventory.getItemForObjectId(target.getObjectId());
				
				if ((consume == null) || (consume == source) || !source.isEnchantable() || (consume.getExtractable() < source.getExtractable()))
				{
					return false;
				}
				
				if (!source.getClass().isInstance(consume))
				{
					return false;
				}
				
				target = getAlkahest();
				final ItemInstance alkahest = inventory.getItemForItemId(target.getItemId());
				
				if ((alkahest == null) || (alkahest.getItemCount() < source.getExtractable()))
				{
					return false;
				}
				
				final int[] range = ALKAHEST_TABLE.get(alkahest.getItemId());
				
				if ((source.getEnchantLevel() < range[0]) || (source.getEnchantLevel() >= range[1]))
				{
					return false;
				}
				
				final int chance = CHANE_TABLE[source.getEnchantLevel()][consume.getRank().ordinal()];
				final boolean fail = !random.chance(chance);
				consume.setOwnerId(0);
				PacketManager.showDeleteItem(actor, consume);
				inventory.removeItem(consume);
				final DataBaseManager manager = DataBaseManager.getInstance();
				manager.updateLocationItem(consume);
				inventory.removeItem(alkahest.getItemId(), source.getExtractable());
				
				if (fail)
				{
					actor.sendPacket(EnchantResult.getFail(), false);
				}
				else
				{
					source.setEnchantLevel(source.getEnchantLevel() + 1);
					manager.updateItem(source);
					actor.sendPacket(EnchantResult.getSuccessful(), false);
				}
				
				final ObjectEventManager eventManager = ObjectEventManager.getInstance();
				eventManager.notifyInventoryChanged(actor);
				return true;
			}
			
			finally
			{
				inventory.unlock();
			}
		}
		
		finally
		{
			setConsume(null);
		}
	}
	
	/**
	 * Method getType.
	 * @return ActionDialogType
	 * @see tera.gameserver.model.actions.dialogs.ActionDialog#getType()
	 */
	@Override
	public ActionDialogType getType()
	{
		return ActionDialogType.ENCHANT_ITEM_DIALOG;
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
			actor.sendPacket(DialogPanel.getInstance(actor, PanelType.ENCHANT_ITEM), true);
			updateDialog();
			return true;
		}
		
		return false;
	}
	
	/**
	 * Method getConsume.
	 * @return ItemInstance
	 */
	public ItemInstance getConsume()
	{
		return consume;
	}
	
	/**
	 * Method setConsume.
	 * @param consume ItemInstance
	 */
	public void setConsume(ItemInstance consume)
	{
		this.consume = consume;
	}
	
	/**
	 * Method getAlkahest.
	 * @return ItemInstance
	 */
	public ItemInstance getAlkahest()
	{
		return alkahest;
	}
	
	/**
	 * Method setAlkahest.
	 * @param alkahest ItemInstance
	 */
	public void setAlkahest(ItemInstance alkahest)
	{
		this.alkahest = alkahest;
	}
	
	/**
	 * Method getSource.
	 * @return ItemInstance
	 */
	public ItemInstance getSource()
	{
		return source;
	}
	
	/**
	 * Method setSource.
	 * @param source ItemInstance
	 */
	public void setSource(ItemInstance source)
	{
		this.source = source;
	}
	
	private void updateDialog()
	{
		final Player actor = getActor();
		
		if (actor != null)
		{
			actor.sendPacket(EnchatItemInfo.getInstance(this), true);
		}
	}
	
	/**
	 * Method getItemId.
	 * @param index int
	 * @return int
	 */
	public int getItemId(int index)
	{
		switch (index)
		{
			case SOURCE_ITEM_INDEX:
			{
				final ItemInstance source = getSource();
				
				if (source != null)
				{
					return source.getItemId();
				}
				
				break;
			}
			
			case CONSUME_ITEM_INDEX:
			{
				final ItemInstance consume = getConsume();
				
				if (consume != null)
				{
					return consume.getItemId();
				}
				
				break;
			}
			
			case ALKAHEST_ITEM_INDEX:
			{
				final ItemInstance alkahest = getAlkahest();
				
				if (alkahest != null)
				{
					return alkahest.getItemId();
				}
				
				break;
			}
		}
		
		return 0;
	}
	
	/**
	 * Method getObjectId.
	 * @param index int
	 * @return int
	 */
	public int getObjectId(int index)
	{
		switch (index)
		{
			case SOURCE_ITEM_INDEX:
			{
				final ItemInstance source = getSource();
				
				if (source != null)
				{
					return source.getObjectId();
				}
				
				break;
			}
			
			case CONSUME_ITEM_INDEX:
			{
				final ItemInstance consume = getConsume();
				
				if (consume != null)
				{
					return consume.getObjectId();
				}
				
				break;
			}
			
			case ALKAHEST_ITEM_INDEX:
			{
				final ItemInstance alkahest = getAlkahest();
				
				if (alkahest != null)
				{
					return alkahest.getObjectId();
				}
				
				break;
			}
		}
		
		return 0;
	}
	
	/**
	 * Method getNeedItemCount.
	 * @param index int
	 * @return int
	 */
	public int getNeedItemCount(int index)
	{
		switch (index)
		{
			case SOURCE_ITEM_INDEX:
			{
				return 1;
			}
			
			case CONSUME_ITEM_INDEX:
			{
				return 1;
			}
			
			case ALKAHEST_ITEM_INDEX:
			{
				final ItemInstance source = getSource();
				
				if (source != null)
				{
					return source.getExtractable();
				}
				
				break;
			}
		}
		
		return 0;
	}
	
	/**
	 * Method isEnchantItem.
	 * @param index int
	 * @return boolean
	 */
	public boolean isEnchantItem(int index)
	{
		switch (index)
		{
			case SOURCE_ITEM_INDEX:
			{
				return true;
			}
			
			default:
			{
				return false;
			}
		}
	}
	
	/**
	 * Method addItem.
	 * @param index int
	 * @param objectId int
	 * @param itemId int
	 */
	public void addItem(int index, int objectId, int itemId)
	{
		final Player actor = getActor();
		
		switch (index)
		{
			case SOURCE_ITEM_INDEX:
			{
				if ((getConsume() != null) || (getAlkahest() != null))
				{
					actor.sendMessage("It is necessary to clean the used items and alkahest.");
					return;
				}
				
				final ItemInstance source = findItem(objectId, itemId);
				
				if (source == null)
				{
					actor.sendMessage("Could not find anything.");
					return;
				}
				
				if (source.getEnchantLevel() >= MAX_ENCHANT_LEVEL)
				{
					actor.sendMessage("The is at maximum enchant level.");
					return;
				}
				
				if (!source.isEnchantable())
				{
					actor.sayMessage("This item can not be enchanted.");
					return;
				}
				
				setSource(source);
				updateDialog();
				break;
			}
			
			case CONSUME_ITEM_INDEX:
			{
				final ItemInstance source = getSource();
				
				if (source == null)
				{
					actor.sendMessage("Unknown item craft.");
					return;
				}
				
				final ItemInstance consume = findItem(objectId, itemId);
				
				if ((consume == null) || (consume == getSource()))
				{
					actor.sendMessage("Could not find anything.");
					return;
				}
				
				if (source.getExtractable() != consume.getExtractable())
				{
					actor.sendMessage("This item is not appropriate for the level of enchantment.");
					return;
				}
				
				if (!source.getClass().isInstance(consume))
				{
					actor.sendMessage("This item does not fit the type.");
					return;
				}
				
				setConsume(consume);
				updateDialog();
				break;
			}
			
			case ALKAHEST_ITEM_INDEX:
			{
				final ItemInstance source = getSource();
				
				if ((source == null) || (getConsume() == null))
				{
					actor.sendMessage("Fill the rest cells.");
					return;
				}
				
				final int[] range = ALKAHEST_TABLE.get(itemId);
				
				if (range == null)
				{
					actor.sendMessage("This item is not alkahest.");
					return;
				}
				
				if ((source.getEnchantLevel() >= range[1]) || (source.getEnchantLevel() < range[0]))
				{
					actor.sendMessage("This alkahest is not suitable.");
					return;
				}
				
				final ItemInstance alkahest = findItem(objectId, itemId);
				
				if (alkahest == null)
				{
					actor.sendMessage("Could not find anything.");
					return;
				}
				
				if (alkahest.getItemCount() < source.getExtractable())
				{
					actor.sendMessage("Insufficient quantity.");
					return;
				}
				
				setAlkahest(alkahest);
				updateDialog();
			}
		}
	}
	
	/**
	 * Method findItem.
	 * @param objectId int
	 * @param itemId int
	 * @return ItemInstance
	 */
	public ItemInstance findItem(int objectId, int itemId)
	{
		final Player actor = getActor();
		
		if (actor == null)
		{
			return null;
		}
		
		final Inventory inventory = actor.getInventory();
		
		if (objectId != 0)
		{
			return inventory.getItemForObjectId(objectId);
		}
		
		return inventory.getItemForItemId(itemId);
	}
	
	/**
	 * Method getEnchantLevel.
	 * @param index int
	 * @return int
	 */
	public int getEnchantLevel(int index)
	{
		if (index == SOURCE_ITEM_INDEX)
		{
			final ItemInstance source = getSource();
			
			if (source != null)
			{
				return source.getEnchantLevel();
			}
		}
		
		return 0;
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		setAlkahest(null);
		setConsume(null);
		setSource(null);
	}
}
