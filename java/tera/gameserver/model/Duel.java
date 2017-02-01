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

import java.util.concurrent.ScheduledFuture;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.npc.summons.Summon;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.serverpackets.SystemMessage;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.SafeTask;
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class Duel extends SafeTask implements Foldable
{
	private static final Logger log = Loggers.getLogger(Duel.class);
	
	private static final FoldablePool<Duel> pool = Pools.newConcurrentFoldablePool(Duel.class);
	
	/**
	 * Method newInstance.
	 * @param actor Player
	 * @param enemy Player
	 * @return Duel
	 */
	public static Duel newInstance(Player actor, Player enemy)
	{
		Duel duel = pool.take();
		
		if (duel == null)
		{
			duel = new Duel();
		}
		
		duel.actor = actor;
		duel.enemy = enemy;
		duel.running = true;
		actor.setDuel(duel);
		enemy.setDuel(duel);
		return duel;
	}
	
	private volatile Player actor;
	
	private volatile Player enemy;
	
	private final DuelPlayer actorSave;
	
	private final DuelPlayer enemySave;
	
	private volatile boolean active;
	
	private volatile boolean running;
	
	private volatile ScheduledFuture<Duel> schedule;
	
	public Duel()
	{
		actorSave = new DuelPlayer();
		enemySave = new DuelPlayer();
	}
	
	/**
	 * Method cancel.
	 * @param disruption boolean
	 * @param separated boolean
	 */
	public synchronized void cancel(boolean disruption, boolean separated)
	{
		if (!isRunning())
		{
			return;
		}
		
		if (schedule != null)
		{
			schedule.cancel(false);
			schedule = null;
		}
		
		setRunning(false);
		final Player actor = getActor();
		final Player enemy = getEnemy();
		actor.setDuel(null);
		enemy.setDuel(null);
		
		if (!separated)
		{
			actor.updateColor(enemy);
			enemy.updateColor(actor);
			PacketManager.cancelTargetHp(actor, enemy);
			PacketManager.cancelTargetHp(enemy, actor);
		}
		
		Summon summon = actor.getSummon();
		
		if (summon != null)
		{
			summon.getAI().abortAttack();
		}
		
		summon = enemy.getSummon();
		
		if (summon != null)
		{
			summon.getAI().abortAttack();
		}
		
		if (separated || disruption)
		{
			actor.sendMessage(MessageType.THE_DUEL_HAS_ENDED);
			enemy.sendMessage(MessageType.THE_DUEL_HAS_ENDED);
		}
		else
		{
			actor.sendMessage(MessageType.CANCELED_TIME_IS_UP);
			enemy.sendMessage(MessageType.CANCELED_TIME_IS_UP);
		}
		
		pool.put(this);
	}
	
	/**
	 * Method cancel.
	 * @param player Player
	 */
	public synchronized void cancel(Player player)
	{
		if (!isRunning())
		{
			return;
		}
		
		synchronized (player)
		{
			player.setCurrentHp(1);
			finish();
		}
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		actor = null;
		enemy = null;
	}
	
	public synchronized void finish()
	{
		if (!isRunning())
		{
			return;
		}
		
		final Player actor = getActor();
		final Player enemy = getEnemy();
		actor.setDuel(null);
		enemy.setDuel(null);
		actor.updateColor(enemy);
		enemy.updateColor(actor);
		PacketManager.cancelTargetHp(actor, enemy);
		PacketManager.cancelTargetHp(enemy, actor);
		Summon summon = actor.getSummon();
		
		if (summon != null)
		{
			summon.getAI().abortAttack();
		}
		
		summon = enemy.getSummon();
		
		if (summon != null)
		{
			summon.getAI().abortAttack();
		}
		
		final SystemMessage packet = SystemMessage.getInstance(MessageType.WINNER_DEFEATED_LOSER_IN_A_DUEL);
		
		if (actor.getCurrentHp() > enemy.getCurrentHp())
		{
			packet.addWinner(actor.getName());
			packet.addLoser(enemy.getName());
			actor.sendMessage(MessageType.DUEL_WON);
			enemy.sendMessage(MessageType.DUEL_LOST);
		}
		else
		{
			packet.addWinner(enemy.getName());
			packet.addLoser(actor.getName());
			actor.sendMessage(MessageType.DUEL_LOST);
			enemy.sendMessage(MessageType.DUEL_WON);
		}
		
		packet.increaseSends();
		packet.increaseSends();
		actor.sendPacket(packet, false);
		enemy.sendPacket(packet, false);
		setRunning(false);
		actorSave.restore(actor);
		enemySave.restore(enemy);
		pool.put(this);
	}
	
	/**
	 * Method getActor.
	 * @return Player
	 */
	public Player getActor()
	{
		return actor;
	}
	
	/**
	 * Method getEnemy.
	 * @return Player
	 */
	public Player getEnemy()
	{
		return enemy;
	}
	
	/**
	 * Method getEnemy.
	 * @param player Player
	 * @return Player
	 */
	public Player getEnemy(Player player)
	{
		if (player == actor)
		{
			return enemy;
		}
		else if (player == enemy)
		{
			return actor;
		}
		
		return null;
	}
	
	/**
	 * Method isActive.
	 * @return boolean
	 */
	public boolean isActive()
	{
		return active;
	}
	
	/**
	 * Method isRunning.
	 * @return boolean
	 */
	public boolean isRunning()
	{
		return running;
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
		active = false;
	}
	
	@Override
	protected synchronized void runImpl()
	{
		if (!isRunning())
		{
			return;
		}
		
		final Player actor = getActor();
		final Player enemy = getEnemy();
		
		if ((actor.getDuel() != this) || (enemy.getDuel() != this))
		{
			log.warning("incorrect work duel.");
		}
		
		if (isActive())
		{
			cancel(false, false);
		}
		else
		{
			setActive(true);
			actor.updateColor(enemy);
			enemy.updateColor(actor);
			PacketManager.showTargetHp(actor, enemy);
			PacketManager.showTargetHp(enemy, actor);
			actorSave.save(actor);
			enemySave.save(enemy);
			final ExecutorManager executor = ExecutorManager.getInstance();
			schedule = executor.scheduleGeneral(this, 300000);
		}
	}
	
	/**
	 * Method setActive.
	 * @param active boolean
	 */
	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	/**
	 * Method setActor.
	 * @param actor Player
	 */
	public void setActor(Player actor)
	{
		this.actor = actor;
	}
	
	/**
	 * Method setEnemy.
	 * @param enemy Player
	 */
	public void setEnemy(Player enemy)
	{
		this.enemy = enemy;
	}
	
	/**
	 * Method setRunning.
	 * @param running boolean
	 */
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	/**
	 * Method update.
	 * @param skill Skill
	 * @param info AttackInfo
	 * @param attacker Character
	 * @param attacked Character
	 * @return boolean
	 */
	public boolean update(Skill skill, AttackInfo info, Character attacker, Character attacked)
	{
		final Duel duel = attacker.getDuel();
		
		if (!attacker.isSummon() && ((duel == null) || (duel != this)))
		{
			cancel(true, false);
		}
		else if ((duel != null) && (duel == this))
		{
			if (!info.isBlocked() && (info.getDamage() >= (attacked.getCurrentHp() - 1)))
			{
				attacked.setCurrentHp(1);
				finish();
				return true;
			}
		}
		else if (attacker.isSummon())
		{
			final Character owner = attacker.getOwner();
			
			if ((duel != null) && ((owner == null) || (owner.getDuel() != this)))
			{
				duel.cancel(true, false);
			}
			else if (!info.isBlocked() && (info.getDamage() >= (attacked.getCurrentHp() - 1)))
			{
				attacked.setCurrentHp(1);
				finish();
				return true;
			}
		}
		
		return false;
	}
}
