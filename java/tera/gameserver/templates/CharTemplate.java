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

import tera.gameserver.model.skillengine.funcs.Func;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Objects;
import rlib.util.Reloadable;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public abstract class CharTemplate implements Reloadable<CharTemplate>
{
	protected static final Logger log = Loggers.getLogger(CharTemplate.class);
	
	protected final VarTable vars;
	
	protected Func[] funcs;
	
	protected int maxHp;
	
	protected int maxMp;
	
	protected int regHp;
	
	protected int regMp;
	
	protected int powerFactor;
	
	protected int defenseFactor;
	
	protected int impactFactor;
	
	protected int balanceFactor;
	
	protected int atkSpd;
	
	protected int runSpd;
	
	protected int critRate;
	
	protected int critRcpt;
	
	protected int turnSpeed;
	
	/**
	 * Constructor for CharTemplate.
	 * @param vars VarTable
	 * @param funcs Func[]
	 */
	public CharTemplate(VarTable vars, Func[] funcs)
	{
		maxHp = vars.getInteger("maxHp");
		maxMp = vars.getInteger("maxMp");
		regHp = vars.getInteger("regHp", 9);
		regMp = vars.getInteger("regMp", 9);
		powerFactor = vars.getInteger("powerFactor", 0);
		defenseFactor = vars.getInteger("defenseFactor", 0);
		impactFactor = vars.getInteger("impactFactor", 50);
		balanceFactor = vars.getInteger("balanceFactor", 50);
		atkSpd = vars.getInteger("atkSpd");
		runSpd = vars.getInteger("runSpd");
		critRate = vars.getInteger("critRate", 50);
		critRcpt = vars.getInteger("critRcpt", 50);
		turnSpeed = vars.getInteger("turnSpeed", 6000);
		this.funcs = funcs;
		this.vars = VarTable.newInstance().set(vars);
	}
	
	/**
	 * Method getAtkSpd.
	 * @return int
	 */
	public final int getAtkSpd()
	{
		return atkSpd;
	}
	
	/**
	 * Method getBalanceFactor.
	 * @return int
	 */
	public final int getBalanceFactor()
	{
		return balanceFactor;
	}
	
	/**
	 * Method getCritRate.
	 * @return int
	 */
	public final int getCritRate()
	{
		return critRate;
	}
	
	/**
	 * Method getCritRcpt.
	 * @return int
	 */
	public final int getCritRcpt()
	{
		return critRcpt;
	}
	
	/**
	 * Method getDefenseFactor.
	 * @return int
	 */
	public final int getDefenseFactor()
	{
		return defenseFactor;
	}
	
	/**
	 * Method getFuncs.
	 * @return Func[]
	 */
	public final Func[] getFuncs()
	{
		return funcs;
	}
	
	/**
	 * Method getImpactFactor.
	 * @return int
	 */
	public final int getImpactFactor()
	{
		return impactFactor;
	}
	
	/**
	 * Method getMaxHp.
	 * @return int
	 */
	public final int getMaxHp()
	{
		return maxHp;
	}
	
	/**
	 * Method getMaxMp.
	 * @return int
	 */
	public final int getMaxMp()
	{
		return maxMp;
	}
	
	/**
	 * Method getModelId.
	 * @return int
	 */
	public int getModelId()
	{
		return 0;
	}
	
	/**
	 * Method getPowerFactor.
	 * @return int
	 */
	public final int getPowerFactor()
	{
		return powerFactor;
	}
	
	/**
	 * Method getRegHp.
	 * @return int
	 */
	public final int getRegHp()
	{
		return regHp;
	}
	
	/**
	 * Method getRegMp.
	 * @return int
	 */
	public final int getRegMp()
	{
		return regMp;
	}
	
	/**
	 * Method getRunSpd.
	 * @return int
	 */
	public final int getRunSpd()
	{
		return runSpd;
	}
	
	/**
	 * Method getTemplateId.
	 * @return int
	 */
	public int getTemplateId()
	{
		return 0;
	}
	
	/**
	 * Method getTemplateType.
	 * @return int
	 */
	public int getTemplateType()
	{
		return 0;
	}
	
	/**
	 * Method getTurnSpeed.
	 * @return int
	 */
	public int getTurnSpeed()
	{
		return turnSpeed;
	}
	
	/**
	 * Method getVars.
	 * @return VarTable
	 */
	public VarTable getVars()
	{
		return vars;
	}
	
	/**
	 * Method reload.
	 * @param update CharTemplate
	 */
	@Override
	public void reload(CharTemplate update)
	{
		if (getClass() != update.getClass())
		{
			return;
		}
		
		Objects.reload(this, update);
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " maxHp = " + maxHp + ", maxMp = " + maxMp + ", regHp = " + regHp + ", regMp = " + regMp + ", powerFactor = " + powerFactor + ", defenseFactor = " + defenseFactor + ", impactFactor = " + impactFactor + ", balanceFactor = " + balanceFactor + ", atkSpd = " + atkSpd + ", runSpd = " + runSpd + ", critRate = " + critRate + ", critRcpt = " + critRcpt;
	}
}