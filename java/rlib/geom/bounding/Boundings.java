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
package rlib.geom.bounding;

import rlib.geom.Vector;

public final class Boundings
{
	public static Bounding newBoundingBox(Vector center, Vector offset, float sizeX, float sizeY, float sizeZ)
	{
		return new AxisAlignedBoundingBox(center, offset, sizeX, sizeY, sizeZ);
	}
	
	public static Bounding newBoundingSphere(Vector center, Vector offset, int radius)
	{
		return new BoundingSphere(center, offset, radius);
	}
	
	public static Bounding newBoundingEmpty()
	{
		return new AbstractBounding(null, null)
		{
		};
	}
	
	private Boundings()
	{
		throw new IllegalArgumentException();
	}
}
