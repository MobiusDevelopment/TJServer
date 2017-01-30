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

import tera.gameserver.model.skillengine.classes.AbsorptionHp;
import tera.gameserver.model.skillengine.classes.Aggro;
import tera.gameserver.model.skillengine.classes.AutoSingleShot;
import tera.gameserver.model.skillengine.classes.Buff;
import tera.gameserver.model.skillengine.classes.CancelCast;
import tera.gameserver.model.skillengine.classes.CancelOwerturn;
import tera.gameserver.model.skillengine.classes.ChanceEffect;
import tera.gameserver.model.skillengine.classes.Charge;
import tera.gameserver.model.skillengine.classes.ChargeComplexStrike;
import tera.gameserver.model.skillengine.classes.ChargeManaHeal;
import tera.gameserver.model.skillengine.classes.ChargeRailFastManaShot;
import tera.gameserver.model.skillengine.classes.ChargeSingleShot;
import tera.gameserver.model.skillengine.classes.ChargeSingleSlowShot;
import tera.gameserver.model.skillengine.classes.ChargeStrike;
import tera.gameserver.model.skillengine.classes.ChargeVampStrike;
import tera.gameserver.model.skillengine.classes.CharmBuff;
import tera.gameserver.model.skillengine.classes.ClearBuff;
import tera.gameserver.model.skillengine.classes.ClearDebuff;
import tera.gameserver.model.skillengine.classes.ComplexModStrike;
import tera.gameserver.model.skillengine.classes.ComplexStrike;
import tera.gameserver.model.skillengine.classes.ConterStrike;
import tera.gameserver.model.skillengine.classes.Cyclone;
import tera.gameserver.model.skillengine.classes.Debuff;
import tera.gameserver.model.skillengine.classes.Defense;
import tera.gameserver.model.skillengine.classes.Effect;
import tera.gameserver.model.skillengine.classes.Heal;
import tera.gameserver.model.skillengine.classes.HealPercent;
import tera.gameserver.model.skillengine.classes.ItemBuff;
import tera.gameserver.model.skillengine.classes.Jump;
import tera.gameserver.model.skillengine.classes.LancerDefense;
import tera.gameserver.model.skillengine.classes.Leash;
import tera.gameserver.model.skillengine.classes.LockOn;
import tera.gameserver.model.skillengine.classes.LockOnEffect;
import tera.gameserver.model.skillengine.classes.LockOnHeal;
import tera.gameserver.model.skillengine.classes.LockOnStrike;
import tera.gameserver.model.skillengine.classes.LockOnStrikePartyBuff;
import tera.gameserver.model.skillengine.classes.ManaGainStrike;
import tera.gameserver.model.skillengine.classes.ManaHeal;
import tera.gameserver.model.skillengine.classes.ManaHealOnAbsorptionHp;
import tera.gameserver.model.skillengine.classes.ManaHealPercent;
import tera.gameserver.model.skillengine.classes.ManaSingleShot;
import tera.gameserver.model.skillengine.classes.ManaStrike;
import tera.gameserver.model.skillengine.classes.Mount;
import tera.gameserver.model.skillengine.classes.NpcSingleFastShot;
import tera.gameserver.model.skillengine.classes.NpcSingleShot;
import tera.gameserver.model.skillengine.classes.NpcSingleSlowShot;
import tera.gameserver.model.skillengine.classes.OwerturnedStrike;
import tera.gameserver.model.skillengine.classes.PartySummon;
import tera.gameserver.model.skillengine.classes.Passive;
import tera.gameserver.model.skillengine.classes.PrepareManaHeal;
import tera.gameserver.model.skillengine.classes.PrepareStrike;
import tera.gameserver.model.skillengine.classes.PvPMode;
import tera.gameserver.model.skillengine.classes.RestoreStamina;
import tera.gameserver.model.skillengine.classes.Resurrect;
import tera.gameserver.model.skillengine.classes.SingleShot;
import tera.gameserver.model.skillengine.classes.SingleSlowShot;
import tera.gameserver.model.skillengine.classes.SlayerFuryStrike;
import tera.gameserver.model.skillengine.classes.SpawnBonfire;
import tera.gameserver.model.skillengine.classes.SpawnBuffTrap;
import tera.gameserver.model.skillengine.classes.SpawnItem;
import tera.gameserver.model.skillengine.classes.SpawnSmokeSummon;
import tera.gameserver.model.skillengine.classes.SpawnSummon;
import tera.gameserver.model.skillengine.classes.SpawnTrap;
import tera.gameserver.model.skillengine.classes.StageStrike;
import tera.gameserver.model.skillengine.classes.Strike;
import tera.gameserver.model.skillengine.classes.SummonAbort;
import tera.gameserver.model.skillengine.classes.SummonAttack;
import tera.gameserver.model.skillengine.classes.TeleportJump;
import tera.gameserver.model.skillengine.classes.TeleportNearBonfire;
import tera.gameserver.model.skillengine.classes.TeleportTown;
import tera.gameserver.model.skillengine.classes.Transform;
import tera.gameserver.model.skillengine.classes.Trigger;
import tera.gameserver.model.skillengine.classes.UnAggro;
import tera.gameserver.model.skillengine.classes.UpdateBonfire;
import tera.gameserver.model.skillengine.classes.WarriorFuryStrike;
import tera.gameserver.templates.SkillTemplate;

