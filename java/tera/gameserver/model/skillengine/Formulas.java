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

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import tera.Config;
import tera.gameserver.manager.RandomManager;
import tera.gameserver.model.AttackInfo;
import tera.gameserver.model.Character;
import tera.gameserver.model.base.PlayerClass;
import tera.gameserver.model.equipment.Equipment;
import tera.gameserver.model.equipment.Slot;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.funcs.StatFunc;
import tera.gameserver.model.skillengine.funcs.stat.MathFunc;
import tera.gameserver.templates.EffectTemplate;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.VarTable;
import rlib.util.random.Random;

/**
 * @author Ronn
 */
public final class Formulas
{
	
	private static final Logger log = Loggers.getLogger(Formulas.class);
	
	private static Formulas instance;
	
	public static Formulas getInstance()
	{
		if (instance == null)
		{
			instance = new Formulas();
		}
		
		return instance;
	}
	
	final float[] ATTACK = new float[200];
	final float[] DEFENSE = new float[200];
	final float[] IMPACT = new float[200];
	final float[] BALANCE = new float[200];
	
	final float[] HEART = new float[200];
	
	private final float[] CAST_MOD = new float[300];
	
	float[][] HP_MOD;
	
	private StatFunc STAMINA_HP;
	private StatFunc STAMINA_MP;
	private StatFunc LEVEL_MOD_HP_PLAYER;
	private StatFunc LEVEL_MOD_REGEN_HP_PLAYER;
	private StatFunc ATTACK_PLAYER;
	private StatFunc IMPACT_PLAYER;
	private StatFunc DEFENSE_PLAYER;
	private StatFunc BALANCE_PLAYER;
	
	private StatFunc BATTLE_WALK_NPC;
	
