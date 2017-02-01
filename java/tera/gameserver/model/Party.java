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
package tera.gameserver.model;

import java.util.Comparator;

import tera.Config;
import tera.gameserver.manager.GameLogManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.CharPickUpItem;
import tera.gameserver.network.serverpackets.CharSay;
import tera.gameserver.network.serverpackets.MessageAddedItem;
import tera.gameserver.network.serverpackets.PartyInfo;
import tera.gameserver.network.serverpackets.PartyLeave;
import tera.gameserver.network.serverpackets.PartyMemberCoords;
import tera.gameserver.network.serverpackets.PartyMemberEffectList;
import tera.gameserver.network.serverpackets.PartyMemberInfo;
import tera.gameserver.network.serverpackets.ServerPacket;
import tera.gameserver.network.serverpackets.SystemMessage;
import tera.util.LocalObjects;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.ArrayComparator;
import rlib.util.array.Arrays;
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 * @created 06.03.2012
 */
public final class Party implements Foldable
{
	private static final Logger log = Loggers.getLogger(Party.class);
	private static final FoldablePool<Party> pool = Pools.newConcurrentFoldablePool(Party.class);
	public static final int MAX_RANGE = 1000;
	
	public static Party newInstance(Player leader, int objectId)
	{
		Party party = pool.take();
		
		if (party == null)
		{
			party = new Party();
		}
		
		party.leader = leader;
		party.objectId = objectId;
		leader.setParty(party);
		party.members.add(leader);
		return party;
	}
	
	private final Comparator<Player> sorter;
	private final Array<Player> members;
	private final Array<Player> pickUped;
	private volatile Player leader;
	private int objectId;
	private boolean lootInCombat;
	private boolean roundLoot;
	
	private Party()
	{
		sorter = new ArrayComparator<Player>()
		{
			@Override
			protected int compareImpl(Player first, Player second)
			{
				if (first == getLeader())
				{
					return 1;
				}
				
				return -1;
			}
		};
		members = Arrays.toConcurrentArray(Player.class, 5);
		pickUped = Arrays.toArray(Player.class, 5);
		setLootInCombat(true);
		setRoundLoot(true);
	}
	
	public void addExp(long added, Character topDamager, Npc npc)
	{
		if ((added < 0) || (npc == null))
		{
			return;
		}
		
		final Array<Player> members = getMembers();
		int counter = 0;
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			int minLevel = Config.WORLD_PLAYER_MAX_LEVEL;
			int maxLevel = 0;
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (npc.getDistance3D(member) < MAX_RANGE)
				{
					counter++;
				}
				
				maxLevel = Math.max(member.getLevel(), maxLevel);
				minLevel = Math.min(member.getLevel(), minLevel);
			}
			
			if ((Math.abs(maxLevel - minLevel) > 6) || (counter < 1))
			{
				return;
			}
			
			added /= counter;
			
			if (added < 1)
			{
				return;
			}
			
			if (counter > 2)
			{
				added *= Config.SERVER_PARTY_RATE_EXP;
			}
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if ((topDamager != null) && (Math.abs(topDamager.getLevel() - member.getLevel()) > 5))
				{
					continue;
				}
				
				if (npc.getDistance3D(member) > MAX_RANGE)
				{
					continue;
				}
				
				float reward = added;
				
				if (Config.ACCOUNT_PREMIUM_EXP && member.hasPremium())
				{
					reward *= Config.ACCOUNT_PREMIUM_EXP_RATE;
				}
				
				final int diff = Math.abs(member.getLevel() - npc.getLevel());
				
				if (diff >= Npc.PENALTY_EXP.length)
				{
					reward *= 0F;
				}
				else if (diff > 5)
				{
					reward *= Npc.PENALTY_EXP[diff];
				}
				
