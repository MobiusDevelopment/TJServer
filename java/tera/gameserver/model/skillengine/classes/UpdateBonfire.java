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

import tera.gameserver.model.Character;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.TObject;
import tera.gameserver.model.World;
import tera.gameserver.model.worldobject.BonfireObject;
import tera.gameserver.templates.SkillTemplate;
import tera.util.LocalObjects;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class UpdateBonfire extends AbstractSkill
{
	
	private BonfireObject bonfire;
	
	/**
	 * Constructor for UpdateBonfire.
	 * @param template SkillTemplate
	 */
	public UpdateBonfire(SkillTemplate template)
	{
		super(template);
	}
	
	/**
	 * Method checkCondition.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Skill#checkCondition(Character, float, float, float)
	 */
	@Override
	public boolean checkCondition(Character attacker, float targetX, float targetY, float targetZ)
	{
		final LocalObjects local = LocalObjects.get();
		final Array<TObject> objects = World.getAround(BonfireObject.class, local.getNextObjectList(), attacker, 30 + attacker.getGeomRadius());
		
		if (objects.isEmpty())
		{
			attacker.sendMessage(MessageType.YOU_CANT_USE_FIREWOOD_RIGHT_NOW);
			return false;
		}
		
		setBonfire((BonfireObject) objects.first());
		return super.checkCondition(attacker, targetX, targetY, targetZ);
	}
	
	/**
	 * Method getBonfire.
	 * @return BonfireObject
	 */
	private BonfireObject getBonfire()
	{
		return bonfire;
	}
	
	/**
	 * Method setBonfire.
	 * @param bonfire BonfireObject
	 */
	private void setBonfire(BonfireObject bonfire)
	{
		this.bonfire = bonfire;
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
		final BonfireObject bonfire = getBonfire();
		
		if (bonfire != null)
		{
			attacker.setHeading(attacker.calcHeading(bonfire.getX(), bonfire.getY()));
		}
		
		super.startSkill(attacker, targetX, targetY, targetZ);
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
		final BonfireObject bonfire = getBonfire();
		
		if (bonfire == null)
		{
			return;
		}
		
		setBonfire(null);
		bonfire.restart();
	}
}
