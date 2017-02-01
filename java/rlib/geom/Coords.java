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

import java.lang.reflect.Array;

import rlib.gamemodel.GameObject;
import rlib.logging.Loggers;
import rlib.util.Rnd;

public abstract class Coords
{
	@SuppressWarnings("unchecked")
	public static <T extends GamePoint> T[] arcCoords(Class<T> type, float x, float y, float z, int heading, int radius, int count, int degree, int width)
	{
		GamePoint[] locs = (GamePoint[]) Array.newInstance(type, count);
		float current = Angles.headingToDegree(heading) - degree;
		float min = current - width;
		float max = current + width;
		float angle = Math.abs(min - max) / count;
		int i = 0;
		while (i < count)
		{
			try
			{
				GamePoint loc = type.newInstance();
				float radians = Angles.degreeToRadians(min + (angle * i));
				float newX = Coords.calcX(x, radius, radians);
				float newY = Coords.calcY(y, radius, radians);
				loc.setXYZ(newX, newY, z);
				locs[i] = loc;
			}
			catch (IllegalAccessException | InstantiationException e)
			{
				Loggers.warning(type, e);
			}
			++i;
		}
		return (T[]) locs;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends GamePoint> T[] arcCoords(Class<T> type, GameObject object, int radius, int count, int degree, int width)
	{
		GamePoint[] locs = (GamePoint[]) Array.newInstance(type, count);
		float current = Angles.headingToDegree(object.getHeading()) - degree;
		float min = current - width;
		float max = current + width;
		float angle = Math.abs(min - max) / count;
		float x = object.getX();
		float y = object.getY();
		float z = object.getZ();
		int i = 0;
		while (i < count)
		{
			try
			{
				GamePoint loc = type.newInstance();
				float radians = Angles.degreeToRadians(min + (angle * i));
				float newX = Coords.calcX(x, radius, radians);
				float newY = Coords.calcY(y, radius, radians);
				loc.setXYZ(newX, newY, z);
				locs[i] = loc;
			}
			catch (IllegalAccessException | InstantiationException e)
			{
				Loggers.warning(type, e);
			}
			++i;
		}
		return (T[]) locs;
	}
	
	public static float calcX(float x, int distance, float radians)
	{
		return x + (distance * (float) Math.cos(radians));
	}
	
	public static float calcX(float x, int distance, int heading)
	{
		return x + (distance * (float) Math.cos(Angles.headingToRadians(heading)));
	}
	
	public static float calcX(float x, int distance, int heading, int offset)
	{
		return x + (distance * (float) Math.cos(Angles.headingToRadians(heading + offset)));
	}
	
	public static float calcY(float y, int distance, float radians)
	{
		return y + (distance * (float) Math.sin(radians));
	}
	
	public static float calcY(float y, int distance, int heading)
	{
		return y + (distance * (float) Math.sin(Angles.headingToRadians(heading)));
	}
	
	public static float calcY(float y, int distance, int heading, int offset)
	{
		return y + (distance * (float) Math.sin(Angles.headingToRadians(heading + offset)));
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends GamePoint> T[] circularCoords(Class<T> type, float x, float y, float z, int radius, int count)
	{
		GamePoint[] locs = (GamePoint[]) Array.newInstance(type, count);
		float angle = 360.0f / count;
		int i = 1;
		while (i <= count)
		{
			try
			{
				GamePoint loc = type.newInstance();
				float radians = Angles.degreeToRadians(i * angle);
				float newX = Coords.calcX(x, radius, radians);
				float newY = Coords.calcY(y, radius, radians);
				loc.setXYZ(newX, newY, z);
				locs[i - 1] = loc;
			}
			catch (IllegalAccessException | InstantiationException e)
			{
				Loggers.warning(type, e);
			}
			++i;
		}
		return (T[]) locs;
	}
	
	public static <T extends GamePoint> T[] getCircularPoints(T[] source, float x, float y, float z, int count, int radius)
	{
		if (count < 1)
		{
			return source;
		}
		float angle = 360.0f / count;
		int i = 1;
		while (i <= count)
		{
			float radians = Angles.degreeToRadians(angle * i);
			float newX = x + (radius * (float) Math.cos(radians));
			float newY = y + (radius * (float) Math.sin(radians));
			T point = source[i - 1];
			point.setXYZ(newX, newY, z);
			++i;
		}
		return source;
	}
	
	public static <T extends GamePoint> T randomCoords(T loc, float x, float y, float z, int radiusMin, int radiusMax)
	{
		return Coords.randomCoords(loc, x, y, z, Rnd.nextInt(35000), radiusMin, radiusMax);
	}
	
	public static <T extends GamePoint> T randomCoords(T loc, float x, float y, float z, int heading, int radiusMin, int radiusMax)
	{
		if ((radiusMax == 0) || (radiusMax < radiusMin))
		{
			loc.setXYZH(x, y, z, heading);
			return loc;
		}
		int radius = Rnd.nextInt(radiusMin, radiusMax);
		float radians = Angles.degreeToRadians(Rnd.nextInt(0, 360));
		float newX = Coords.calcX(x, radius, radians);
		float newY = Coords.calcY(y, radius, radians);
		loc.setXYZH(newX, newY, z, heading);
		return loc;
	}
	
	public static void spawnCircularObjects(GameObject locator, rlib.util.array.Array<? extends GameObject> objects, int radius)
	{
		if (objects.size() < 1)
		{
			return;
		}
		float angle = 360.0f / objects.size();
		float x = locator.getX();
		float y = locator.getY();
		float z = locator.getZ();
		int i = 1;
		int length = objects.size();
		while (i <= length)
		{
			float radians = Angles.degreeToRadians(angle * i);
			float newX = x + (radius * (float) Math.cos(radians));
			float newY = y + (radius * (float) Math.sin(radians));
			GameObject item = objects.get(i - 1);
			item.spawnMe(newX, newY, z, 0);
			++i;
		}
	}
	
	public static void spawnCircularObjects(GameObject locator, GameObject[] objects, int length, int radius)
	{
		if (length < 1)
		{
			return;
		}
		float angle = 360.0f / length;
		float x = locator.getX();
		float y = locator.getY();
		float z = locator.getZ();
		int i = 1;
		while (i <= length)
		{
			float radians = Angles.degreeToRadians(angle * i);
			float newX = x + (radius * (float) Math.cos(radians));
			float newY = y + (radius * (float) Math.sin(radians));
			GameObject item = objects[i - 1];
			item.spawnMe(newX, newY, z, 0);
			++i;
		}
	}
}
