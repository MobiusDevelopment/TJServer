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
package tera.gameserver.scripts.items;

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.MessageAddedItem;
import tera.gameserver.network.serverpackets.SystemMessage;

import rlib.util.random.Random;
import rlib.util.random.Randoms;

/**
 * @author Ronn
 */
public class EventRewardBox extends AbstractItemExecutor
{
	/**
	 */
	private static class RewardInfo
	{
		private final int itemId;
		private final int minCount;
		private final int maxCount;
		private final int chance;
		
		/**
		 * Constructor for RewardInfo.
		 * @param itemId int
		 * @param minCount int
		 * @param maxCount int
		 * @param chance int
		 */
		public RewardInfo(int itemId, int minCount, int maxCount, int chance)
		{
			this.itemId = itemId;
			this.minCount = minCount;
			this.maxCount = maxCount;
			this.chance = chance;
		}
		
		/**
		 * Method getChance.
		 * @return int
		 */
		public final int getChance()
		{
			return chance;
		}
		
		/**
		 * Method getItemId.
		 * @return int
		 */
		public final int getItemId()
		{
			return itemId;
		}
		
		/**
		 * Method getMaxCount.
		 * @return int
		 */
		public final int getMaxCount()
		{
			return maxCount;
		}
		
		/**
		 * Method getMinCount.
		 * @return int
		 */
		public final int getMinCount()
		{
			return minCount;
		}
	}
	
	private static RewardInfo[][] REWARD =
	{
		// 0: 0 - 34
		{
			// Sylva Medicated Bandage
			new RewardInfo(174, 1, 10, 50000),
			// Speed Potion
			new RewardInfo(8007, 1, 10, 50000),
			
			// Major Mana Potion IV
			new RewardInfo(6023, 1, 5, 20000),
			// Major Healing Potion IV
			new RewardInfo(6007, 1, 5, 20000),
			
			// Onslaught Charm I
			new RewardInfo(7100, 1, 10, 30000),
			// Etheral Charm I
			new RewardInfo(7104, 1, 10, 30000),
			// Sanguine Charm I
			new RewardInfo(7108, 1, 10, 30000),
			
			// Major Healing Elixir IV
			new RewardInfo(6055, 1, 5, 10000),
			// Major Mana Elixir IV
			new RewardInfo(6071, 1, 5, 10000),
			
			new RewardInfo(50017, 1, 1, 10),
			new RewardInfo(50018, 1, 1, 10),
			new RewardInfo(50021, 1, 1, 10),
			new RewardInfo(50022, 1, 1, 10),
			new RewardInfo(50023, 1, 1, 10),
			new RewardInfo(50024, 1, 1, 10),
			new RewardInfo(50025, 1, 1, 10),
			new RewardInfo(50026, 1, 1, 10),
			new RewardInfo(50027, 1, 1, 10),
			new RewardInfo(50028, 1, 1, 10),
			new RewardInfo(50029, 1, 1, 10),
			new RewardInfo(50030, 1, 1, 10),
			new RewardInfo(50031, 1, 1, 10),
			new RewardInfo(50032, 1, 1, 10),
			new RewardInfo(50033, 1, 1, 10),
			new RewardInfo(50034, 1, 1, 10),
			new RewardInfo(50035, 1, 1, 10),
			new RewardInfo(50036, 1, 1, 10),
			new RewardInfo(50037, 1, 1, 10),
			new RewardInfo(50038, 1, 1, 10),
			new RewardInfo(50039, 1, 1, 10),
			new RewardInfo(50040, 1, 1, 10),
		},
		// 1: 35 - 47
		{
			// Shetla Medicated Bandage
			new RewardInfo(175, 1, 10, 50000),
			// Speed Potion
			new RewardInfo(8007, 1, 10, 50000),
			
			// Major Mana Potion V
			new RewardInfo(6025, 1, 5, 20000),
			// Major Healing Potion V
			new RewardInfo(6009, 1, 5, 20000),
			
			// Onslaught Charm II
			new RewardInfo(7101, 1, 10, 30000),
			// Etheral Charm II
			new RewardInfo(7105, 1, 10, 30000),
			// Sanguine Charm II
			new RewardInfo(7109, 1, 10, 30000),
			
			// Major Healing Elixir V
			new RewardInfo(6057, 1, 5, 10000),
			// Major Mana Elixir V
			new RewardInfo(6073, 1, 5, 10000),
			
			new RewardInfo(50017, 1, 1, 20),
			new RewardInfo(50018, 1, 1, 20),
			new RewardInfo(50021, 1, 1, 20),
			new RewardInfo(50022, 1, 1, 20),
			new RewardInfo(50023, 1, 1, 20),
			new RewardInfo(50024, 1, 1, 20),
			new RewardInfo(50025, 1, 1, 20),
			new RewardInfo(50026, 1, 1, 20),
			new RewardInfo(50027, 1, 1, 20),
			new RewardInfo(50028, 1, 1, 20),
			new RewardInfo(50029, 1, 1, 20),
			new RewardInfo(50030, 1, 1, 20),
			new RewardInfo(50031, 1, 1, 20),
			new RewardInfo(50032, 1, 1, 20),
			new RewardInfo(50033, 1, 1, 20),
			new RewardInfo(50034, 1, 1, 20),
			new RewardInfo(50035, 1, 1, 20),
			new RewardInfo(50036, 1, 1, 20),
			new RewardInfo(50037, 1, 1, 20),
			new RewardInfo(50038, 1, 1, 20),
			new RewardInfo(50039, 1, 1, 20),
			new RewardInfo(50040, 1, 1, 20),
		},
		// 2: 48 - 57
		{
			// Toira Medicated Bandage
			new RewardInfo(176, 1, 10, 50000),
			// Speed Potion
			new RewardInfo(8007, 1, 10, 50000),
			
			// Major Mana Potion VI
			new RewardInfo(6027, 1, 5, 20000),
			// Major Healing Potion VI
			new RewardInfo(6011, 1, 5, 20000),
			
			// Onslaught Charm III
			new RewardInfo(7102, 1, 10, 30000),
			// Etheral Charm III
			new RewardInfo(7106, 1, 10, 30000),
			// Sanguine Charm III
			new RewardInfo(7110, 1, 10, 30000),
			
			// Major Healing Elixir VI
			new RewardInfo(6059, 1, 5, 10000),
			// Major Mana Elixir VI
			new RewardInfo(6075, 1, 5, 10000),
			
			new RewardInfo(50017, 1, 1, 30),
			new RewardInfo(50018, 1, 1, 30),
			new RewardInfo(50021, 1, 1, 30),
			new RewardInfo(50022, 1, 1, 30),
			new RewardInfo(50023, 1, 1, 30),
			new RewardInfo(50024, 1, 1, 30),
			new RewardInfo(50025, 1, 1, 30),
			new RewardInfo(50026, 1, 1, 30),
			new RewardInfo(50027, 1, 1, 30),
			new RewardInfo(50028, 1, 1, 30),
			new RewardInfo(50029, 1, 1, 30),
			new RewardInfo(50030, 1, 1, 30),
			new RewardInfo(50031, 1, 1, 30),
			new RewardInfo(50032, 1, 1, 30),
			new RewardInfo(50033, 1, 1, 30),
			new RewardInfo(50034, 1, 1, 30),
			new RewardInfo(50035, 1, 1, 30),
			new RewardInfo(50036, 1, 1, 30),
			new RewardInfo(50037, 1, 1, 30),
			new RewardInfo(50038, 1, 1, 30),
			new RewardInfo(50039, 1, 1, 30),
			new RewardInfo(50040, 1, 1, 30),
		},
		// 3: 58 - 60
		{
			// Luria Medicated Bandage
			new RewardInfo(177, 1, 10, 50000),
			// Speed Potion
			new RewardInfo(8007, 1, 10, 50000),
			
			// Major Mana Potion VIII
			new RewardInfo(6031, 1, 5, 20000),
			// Major Healing Potion VIII
			new RewardInfo(6015, 1, 5, 20000),
			
			// Onslaught Charm IV
			new RewardInfo(7103, 1, 10, 30000),
			// Etheral Charm IV
			new RewardInfo(7107, 1, 10, 30000),
			// Sanguine Charm IV
			new RewardInfo(7111, 1, 10, 30000),
			
			// Major Healing Elixir VIII
			new RewardInfo(6063, 1, 5, 10000),
			// Major Mana Elixir VIII
			new RewardInfo(6079, 1, 5, 10000),
			
			new RewardInfo(50017, 1, 1, 40),
			new RewardInfo(50018, 1, 1, 40),
			new RewardInfo(50021, 1, 1, 40),
			new RewardInfo(50022, 1, 1, 40),
			new RewardInfo(50023, 1, 1, 40),
			new RewardInfo(50024, 1, 1, 40),
			new RewardInfo(50025, 1, 1, 40),
			new RewardInfo(50026, 1, 1, 40),
			new RewardInfo(50027, 1, 1, 40),
			new RewardInfo(50028, 1, 1, 40),
			new RewardInfo(50029, 1, 1, 40),
			new RewardInfo(50030, 1, 1, 40),
			new RewardInfo(50031, 1, 1, 40),
			new RewardInfo(50032, 1, 1, 40),
			new RewardInfo(50033, 1, 1, 40),
			new RewardInfo(50034, 1, 1, 40),
			new RewardInfo(50035, 1, 1, 40),
			new RewardInfo(50036, 1, 1, 40),
			new RewardInfo(50037, 1, 1, 40),
			new RewardInfo(50038, 1, 1, 40),
			new RewardInfo(50039, 1, 1, 40),
			new RewardInfo(50040, 1, 1, 40),
		},
	};
	private final Random random;
	
