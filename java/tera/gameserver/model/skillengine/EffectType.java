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
package tera.gameserver.model.skillengine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.effects.AuraManaDamOverTime;
import tera.gameserver.model.skillengine.effects.Buff;
import tera.gameserver.model.skillengine.effects.CancelEffect;
import tera.gameserver.model.skillengine.effects.CharmBuff;
import tera.gameserver.model.skillengine.effects.DamOverTime;
import tera.gameserver.model.skillengine.effects.DamOverTimePercent;
import tera.gameserver.model.skillengine.effects.DamageAbsorption;
import tera.gameserver.model.skillengine.effects.DamageTransfer;
import tera.gameserver.model.skillengine.effects.Debuff;
import tera.gameserver.model.skillengine.effects.Heal;
import tera.gameserver.model.skillengine.effects.HealMod;
import tera.gameserver.model.skillengine.effects.HealOverTime;
import tera.gameserver.model.skillengine.effects.Invul;
import tera.gameserver.model.skillengine.effects.ManaHealOverTime;
import tera.gameserver.model.skillengine.effects.ManaHealOverTimePercent;
import tera.gameserver.model.skillengine.effects.NoBattleEffect;
import tera.gameserver.model.skillengine.effects.NoOwerturnEffect;
import tera.gameserver.model.skillengine.effects.PercentHealOverTime;
import tera.gameserver.model.skillengine.effects.Pheonix;
import tera.gameserver.model.skillengine.effects.Root;
import tera.gameserver.model.skillengine.effects.SkillBlocking;
import tera.gameserver.model.skillengine.effects.Stun;
import tera.gameserver.model.skillengine.effects.Turn;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public enum EffectType
{
	
	BUFF(Buff.class),
	
	CHARM_BUFF(CharmBuff.class),
	
	HEAL(Heal.class),
	
	HEAL_MOD(HealMod.class),
	
	HEAL_OVER_TIME(HealOverTime.class),
	
	PERCENT_HEAL_OVER_TIME(PercentHealOverTime.class),
	
	DEBUFF(Debuff.class),
	
	STUN(Stun.class),
	
	SKILL_BLOCKING(SkillBlocking.class),
	
	DAM_OVER_TIME(DamOverTime.class),
	
	DAM_OVER_TIME_PERCENT(DamOverTimePercent.class),
	
	NO_BATTLE_EFFECT(NoBattleEffect.class),
	
	NO_OWERTURN_EFFECT(NoOwerturnEffect.class),
	
	MANA_HEAL_OVER_TIME(ManaHealOverTime.class),
	
	MANA_HEAL_OVER_TIME_PERCENT(ManaHealOverTimePercent.class),
	
	AURA_MANA_DAM_OVER_TIME(AuraManaDamOverTime.class),
	
	ROOT(Root.class),
	
	DAMAGE_ABSORPTION(DamageAbsorption.class),
	
	PHEONIX(Pheonix.class),
	
	INVUL(Invul.class),
	
	TURN(Turn.class),
	
	DAMAGE_TRANSFER(DamageTransfer.class),
	
	CANCEL_EFFECT(CancelEffect.class);
	
	private Constructor<? extends Effect> constructor;
	
	/**
	 * Constructor for EffectType.
	 * @param effectClass Class<? extends Effect>
	 */
	private EffectType(Class<? extends Effect> effectClass)
	{
		try
		{
			constructor = effectClass.getConstructor(EffectTemplate.class, Character.class, Character.class, SkillTemplate.class);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Method newInstance.
	 * @param template EffectTemplate
	 * @param effector Character
	 * @param effected Character
	 * @param skill SkillTemplate
	 * @return Effect
	 */
	public Effect newInstance(EffectTemplate template, Character effector, Character effected, SkillTemplate skill)
	{
		try
		{
			return constructor.newInstance(template, effector, effected, skill);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			return null;
		}
	}
}
