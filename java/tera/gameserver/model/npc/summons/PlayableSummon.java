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
package tera.gameserver.model.npc.summons;

import java.util.concurrent.ScheduledFuture;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.EmotionType;
import tera.gameserver.model.npc.playable.NpcAppearance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.CharDead;
import tera.gameserver.network.serverpackets.DeleteCharacter;
import tera.gameserver.network.serverpackets.NpcPlayableInfo;
import tera.gameserver.network.serverpackets.PlayerBattleStance;
import tera.gameserver.tables.NpcAppearanceTable;
import tera.gameserver.tasks.EmotionTask;
import tera.gameserver.templates.NpcTemplate;

import rlib.util.SafeTask;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class PlayableSummon extends Summon
{
	public static final int DEAD_TIME = 5000;
	
	private final SafeTask deleteTask;
	
	private volatile ScheduledFuture<SafeTask> schedule;
	
	private NpcAppearance appearance;
	
	/**
	 * Constructor for PlayableSummon.
	 * @param objectId int
	 * @param template NpcTemplate
	 */
	public PlayableSummon(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		final VarTable vars = template.getVars();
		final NpcAppearanceTable appearanceTable = NpcAppearanceTable.getInstance();
		setAppearance(appearanceTable.getAppearance(vars.getString("appearance")));
		deleteTask = new SafeTask()
		{
			@Override
			protected void runImpl()
			{
				deleteMe(DeleteCharacter.DISAPPEARS);
			}
		};
	}
	
	/**
	 * Method setOwner.
	 * @param owner Character
	 */
	@Override
	public void setOwner(Character owner)
	{
		super.setOwner(owner);
		
		if (owner != null)
		{
			setTitle(owner.getName());
		}
	}
	
	/**
	 * Method getAppearance.
	 * @return NpcAppearance
	 */
	@Override
	public NpcAppearance getAppearance()
	{
		return appearance;
	}
	
	/**
	 * Method setAppearance.
	 * @param appearance NpcAppearance
	 */
	public void setAppearance(NpcAppearance appearance)
	{
		if (appearance == null)
		{
			throw new IllegalArgumentException("not found appearance.");
		}
		
		this.appearance = appearance;
	}
	
	/**
	 * Method getModelId.
	 * @return int
	 */
	@Override
	public int getModelId()
	{
		return getTemplate().getIconId();
	}
	
	/**
	 * Method addMe.
	 * @param player Player
	 */
	@Override
	public void addMe(Player player)
	{
		player.sendPacket(NpcPlayableInfo.getInstance(this), true);
	}
	
	/**
	 * Method startBattleStance.
	 * @param enemy Character
	 * @return boolean
	 */
	@Override
	public boolean startBattleStance(Character enemy)
	{
		if (enemy == null)
		{
			if (!isBattleStanced())
			{
				return false;
			}
			
			broadcastPacket(PlayerBattleStance.getInstance(this, PlayerBattleStance.STANCE_OFF));
			setBattleStanced(false);
			return true;
		}
		if (isBattleStanced())
		{
			return false;
		}
		
		broadcastPacket(PlayerBattleStance.getInstance(this, PlayerBattleStance.STANCE_ON));
		setBattleStanced(true);
		return true;
	}
	
	@Override
	public void stopBattleStance()
	{
		broadcastPacket(PlayerBattleStance.getInstance(this, PlayerBattleStance.STANCE_OFF));
		setBattleStanced(false);
	}
	
	/**
	 * Method doDie.
	 * @param attacker Character
	 */
	@Override
	public void doDie(Character attacker)
	{
		super.doDie(attacker);
		broadcastPacket(CharDead.getInstance(this, true));
		final ExecutorManager executorManager = ExecutorManager.getInstance();
		setSchedule(executorManager.scheduleGeneral(deleteTask, DEAD_TIME));
	}
	
	/**
	 * Method deleteMe.
	 * @param type int
	 */
	@Override
	public void deleteMe(int type)
	{
		if (type == DeleteCharacter.DEAD)
		{
			return;
		}
		
		super.deleteMe(type);
	}
	
	/**
	 * Method getOwerturnId.
	 * @return int
	 */
	@Override
	public int getOwerturnId()
	{
		return 0x080F6C72;
	}
	
	/**
	 * Method isOwerturnImmunity.
	 * @return boolean
	 */
	@Override
	public boolean isOwerturnImmunity()
	{
		return false;
	}
	
	/**
	 * Method getTemplateType.
	 * @return int
	 */
	@Override
	public int getTemplateType()
	{
		return 0;
	}
	
	/**
	 * Method setSchedule.
	 * @param schedule ScheduledFuture<SafeTask>
	 */
	public void setSchedule(ScheduledFuture<SafeTask> schedule)
	{
		this.schedule = schedule;
	}
	
	/**
	 * Method getSchedule.
	 * @return ScheduledFuture<SafeTask>
	 */
	public ScheduledFuture<SafeTask> getSchedule()
	{
		return schedule;
	}
	
	@Override
	public void finishDead()
	{
		boolean isRun = false;
		ScheduledFuture<SafeTask> schedule = getSchedule();
		
		if (schedule != null)
		{
			synchronized (this)
			{
				schedule = getSchedule();
				
				if (schedule != null)
				{
					schedule.cancel(false);
					setSchedule(null);
					isRun = true;
				}
			}
		}
		
		if (isRun)
		{
			deleteTask.run();
		}
	}
	
	/**
	 * Method isBroadcastEndSkillForCollision.
	 * @return boolean
	 */
	@Override
	public boolean isBroadcastEndSkillForCollision()
	{
		return false;
	}
	
	/**
	 * Method getAutoEmotions.
	 * @return EmotionType[]
	 */
	@Override
	protected EmotionType[] getAutoEmotions()
	{
		return EmotionTask.PLAYER_TYPES;
	}
	
	/**
	 * Method getOwerturnTime.
	 * @return int
	 */
	@Override
	public int getOwerturnTime()
	{
		return 3000;
	}
}