	/**
	 * Constructor for EventRewardBox.
	 * @param itemIds int[]
	 * @param access int
	 */
	public EventRewardBox(int[] itemIds, int access)
	{
		super(itemIds, access);
		random = Randoms.newRealRandom();
	}
	
	/**
	 * Method execution.
	 * @param item ItemInstance
	 * @param player Player
	 * @see tera.gameserver.scripts.items.ItemExecutor#execution(ItemInstance, Player)
	 */
	@Override
	public void execution(ItemInstance item, Player player)
	{
		final int index = item.getItemId() - 408;
		
		if ((index < 0) || (index >= REWARD.length))
		{
			return;
		}
		
		final RewardInfo[] rewards = REWARD[index];
		
		if (rewards.length < 1)
		{
			player.sendMessage("You have not reached chest reward level yet, try later.");
			return;
		}
		
		player.sendPacket(SystemMessage.getInstance(MessageType.ITEM_USE).addItem(item.getItemId(), 1), true);
		final Inventory inventory = player.getInventory();
		
		if (!inventory.removeItem(item.getItemId(), 1L))
		{
			return;
		}
		
		for (RewardInfo reward : rewards)
		{
			if (random.nextInt(0, 100000) > reward.getChance())
			{
				continue;
			}
			
			final int count = random.nextInt(reward.getMinCount(), reward.getMaxCount());
			
			if (inventory.addItem(reward.getItemId(), count, "EventRewardBox"))
			{
				player.sendPacket(MessageAddedItem.getInstance(player.getName(), reward.getItemId(), count), true);
			}
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyInventoryChanged(player);
	}
}