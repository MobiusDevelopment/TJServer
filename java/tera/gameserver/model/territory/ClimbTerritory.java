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
package tera.gameserver.model.territory;

import org.w3c.dom.Node;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class ClimbTerritory extends AbstractTerritory
{
	private final float targetX;
	private final float targetY;
	private final float targetZ;
	
	/**
	 * Constructor for ClimbTerritory.
	 * @param node Node
	 * @param type TerritoryType
	 */
	public ClimbTerritory(Node node, TerritoryType type)
	{
		super(node, type);
		final VarTable vars = VarTable.newInstance(node);
		targetX = vars.getFloat("targetX");
		targetY = vars.getFloat("targetY");
		targetZ = vars.getFloat("targetZ");
	}
	
	/**
	 * Method getTargetX.
	 * @return float
	 */
	public final float getTargetX()
	{
		return targetX;
	}
	
	/**
	 * Method getTargetY.
	 * @return float
	 */
	public final float getTargetY()
	{
		return targetY;
	}
	
	/**
	 * Method getTargetZ.
	 * @return float
	 */
	public final float getTargetZ()
	{
		return targetZ;
	}
}