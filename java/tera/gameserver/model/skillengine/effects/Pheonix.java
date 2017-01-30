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
package tera.gameserver.model.skillengine.effects;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.EffectList;
import tera.gameserver.model.listeners.DieListener;
import tera.gameserver.network.serverpackets.CharDead;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class Pheonix extends AbstractEffect implements DieListener, Runnable
{
	/**
	 * Constructor for Pheonix.
	 * @param template EffectTemplate
	 * @param effector Character
	 * @param effected Character
	 * @param skill SkillTemplate
	 */
	public Pheonix(EffectTemplate template, Character effector, Character effected, SkillTemplate skill)
	{
		super(template, effector, effected, skill);
	}
	
	/**
	 * Method onActionTime.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#onActionTime()
	 */
	@Override
	public boolean onActionTime()
	{
		return true;
	}
	
	/**
	 * Method onDie.
	 * @param killer Character
	 * @param killed Character
	 * @see tera.gameserver.model.listeners.DieListener#onDie(Character, Character)
	 */
	@Override
	public void onDie(Character killer, Character killed)
	{
		final ExecutorManager executor = ExecutorManager.getInstance();
		
		try
		{
			killed.setCurrentHp(killed.getMaxHp());
			killed.updateHp();
			killed.broadcastPacket(CharDead.getInstance(killed, false));
		}
		
		finally
		{
			executor.execute(this);
		}
	}
	
	/**
	 * Method onExit.
	 * @see tera.gameserver.model.skillengine.Effect#onExit()
	 */
	@Override
	public void onExit()
	{
		final Character effected = getEffected();
		
		if (effected != null)
		{
			effected.removeDieListener(this);
		}
		
		super.onExit();
	}
	
	/**
	 * Method onStart.
	 * @see tera.gameserver.model.skillengine.Effect#onStart()
	 */
	@Override
	public void onStart()
	{
		final Character effected = getEffected();
		
		if (effected != null)
		{
			effected.addDieListener(this);
		}
		
		super.onStart();
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		final Character effector = getEffector();
		
		if (effector == null)
		{
			return;
		}
		
		final EffectList effectList = effector.getEffectList();
		effectList.lock();
		
		try
		{
			exit();
		}
		
		finally
		{
			effectList.unlock();
		}
	}
}
