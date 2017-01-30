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
package tera.gameserver.model.npc.playable;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.CharDead;
import tera.gameserver.network.serverpackets.DeleteCharacter;
import tera.gameserver.network.serverpackets.NpcPlayableInfo;
import tera.gameserver.network.serverpackets.PlayerBattleStance;
import tera.gameserver.tables.NpcAppearanceTable;
import tera.gameserver.templates.NpcTemplate;

import rlib.util.SafeTask;
import rlib.util.Strings;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class PlayableNpc extends Npc
{
	private final SafeTask deleteTask;
	private NpcAppearance appearance;
	
	/**
	 * Constructor for PlayableNpc.
	 * @param objectId int
	 * @param template NpcTemplate
	 */
	public PlayableNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		final VarTable vars = template.getVars();
		final NpcAppearanceTable appearanceTable = NpcAppearanceTable.getInstance();
		setAppearance(appearanceTable.getAppearance(vars.getString("appearance")));
		setTitle(vars.getString("title", Strings.EMPTY));
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
	 * Method addMe.
	 * @param player Player
	 */
	@Override
	public void addMe(Player player)
	{
		player.sendPacket(NpcPlayableInfo.getInstance(this), true);
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
		ExecutorManager.getInstance().scheduleGeneral(deleteTask, 10000);
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
	 * Method isBroadcastEndSkillForCollision.
	 * @return boolean
	 */
	@Override
	public boolean isBroadcastEndSkillForCollision()
	{
		return false;
	}
}