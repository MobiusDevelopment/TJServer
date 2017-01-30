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
package tera.gameserver.model;

import tera.gameserver.model.skillengine.Skill;
import tera.util.Location;

import rlib.util.pools.Foldable;

/**
 * @author Ronn
 */
public class WaitCastSkill implements Foldable
{
	private final Location targetLoc;
	private Skill skill;
	
	public WaitCastSkill()
	{
		targetLoc = new Location();
	}
	
	/**
	 * Method equals.
	 * @param obj Object
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj)
	{
		return (obj == this) || (obj == skill);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		skill = null;
	}
	
	/**
	 * Method getSkill.
	 * @return Skill
	 */
	public final Skill getSkill()
	{
		return skill;
	}
	
	/**
	 * Method getTargetLoc.
	 * @return Location
	 */
	public final Location getTargetLoc()
	{
		return targetLoc;
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
	}
	
	/**
	 * Method setSkill.
	 * @param skill Skill
	 */
	public final void setSkill(Skill skill)
	{
		this.skill = skill;
	}
	
	/**
	 * Method setTargetLoc.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param heading int
	 */
	public final void setTargetLoc(float x, float y, float z, int heading)
	{
		targetLoc.setXYZH(x, y, z, heading);
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return skill.getName();
	}
}