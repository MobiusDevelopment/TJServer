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
package tera.gameserver.document;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import tera.gameserver.model.base.PlayerClass;
import tera.gameserver.model.base.Race;
import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.npc.interaction.conditions.ConditionLogicAnd;
import tera.gameserver.model.npc.interaction.conditions.ConditionLogicNot;
import tera.gameserver.model.npc.interaction.conditions.ConditionLogicOr;
import tera.gameserver.model.npc.interaction.conditions.ConditionNpcId;
import tera.gameserver.model.npc.interaction.conditions.ConditionPlayeClasses;
import tera.gameserver.model.npc.interaction.conditions.ConditionPlayerHasItem;
import tera.gameserver.model.npc.interaction.conditions.ConditionPlayerHeart;
import tera.gameserver.model.npc.interaction.conditions.ConditionPlayerLearnedSkill;
import tera.gameserver.model.npc.interaction.conditions.ConditionPlayerMaxLevel;
import tera.gameserver.model.npc.interaction.conditions.ConditionPlayerMinLevel;
import tera.gameserver.model.npc.interaction.conditions.ConditionPlayerMoreVar;
import tera.gameserver.model.npc.interaction.conditions.ConditionPlayerRaces;
import tera.gameserver.model.npc.interaction.conditions.ConditionPlayerVar;
import tera.gameserver.model.npc.interaction.conditions.ConditionQuestComplete;
import tera.gameserver.model.npc.interaction.conditions.ConditionQuestState;
import tera.gameserver.model.quests.Quest;

