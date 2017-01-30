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
package tera.gameserver.model.skillengine.conditions;

import org.w3c.dom.Node;

import tera.gameserver.model.skillengine.Condition;

/**
 * @author Ronn
 */
public abstract class AbstractCondition implements Condition
{
	protected static final String MESSAGE = "No condition.";
	private String msg = MESSAGE;
	
	/**
	 * Method getMsg.
	 * @return String
	 * @see tera.gameserver.model.skillengine.Condition#getMsg()
	 */
	@Override
	public String getMsg()
	{
		return msg;
	}
	
	/**
	 * Method setMsg.
	 * @param msg Node
	 * @return Condition
	 * @see tera.gameserver.model.skillengine.Condition#setMsg(Node)
	 */
	@Override
	public Condition setMsg(Node msg)
	{
		if (msg == null)
		{
			return this;
		}
		
		this.msg = msg.getNodeValue();
		return this;
	}
}
