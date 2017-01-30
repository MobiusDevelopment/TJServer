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
package tera.gameserver.model.npc.summons;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.StatType;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.model.skillengine.funcs.StatFunc;
import tera.gameserver.templates.NpcTemplate;

/**
 * @author Ronn
 */
public class SmokeSummon extends DefaultSummon
{
	private final Func maxHpFunc;
	int maxHp;
	
	/**
	 * Constructor for SmokeSummon.
	 * @param objectId int
	 * @param template NpcTemplate
	 */
	public SmokeSummon(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		maxHpFunc = new StatFunc()
		{
			@Override
			public void addFuncTo(Character owner)
			{
				owner.addStatFunc(this);
			}
			
			@Override
			public float calc(Character attacker, Character attacked, Skill skill, float val)
			{
				final Character owner = getOwner();
				
				if (owner == null)
				{
					return val;
				}
				
				return maxHp;
			}
			
			@Override
			public int compareTo(StatFunc func)
			{
				return 0x90 - func.getOrder();
			}
			
			@Override
			public int getOrder()
			{
				return 0x90;
			}
			
			@Override
			public StatType getStat()
			{
				return StatType.MAX_HP;
			}
			
			@Override
			public void removeFuncTo(Character owner)
			{
				owner.removeStatFunc(this);
			}
		};
		maxHpFunc.addFuncTo(this);
	}
	
	/**
	 * Method getTemplateId.
	 * @return int
	 */
	@Override
	public int getTemplateId()
	{
		final Character owner = getOwner();
		
		if (owner != null)
		{
			return owner.getTemplateId() * 100;
		}
		
		return super.getTemplateId();
	}
	
	@Override
	public void spawnMe()
	{
		final Character owner = getOwner();
		
		if (owner != null)
		{
			setMaxHp(Math.max((int) (((owner.getMaxHp() * 20) / 100) * 7.5F), 1));
		}
		else
		{
			setMaxHp(1);
		}
		
		setCurrentHp(getMaxHp());
		setCurrentMp(getMaxMp());
		super.spawnMe();
	}
	
	/**
	 * Method setMaxHp.
	 * @param maxHp int
	 */
	public void setMaxHp(int maxHp)
	{
		this.maxHp = maxHp;
	}
}