import rlib.logging.Loggers;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public final class DocumentQuestCondition
{
	private static DocumentQuestCondition instance;
	
	/**
	 * Method getInstance.
	 * @return DocumentQuestCondition
	 */
	public static DocumentQuestCondition getInstance()
	{
		if (instance == null)
		{
			instance = new DocumentQuestCondition();
		}
		
		return instance;
	}
	
	/**
	 * Method joinAnd.
	 * @param first Condition
	 * @param second Condition
	 * @return Condition
	 */
	public static Condition joinAnd(Condition first, Condition second)
	{
		if (first == null)
		{
			first = new ConditionLogicAnd();
		}
		
		((ConditionLogicAnd) first).add(second);
		return first;
	}
	
	private DocumentQuestCondition()
	{
		super();
	}
	
	/**
	 * Method parse.
	 * @param node Node
	 * @param quest Quest
	 * @return Condition
	 */
	private Condition parse(Node node, Quest quest)
	{
		switch (node.getNodeName())
		{
			case "npc":
				return parseNpcCondition(node, quest);
			
			case "quest":
				return parseQuestCondition(node, quest);
			
			case "player":
				return parsePlayerCondition(node, quest);
			
			case "and":
				return parseLogicAnd(node, quest);
			
			case "or":
				return parseLogicOr(node, quest);
			
			case "not":
				return parseLogicNot(node, quest);
		}
		
		return null;
	}
	
	/**
	 * Method parseCondition.
	 * @param container ConditionLogicAnd
	 * @param node Node
	 * @param quest Quest
	 */
	public void parseCondition(ConditionLogicAnd container, Node node, Quest quest)
	{
		if (node == null)
		{
			return;
		}
		
		for (Node cond = node.getFirstChild(); cond != null; cond = cond.getNextSibling())
		{
			if (cond.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			Condition condition = parse(cond, quest);
			
			if (condition != null)
			{
				container.add(condition);
			}
			
			condition = null;
		}
	}
	
	/**
	 * Method parseCondition.
	 * @param container ConditionLogicOr
	 * @param node Node
	 * @param quest Quest
	 */
	public void parseCondition(ConditionLogicOr container, Node node, Quest quest)
	{
		if (node == null)
		{
			return;
		}
		
		for (Node cond = node.getFirstChild(); cond != null; cond = cond.getNextSibling())
		{
			if (cond.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			Condition condition = parse(cond, quest);
			
			if (condition != null)
			{
				container.add(condition);
			}
			
			condition = null;
		}
	}
	
	/**
	 * Method parseCondition.
	 * @param node Node
	 * @param quest Quest
	 * @return Condition
	 */
	public Condition parseCondition(Node node, Quest quest)
	{
		if (node == null)
		{
			return null;
		}
		
		for (Node cond = node.getFirstChild(); cond != null; cond = cond.getNextSibling())
		{
			if (cond.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			final Condition condition = parse(cond, quest);
			
			if (condition != null)
			{
				return condition;
			}
		}
		
		return null;
	}
	
	/**
	 * Method parseLogicAnd.
	 * @param node Node
	 * @param quest Quest
	 * @return Condition
	 */
	private Condition parseLogicAnd(Node node, Quest quest)
	{
		final ConditionLogicAnd condition = new ConditionLogicAnd();
		parseCondition(condition, node, quest);
		return condition;
	}
	
	/**
	 * Method parseLogicNot.
	 * @param node Node
	 * @param quest Quest
	 * @return Condition
	 */
	private Condition parseLogicNot(Node node, Quest quest)
	{
		final Condition condition = parseCondition(node, quest);
		
		if (condition != null)
		{
			return new ConditionLogicNot(condition);
		}
		
		return null;
	}
	
	/**
	 * Method parseLogicOr.
	 * @param node Node
	 * @param quest Quest
	 * @return Condition
	 */
	private Condition parseLogicOr(Node node, Quest quest)
	{
		final ConditionLogicOr condition = new ConditionLogicOr();
		parseCondition(condition, node, quest);
		return condition;
	}
	
	/**
	 * Method parseNpcCondition.
	 * @param node Node
	 * @param quest Quest
	 * @return Condition
	 */
	private Condition parseNpcCondition(Node node, Quest quest)
	{
		Condition newCondition = null;
		final NamedNodeMap attrs = node.getAttributes();
		final VarTable vars = VarTable.newInstance(node);
		
		for (int i = 0, length = attrs.getLength(); i < length; i++)
		{
			final Node item = attrs.item(i);
			
			switch (item.getNodeName())
			{
				case "id":
				{
					final int id = vars.getInteger("id");
					final int type = vars.getInteger("type");
					newCondition = new ConditionNpcId(quest, id, type);
					break;
				}
			}
		}
		
		if (newCondition == null)
		{
			Loggers.warning(this, "unrecognized <npc> condition " + vars + " in quest " + quest.getName());
		}
		
		return newCondition;
	}
	
	/**
	 * Method parsePlayerCondition.
	 * @param node Node
	 * @param quest Quest
	 * @return Condition
	 */
	private Condition parsePlayerCondition(Node node, Quest quest)
	{
		Condition newCondition = null;
		final NamedNodeMap attrs = node.getAttributes();
		final VarTable vars = VarTable.newInstance(node);
		
		for (int i = 0, length = attrs.getLength(); i < length; i++)
		{
			final Node item = attrs.item(i);
			
			switch (item.getNodeName())
			{
				case "classes":
				{
					final String[] classes = vars.getString("classes").split(";");
					final PlayerClass[] playerClasses = new PlayerClass[classes.length];
					
					for (int g = 0, size = classes.length; g < size; g++)
					{
						playerClasses[g] = PlayerClass.valueOf(classes[g]);
					}
					
					newCondition = new ConditionPlayeClasses(quest, playerClasses);
					break;
				}
				
				case "races":
				{
					final String[] classes = vars.getString("races").split(";");
					final Race[] races = new Race[classes.length];
					
					for (int g = 0, size = classes.length; g < size; g++)
					{
						races[g] = Race.valueOf(classes[g]);
					}
					
					newCondition = new ConditionPlayerRaces(quest, races);
					break;
				}
				
				case "var":
				{
					newCondition = new ConditionPlayerVar(quest, vars.getString("var"), vars.getInteger("val"));
					break;
				}
				
				case "moreVar":
				{
					newCondition = new ConditionPlayerMoreVar(quest, vars.getString("moreVar"), vars.getInteger("val"));
					break;
				}
				
				case "hasItem":
				{
					newCondition = new ConditionPlayerHasItem(quest, vars.getInteger("hasItem"), vars.getInteger("count"));
					break;
				}
				
				case "heart":
				{
					newCondition = new ConditionPlayerHeart(quest, vars.getInteger("heart"));
					break;
				}
				
				case "minLevel":
				{
					newCondition = new ConditionPlayerMinLevel(quest, vars.getInteger("minLevel"));
					break;
				}
				
				case "maxLevel":
				{
					newCondition = new ConditionPlayerMaxLevel(quest, vars.getInteger("maxLevel"));
					break;
				}
				
				case "learnedSkill":
				{
					newCondition = new ConditionPlayerLearnedSkill(quest, vars.getInteger("learnedSkill"));
					break;
				}
			}
		}
		
		if (newCondition == null)
		{
			Loggers.warning(this, "unrecognized <player> condition " + vars + " in quest " + quest.getName());
		}
		
		return newCondition;
	}
	
	/**
	 * Method parseQuestCondition.
	 * @param node Node
	 * @param quest Quest
	 * @return Condition
	 */
	private Condition parseQuestCondition(Node node, Quest quest)
	{
		Condition newCondition = null;
		final NamedNodeMap attrs = node.getAttributes();
		final VarTable vars = VarTable.newInstance(node);
		
		for (int i = 0, length = attrs.getLength(); i < length; i++)
		{
			final Node item = attrs.item(i);
			
			switch (item.getNodeName())
			{
				case "state":
				{
					newCondition = new ConditionQuestState(quest, vars.getInteger("state"));
					break;
				}
				
				case "complete":
				{
					newCondition = new ConditionQuestComplete(quest, vars.getInteger("complete"));
					break;
				}
			}
		}
		
		if (newCondition == null)
		{
			Loggers.warning(this, "unrecognized <quest> condition " + vars + " in quest " + quest.getName());
		}
		
		return newCondition;
	}
}
