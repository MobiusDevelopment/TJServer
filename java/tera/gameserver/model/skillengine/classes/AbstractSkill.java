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
import tera.gameserver.model.MessageType;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.skillengine.Condition;
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.model.skillengine.Formulas;
import tera.gameserver.model.skillengine.OperateType;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.SkillName;
import tera.gameserver.model.skillengine.SkillRangeType;
import tera.gameserver.model.skillengine.SkillType;
import tera.gameserver.model.skillengine.TargetType;
import tera.gameserver.network.serverpackets.MoveSkill;
import tera.gameserver.network.serverpackets.SkillEnd;
import tera.gameserver.network.serverpackets.SkillStart;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 12.04.2012
 */
public abstract class AbstractSkill implements Skill
{
	public static final Skill[] EMPTY_SKILLS = new Skill[0];
	
	protected int castId;
	
	protected int applyOrder;
	
	protected float impactX;
	protected float impactY;
	protected float impactZ;
	
	protected SkillTemplate template;
	
	public AbstractSkill(SkillTemplate template)
	{
		this.template = template;
	}
	
	protected void addAggroTo(Character caster, Character target, int aggro)
	{
		final Array<Npc> hateList = target.getLocalHateList();
		
		if (!hateList.isEmpty())
		{
			final Npc[] array = hateList.array();
			
			for (int i = 0, length = hateList.size(); i < length; i++)
			{
				array[i].addAggro(caster, aggro, false);
			}
		}
	}
	
	protected void addEffects(Character caster)
	{
		final EffectTemplate[] effectTemplates = template.getEffectTemplates();
		
		if ((effectTemplates == null) || (effectTemplates.length == 0))
		{
			return;
		}
		
		final Formulas formulas = Formulas.getInstance();
		
		for (EffectTemplate effectTemplate : effectTemplates)
		{
			final EffectTemplate temp = effectTemplate;
			
			if (!temp.isOnCaster() || (formulas.calcEffect(caster, caster, temp, this) < 0))
			{
				continue;
			}
			
			runEffect(temp.newInstance(caster, caster, template), caster);
		}
		
		return;
	}
	
	protected void addEffects(Character effector, Character effected)
	{
		final EffectTemplate[] effectTemplates = template.getEffectTemplates();
		
		if ((effectTemplates == null) || (effectTemplates.length == 0))
		{
			return;
		}
		
		final Formulas formulas = Formulas.getInstance();
		
		for (EffectTemplate temp : effectTemplates)
		{
			if (temp.isOnCaster())
			{
				continue;
			}
			
			final float mod = formulas.calcEffect(effector, effected, temp, this);
			
			if (mod < 0)
			{
				continue;
			}
			
			final Effect effect = temp.newInstance(effector, effected, template);
			
			if (effect.isDynamicTime())
			{
				effect.setPeriod((int) Math.max(temp.getTime() * Math.min(mod, 1), 1));
			}
			
			if (effect.isDynamicCount())
			{
				effect.setCount((int) Math.max(temp.getCount() * Math.min(mod, 1), 1));
			}
			
			runEffect(effect, effected);
		}
	}
	
	@Override
	public void addTargets(Array<Character> targets, Character attacker, float targetX, float targetY, float targetZ)
	{
		getTargetType().getTargets(targets, this, targetX, targetY, targetZ, attacker);
	}
	
	@Override
	public AttackInfo applySkill(Character attacker, Character target)
	{
		return null;
	}
	
	@Override
	public int blockMpConsume(int damage)
	{
		return 0;
	}
	
