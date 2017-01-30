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

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.Duel;
import tera.gameserver.network.serverpackets.Damage;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class DamOverTime extends AbstractEffect
{
	/**
	 * Constructor for DamOverTime.
	 * @param template EffectTemplate
	 * @param effector Character
	 * @param effected Character
	 * @param skill SkillTemplate
	 */
	public DamOverTime(EffectTemplate template, Character effector, Character effected, SkillTemplate skill)
	{
		super(template, effector, effected, skill);
	}
	
	/**
	 * Method getDamage.
	 * @param effector Character
	 * @param effected Character
	 * @return int
	 */
	protected int getDamage(Character effector, Character effected)
	{
		return getTemplate().getPower();
	}
	
	/**
	 * Method onActionTime.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#onActionTime()
	 */
	@Override
	public boolean onActionTime()
	{
		final Character effected = getEffected();
		final Character effector = getEffector();
		
		if ((effected == null) || (effector == null))
		{
			LOGGER.warning(this, new Exception("not found effector of effected"));
			return false;
		}
		
		if (effected.isDead())
		{
			return false;
		}
		
		final int damage = getDamage(effector, effected);
		final Duel duel = effected.getDuel();
		
		if (damage > (effected.getCurrentHp() - 2))
		{
			return false;
		}
		
		if ((damage > effected.getCurrentHp()) && (duel != null) && (effector.getDuel() == duel))
		{
			duel.finish();
			return false;
		}
		
		effected.setCurrentHp(effected.getCurrentHp() - damage);
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyAttacked(effected, effector, null, damage, false);
		eventManager.notifyHpChanged(effected);
		effected.broadcastPacket(Damage.getInstance(effector, effected, getSkillId(), damage, false, false, Damage.DAMAGE));
		return true;
	}
}
