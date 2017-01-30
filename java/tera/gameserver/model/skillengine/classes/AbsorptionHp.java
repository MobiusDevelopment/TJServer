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

import tera.gameserver.model.AttackInfo;
import tera.gameserver.model.Character;
import tera.gameserver.network.serverpackets.RequestSkillStart;
import tera.gameserver.network.serverpackets.SkillEnd;
import tera.gameserver.templates.SkillTemplate;

import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.wraps.Wrap;
import rlib.util.wraps.WrapType;
import rlib.util.wraps.Wraps;

/**
 * @author Ronn
 */
public class AbsorptionHp extends PrepareNextSkill
{
	
	private int absHp;
	
	/**
	 * Constructor for AbsorptionHp.
	 * @param template SkillTemplate
	 */
	public AbsorptionHp(SkillTemplate template)
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
		final AttackInfo info = super.applySkill(attacker, target);
		
		if (!info.isBlocked())
		{
			absHp += info.getDamage();
		}
		
		return info;
	}
	
	/**
	 * Method endSkill.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param force boolean
	 * @see tera.gameserver.model.skillengine.Skill#endSkill(Character, float, float, float, boolean)
	 */
	@Override
	public void endSkill(Character attacker, float targetX, float targetY, float targetZ, boolean force)
	{
		template.removeCastFuncs(attacker);
		attacker.setActivateSkill(null);
		
		if ((absHp < 1) || force || attacker.isAttackBlocking() || attacker.isOwerturned())
		{
			attacker.broadcastPacket(SkillEnd.getInstance(attacker, castId, template.getId()));
			return;
		}
		
		final int attackId = template.getId() + template.getOffsetId();
		final Table<IntKey, Wrap> variables = attacker.getSkillVariables();
		final Wrap wrap = variables.get(attackId);
		
		if (!((wrap == null) || (wrap.getWrapType() != WrapType.INTEGER)))
		{
			wrap.setInt(absHp);
		}
		else
		{
			variables.put(attackId, Wraps.newIntegerWrap(absHp, true));
		}
		
		attacker.setCastId(castId);
		attacker.sendPacket(RequestSkillStart.getInstance(attackId), true);
	}
	
	/**
	 * Method getPower.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Skill#getPower()
	 */
	@Override
	public int getPower()
	{
		return super.getPower() * getCastCount();
	}
	
	/**
	 * Method isCanceable.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Skill#isCanceable()
	 */
	@Override
	public boolean isCanceable()
	{
		return applyOrder > 0;
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
		absHp = 0;
	}
}
