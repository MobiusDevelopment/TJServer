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
package tera.gameserver.model.items;

import tera.gameserver.model.Character;
import tera.gameserver.model.TObject;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.serverpackets.CharPickUpItem;
import tera.gameserver.templates.CommonTemplate;
import tera.gameserver.templates.ItemTemplate;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public final class CommonInstance extends ItemInstance
{
	
	private Skill activeSkill;
	
	/**
	 * Constructor for CommonInstance.
	 * @param objectId int
	 * @param template ItemTemplate
	 */
	public CommonInstance(int objectId, ItemTemplate template)
	{
		super(objectId, template);
		final SkillTemplate temp = getTemplate().getActiveSkill();
		
		if (temp != null)
		{
			activeSkill = temp.newInstance();
		}
	}
	
	/**
	 * Method getActiveSkill.
	 * @return Skill
	 */
	@Override
	public Skill getActiveSkill()
	{
		return activeSkill;
	}
	
	/**
	 * Method getCommon.
	 * @return CommonInstance
	 */
	@Override
	public CommonInstance getCommon()
	{
		return this;
	}
	
	/**
	 * Method getTemplate.
	 * @return CommonTemplate
	 */
	@Override
	public CommonTemplate getTemplate()
	{
		return (CommonTemplate) template;
	}
	
	/**
	 * Method isCommon.
	 * @return boolean
	 */
	@Override
	public boolean isCommon()
	{
		return true;
	}
	
	/**
	 * Method isHerb.
	 * @return boolean
	 */
	@Override
	public boolean isHerb()
	{
		return template.getType() == CommonType.HERB;
	}
	
	/**
	 * Method pickUpMe.
	 * @param target TObject
	 * @return boolean
	 */
	@Override
	public boolean pickUpMe(TObject target)
	{
		if (!isHerb())
		{
			return super.pickUpMe(target);
		}
		if (target == null)
		{
			return false;
		}
		
		final Character character = target.getCharacter();
		
		if (character == null)
		{
			return false;
		}
		
		character.broadcastPacket(CharPickUpItem.getInstance(character, this));
		deleteMe();
		character.getAI().startUseItem(this, character.getHeading(), true);
		return true;
	}
}