import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public enum SkillType
{
	
	AGGRO(Aggro.class),
	
	UNAGGRO(UnAggro.class),
	
	RESTORE_STAMINA(RestoreStamina.class),
	
	SINGLE_SHOT(SingleShot.class),
	
	AUTO_SINGLE_SHOT(AutoSingleShot.class),
	
	CHARHE_SINGLE_SHOT(ChargeSingleShot.class),
	
	MANA_SINGLE_SHOT(ManaSingleShot.class),
	
	NPC_SINGLE_SHOT(NpcSingleShot.class),
	
	NPC_SINGLE_FAST_SHOT(NpcSingleFastShot.class),
	
	CHARGE_RAIL_FAST_MANA_SHOT(ChargeRailFastManaShot.class),
	
	STRIKE(Strike.class),
	
	STAGE_STRIKE(StageStrike.class),
	
	CONTER_STRIKE(ConterStrike.class),
	
	SLAYER_FURY_STRIKE(SlayerFuryStrike.class),
	
	WARRIOR_FURY_STRIKE(WarriorFuryStrike.class),
	
	MANA_STRIKE(ManaStrike.class),
	
	MANA_GAIN_STRIKE(ManaGainStrike.class),
	
	COMPLEX_STRIKE(ComplexStrike.class),
	
	COMPLEX_MOD_STRIKE(ComplexModStrike.class),
	
	PREPARE_STRIKE(PrepareStrike.class),
	
	OWERTURNED_STRIKE(OwerturnedStrike.class),
	
	CHARGE_COMPLEX_STRIKE(ChargeComplexStrike.class),
	
	CHARGE_STRIKE(ChargeStrike.class),
	
	CHARGE_VAMP_STRIKE(ChargeVampStrike.class),
	
	LEASH(Leash.class),
	
	SPAWN_SUMMON(SpawnSummon.class),
	
	SPAWN_SMOKE_SUMMON(SpawnSmokeSummon.class),
	
	SPAWN_TRAP(SpawnTrap.class),
	
	SUMMON_ATTACK(SummonAttack.class),
	
	SUMMON_ABORT(SummonAbort.class),
	
	CHARGE(Charge.class),
	
	CYCLONE(Cyclone.class),
	
	CHARGE_MANA_HEAL(ChargeManaHeal.class),
	
	DEFENSE(Defense.class),
	
	LANCER_DEFENSE(LancerDefense.class),
	
	LOCK_ON(LockOn.class),
	
	LOCK_ON_EFFECT(LockOnEffect.class),
	
	LOCK_ON_STRIKE(LockOnStrike.class),
	
	LOCK_ON_HEAL(LockOnHeal.class),
	
	LOCK_ON_STRIKE_PARTY_BUFF(LockOnStrikePartyBuff.class),
	
	ABSORPTION_HP(AbsorptionHp.class),
	
	MANA_HEAL_ON_ABSORPTION_HP(ManaHealOnAbsorptionHp.class),
	
	JUMP(Jump.class),
	
	SPAWN_ITEM(SpawnItem.class),
	
	HEAL(Heal.class),
	
	HEAL_PERCENT(HealPercent.class),
	
	MANAHEAL(ManaHeal.class),
	
	PREPARE_MANAHEAL(PrepareManaHeal.class),
	
	MANAHEAL_PERCENT(ManaHealPercent.class),
	
	EFFECT(Effect.class),
	
	BUFF(Buff.class),
	
	CHARM_BUFF(CharmBuff.class),
	
	DEBUFF(Debuff.class),
	
	CHANCE_EFFECT(ChanceEffect.class),
	
	ITEM_BUFF(ItemBuff.class),
	
	PASSIVE(Passive.class),
	
	SINGLE_SLOW_SHOT(SingleSlowShot.class),
	
	CHARGE_SINGLE_SLOW_SHOT(ChargeSingleSlowShot.class),
	
	MANA_SINGLE_SLOW_SHOT(SingleSlowShot.class),
	
	NPC_SINGLE_SLOW_SHOT(NpcSingleSlowShot.class),
	
	TRIGGER(Trigger.class),
	
	SPAWN_BONFIRE(SpawnBonfire.class),
	
	SPAWN_BUFF_TRAP(SpawnBuffTrap.class),
	
	RESTART_BONFIRE(UpdateBonfire.class),
	
	TRANSFORM(Transform.class),
	
	MOUNT(Mount.class),
	
	CLEAR_DEBUFF(ClearDebuff.class),
	
	CLEAR_BUFF(ClearBuff.class),
	
	CANCEL_OWERTURN(CancelOwerturn.class),
	
	CANCEL_CAST(CancelCast.class),
	
	PARTY_SUMMON(PartySummon.class),
	
	RESURRECT(Resurrect.class),
	
	TELEPORT_JUMP(TeleportJump.class),
	
	TELEPORT_TOWN(TeleportTown.class),
	
	TELEPORT_NEAR_BONFIRE(TeleportNearBonfire.class),
	
	PVP_MODE(PvPMode.class);
	
	private Constructor<? extends Skill> constructor;
	
	/**
	 * Constructor for SkillType.
	 * @param type Class<? extends Skill>
	 */
	private SkillType(Class<? extends Skill> type)
	{
		try
		{
			constructor = type.getConstructor(SkillTemplate.class);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			Loggers.warning(this, e);
		}
	}
	
	/**
	 * Method newInstance.
	 * @param template SkillTemplate
	 * @return Skill
	 */
	public Skill newInstance(SkillTemplate template)
	{
		try
		{
			return constructor.newInstance(template);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			Loggers.warning(this, e);
		}
		
		return null;
	}
}
