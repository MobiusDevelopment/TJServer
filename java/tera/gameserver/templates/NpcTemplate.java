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
package tera.gameserver.templates;

import tera.gameserver.IdFactory;
import tera.gameserver.model.Character;
import tera.gameserver.model.MinionData;
import tera.gameserver.model.drop.NpcDrop;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.NpcType;
import tera.gameserver.model.npc.interaction.DialogData;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestData;
import tera.gameserver.model.skillengine.SkillGroup;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.tables.NpcTable;
import tera.gameserver.tables.SkillTable;

import rlib.util.Strings;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public final class NpcTemplate extends CharTemplate
{
	
	protected String name;
	
	protected String title;
	
	protected String factionId;
	
	protected NpcDrop drop;
	
	protected MinionData minions;
	
	protected DialogData dialog;
	
	protected QuestData quests;
	
	protected NpcType npcType;
	
	protected SkillTemplate[][] skills;
	
	protected float geomRadius;
	
	protected float geomHeight;
	
	protected int id;
	
	protected int iconId;
	
	protected int type;
	
	protected int modelId;
	
	protected int exp;
	
	protected int attack;
	
	protected int defense;
	
	protected int impact;
	
	protected int balance;
	
	protected int level;
	
	protected int aggro;
	
	protected int factionRange;
	
	protected int owerturnDist;
	
	protected int owerturnTime;
	
	protected boolean isRaid;
	
	protected boolean canDrop;
	
	protected boolean owerturnImmunity;
	
	/**
	 * Constructor for NpcTemplate.
	 * @param vars VarTable
	 * @param funcs Func[]
	 */
	public NpcTemplate(VarTable vars, Func[] funcs)
	{
		super(vars, funcs);
		id = vars.getInteger("id");
		iconId = vars.getInteger("iconId", id);
		modelId = vars.getInteger("modelId", iconId);
		exp = vars.getInteger("exp", 0);
		attack = vars.getInteger("attack");
		defense = vars.getInteger("defense");
		impact = vars.getInteger("impact");
		balance = vars.getInteger("balance");
		level = vars.getInteger("level");
		type = vars.getInteger("type");
		owerturnDist = vars.getInteger("owerturnDist", 50);
		owerturnTime = vars.getInteger("owerturnTime", 4500);
		name = vars.getString("name");
		title = vars.getString("title", "");
		factionId = vars.getString("fractionId", Strings.EMPTY);
		geomRadius = vars.getFloat("geomRadius", 40F);
		geomHeight = vars.getFloat("geomHeight", 60F);
		aggro = vars.getShort("aggro", (short) 120);
		factionRange = vars.getShort("fractionRange", (short) 0);
		isRaid = vars.getBoolean("isRaid", false);
		owerturnImmunity = vars.getBoolean("owerturnImmunity", false);
		npcType = NpcType.valueOf(vars.getString("class"));
		quests = new QuestData();
		skills = new SkillTemplate[SkillGroup.length][];
		setSkills(SkillTable.parseSkills(vars.getString("skills", ""), NpcTable.NPC_SKILL_CLASS_ID));
	}
	
	/**
	 * Method addQuest.
	 * @param quest Quest
	 */
	public final void addQuest(Quest quest)
	{
		quests.addQuest(quest);
	}
	
	/**
	 * Method equals.
	 * @param obj Object
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		
		if (obj == null)
		{
			return false;
		}
		
		if (getClass() != obj.getClass())
		{
			return false;
		}
		
		final NpcTemplate other = (NpcTemplate) obj;
		
		if (id != other.id)
		{
			return false;
		}
		
		if (type != other.type)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Method getAggro.
	 * @return int
	 */
	public final int getAggro()
	{
		return aggro;
	}
	
	/**
	 * Method getAttack.
	 * @return int
	 */
	public final int getAttack()
	{
		return attack;
	}
	
	/**
	 * Method getBalance.
	 * @return int
	 */
	public final int getBalance()
	{
		return balance;
	}
	
	/**
	 * Method getDefense.
	 * @return int
	 */
	public final int getDefense()
	{
		return defense;
	}
	
	/**
	 * Method getDialog.
	 * @return DialogData
	 */
	public final DialogData getDialog()
	{
		return dialog;
	}
	
	/**
	 * Method getDrop.
	 * @param items Array<ItemInstance>
	 * @param npc Npc
	 * @param attacker Character
	 * @return Array<ItemInstance>
	 */
	public final Array<ItemInstance> getDrop(Array<ItemInstance> items, Npc npc, Character attacker)
	{
		drop.addDrop(items, npc, attacker);
		return items;
	}
	
	/**
	 * Method getExp.
	 * @return int
	 */
	public final int getExp()
	{
		return exp;
	}
	
	/**
	 * Method getFactionId.
	 * @return String
	 */
	public final String getFactionId()
	{
		return factionId;
	}
	
	/**
	 * Method getFactionRange.
	 * @return int
	 */
	public final int getFactionRange()
	{
		return factionRange;
	}
	
	/**
	 * Method getGeomHeight.
	 * @return float
	 */
	public final float getGeomHeight()
	{
		return geomHeight;
	}
	
	/**
	 * Method getGeomRadius.
	 * @return float
	 */
	public final float getGeomRadius()
	{
		return geomRadius;
	}
	
	/**
	 * Method getIconId.
	 * @return int
	 */
	public final int getIconId()
	{
		return iconId;
	}
	
	/**
	 * Method getImpact.
	 * @return int
	 */
	public final int getImpact()
	{
		return impact;
	}
	
	/**
	 * Method getLevel.
	 * @return int
	 */
	public final int getLevel()
	{
		return level;
	}
	
	/**
	 * Method getMinions.
	 * @return MinionData
	 */
	public final MinionData getMinions()
	{
		return minions;
	}
	
	/**
	 * Method getModelId.
	 * @return int
	 */
	@Override
	public int getModelId()
	{
		return modelId;
	}
	
	/**
	 * Method getName.
	 * @return String
	 */
	public final String getName()
	{
		return name;
	}
	
	/**
	 * Method getNpcType.
	 * @return NpcType
	 */
	public final NpcType getNpcType()
	{
		return npcType;
	}
	
	/**
	 * Method getOwerturnDist.
	 * @return int
	 */
	public final int getOwerturnDist()
	{
		return owerturnDist;
	}
	
	/**
	 * Method getOwerturnTime.
	 * @return int
	 */
	public final int getOwerturnTime()
	{
		return owerturnTime;
	}
	
	/**
	 * Method getQuests.
	 * @return QuestData
	 */
	public final QuestData getQuests()
	{
		return quests;
	}
	
	/**
	 * Method getSkills.
	 * @return SkillTemplate[][]
	 */
	public final SkillTemplate[][] getSkills()
	{
		return skills;
	}
	
	/**
	 * Method getTemplateId.
	 * @return int
	 */
	@Override
	public int getTemplateId()
	{
		return id;
	}
	
	/**
	 * Method getTemplateType.
	 * @return int
	 */
	@Override
	public int getTemplateType()
	{
		return type;
	}
	
	/**
	 * Method getTitle.
	 * @return String
	 */
	public final String getTitle()
	{
		return title;
	}
	
	/**
	 * Method hashCode.
	 * @return int
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + id;
		result = (prime * result) + type;
		return result;
	}
	
	/**
	 * Method isCanDrop.
	 * @return boolean
	 */
	public final boolean isCanDrop()
	{
		return canDrop;
	}
	
	/**
	 * Method isOwerturnImmunity.
	 * @return boolean
	 */
	public boolean isOwerturnImmunity()
	{
		return owerturnImmunity;
	}
	
	/**
	 * Method isRaid.
	 * @return boolean
	 */
	public final boolean isRaid()
	{
		return isRaid;
	}
	
	/**
	 * Method newInstance.
	 * @return Npc
	 */
	public final Npc newInstance()
	{
		final IdFactory idFactory = IdFactory.getInstance();
		return newInstance(idFactory.getNextNpcId());
	}
	
	/**
	 * Method newInstance.
	 * @param objectId int
	 * @return Npc
	 */
	public final Npc newInstance(int objectId)
	{
		return npcType.newInstance(objectId, this);
	}
	
	/**
	 * Method setCanDrop.
	 * @param canDrop boolean
	 */
	public final void setCanDrop(boolean canDrop)
	{
		this.canDrop = canDrop;
	}
	
	/**
	 * Method setDialog.
	 * @param dialog DialogData
	 */
	public final void setDialog(DialogData dialog)
	{
		this.dialog = dialog;
	}
	
	/**
	 * Method setDrop.
	 * @param drop NpcDrop
	 */
	public final void setDrop(NpcDrop drop)
	{
		this.drop = drop;
	}
	
	/**
	 * Method setMinions.
	 * @param minions MinionData
	 */
	public final void setMinions(MinionData minions)
	{
		this.minions = minions;
	}
	
	/**
	 * Method setSkills.
	 * @param newSkills Array<SkillTemplate>
	 */
	public void setSkills(Array<SkillTemplate> newSkills)
	{
		for (SkillTemplate skill : newSkills)
		{
			final SkillGroup group = skill.getSkillGroup();
			
			if (group == SkillGroup.NONE)
			{
				continue;
			}
			
			skills[group.ordinal()] = Arrays.addToArray(skills[group.ordinal()], skill, SkillTemplate.class);
		}
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "NpcTemplate npcId = " + id + ", exp = " + exp + ", attack = " + attack + ", defense = " + defense + ", owerAttack = " + impact + ", owerDefense = " + balance + ", level = " + level + ", aggro = " + aggro + ", factionRange = " + factionRange + ", name = " + name + ", title = " + title + ", factionId = " + factionId + ", type = " + type + ", isRaid = " + isRaid + ", canDrop = " + canDrop + ", drop = " + drop + ", minions = " + minions + ", dialog = " + dialog + ", geomRadius = " + geomRadius + ", geomHeight = " + geomHeight + ", npcType = " + npcType;
	}
}