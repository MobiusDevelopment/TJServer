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
package rlib.geom;

public abstract class Geometry
{
	public static float getDistance(float startX, float startY, float startZ, float targetX, float targetY, float targetZ)
	{
		return (float) Math.sqrt(Geometry.getSquareDistance(startX, startY, startZ, targetX, targetY, targetZ));
	}
	
	public static float getDistanceToLine(float startX, float startY, float endX, float endY, float targetX, float targetY)
	{
		return (float) Math.sqrt(Geometry.getSquareDistanceToLine(startX, startY, endX, endY, targetX, targetY));
	}
	
	public static float getDistanceToLine(float startX, float startY, float startZ, float endX, float endY, float endZ, float targetX, float targetY, float targetZ)
	{
		return (float) Math.sqrt(Geometry.getSquareDistanceToLine(startX, startY, startZ, endX, endY, endZ, targetX, targetY, targetZ));
	}
	
	public static float getSquareDistance(float startX, float startY, float startZ, float targetX, float targetY, float targetZ)
	{
		float dx = targetX - startX;
		float dy = targetY - startY;
		float dz = targetZ - startZ;
		return (dx * dx) + (dy * dy) + (dz * dz);
	}
	
	public static float getSquareDistanceToLine(float startX, float startY, float endX, float endY, float targetX, float targetY)
	{
		float dotprod = ((targetX -= startX) * (endX -= startX)) + ((targetY -= startY) * (endY -= startY));
		float projlenSq = dotprod <= 0.0f ? 0.0f : ((dotprod = ((targetX = endX - targetX) * endX) + ((targetY = endY - targetY) * endY)) <= 0.0f ? 0.0f : (dotprod * dotprod) / ((endX * endX) + (endY * endY)));
		float lenSq = ((targetX * targetX) + (targetY * targetY)) - projlenSq;
		if (lenSq < 0.0f)
		{
			lenSq = 0.0f;
		}
		return lenSq;
	}
	
	public static float getSquareDistanceToLine(float startX, float startY, float startZ, float endX, float endY, float endZ, float targetX, float targetY, float targetZ)
	{
		float pointX = targetX - startX;
		float pointY = targetY - startY;
		float pointZ = targetZ - startZ;
		float lineX = endX - startX;
		float lineY = endY - startY;
		float lineZ = endZ - startZ;
		float c1 = Geometry.scalar(pointX, pointY, pointZ, lineX, lineY, lineZ);
		if (c1 < 0.0f)
		{
			return Geometry.squareLength(targetX, targetY, targetZ, startX, startY, startZ);
		}
		float c2 = Geometry.scalar(lineX, lineY, lineZ, lineX, lineY, lineZ);
		if (c2 <= c1)
		{
			return Geometry.squareLength(targetX, targetY, targetZ, endX, endY, endZ);
		}
		float b = c1 / c2;
		pointX = startX + (lineX * b);
		pointY = startY + (lineY * b);
		pointZ = startZ + (lineZ * b);
		return Geometry.squareLength(targetX, targetY, targetZ, pointX, pointY, pointZ);
	}
	
	public static float scalar(float x1, float y1, float z1, float x2, float y2, float z2)
	{
		return (x1 * x2) + (y1 * y2) + (z1 * z2);
	}
	
	public static float squareLength(float x1, float y1, float z1, float x2, float y2, float z2)
	{
		float dx = x1 - x2;
		float dy = y1 - y2;
		float dz = z1 - z2;
		return (dx * dx) + (dy * dy) + (dz * dz);
	}
}