	private Formulas()
	{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		final File file = new File(Config.SERVER_DIR + "/data/base_stats.xml");
		
		try
		{
			final Document doc = factory.newDocumentBuilder().parse(file);
			
			for (Node list = doc.getFirstChild(); list != null; list = list.getNextSibling())
			{
				for (Node node = list.getFirstChild(); node != null; node = node.getNextSibling())
				{
					if ((node.getNodeType() != Node.ELEMENT_NODE) || !"set".equals(node.getNodeName()))
					{
						continue;
					}
					
					final VarTable vars = VarTable.newInstance(node);
					final String type = vars.getString("type");
					final int order = vars.getInteger("order");
					final float value = vars.getFloat("value");
					
					switch (type)
					{
						case "heart":
							HEART[order] = value;
							break;
					}
				}
			}
			
			for (int i = 1, length = ATTACK.length; i < length; i++)
			{
				ATTACK[i] = (0.01F * i) + 3;
			}
			
			for (int i = 1, length = DEFENSE.length; i < length; i++)
			{
				DEFENSE[i] = (0.01F * i) + 0.5F;
			}
			
			for (int i = 0, length = IMPACT.length; i < length; i++)
			{
				IMPACT[i] = 0.01F * i;
			}
			
			for (int i = 0, length = BALANCE.length; i < length; i++)
			{
				BALANCE[i] = 0.01F * i;
			}
			
			for (int i = 0, length = HEART.length; i < length; i++)
			{
				if (HEART[i] == 0F)
				{
					HEART[i] = 1F;
				}
			}
		}
		catch (SAXException | IOException | ParserConfigurationException e)
		{
			Loggers.warning(Formulas.class, e);
		}
		
		HP_MOD = new float[PlayerClass.values().length][Config.WORLD_PLAYER_MAX_LEVEL + 1];
		
		for (PlayerClass cs : PlayerClass.values())
		{
			for (int i = 1; i < HP_MOD[cs.getId()].length; i++)
			{
				HP_MOD[cs.getId()][i] = (float) Math.pow(cs.getHpMod(), i - 1);
			}
		}
		
		for (int i = 0; i < CAST_MOD.length; i++)
		{
			CAST_MOD[i] = 18000F / (Math.max(i, 1) + 180) / 10;
		}
		
		ATTACK_PLAYER = new MathFunc(StatType.ATTACK, 0x10, null, null)
		{
			@Override
			public float calc(Character attacker, Character attacked, Skill skill, float val)
			{
				float total = 0F;
				
				if (attacker.isPlayer())
				{
					final Equipment equipment = attacker.getEquipment();
					
					if (equipment != null)
					{
						final Slot[] slots = equipment.getSlots();
						
						for (Slot slot : slots)
						{
							final ItemInstance item = slot.getItem();
							
							if (item != null)
							{
								total += item.getAttack();
							}
						}
					}
				}
				
				return val + (ATTACK[attacker.getPowerFactor()] * total);
			}
		};
		IMPACT_PLAYER = new MathFunc(StatType.IMPACT, 0x10, null, null)
		{
			@Override
			public float calc(Character attacker, Character attacked, Skill skill, float val)
			{
				float total = 0F;
				
				if (attacker.isPlayer())
				{
					final Equipment equipment = attacker.getEquipment();
					
					if (equipment != null)
					{
						final Slot[] slots = equipment.getSlots();
						
						for (Slot slot : slots)
						{
							final ItemInstance item = slot.getItem();
							
							if (item != null)
							{
								total += item.getImpact();
							}
						}
					}
				}
				
				return val + (total * IMPACT[attacker.getImpactFactor()]);
			}
		};
		DEFENSE_PLAYER = new MathFunc(StatType.DEFENSE, 0x10, null, null)
		{
			@Override
			public float calc(Character attacker, Character attacked, Skill skill, float val)
			{
				float total = 0F;
				
				if (attacker.isPlayer())
				{
					final Equipment equipment = attacker.getEquipment();
					
					if (equipment != null)
					{
						final Slot[] slots = equipment.getSlots();
						
						for (Slot slot : slots)
						{
							final ItemInstance item = slot.getItem();
							
							if (item != null)
							{
								total += item.getDefence();
							}
						}
					}
				}
				
				return val + (total * DEFENSE[attacker.getDefenseFactor()]);
			}
		};
		BALANCE_PLAYER = new MathFunc(StatType.BALANCE, 0x10, null, null)
		{
			@Override
			public float calc(Character attacker, Character attacked, Skill skill, float val)
			{
				float total = 0F;
				
				if (attacker.isPlayer())
				{
					final Equipment equipment = attacker.getEquipment();
					
					if (equipment != null)
					{
						final Slot[] slots = equipment.getSlots();
						
						for (Slot slot : slots)
						{
							final ItemInstance item = slot.getItem();
							
							if (item != null)
							{
								total += item.getBalance();
							}
						}
					}
				}
				
				return val + (total * BALANCE[attacker.getBalanceFactor()]);
			}
		};
		STAMINA_HP = new MathFunc(StatType.MAX_HP, 0x50, null, null)
		{
			@Override
			public float calc(Character attacker, Character attacked, Skill skill, float val)
			{
				if (!attacker.isPlayer())
				{
					return val;
				}
				
				final Player player = attacker.getPlayer();
				return val * HEART[player.getStamina()];
			}
		};
		STAMINA_MP = new MathFunc(StatType.MAX_MP, 0x50, null, null)
		{
			@Override
			public float calc(Character attacker, Character attacked, Skill skill, float val)
			{
				if (!attacker.isPlayer())
				{
					return val;
				}
				
				final Player player = (Player) attacker;
				return val * HEART[player.getStamina()];
			}
		};
		LEVEL_MOD_HP_PLAYER = new MathFunc(StatType.MAX_HP, 0x10, null, null)
		{
			@Override
			public float calc(Character attacker, Character attacked, Skill skill, float val)
			{
				if (!attacker.isPlayer())
				{
					return val;
				}
				
				final Player player = (Player) attacker;
				return val * HP_MOD[player.getClassId()][player.getLevel()];
			}
		};
		LEVEL_MOD_REGEN_HP_PLAYER = new MathFunc(StatType.REGEN_HP, 0x10, null, null)
		{
			@Override
			public float calc(Character attacker, Character attacked, Skill skill, float val)
			{
				if (!attacker.isPlayer())
				{
					return val;
				}
				
				final Player player = (Player) attacker;
				return val * HP_MOD[player.getClassId()][player.getLevel()];
			}
		};
		BATTLE_WALK_NPC = new MathFunc(StatType.RUN_SPEED, 0x50, null, null)
		{
			@Override
			public float calc(Character attacker, Character attacked, Skill skill, float val)
			{
				if (!attacker.isNpc())
				{
					return val;
				}
				
				if (!attacker.isBattleStanced())
				{
					val = attacker.isSummon() ? val * 2 : val / 2;
				}
				
				return val;
			}
		};
		log.info("initialized.");
	}
	
	public void addFuncsToNewCharacter(Character character)
	{
	}
	
