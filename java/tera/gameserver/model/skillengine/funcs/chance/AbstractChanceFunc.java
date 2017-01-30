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
package tera.gameserver.model.skillengine.funcs.chance;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Condition;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.parser.FuncParser;
import tera.gameserver.tables.SkillTable;
import tera.gameserver.templates.SkillTemplate;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public abstract class AbstractChanceFunc implements ChanceFunc
{
	protected static final Logger log = Loggers.getLogger(ChanceFunc.class);
	protected Skill skill;
	protected Condition cond;
	protected int id;
	protected int classId;
	protected int chance;
	protected boolean onAttack;
	protected boolean onCritAttack;
	protected boolean onAttacked;
	protected boolean onCritAttacked;
	protected boolean onOwerturn;
	protected boolean onOwerturned;
	protected boolean onShieldBlocked;
	
	/**
	 * Constructor for AbstractChanceFunc.
	 * @param vars VarTable
	 * @param condition Condition
	 */
	public AbstractChanceFunc(VarTable vars, Condition condition)
	{
		id = vars.getInteger("id");
		classId = vars.getInteger("class");
		chance = vars.getInteger("chance");
		cond = condition;
		onAttack = vars.getBoolean("attack", false);
		onCritAttack = vars.getBoolean("critAttack", false);
		onAttacked = vars.getBoolean("attacked", false);
		onCritAttacked = vars.getBoolean("critAttacked", false);
		onOwerturn = vars.getBoolean("owerturn", false);
		onOwerturned = vars.getBoolean("owerturned", false);
		onShieldBlocked = vars.getBoolean("shieldBlocked", false);
		final FuncParser funcParser = FuncParser.getInstance();
		funcParser.addChanceFunc(this);
	}
	
	/**
	 * Method addFuncTo.
	 * @param owner Character
	 * @see tera.gameserver.model.skillengine.funcs.Func#addFuncTo(Character)
	 */
	@Override
	public void addFuncTo(Character owner)
	{
		owner.addChanceFunc(this);
	}
	
	/**
	 * Method apply.
	 * @param attacker Character
	 * @param attacked Character
	 * @param skill Skill
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.funcs.chance.ChanceFunc#apply(Character, Character, Skill)
	 */
	@Override
	public boolean apply(Character attacker, Character attacked, Skill skill)
	{
		return (cond == null) || cond.test(attacker, attacked, skill, 0);
	}
	
	/**
	 * Method getChance.
	 * @return int
	 * @see tera.gameserver.model.skillengine.funcs.chance.ChanceFunc#getChance()
	 */
	@Override
	public int getChance()
	{
		return chance;
	}
	
	/**
	 * Method getSkill.
	 * @return Skill
	 * @see tera.gameserver.model.skillengine.funcs.chance.ChanceFunc#getSkill()
	 */
	@Override
	public Skill getSkill()
	{
		return skill;
	}
	
	/**
	 * Method isOnAttack.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.funcs.chance.ChanceFunc#isOnAttack()
	 */
	@Override
	public boolean isOnAttack()
	{
		return onAttack;
	}
	
	/**
	 * Method isOnAttacked.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.funcs.chance.ChanceFunc#isOnAttacked()
	 */
	@Override
	public boolean isOnAttacked()
	{
		return onAttacked;
	}
	
	/**
	 * Method isOnCritAttack.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.funcs.chance.ChanceFunc#isOnCritAttack()
	 */
	@Override
	public boolean isOnCritAttack()
	{
		return onCritAttack;
	}
	
	/**
	 * Method isOnCritAttacked.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.funcs.chance.ChanceFunc#isOnCritAttacked()
	 */
	@Override
	public boolean isOnCritAttacked()
	{
		return onCritAttacked;
	}
	
	/**
	 * Method isOnOwerturn.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.funcs.chance.ChanceFunc#isOnOwerturn()
	 */
	@Override
	public boolean isOnOwerturn()
	{
		return onOwerturn;
	}
	
	/**
	 * Method isOnOwerturned.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.funcs.chance.ChanceFunc#isOnOwerturned()
	 */
	@Override
	public boolean isOnOwerturned()
	{
		return onOwerturned;
	}
	
	/**
	 * Method isOnShieldBlocked.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.funcs.chance.ChanceFunc#isOnShieldBlocked()
	 */
	@Override
	public boolean isOnShieldBlocked()
	{
		return onShieldBlocked;
	}
	
	/**
	 * Method prepare.
	 * @see tera.gameserver.model.skillengine.funcs.chance.ChanceFunc#prepare()
	 */
	@Override
	public void prepare()
	{
		final SkillTable skillTable = SkillTable.getInstance();
		final SkillTemplate template = skillTable.getSkill(classId, id);
		
		if (template == null)
		{
			log.warning(this, "Not found template for " + classId + " - " + id);
			return;
		}
		
		skill = template.newInstance();
	}
	
	/**
	 * Method removeFuncTo.
	 * @param owner Character
	 * @see tera.gameserver.model.skillengine.funcs.Func#removeFuncTo(Character)
	 */
	@Override
	public void removeFuncTo(Character owner)
	{
		owner.removeChanceFunc(this);
	}
}