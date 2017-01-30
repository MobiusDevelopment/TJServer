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
import rlib.geom.VectorBuffer;

public final class BoundingSphere extends AbstractBounding
{
	protected float radius;
	protected float squareRadius;
	private static /* synthetic */ int[] $SWITCH_TABLE$rlib$geom$bounding$BoundingType;
	
	protected BoundingSphere(Vector center, Vector offset, float radius)
	{
		super(center, offset);
		this.radius = radius;
		this.squareRadius = radius * radius;
	}
	
	@Override
	public boolean contains(float x, float y, float z, VectorBuffer buffer)
	{
		Vector center = this.getResultCenter(buffer);
		if (center.distanceSquared(x, y, z) < this.squareRadius)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public BoundingType getBoundingType()
	{
		return BoundingType.SPHERE;
	}
	
	@Override
	public Vector getResultCenter(VectorBuffer buffer)
	{
		Vector vector = buffer.getNextVector();
		vector.set(this.center);
		if (this.offset == Vector.ZERO)
		{
			return vector;
		}
		return vector.addLocal(this.offset);
	}
	
	@Override
	public boolean intersects(Bounding bounding, VectorBuffer buffer)
	{
		switch (BoundingSphere.$SWITCH_TABLE$rlib$geom$bounding$BoundingType()[bounding.getBoundingType().ordinal()])
		{
			case 3:
			{
				return false;
			}
			case 2:
			{
				BoundingSphere sphere = (BoundingSphere) bounding;
				Vector diff = this.getResultCenter(buffer);
				diff.subtractLocal(sphere.getResultCenter(buffer));
				float rsum = this.getRadius() + sphere.getRadius();
				if (diff.dot(diff) <= (rsum * rsum))
				{
					return true;
				}
				return false;
			}
			case 1:
			{
				AxisAlignedBoundingBox box = (AxisAlignedBoundingBox) bounding;
				Vector center = this.getResultCenter(buffer);
				Vector target = box.getResultCenter(buffer);
				if (Math.abs(target.getX() - center.getX()) >= (this.getRadius() + box.getSizeX()))
				{
					return false;
				}
				if (Math.abs(target.getY() - center.getY()) >= (this.getRadius() + box.getSizeY()))
				{
					return false;
				}
				if (Math.abs(target.getZ() - center.getZ()) >= (this.getRadius() + box.getSizeZ()))
				{
					return false;
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean intersects(Vector start, Vector direction, VectorBuffer buffer)
	{
		Vector diff = buffer.getNextVector();
		diff.set(start).subtractLocal(this.getResultCenter(buffer));
		float a = start.dot(diff) - this.squareRadius;
		if (a <= 0.0)
		{
			return true;
		}
		float b = direction.dot(diff);
		if (b >= 0.0)
		{
			return false;
		}
		if ((b * b) >= a)
		{
			return true;
		}
		return false;
	}
	
	public float getRadius()
	{
		return this.radius;
	}
	
	@Override
	public String toString()
	{
		return "BoundingSphere [radius=" + this.radius + ", squareRadius=" + this.squareRadius + "]";
	}
	
	static /* synthetic */ int[] $SWITCH_TABLE$rlib$geom$bounding$BoundingType()
	{
		int[] arrn;
		int[] arrn2 = $SWITCH_TABLE$rlib$geom$bounding$BoundingType;
		if (arrn2 != null)
		{
			return arrn2;
		}
		arrn = new int[BoundingType.values().length];
		try
		{
			arrn[BoundingType.AXIS_ALIGNED_BOX.ordinal()] = 1;
		}
		catch (NoSuchFieldError noSuchFieldError)
		{
		}
		try
		{
			arrn[BoundingType.EMPTY.ordinal()] = 3;
		}
		catch (NoSuchFieldError noSuchFieldError)
		{
		}
		try
		{
			arrn[BoundingType.SPHERE.ordinal()] = 2;
		}
		catch (NoSuchFieldError noSuchFieldError)
		{
		}
		$SWITCH_TABLE$rlib$geom$bounding$BoundingType = arrn;
		return $SWITCH_TABLE$rlib$geom$bounding$BoundingType;
	}
}
