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
package tera.gameserver.model.ai.npc;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Skill;

import rlib.util.pools.Foldable;

/**
 * @author Ronn
 * @created 12.04.2012
 */
public final class Task implements Foldable
{
	
	private Character target;
	
	private Skill skill;
	
	private TaskType type;
	
	private String message;
	
	private float x;
	private float y;
	private float z;
	
	private int heading;
	
	public Task()
	{
		super();
	}
	
	/**
	 * Constructor for Task.
	 * @param type TaskType
	 * @param target Character
	 */
	public Task(TaskType type, Character target)
	{
		this.type = type;
		this.target = target;
	}
	
	/**
	 * Constructor for Task.
	 * @param type TaskType
	 * @param target Character
	 * @param skill Skill
	 */
	public Task(TaskType type, Character target, Skill skill)
	{
		this.type = type;
		this.target = target;
		this.skill = skill;
	}
	
	/**
	 * Constructor for Task.
	 * @param type TaskType
	 * @param target Character
	 * @param skill Skill
	 * @param heading int
	 */
	public Task(TaskType type, Character target, Skill skill, int heading)
	{
		this(type, target, skill);
		this.heading = heading;
	}
	
	/**
	 * Constructor for Task.
	 * @param type TaskType
	 * @param x float
	 * @param y float
	 * @param z float
	 */
	public Task(TaskType type, float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
	}
	
	/**
	 * Constructor for Task.
	 * @param type TaskType
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param skill Skill
	 * @param target Character
	 */
	public Task(TaskType type, float x, float y, float z, Skill skill, Character target)
	{
		this(type, x, y, z);
		this.skill = skill;
		this.target = target;
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		type = null;
		target = null;
		skill = null;
	}
	
	/**
	 * Method getHeading.
	 * @return int
	 */
	public int getHeading()
	{
		return heading;
	}
	
	/**
	 * Method getMessage.
	 * @return String
	 */
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * Method getSkill.
	 * @return Skill
	 */
	public Skill getSkill()
	{
		return skill;
	}
	
	/**
	 * Method getTarget.
	 * @return Character
	 */
	public Character getTarget()
	{
		return target;
	}
	
	/**
	 * Method getType.
	 * @return TaskType
	 */
	public TaskType getType()
	{
		return type;
	}
	
	/**
	 * Method getX.
	 * @return float
	 */
	public float getX()
	{
		return x;
	}
	
	/**
	 * Method getY.
	 * @return float
	 */
	public float getY()
	{
		return y;
	}
	
	/**
	 * Method getZ.
	 * @return float
	 */
	public float getZ()
	{
		return z;
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
	 * Method setHeading.
	 * @param heading int
	 * @return Task
	 */
	public Task setHeading(int heading)
	{
		this.heading = heading;
		return this;
	}
	
	/**
	 * Method setMessage.
	 * @param message String
	 * @return Task
	 */
	public Task setMessage(String message)
	{
		this.message = message;
		return this;
	}
	
	/**
	 * Method setSkill.
	 * @param skill Skill
	 * @return Task
	 */
	public Task setSkill(Skill skill)
	{
		this.skill = skill;
		return this;
	}
	
	/**
	 * Method setTarget.
	 * @param target Character
	 * @return Task
	 */
	public Task setTarget(Character target)
	{
		this.target = target;
		return this;
	}
	
	/**
	 * Method setType.
	 * @param type TaskType
	 * @return Task
	 */
	public Task setType(TaskType type)
	{
		this.type = type;
		
		if (type == null)
		{
			Thread.dumpStack();
		}
		
		return this;
	}
	
	/**
	 * Method setX.
	 * @param x float
	 * @return Task
	 */
	public Task setX(float x)
	{
		this.x = x;
		return this;
	}
	
	/**
	 * Method setY.
	 * @param y float
	 * @return Task
	 */
	public Task setY(float y)
	{
		this.y = y;
		return this;
	}
	
	/**
	 * Method setZ.
	 * @param z float
	 * @return Task
	 */
	public Task setZ(float z)
	{
		this.z = z;
		return this;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "Task [target=" + target + ", skill=" + skill + ", type=" + type + ", message=" + message + ", x=" + x + ", y=" + y + ", z=" + z + ", heading=" + heading + "]";
	}
}
