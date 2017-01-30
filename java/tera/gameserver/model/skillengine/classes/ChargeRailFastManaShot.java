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
package tera.gameserver.model.skillengine.classes;

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.AttackInfo;
import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.shots.FastShot;
import tera.gameserver.network.serverpackets.Damage;
import tera.gameserver.network.serverpackets.StartFastShot;
import tera.gameserver.templates.SkillTemplate;
import tera.util.LocalObjects;

/**
 * @author Ronn
 */
public class ChargeRailFastManaShot extends ChargeDam
{
	/**
	 * Constructor for ChargeRailFastManaShot.
	 * @param template SkillTemplate
	 */
	public ChargeRailFastManaShot(SkillTemplate template)
	{
		super(template);
	}
	
	/**
	 * Method applySkill.
	 * @param attacker Character
	 * @param target Character
	 * @return AttackInfo
	 * @see tera.gameserver.model.skillengine.Skill#applySkill(Character, Character)
	 */
	@Override
	public AttackInfo applySkill(Character attacker, Character target)
	{
		final LocalObjects local = LocalObjects.get();
		final AttackInfo info = local.getNextAttackInfo();
		info.setDamage(getPower());
		target.broadcastPacket(Damage.getInstance(attacker, target, getDamageId(), info.getDamage(), false, false, Damage.DAMAGE));
		attacker.effectHealMp(info.getDamage(), target);
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyAttack(attacker, target, this, info.getDamage(), false);
		eventManager.notifyAttacked(target, attacker, this, info.getDamage(), false);
		return info;
	}
	
	/**
	 * Method startSkill.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @see tera.gameserver.model.skillengine.Skill#startSkill(Character, float, float, float)
	 */
	@Override
	public void startSkill(Character attacker, float targetX, float targetY, float targetZ)
	{
		super.startSkill(attacker, targetX, targetY, targetZ);
		attacker.broadcastPacket(StartFastShot.getInstance(attacker, this, castId, targetX, targetY, targetZ));
	}
	
	/**
	 * Method useSkill.
	 * @param character Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @see tera.gameserver.model.skillengine.Skill#useSkill(Character, float, float, float)
	 */
	@Override
	public void useSkill(Character character, float targetX, float targetY, float targetZ)
	{
		setImpactX(character.getX());
		setImpactY(character.getY());
		setImpactZ(character.getZ());
		FastShot.startShot(character, this, targetX, targetY, targetZ);
	}
}
