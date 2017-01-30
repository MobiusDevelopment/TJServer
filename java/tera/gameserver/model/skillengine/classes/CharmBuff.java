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
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Formulas;
import tera.gameserver.model.worldobject.BonfireObject;
import tera.gameserver.network.serverpackets.CharmSmoke;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;
import tera.util.LocalObjects;

import rlib.util.Rnd;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class CharmBuff extends Buff
{
	private BonfireObject bonfire;
	
	/**
	 * Constructor for CharmBuff.
	 * @param template SkillTemplate
	 */
	public CharmBuff(SkillTemplate template)
	{
		super(template);
	}
	
	/**
	 * Method addEffects.
	 * @param effector Character
	 * @param effected Character
	 */
	@Override
	protected void addEffects(Character effector, Character effected)
	{
		final Player player = effected.getPlayer();
		
		if (player == null)
		{
			return;
		}
		
		final EffectTemplate[] effectTemplates = template.getEffectTemplates();
		
		if ((effectTemplates == null) || (effectTemplates.length == 0))
		{
			return;
		}
		
		final EffectTemplate target = effectTemplates[Rnd.nextInt(0, effectTemplates.length - 1)];
		final Formulas formulas = Formulas.getInstance();
		
		if (target.isOnCaster() || (formulas.calcEffect(effector, effected, target, this) < 0))
		{
			return;
		}
		
		runEffect(target.newInstance(effector, effected, template), effected);
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
			attacker.sendMessage(MessageType.CHARMS_CAN_ONLY_BE_USED_NEAR_A_CAMPFIRE);
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
		character.broadcastPacket(CharmSmoke.getInstance(bonfire));
		final LocalObjects local = LocalObjects.get();
		final Array<Character> targets = local.getNextCharList();
		addTargets(targets, character, bonfire.getX(), bonfire.getY(), bonfire.getZ());
		final Character[] array = targets.array();
		
		for (int i = 0, length = targets.size(); i < length; i++)
		{
			final Character target = array[i];
			
			if (target.isDead() || target.isInvul())
			{
				continue;
			}
			
			applySkill(character, target);
		}
	}
}