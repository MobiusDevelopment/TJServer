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

import rlib.geom.Ray;
import rlib.geom.Rotation;
import rlib.geom.Vector;
import rlib.geom.VectorBuffer;
import rlib.logging.Logger;
import rlib.logging.Loggers;

public abstract class AbstractBounding implements Bounding
{
	protected static final Logger log = Loggers.getLogger(Bounding.class);
	protected Vector center;
	protected Vector offset;
	
	protected AbstractBounding(Vector center, Vector offset)
	{
		this.center = center;
		this.offset = offset;
	}
	
	@Override
	public boolean contains(Vector point, VectorBuffer buffer)
	{
		return this.contains(point.getX(), point.getY(), point.getZ(), buffer);
	}
	
	@Override
	public final float distanceTo(Vector point)
	{
		return this.center.distance(point);
	}
	
	@Override
	public final Vector getCenter()
	{
		return this.center;
	}
	
	@Override
	public Vector getOffset()
	{
		return this.offset;
	}
	
	@Override
	public final boolean intersects(Ray ray, VectorBuffer buffer)
	{
		return this.intersects(ray.getStart(), ray.getDirection(), buffer);
	}
	
	@Override
	public void setCenter(Vector center)
	{
		this.center = center;
	}
	
	@Override
	public boolean contains(float x, float y, float z, VectorBuffer buffer)
	{
		return false;
	}
	
	@Override
	public BoundingType getBoundingType()
	{
		return BoundingType.EMPTY;
	}
	
	@Override
	public Vector getResultCenter(VectorBuffer buffer)
	{
		return null;
	}
	
	@Override
	public boolean intersects(Bounding bounding, VectorBuffer buffer)
	{
		return false;
	}
	
	@Override
	public boolean intersects(Vector start, Vector direction, VectorBuffer buffer)
	{
		return false;
	}
	
	@Override
	public void update(Rotation rotation, VectorBuffer buffer)
	{
	}
}