	public void addFuncsToNewNpc(Character npc)
	{
		BATTLE_WALK_NPC.addFuncTo(npc);
	}
	
	public void addFuncsToNewPlayer(Player player)
	{
		ATTACK_PLAYER.addFuncTo(player);
		IMPACT_PLAYER.addFuncTo(player);
		DEFENSE_PLAYER.addFuncTo(player);
		BALANCE_PLAYER.addFuncTo(player);
		STAMINA_HP.addFuncTo(player);
		STAMINA_MP.addFuncTo(player);
		LEVEL_MOD_HP_PLAYER.addFuncTo(player);
		LEVEL_MOD_REGEN_HP_PLAYER.addFuncTo(player);
	}
	
	public AttackInfo calcDamageSkill(AttackInfo info, Skill skill, Character attacker, Character attacked)
	{
		final RandomManager randomManager = RandomManager.getInstance();
		Random rand = randomManager.getCritRandom();
		info.addDamage(attacker.getAttack(attacked, skill));
		info.mulDamage(skill.getPower() / skill.getCastCount());
		info.divDamage(Math.max(1, attacked.getDefense(attacker, skill)));
		{
			final float chance = attacker.getCritRate(attacked, skill);
			final float resist = (100F - attacked.getCritRateRcpt(attacker, skill)) / 100F;
			info.setCrit(rand.chance((chance * resist) / 5F));
		}
		info.setDamage(Math.max(info.getDamage(), 1));
		attacked.onDamage(attacker, skill, info);
		attacked.onShield(attacker, skill, info);
		
		if (info.isCrit())
		{
			info.mulDamage(attacker.getCritDamage(attacked, skill));
		}
		
		if (attacked.isInvul())
		{
			info.setDamage(0);
		}
		
		rand = randomManager.getDamageRandom();
		
		if (!info.isNoDamage())
		{
			info.setDamage((info.getDamage() * 100) / rand.nextInt(95, 105));
		}
		
		info.setOwerturn(!info.isBlocked() && skill.isCanOwerturn() && calcOwerturn(attacker, attacked, skill));
		return info;
	}
	
	public float calcEffect(Character attacker, Character attacked, EffectTemplate effect, Skill skill)
	{
		int chance = effect.getChance();
		
		if (chance == -1)
		{
			return 1;
		}
		
		final ResistType resistType = effect.getResistType();
		
		if (!resistType.checkCondition(attacker, attacked))
		{
			return -1;
		}
		
		final float power = (attacker.calcStat(resistType.getPowerStat(), 1, attacked, skill) * attacker.getLevel()) / Math.max(attacked.getLevel(), 1);
		final float resist = Math.max(1, 100 - attacked.calcStat(resistType.getRcptStat(), 0, attacker, skill)) / 100F;
		final float mod = 1F * power * resist;
		chance = Math.min((int) (chance * mod), 95);
		final RandomManager randomManager = RandomManager.getInstance();
		final Random rand = randomManager.getEffectRandom();
		
		if (rand.chance(chance))
		{
			attacker.sendMessage("Chance effect " + chance + " %. Success!");
			return mod;
		}
		attacker.sendMessage("Chance effect " + chance + " %. Failure!");
		return -1;
	}
	
	public boolean calcOwerturn(Character attacker, Character attacked, Skill skill)
	{
		final ResistType resistType = ResistType.owerturnResist;
		
		if (!resistType.checkCondition(attacker, attacked))
		{
			return false;
		}
		
		final float chance = (attacker.getImpact(attacked, skill) * 2F) / Math.max(attacked.getBalance(attacker, skill), 1);
		final float power = (skill.getOwerturnMod() * attacker.calcStat(resistType.getPowerStat(), 1, attacked, skill) * attacker.getLevel()) / Math.max(attacked.getLevel(), 1);
		final float resist = Math.max(1, 100 - attacked.calcStat(resistType.getRcptStat(), 0, attacker, skill));
		final RandomManager randomManager = RandomManager.getInstance();
		final Random rand = randomManager.getOwerturnRandom();
		return rand.chance(Math.min((chance * power * resist) / 100F, 80F));
	}
	
	public int castTime(int hitTime, Character caster)
	{
		if (hitTime < 1)
		{
			return 0;
		}
		
		return (hitTime * 70) / Math.max(caster.getAtkSpd(), 1);
	}
	
	public int getChanceCollect(int req, int level)
	{
		return Math.max((65 + req) - level, 95);
	}
}
