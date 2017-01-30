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

import java.util.concurrent.ScheduledFuture;

import tera.gameserver.IdFactory;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.manager.GameLogManager;
import tera.gameserver.manager.GuildManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Guild;
import tera.gameserver.model.Party;
import tera.gameserver.model.actions.Action;
import tera.gameserver.model.actions.ActionType;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.ActionInvite;
import tera.gameserver.network.serverpackets.GuildCheckName;
import tera.gameserver.network.serverpackets.GuildInfo;
import tera.gameserver.network.serverpackets.GuildMembers;

import rlib.util.SafeTask;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public class JoinGuildAction extends SafeTask implements Action
{
	private static final FoldablePool<JoinGuildAction> pool = Pools.newConcurrentFoldablePool(JoinGuildAction.class);
	
	public static JoinGuildAction newInstance(Player actor, String guildName, Array<Player> members, int price)
	{
		JoinGuildAction action = pool.take();
		
		if (action == null)
		{
			action = new JoinGuildAction();
		}
		
		final IdFactory idFactory = IdFactory.getInstance();
		action.actor = actor;
		action.objectId = idFactory.getNextActionId();
		action.guildName = guildName;
		action.members = members;
		action.result = members.size() - 1;
		action.price = price;
		return action;
	}
	
	private Player actor;
	
	private String guildName;
	
	private final Array<Player> players;
	
	private Array<Player> members;
	
	protected ScheduledFuture<?> schedule;
	
	private int objectId;
	
	private int result;
	
	private int price;
	
	public JoinGuildAction()
	{
		players = Arrays.toConcurrentArray(Player.class);
	}
	
	@Override
	public synchronized void assent(Player player)
	{
		players.add(player);
		
		if (players.size() < result)
		{
			return;
		}
		
		try
		{
			if (schedule != null)
			{
				schedule.cancel(false);
				schedule = null;
			}
			
			if (actor.hasGuild())
			{
				player.sendMessage("You are already a member of a guild.");
				return;
			}
			
			final Party party = player.getParty();
			
			if (party == null)
			{
				player.sendMessage("You must be composed in the group.");
				return;
			}
			
			final Array<Player> members = party.getMembers();
			members.readLock();
			
			try
			{
				final Player[] array = members.array();
				
				for (int i = 0, length = members.size(); i < length; i++)
				{
					if (array[i].hasGuild())
					{
						actor.sendMessage("Our group already has a man in a guild.");
						return;
					}
				}
			}
			
			finally
			{
				members.readUnlock();
			}
			final Inventory inventory = actor.getInventory();
			
			if (inventory.getMoney() < price)
			{
				actor.sendMessage("You do not have enough money.");
				return;
			}
			
			final GuildManager guildManager = GuildManager.getInstance();
			final Guild clan = guildManager.createNewGuild(guildName, actor);
			
			if (clan == null)
			{
				actor.sendMessage("Failed to create a guild.");
			}
			else
			{
				inventory.subMoney(price);
				final GameLogManager gameLogger = GameLogManager.getInstance();
				gameLogger.writeItemLog(player.getName() + " buy guild for " + price + " gold");
				final ObjectEventManager eventManager = ObjectEventManager.getInstance();
				eventManager.notifyInventoryChanged(actor);
				actor.sendPacket(GuildCheckName.getInstance(guildName), true);
				members.readLock();
				
				try
				{
					final Player[] array = members.array();
					
					for (int i = 0, length = members.size(); i < length; i++)
					{
						final Player member = array[i];
						
						if (member != actor)
						{
							clan.joinMember(member);
						}
					}
					
					for (int i = 0, length = members.size(); i < length; i++)
					{
						final Player mem = array[i];
						mem.sendPacket(GuildInfo.getInstance(mem), true);
						mem.sendPacket(GuildMembers.getInstance(mem), true);
					}
				}
				
				finally
				{
					members.readUnlock();
				}
			}
		}
		
		finally
		{
			clear();
		}
	}
	
	@Override
	public synchronized void cancel(Player palyer)
	{
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				array[i].sendMessage("Create clan interrupted because of disagreement of the party members.");
			}
			
			actor.sendMessage("Create clan interrupted because of disagreement of the party members.");
		}
		
		finally
		{
			members.readUnlock();
		}
		
		if (schedule != null)
		{
			schedule.cancel(false);
			schedule = null;
		}
		
		clear();
		pool.put(this);
	}
	
	protected void clear()
	{
		actor.setLastAction(null);
		final Player[] array = members.array();
		
		for (int i = 0, length = members.size(); i < length; i++)
		{
			array[i].setLastAction(null);
		}
		
		actor = null;
		players.clear();
	}
	
	@Override
	public void finalyze()
	{
		objectId = 0;
		result = 0;
	}
	
	@Override
	public Player getActor()
	{
		return actor;
	}
	
	@Override
	public int getId()
	{
		return ActionType.CREATE_GUILD.ordinal();
	}
	
	@Override
	public int getObjectId()
	{
		return objectId;
	}
	
	@Override
	public Player getTarget()
	{
		return null;
	}
	
	@Override
	public ActionType getType()
	{
		return ActionType.CREATE_GUILD;
	}
	
	@Override
	public void init(Player actor, String name)
	{
	}
	
	@Override
	public synchronized void invite()
	{
		if (members.size() < 2)
		{
			cancel(actor);
			return;
		}
		
		final ActionInvite action = ActionInvite.getInstance(actor.getName(), guildName, ActionType.CREATE_GUILD.ordinal(), objectId);
		actor.setLastAction(this);
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player player = array[i];
				
				if (player == actor)
				{
					continue;
				}
				
				action.increaseSends();
			}
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player player = array[i];
				
				if (player == actor)
				{
					continue;
				}
				
				player.setLastAction(this);
				player.sendPacket(action, false);
			}
		}
		
		finally
		{
			members.readUnlock();
		}
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, 60000);
	}
	
	@Override
	public void reinit()
	{
	}
	
	@Override
	protected void runImpl()
	{
		cancel(actor);
	}
	
	@Override
	public void setActor(Player actor)
	{
		this.actor = actor;
	}
	
	@Override
	public void setTarget(Object target)
	{
	}
	
	@Override
	public boolean test()
	{
		return true;
	}
}
