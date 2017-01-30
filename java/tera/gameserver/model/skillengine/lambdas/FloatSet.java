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
package tera.gameserver.model.skillengine.lambdas;

/**
 * @author Ronn
 * @created 28.02.2012
 */
public final class FloatSet extends LambdaFloat
{
	/**
	 * Constructor for FloatSet.
	 * @param value float
	 */
	public FloatSet(float value)
	{
		super(value);
	}
	
	/**
	 * Method calc.
	 * @param val float
	 * @return float
	 * @see tera.gameserver.model.skillengine.lambdas.Lambda#calc(float)
	 */
	@Override
	public float calc(float val)
	{
		return value;
	}
}