	@Override
	public boolean checkCondition(Character attacker, float targetX, float targetY, float targetZ)
	{
		if (attacker.isSkillDisabled(this))
		{
			return false;
		}
		
		if (attacker.isRooted() && (isEvasion() || (getMoveDistance() != 0)))
		{
			attacker.sendMessage("Cannot use while immobilized.");
			return false;
		}
		
		final Skill activate = attacker.getActivateSkill();
		
		if (!isForceCast() && (activate != null) && (activate != this))
		{
			attacker.sendMessage(MessageType.YOU_CANNOT_USE_THAT_SKILL_AT_THE_MOMENT);
			return false;
		}
		
		if (attacker.getCurrentMp() < template.getMpConsume())
		{
			attacker.sendMessage(MessageType.NOT_ENOUGH_MP);
			return false;
		}
		
		if (attacker.getCurrentHp() <= (template.getHpConsume() + 1))
		{
			attacker.sendMessage("Not enough HP.");
			return false;
		}
		
		if (template.getItemIdConsume() != 0)
		{
			final Inventory inventory = attacker.getInventory();
			
			if (inventory == null)
			{
				return false;
			}
			
			final int count = inventory.getItemCount(template.getItemIdConsume());
			
			if (template.getItemCountConsume() > count)
			{
				attacker.sendMessage("You do not have the necessary items in inventory.");
				return false;
			}
		}
		
		final Condition condition = template.getCondition();
		
		if ((condition != null) && !condition.test(attacker, null, this, 0))
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public void endSkill(Character attacker, float targetX, float targetY, float targetZ, boolean force)
	{
		attacker.broadcastPacket(SkillEnd.getInstance(attacker, castId, template.getIconId()));
		template.removeCastFuncs(attacker);
		addEffects(attacker);
	}
	
	@Override
	public void finalyze()
	{
	}
	
	@Override
	public void fold()
	{
		template.put(this);
	}
	
	@Override
	public int getAggroPoint()
	{
		return template.getAggroPoint();
	}
	
	@Override
	public int getCastCount()
	{
		return template.getCastCount();
	}
	
	@Override
	public int getCastHeading()
	{
		final int[] heading = template.getCastHeading();
		return heading[Math.min(applyOrder, heading.length - 1)];
	}
	
	@Override
	public int getCastId()
	{
		return castId;
	}
	
	@Override
	public int getCastMaxRange()
	{
		return template.getCastMaxRange();
	}
	
	@Override
	public int getCastMinRange()
	{
		return template.getCastMinRange();
	}
	
	@Override
	public int getChance()
	{
		return template.getChance();
	}
	
	@Override
	public int getClassId()
	{
		return template.getClassId();
	}
	
	@Override
	public Condition getCondition()
	{
		return template.getCondition();
	}
	
	@Override
	public int getDamageId()
	{
		return template.getDamageId();
	}
	
	@Override
	public int getDegree()
	{
		final int[] degrees = template.getDegree();
		return degrees[Math.min(applyOrder, degrees.length - 1)];
	}
	
	@Override
	public int getDelay()
	{
		return template.getDelay();
	}
	
	@Override
	public EffectTemplate[] getEffectTemplates()
	{
		return template.getEffectTemplates();
	}
	
	@Override
	public String getGroup()
	{
		return template.getGroup();
	}
	
	@Override
	public int getHeading()
	{
		final int[] heading = template.getHeading();
		return heading[Math.min(applyOrder, heading.length - 1)];
	}
	
	@Override
	public int getHitTime()
	{
		return template.getHitTime();
	}
	
	@Override
	public int getHpConsume()
	{
		return template.getHpConsume();
	}
	
	@Override
	public int getIconId()
	{
		return template.getIconId();
	}
	
	@Override
	public int getId()
	{
		return template.getId();
	}
	
	@Override
	public final float getImpactX()
	{
		return impactX;
	}
	
	@Override
	public final float getImpactY()
	{
		return impactY;
	}
	
	@Override
	public final float getImpactZ()
	{
		return impactZ;
	}
	
	@Override
	public int getInterval()
	{
		final int[] intervals = template.getInterval();
		return intervals[Math.min(applyOrder, intervals.length - 1)];
	}
	
	@Override
	public int getItemCount()
	{
		return template.getItemCount();
	}
	
	@Override
	public long getItemCountConsume()
	{
		return template.getItemCountConsume();
	}
	
	@Override
	public int getItemId()
	{
		return template.getItemId();
	}
	
	@Override
	public int getItemIdConsume()
	{
		return template.getItemIdConsume();
	}
	
	@Override
	public int getLevel()
	{
		return template.getLevel();
	}
	
	@Override
	public int getMaxTargets()
	{
		return template.getMaxTargets();
	}
	
	@Override
	public int getMinRange()
	{
		return template.getMinRange();
	}
	
	@Override
	public int getMoveDelay()
	{
		return template.getMoveDelay();
	}
	
	@Override
	public int getMoveDistance()
	{
		return template.getMoveDistance();
	}
	
	@Override
	public int getMoveHeading()
	{
		return template.getMoveHeding();
	}
	
	@Override
	public int getMoveTime()
	{
		return template.getMoveTime();
	}
	
	@Override
	public int getMpConsume()
	{
		return template.getMpConsume();
	}
	
	@Override
	public String getName()
	{
		return template.getName();
	}
	
	@Override
	public OperateType getOperateType()
	{
		return template.getOperateType();
	}
	
	@Override
	public float getOwerturnMod()
	{
		return template.getOwerturnMod();
	}
	
	@Override
	public int getPower()
	{
		final int[] powers = template.getPower();
		return powers[Math.min(applyOrder, powers.length - 1)];
	}
	
	@Override
	public int getRadius()
	{
		final int[] redius = template.getRadius();
		return redius[Math.min(applyOrder, redius.length - 1)];
	}
	
	@Override
	public int getRange()
	{
		final int[] ranges = template.getRange();
		return ranges[Math.min(applyOrder, ranges.length - 1)];
	}
	
	@Override
	public SkillRangeType getRangeType()
	{
		return template.getRangeType();
	}
	
	@Override
	public int getReuseDelay(Character caster)
	{
		if (isStaticReuseDelay())
		{
			return template.getReuseDelay();
		}
		
		final SkillRangeType rangeType = getRangeType();
		return (int) (template.getReuseDelay() * caster.calcStat(rangeType.getReuseStat(), 1, null, null) * rangeType.getReuseMod());
	}
	
	@Override
	public int getReuseId()
	{
		return template.getReuseId();
	}
	
	@Override
	public int[] getReuseIds()
	{
		return template.getReuseIds();
	}
	
	@Override
	public SkillName getSkillName()
	{
		return template.getSkillName();
	}
	
	@Override
	public SkillType getSkillType()
	{
		return template.getSkillType();
	}
	
	@Override
	public int getSpeed()
	{
		return template.getSpeed();
	}
	
	@Override
	public int getStage()
	{
		final int[] stages = template.getStage();
		return stages[Math.min(applyOrder, stages.length - 1)];
	}
	
	@Override
	public TargetType getTargetType()
	{
		final TargetType[] types = template.getTargetType();
		return types[Math.min(applyOrder, types.length - 1)];
	}
	
	@Override
	public final SkillTemplate getTemplate()
	{
		return template;
	}
	
	@Override
	public int getTransformId()
	{
		return template.getTransformId();
	}
	
	@Override
	public int getWidth()
	{
		final int[] widths = template.getWidth();
		return widths[Math.min(applyOrder, widths.length - 1)];
	}
	
	@Override
	public int hashCode()
	{
		return template.getId();
	}
	
	@Override
	public boolean hasPrevSkillName(SkillName skillName)
	{
		return Arrays.contains(template.getPrevSkillNames(), skillName);
	}
	
	@Override
	public boolean isActive()
	{
		final OperateType operateType = template.getOperateType();
		return (operateType == OperateType.ACTIVE) || (operateType == OperateType.ACTIVATE) || (operateType == OperateType.CHARGE) || (operateType == OperateType.LOCK_ON);
	}
	
	@Override
	public boolean isAltCast()
	{
		return template.isAltCast();
	}
	
	@Override
	public boolean isApply()
	{
		final boolean[] apply = template.isApply();
		return apply[Math.min(applyOrder, apply.length - 1)];
	}
	
	@Override
	public boolean isBlockingMove()
	{
		return template.isBlockingMove();
	}
	
	@Override
	public boolean isCanceable()
	{
		return true;
	}
	
	@Override
	public boolean isCanOwerturn()
	{
		return template.isCanOwerturn();
	}
	
	@Override
	public boolean isCastToMove()
	{
		return template.isCastToMove();
	}
	
	@Override
	public boolean isEvasion()
	{
		return template.isEvasion();
	}
	
	@Override
	public boolean isForceCast()
	{
		return template.isForceCast();
	}
	
	@Override
	public boolean isHasFast()
	{
		return template.isHasFast();
	}
	
	@Override
	public boolean isIgnoreBarrier()
	{
		return template.isIgnoreBarrier();
	}
	
	@Override
	public boolean isImplemented()
	{
		return template.isImplemented();
	}
	
	@Override
	public boolean isNoCaster()
	{
		return template.isNoCaster();
	}
	
	@Override
	public boolean isOffensive()
	{
		switch (template.getSkillType())
		{
			case TRANSFORM:
			case TELEPORT_JUMP:
			case SPAWN_ITEM:
			case SPAWN_BONFIRE:
			case RESURRECT:
			case RESTART_BONFIRE:
			case PREPARE_MANAHEAL:
			case PARTY_SUMMON:
			case MANAHEAL_PERCENT:
			case HEAL_PERCENT:
			case HEAL:
			case ITEM_BUFF:
			case JUMP:
			case DEFENSE:
			case LANCER_DEFENSE:
			case MANAHEAL:
			case CLEAR_DEBUFF:
			case CANCEL_OWERTURN:
			case CHARGE_MANA_HEAL:
				return false;
			
			default:
				break;
		}
		
		return true;
	}
	
	@Override
	public boolean isOneTarget()
	{
		switch (getTargetType())
		{
			case TARGET_BACK_RAIL:
			case TARGET_RAIL:
			case TARGET_ONE:
			case TARGET_AREA:
				return true;
			
			default:
				return false;
		}
	}
	
	@Override
	public boolean isPassive()
	{
		return template.getOperateType() == OperateType.PASSIVE;
	}
	
	@Override
	public boolean isRush()
	{
		return template.isRush();
	}
	
	@Override
	public boolean isShieldIgnore()
	{
		return template.isShieldIgnore();
	}
	
	@Override
	public boolean isShortSkill()
	{
		return template.isShortSkill();
	}
	
	@Override
	public boolean isStaticCast()
	{
		return template.isStaticCast();
	}
	
	@Override
	public boolean isStaticInterval()
	{
		return template.isStaticInterval();
	}
	
	@Override
	public boolean isToggle()
	{
		return template.isToggle();
	}
	
	@Override
	public boolean isTrigger()
	{
		return template.isTrigger();
	}
	
	@Override
	public boolean isVisibleOnSkillList()
	{
		return template.isVisibleOnSkillList();
	}
	
	@Override
	public boolean isWaitable()
	{
		return true;
	}
	
	@Override
	public void reinit()
	{
	}
	
	protected void runEffect(Effect effect, Character effected)
	{
		if (effect.getPeriod() != 0)
		{
			effected.addEffect(effect);
		}
		else
		{
			effect.onStart();
			effect.onActionTime();
			effect.onExit();
			effect.fold();
		}
	}
	
	@Override
	public final void setImpactX(float impactX)
	{
		this.impactX = impactX;
	}
	
	@Override
	public final void setImpactY(float impactY)
	{
		this.impactY = impactY;
	}
	
	@Override
	public final void setImpactZ(float impactZ)
	{
		this.impactZ = impactZ;
	}
	
	@Override
	public void startSkill(Character attacker, float targetX, float targetY, float targetZ)
	{
		template.addCastFuncs(attacker);
		applyOrder = 0;
		castId = attacker.nextCastId();
		attacker.broadcastPacket(SkillStart.getInstance(attacker, template.getIconId(), castId, 0));
		
		if (isRush())
		{
			final Character target = attacker.getTarget();
			
			if (target != null)
			{
				attacker.broadcastPacket(MoveSkill.getInstance(attacker, target));
			}
			else
			{
				attacker.broadcastPacket(MoveSkill.getInstance(attacker, targetX, targetY, targetZ));
			}
		}
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " template = " + template;
	}
	
	@Override
	public void useSkill(Character character, float targetX, float targetY, float targetZ)
	{
		applyOrder++;
	}
	
	@Override
	public boolean isStaticReuseDelay()
	{
		return template.isStaticReuseDelay();
	}
	
	@Override
	public int getSpeedOffset()
	{
		return template.getSpeedOffset();
	}
	
	@Override
	public boolean isCorrectableTarget()
	{
		return template.isCorrectableTarget();
	}
	
	@Override
	public boolean isTargetSelf()
	{
		return getTargetType() == TargetType.TARGET_SELF;
	}
}