				if (reward >= 1)
				{
					member.addExp((int) reward, npc, npc.getName());
				}
			}
		}
		
		finally
		{
			members.readUnlock();
		}
	}
	
	public boolean addPlayer(Player newMember)
	{
		final Array<Player> members = getMembers();
		members.writeLock();
		
		try
		{
			if (members.size() > 4)
			{
				return false;
			}
			
			if (members.contains(newMember))
			{
				return false;
			}
			
			members.add(newMember);
		}
		
		finally
		{
			members.writeUnlock();
		}
		newMember.setParty(this);
		updatePartyColorName(newMember);
		updateInfo();
		updateStat();
		return true;
	}
	
	public void allRemove()
	{
		final Array<Player> members = getMembers();
		members.readLock();
		
		try
		{
			final ServerPacket packet = PartyLeave.getInstance();
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				packet.increaseSends();
			}
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				array[i].sendPacket(packet, false);
			}
		}
		
		finally
		{
			members.readUnlock();
		}
	}
	
	public void change(boolean lootIsCombat, boolean roundLoot)
	{
		setLootInCombat(lootIsCombat);
		setRoundLoot(roundLoot);
		updateInfo();
	}
	
	public void disband(Player player)
	{
		final Array<Player> members = getMembers();
		members.writeLock();
		
		try
		{
			if (!isLeader(player))
			{
				return;
			}
			
			final ServerPacket packet = PartyLeave.getInstance();
			final ServerPacket message = SystemMessage.getInstance(MessageType.YOUR_PARTY_HAS_DISBANDED);
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				packet.increaseSends();
				message.increaseSends();
			}
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				member.setParty(null);
				member.sendPacket(packet, false);
				member.sendPacket(message, false);
			}
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				updatePartyColorName(array[i]);
			}
			
			pool.put(this);
		}
		
		finally
		{
			members.writeUnlock();
		}
	}
	
	@Override
	public void finalyze()
	{
		leader = null;
		objectId = 0;
		members.clear();
	}
	
	public Player getLeader()
	{
		return leader;
	}
	
	public int getLeaderId()
	{
		if (leader != null)
		{
			return leader.getObjectId();
		}
		
		return -1;
	}
	
	public Array<Player> getMembers()
	{
		return members;
	}
	
	public final int getObjectId()
	{
		return objectId;
	}
	
	public boolean isLeader(Player player)
	{
		return player == leader;
	}
	
	public final boolean isLootInCombat()
	{
		return lootInCombat;
	}
	
	public final boolean isRoundLoot()
	{
		return roundLoot;
	}
	
	public void kickPlayer(Player player, int objectId)
	{
		final Array<Player> members = getMembers();
		members.writeLock();
		
		try
		{
			if (!isLeader(player))
			{
				return;
			}
			
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (member.getObjectId() == objectId)
				{
					removePlayer(member);
					member.sendMessage(MessageType.YOU_VE_BEEN_KICKED_FROM_THE_PARTY);
					return;
				}
			}
		}
		
		finally
		{
			members.writeUnlock();
		}
	}
	
	public void makeLeader(Player player, int objectId)
	{
		final Array<Player> members = getMembers();
		members.writeLock();
		
		try
		{
			if (!isLeader(player))
			{
				return;
			}
			
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (member.getObjectId() == objectId)
				{
					setLeader(member);
					final SystemMessage message = SystemMessage.getInstance(MessageType.PARTY_PLAYER_NAME_IS_NOW_PARTY_LEADER);
					message.add("PartyPlayerName", member.getName());
					sendPacket(member, message);
					member.sendMessage(MessageType.YPU_ARE_NOW_PARTY_LEADER);
					updateInfo();
					return;
				}
			}
		}
		
		finally
		{
			members.writeUnlock();
		}
	}
	
	public boolean pickUpItem(ItemInstance item, Character owner)
	{
		if (owner.isBattleStanced() && !isLootInCombat())
		{
			owner.sendMessage("You cannot pickup while in combat.");
			return false;
		}
		
		if (item.getItemId() == Inventory.MONEY_ITEM_ID)
		{
			return pickUpMoney(item, owner);
		}
		if (item.isHerb() || !isRoundLoot())
		{
			return pickUpNoRound(item, owner);
		}
		return pickUpRound(item, owner);
	}
	
	private boolean pickUpMoney(ItemInstance item, Character owner)
	{
		final Array<Player> members = getMembers();
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			int counter = 0;
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if ((owner == member) || (owner.getDistance3D(member) < MAX_RANGE))
				{
					counter++;
				}
			}
			
			if (counter < 1)
			{
				return false;
			}
			
			final int money = (int) Math.max(item.getItemCount() / counter, 1);
			owner.broadcastPacket(CharPickUpItem.getInstance(owner, item));
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if ((owner == member) || (owner.getDistance3D(member) < MAX_RANGE))
				{
					final Inventory inventory = member.getInventory();
					
					if (inventory != null)
					{
						inventory.addMoney(money);
						PacketManager.showAddGold(member, money);
						final ObjectEventManager eventManager = ObjectEventManager.getInstance();
						eventManager.notifyInventoryChanged(member);
						eventManager.notifyPickUpItem(owner, item);
						final GameLogManager gameLogger = GameLogManager.getInstance();
						gameLogger.writeItemLog(member.getName() + " pick up item [id = " + item.getItemId() + ", count = " + money + ", name = " + item.getName() + "]");
					}
				}
			}
			
			return true;
		}
		
		finally
		{
			members.readUnlock();
		}
	}
	
	private boolean pickUpNoRound(ItemInstance item, Character owner)
	{
		final Inventory inventory = owner.getInventory();
		
		if (inventory == null)
		{
			return false;
		}
		
		final long itemCount = item.getItemCount();
		
		if (!inventory.putItem(item))
		{
			owner.sendMessage(MessageType.INVENTORY_IS_FULL);
		}
		else
		{
			owner.broadcastPacket(CharPickUpItem.getInstance(owner, item));
			final GameLogManager gameLogger = GameLogManager.getInstance();
			gameLogger.writeItemLog(owner.getName() + " pick up item [id = " + item.getItemId() + ", count = " + itemCount + ", name = " + item.getName() + "]");
			final ObjectEventManager eventManager = ObjectEventManager.getInstance();
			eventManager.notifyInventoryChanged(owner);
			eventManager.notifyPickUpItem(owner, item);
			final ServerPacket packet = MessageAddedItem.getInstance(owner.getName(), item.getItemId(), (int) itemCount);
			owner.sendPacket(packet, true);
			final SystemMessage message = SystemMessage.getInstance(MessageType.PARTY_PLAYER_NAME_PICK_UP_ITEM_NAME_ITEM_AMOUNT);
			message.add("PartyPlayerName", owner.getName());
			message.addItem(item.getItemId(), (int) itemCount);
			sendPacket(owner.getPlayer(), message);
			
			if (!item.isHerb() && isRoundLoot())
			{
				pickUped.add(owner.getPlayer());
			}
			
			return true;
		}
		
		return false;
	}
	
	private boolean pickUpRound(ItemInstance item, Character owner)
	{
		final Array<Player> members = getMembers();
		members.writeLock();
		
		try
		{
			final Player[] array = members.array();
			Player target = null;
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (((owner == member) || (owner.getDistance3D(member) < MAX_RANGE)) && !pickUped.contains(member))
				{
					target = member;
					break;
				}
			}
			
			if (target == null)
			{
				pickUped.clear();
				
				for (int i = 0, length = members.size(); i < length; i++)
				{
					final Player member = array[i];
					
					if ((owner == member) || ((owner.getDistance3D(member) < MAX_RANGE) && !pickUped.contains(member)))
					{
						target = member;
						break;
					}
				}
			}
			
			if (target == null)
			{
				return false;
			}
			
			final Inventory inventory = target.getInventory();
			
			if (inventory == null)
			{
				return false;
			}
			
			final long itemCount = item.getItemCount();
			
			if (!inventory.putItem(item))
			{
				target.sendMessage(MessageType.INVENTORY_IS_FULL);
				
				if (target != owner)
				{
					return pickUpNoRound(item, owner);
				}
			}
			else
			{
				owner.broadcastPacket(CharPickUpItem.getInstance(owner, item));
				final GameLogManager gameLogger = GameLogManager.getInstance();
				gameLogger.writeItemLog(target.getName() + " pick up item [id = " + item.getItemId() + ", count = " + itemCount + ", name = " + item.getName() + "]");
				final ObjectEventManager eventManager = ObjectEventManager.getInstance();
				eventManager.notifyInventoryChanged(target);
				eventManager.notifyPickUpItem(target, item);
				final ServerPacket packet = MessageAddedItem.getInstance(target.getName(), item.getItemId(), (int) itemCount);
				target.sendPacket(packet, true);
				final SystemMessage message = SystemMessage.getInstance(MessageType.PARTY_PLAYER_NAME_PICK_UP_ITEM_NAME_ITEM_AMOUNT);
				message.add("PartyPlayerName", target.getName());
				message.addItem(item.getItemId(), (int) itemCount);
				sendPacket(target, message);
				pickUped.add(target);
				return true;
			}
			
			return false;
		}
		
		finally
		{
			members.writeUnlock();
		}
	}
	
	@Override
	public void reinit()
	{
		lootInCombat = true;
		roundLoot = true;
	}
	
	public boolean removePlayer(Player oldMember)
	{
		final Array<Player> members = getMembers();
		members.writeLock();
		
		try
		{
			if (!members.fastRemove(oldMember))
			{
				return false;
			}
			oldMember.setParty(null);
			updatePartyColorName(oldMember);
			oldMember.sendPacket(PartyLeave.getInstance(), true);
			
			if (members.size() < 2)
			{
				final Player last = members.first();
				last.setParty(null);
				last.sendPacket(PartyLeave.getInstance(), true);
				pool.put(this);
				return true;
			}
			
			if (oldMember == leader)
			{
				setLeader(members.first());
			}
		}
		
		finally
		{
			members.writeUnlock();
		}
		allRemove();
		updateInfo();
		updateStat();
		return true;
	}
	
	public void sendMessage(MessageType type)
	{
		final Array<Player> members = getMembers();
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				array[i].sendMessage(type);
			}
		}
		
		finally
		{
			members.readUnlock();
		}
	}
	
	public void sendMessage(Player player, String message)
	{
		final Array<Player> members = getMembers();
		members.readLock();
		
		try
		{
			final CharSay packet = CharSay.getInstance(player.getName(), message, SayType.PARTY_CHAT, player.getObjectId(), player.getSubId());
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				packet.increaseSends();
			}
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				array[i].sendPacket(packet, false);
			}
		}
		
		finally
		{
			members.readUnlock();
		}
	}
	
	public void sendPacket(Player player, ServerPacket packet)
	{
		final Array<Player> members = getMembers();
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size() - 1; i < length; i++)
			{
				packet.increaseSends();
			}
			
			if (player == null)
			{
				packet.increaseSends();
			}
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (member == player)
				{
					continue;
				}
				
				member.sendPacket(packet, false);
			}
		}
		
		finally
		{
			members.readUnlock();
		}
	}
	
	public void setLeader(Player newLeader)
	{
		if (leader == newLeader)
		{
			return;
		}
		
		leader = newLeader;
		members.sort(sorter);
	}
	
	public final void setLootInCombat(boolean lootInCombat)
	{
		this.lootInCombat = lootInCombat;
	}
	
	public final void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}
	
	public final void setRoundLoot(boolean roundLoot)
	{
		this.roundLoot = roundLoot;
	}
	
	public int size()
	{
		return members.size();
	}
	
	public void updateCoords(Player player)
	{
		final Array<Player> members = getMembers();
		final ServerPacket packet = PartyMemberCoords.getInstance(player);
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (member == player)
				{
					continue;
				}
				
				packet.increaseSends();
			}
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (member == player)
				{
					continue;
				}
				
				member.sendPacket(packet, false);
			}
		}
		
		finally
		{
			members.readUnlock();
		}
	}
	
	public void updateEffects(Player player)
	{
		final EffectList effectList = player.getEffectList();
		
		if (effectList == null)
		{
			log.warning("not found effect list.");
			return;
		}
		
		final PartyMemberEffectList packet = PartyMemberEffectList.getInstance(player);
		final Array<Player> members = getMembers();
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (member == player)
				{
					continue;
				}
				
				packet.increaseSends();
			}
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (member == player)
				{
					continue;
				}
				
				member.sendPacket(packet, false);
			}
		}
		
		finally
		{
			members.readUnlock();
		}
	}
	
	public void updateInfo()
	{
		final Array<Player> members = getMembers();
		final ServerPacket packet = PartyInfo.getInstance(this);
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				packet.increaseSends();
			}
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				array[i].sendPacket(packet, false);
			}
		}
		
		finally
		{
			members.readUnlock();
		}
	}
	
	public void updateMember(Player player)
	{
		final Array<Player> members = getMembers();
		final ServerPacket stats = PartyMemberInfo.getInstance(player);
		final ServerPacket coords = PartyMemberCoords.getInstance(player);
		final ServerPacket effects = PartyMemberEffectList.getInstance(player);
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (member == player)
				{
					continue;
				}
				
				effects.increaseSends();
				stats.increaseSends();
				coords.increaseSends();
			}
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (member == player)
				{
					continue;
				}
				
				member.sendPacket(effects, false);
				member.sendPacket(stats, false);
				member.sendPacket(coords, false);
			}
		}
		
		finally
		{
			members.readUnlock();
		}
	}
	
	public void updatePartyColorName(Player player)
	{
		final Array<Player> members = getMembers();
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (member == player)
				{
					continue;
				}
				
				player.updateColor(member);
				member.updateColor(player);
			}
		}
		
		finally
		{
			members.readUnlock();
		}
	}
	
	public void updateStat()
	{
		final Array<Player> members = getMembers();
		final LocalObjects local = LocalObjects.get();
		final Array<Player> players = local.getNextPlayerList();
		players.addAll(members);
		final Player[] array = players.array();
		
		for (int i = 0, length = players.size(); i < length; i++)
		{
			updateMember(array[i]);
		}
	}
	
	public void updateStat(Player player)
	{
		final Array<Player> members = getMembers();
		final ServerPacket packet = PartyMemberInfo.getInstance(player);
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (member == player)
				{
					continue;
				}
				
				packet.increaseSends();
			}
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (member == player)
				{
					continue;
				}
				
				member.sendPacket(packet, false);
			}
		}
		
		finally
		{
			members.readUnlock();
		}
	}
}