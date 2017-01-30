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
package tera.gameserver.parser;

import java.io.File;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import tera.gameserver.model.items.ArmorType;
import tera.gameserver.model.items.WeaponType;
import tera.gameserver.model.npc.NpcType;
import tera.gameserver.model.skillengine.Condition;
import tera.gameserver.model.skillengine.SkillName;
import tera.gameserver.model.skillengine.conditions.ConditionAttackerAggroMe;
import tera.gameserver.model.skillengine.conditions.ConditionAttackerEffectorEffectId;
import tera.gameserver.model.skillengine.conditions.ConditionAttackerNpcRage;
import tera.gameserver.model.skillengine.conditions.ConditionAttackerNpcType;
import tera.gameserver.model.skillengine.conditions.ConditionAttackerNpcTypes;
import tera.gameserver.model.skillengine.conditions.ConditionAttackerOwerturned;
import tera.gameserver.model.skillengine.conditions.ConditionAttackerPvP;
import tera.gameserver.model.skillengine.conditions.ConditionAttackerSide;
import tera.gameserver.model.skillengine.conditions.ConditionHasEffectId;
import tera.gameserver.model.skillengine.conditions.ConditionLogicAnd;
import tera.gameserver.model.skillengine.conditions.ConditionLogicNot;
import tera.gameserver.model.skillengine.conditions.ConditionLogicOr;
import tera.gameserver.model.skillengine.conditions.ConditionPlayerBattleStance;
import tera.gameserver.model.skillengine.conditions.ConditionPlayerCastSkillName;
import tera.gameserver.model.skillengine.conditions.ConditionPlayerNotBarrier;
import tera.gameserver.model.skillengine.conditions.ConditionPlayerOnCast;
import tera.gameserver.model.skillengine.conditions.ConditionPlayerOwerturned;
import tera.gameserver.model.skillengine.conditions.ConditionPlayerPercentHP;
import tera.gameserver.model.skillengine.conditions.ConditionPlayerStamina;
import tera.gameserver.model.skillengine.conditions.ConditionTargetAggroMe;
import tera.gameserver.model.skillengine.conditions.ConditionTargetNpcRage;
import tera.gameserver.model.skillengine.conditions.ConditionTargetNpcType;
import tera.gameserver.model.skillengine.conditions.ConditionTargetNpcTypes;
import tera.gameserver.model.skillengine.conditions.ConditionTargetOwerturned;
import tera.gameserver.model.skillengine.conditions.ConditionTargetPlayer;
import tera.gameserver.model.skillengine.conditions.ConditionTargetSide;
import tera.gameserver.model.skillengine.conditions.ConditionUsingItem;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public final class ConditionParser
{
	private static final Logger log = Loggers.getLogger(ConditionParser.class);
	
	private static ConditionParser instance;
	
	/**
	 * Method getInstance.
	 * @return ConditionParser
	 */
	public static ConditionParser getInstance()
	{
		if (instance == null)
		{
			instance = new ConditionParser();
		}
		
		return instance;
	}
	
	private ConditionParser()
	{
		log.info("initializable.");
	}
	
	/**
	 * Method joinAnd.
	 * @param first Condition
	 * @param second Condition
	 * @return Condition
	 */
	public Condition joinAnd(Condition first, Condition second)
	{
		if (first == null)
		{
			first = new ConditionLogicAnd();
		}
		
		((ConditionLogicAnd) first).add(second);
		return first;
	}
	
	/**
	 * Method parseAttackerCondition.
	 * @param node Node
	 * @param skillId int
	 * @param file File
	 * @return Condition
	 */
	private Condition parseAttackerCondition(Node node, int skillId, File file)
	{
		Condition newCondition = null;
		final NamedNodeMap attrs = node.getAttributes();
		
		for (int index = 0; index < attrs.getLength(); index++)
		{
			final Node item = attrs.item(index);
			final String nodeName = item.getNodeName();
			final String nodeValue = item.getNodeValue();
			final Node msg = attrs.getNamedItem("msg");
			
			switch (nodeName)
			{
				case "pvp":
				{
					final boolean value = Boolean.parseBoolean(nodeValue);
					newCondition = joinAnd(newCondition, new ConditionAttackerPvP(value).setMsg(msg));
					break;
				}
				
				case "player":
				{
					final boolean value = Boolean.parseBoolean(nodeValue);
					newCondition = joinAnd(newCondition, new ConditionAttackerPvP(value).setMsg(msg));
					break;
				}
				
				case "npcType":
				{
					newCondition = joinAnd(newCondition, new ConditionAttackerNpcType(NpcType.valueOf(nodeValue)).setMsg(msg));
					break;
				}
				
				case "npcRage":
				{
					final boolean value = Boolean.parseBoolean(nodeValue);
					newCondition = joinAnd(newCondition, new ConditionAttackerNpcRage(value).setMsg(msg));
					break;
				}
				
				case "npcTypes":
				{
					final String[] strTypes = nodeValue.split(",");
					final NpcType[] types = new NpcType[strTypes.length];
					
					for (int i = 0; i < strTypes.length; i++)
					{
						types[i] = NpcType.valueOf(strTypes[i]);
					}
					
					newCondition = joinAnd(newCondition, new ConditionAttackerNpcTypes(types)).setMsg(msg);
					break;
				}
				
				case "owerturned":
				{
					newCondition = joinAnd(newCondition, new ConditionAttackerOwerturned(Boolean.parseBoolean(nodeValue)).setMsg(msg));
					break;
				}
				
				case "effectorEffectId":
				{
					newCondition = joinAnd(newCondition, new ConditionAttackerEffectorEffectId(Integer.parseInt(nodeValue)).setMsg(msg));
					break;
				}
				
				case "aggroMe":
				{
					newCondition = joinAnd(newCondition, new ConditionAttackerAggroMe(Boolean.parseBoolean(nodeValue)).setMsg(msg));
					break;
				}
				
				case "side":
				{
					newCondition = joinAnd(newCondition, new ConditionAttackerSide(nodeValue).setMsg(msg));
					break;
				}
				
				default:
					log.warning("not found condition name " + nodeName);
			}
		}
		
		if (newCondition == null)
		{
			log.warning("unrecognized <attacker> condition in skill " + skillId + " in file " + file);
		}
		
		return newCondition;
	}
	
	/**
	 * Method parseCondition.
	 * @param node Node
	 * @param skill int
	 * @param file File
	 * @return Condition
	 */
	public Condition parseCondition(Node node, int skill, File file)
	{
		if (node == null)
		{
			return null;
		}
		
		Condition condition = null;
		
		switch (node.getNodeName())
		{
			case "and":
				condition = parseLogicAnd(node, skill, file);
				break;
			
			case "or":
				condition = parseLogicOr(node, skill, file);
				break;
			
			case "not":
				condition = parseLogicNot(node, skill, file);
				break;
			
			case "player":
				condition = parsePlayerCondition(node, skill, file);
				break;
			
			case "target":
				condition = parseTargetCondition(node, skill, file);
				break;
			
			case "has":
				condition = parseHasCondition(node, skill, file);
				break;
			
			case "using":
				condition = parseUsingCondition(node, skill, file);
				break;
			
			case "attacker":
				condition = parseAttackerCondition(node, skill, file);
				break;
		}
		
		return condition;
	}
	
	/**
	 * Method parseHasCondition.
	 * @param node Node
	 * @param skill int
	 * @param file File
	 * @return Condition
	 */
	private Condition parseHasCondition(Node node, int skill, File file)
	{
		Condition newCondition = null;
		final NamedNodeMap attrs = node.getAttributes();
		final Node msg = attrs.getNamedItem("msg");
		
		for (int index = 0; index < attrs.getLength(); index++)
		{
			final Node item = attrs.item(index);
			final String nodeName = item.getNodeName();
			final String nodeValue = item.getNodeValue();
			
			switch (nodeName)
			{
				case "effectId":
				{
					newCondition = joinAnd(newCondition, new ConditionHasEffectId(Integer.parseInt(nodeValue)).setMsg(msg));
					break;
				}
			}
		}
		
		if (newCondition == null)
		{
			log.warning("unrecognized <has> condition in skill " + skill + " in file " + file);
		}
		
		return newCondition;
	}
	
	/**
	 * Method parseLogicAnd.
	 * @param node Node
	 * @param skillId int
	 * @param file File
	 * @return Condition
	 */
	private Condition parseLogicAnd(Node node, int skillId, File file)
	{
		final ConditionLogicAnd condition = new ConditionLogicAnd();
		
		for (node = node.getFirstChild(); node != null; node = node.getNextSibling())
		{
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				condition.add(parseCondition(node, skillId, file));
			}
		}
		
		if (condition.isEmpty())
		{
			log.warning("unrecognized <and> condition in skill " + skillId + " in file " + file);
		}
		
		return condition;
	}
	
	/**
	 * Method parseLogicNot.
	 * @param node Node
	 * @param skill int
	 * @param file File
	 * @return Condition
	 */
	private Condition parseLogicNot(Node node, int skill, File file)
	{
		for (node = node.getFirstChild(); node != null; node = node.getNextSibling())
		{
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				return new ConditionLogicNot(parseCondition(node, skill, file));
			}
		}
		
		log.warning("empty <not> condition in skill " + skill + " in file " + file);
		return null;
	}
	
	/**
	 * Method parseLogicOr.
	 * @param node Node
	 * @param skillId int
	 * @param file File
	 * @return Condition
	 */
	private Condition parseLogicOr(Node node, int skillId, File file)
	{
		final ConditionLogicOr condition = new ConditionLogicOr();
		
		for (node = node.getFirstChild(); node != null; node = node.getNextSibling())
		{
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				condition.add(parseCondition(node, skillId, file));
			}
		}
		
		if (condition.isEmpty())
		{
			log.warning("empty <or> condition in skill " + skillId + " in file " + file);
		}
		
		return condition;
	}
	
	/**
	 * Method parsePlayerCondition.
	 * @param node Node
	 * @param skillId int
	 * @param file File
	 * @return Condition
	 */
	private Condition parsePlayerCondition(Node node, int skillId, File file)
	{
		Condition newCondition = null;
		final NamedNodeMap attrs = node.getAttributes();
		final Node msg = attrs.getNamedItem("msg");
		
		for (int index = 0; index < attrs.getLength(); index++)
		{
			final Node item = attrs.item(index);
			final String nodeName = item.getNodeName();
			final String nodeValue = item.getNodeValue();
			
			switch (nodeName)
			{
				case "percentHp":
				{
					final int percent = Integer.parseInt(nodeValue);
					newCondition = joinAnd(newCondition, new ConditionPlayerPercentHP(percent).setMsg(msg));
					break;
				}
				
				case "battle":
				{
					final boolean value = Boolean.parseBoolean(nodeValue);
					newCondition = joinAnd(newCondition, new ConditionPlayerBattleStance(value).setMsg(msg));
					break;
				}
				
				case "notBarrier":
				{
					final boolean value = Boolean.parseBoolean(nodeValue);
					newCondition = joinAnd(newCondition, new ConditionPlayerNotBarrier(value).setMsg(msg));
					break;
				}
				
				case "owerturned":
				{
					final boolean value = Boolean.parseBoolean(nodeValue);
					newCondition = joinAnd(newCondition, new ConditionPlayerOwerturned(value).setMsg(msg));
					break;
				}
				
				case "onCast":
				{
					final boolean value = Boolean.parseBoolean(nodeValue);
					newCondition = joinAnd(newCondition, new ConditionPlayerOnCast(value).setMsg(msg));
					break;
				}
				
				case "castSkillName":
				{
					newCondition = joinAnd(newCondition, new ConditionPlayerCastSkillName(SkillName.valueOf(nodeValue)).setMsg(msg));
					break;
				}
				
				case "stamina":
				{
					newCondition = joinAnd(newCondition, new ConditionPlayerStamina(Integer.parseInt(nodeValue)).setMsg(msg));
					break;
				}
			}
		}
		
		if (newCondition == null)
		{
			log.warning("unrecognized <player> condition in skill " + skillId + " in file " + file);
		}
		
		return newCondition;
	}
	
	/**
	 * Method parseTargetCondition.
	 * @param node Node
	 * @param skillId int
	 * @param file File
	 * @return Condition
	 */
	private Condition parseTargetCondition(Node node, int skillId, File file)
	{
		Condition newCondition = null;
		final NamedNodeMap attrs = node.getAttributes();
		final Node msg = attrs.getNamedItem("msg");
		
		for (int index = 0; index < attrs.getLength(); index++)
		{
			final Node item = attrs.item(index);
			final String nodeName = item.getNodeName();
			final String nodeValue = item.getNodeValue();
			
			switch (nodeName)
			{
				case "side":
				{
					newCondition = joinAnd(newCondition, new ConditionTargetSide(nodeValue).setMsg(msg));
					break;
				}
				
				case "npcType":
				{
					newCondition = joinAnd(newCondition, new ConditionTargetNpcType(NpcType.valueOf(nodeValue)).setMsg(msg));
					break;
				}
				
				case "npcTypes":
				{
					final String[] strTypes = nodeValue.split(",");
					final NpcType[] types = new NpcType[strTypes.length];
					
					for (int i = 0; i < strTypes.length; i++)
					{
						types[i] = NpcType.valueOf(strTypes[i]);
					}
					
					newCondition = joinAnd(newCondition, new ConditionTargetNpcTypes(types).setMsg(msg));
					break;
				}
				
				case "owerturned":
				{
					newCondition = joinAnd(newCondition, new ConditionTargetOwerturned(Boolean.parseBoolean(nodeValue)).setMsg(msg));
					break;
				}
				
				case "player":
				{
					newCondition = joinAnd(newCondition, new ConditionTargetPlayer(Boolean.parseBoolean(nodeValue)).setMsg(msg));
					break;
				}
				
				case "aggroMe":
				{
					newCondition = joinAnd(newCondition, new ConditionTargetAggroMe(Boolean.parseBoolean(nodeValue)).setMsg(msg));
					break;
				}
				
				case "npcRage":
				{
					final boolean value = Boolean.parseBoolean(nodeValue);
					newCondition = joinAnd(newCondition, new ConditionTargetNpcRage(value).setMsg(msg));
					break;
				}
				
				default:
					log.warning("not found condition " + nodeName);
			}
		}
		
		if (newCondition == null)
		{
			log.warning("unrecognized <target> condition in skill " + skillId + " in file " + file);
		}
		
		return newCondition;
	}
	
	/**
	 * Method parseUsingCondition.
	 * @param atrr Node
	 * @param skill int
	 * @param file File
	 * @return Condition
	 */
	private Condition parseUsingCondition(Node atrr, int skill, File file)
	{
		Condition newCondition = null;
		final NamedNodeMap attrs = atrr.getAttributes();
		final Node msg = attrs.getNamedItem("msg");
		
		for (int index = 0; index < attrs.getLength(); index++)
		{
			final Node item = attrs.item(index);
			final String nodeName = item.getNodeName();
			final String nodeValue = item.getNodeValue();
			
			switch (nodeName)
			{
				case "weapon":
				{
					final String[] values = nodeValue.split(", ");
					final Array<Enum<?>> types = Arrays.toArray(Enum.class, 1);
					
					for (String val : values)
					{
						try
						{
							types.add(WeaponType.valueOf(val));
						}
						catch (IllegalArgumentException e)
						{
							e.printStackTrace();
						}
					}
					
					types.trimToSize();
					newCondition = joinAnd(newCondition, new ConditionUsingItem(types.array()).setMsg(msg));
					break;
				}
				
				case "armor":
				{
					final String[] values = nodeValue.split(", ");
					final Array<Enum<?>> types = Arrays.toArray(Enum.class, 1);
					
					for (String val : values)
					{
						try
						{
							types.add(ArmorType.valueOf(val));
						}
						catch (IllegalArgumentException e)
						{
							e.printStackTrace();
						}
					}
					
					types.trimToSize();
					newCondition = joinAnd(newCondition, new ConditionUsingItem(types.array()).setMsg(msg));
					break;
				}
			}
		}
		
		if (newCondition == null)
		{
			log.warning("unrecognized <using> condition in skill " + skill + " in file " + file);
		}
		
		return newCondition;
	}
}
