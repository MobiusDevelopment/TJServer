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
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.AttackInfo;
import tera.gameserver.model.Character;
import tera.gameserver.model.EffectList;
import tera.gameserver.model.listeners.DamageListener;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class DamageAbsorption extends AbstractEffect implements DamageListener, Runnable
{
	
	private int limit;
	
	private final int consume;
	
	/**
	 * Constructor for DamageAbsorption.
	 * @param template EffectTemplate
	 * @param effector Character
	 * @param effected Character
	 * @param skill SkillTemplate
	 */
	public DamageAbsorption(EffectTemplate template, Character effector, Character effected, SkillTemplate skill)
	{
		super(template, effector, effected, skill);
		consume = (int) template.getValue();
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
	 * Method onDamage.
	 * @param attacker Character
	 * @param attacked Character
	 * @param info AttackInfo
	 * @param skill Skill
	 * @see tera.gameserver.model.listeners.DamageListener#onDamage(Character, Character, AttackInfo, Skill)
	 */
	@Override
	public void onDamage(Character attacker, Character attacked, AttackInfo info, Skill skill)
	{
		if ((limit < 1) || info.isNoDamage())
		{
			return;
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		final int current = attacked.getCurrentMp();
		
		if ((current < 1) && (consume > 0))
		{
			executor.execute(this);
			return;
		}
		
		final int damage = info.getDamage();
		int abs = damage > limit ? limit : damage;
		
		if (consume > 0)
		{
			int mp = Math.max(1, abs / consume);
			
			if (current < mp)
			{
				mp = current;
				abs = mp * consume;
			}
			
			attacked.setCurrentMp(current - mp);
			final ObjectEventManager eventManager = ObjectEventManager.getInstance();
			eventManager.notifyMpChanged(attacked);
		}
		
		info.setDamage(Math.max(damage - abs, 0));
		limit -= abs;
		
		if (limit < 1)
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
			effected.removeDamageListener(this);
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
			effected.addDamageListener(this);
		}
		
		limit = template.getPower();
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
