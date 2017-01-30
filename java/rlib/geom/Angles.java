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

public abstract class Angles
{
	public static int calcHeading(float x, float y, float targetX, float targetY)
	{
		return (int) (Math.atan2(y - targetY, x - targetX) * 10430.3779296875) + 32768;
	}
	
	public static int calcHeadingTo(float x, float y, int heading, float targetX, float targetY)
	{
		int newHeading = Angles.calcHeading(x, y, targetX, targetY);
		if ((newHeading = heading - newHeading) < 0)
		{
			newHeading = (newHeading + 1 + Integer.MAX_VALUE) & 65535;
		}
		else if (newHeading > 65535)
		{
			newHeading &= 65535;
		}
		return newHeading;
	}
	
	public static int degreeToHeading(float degree)
	{
		if (degree < 0.0f)
		{
			degree += 360.0f;
		}
		return (int) (degree * 182.04445f);
	}
	
	public static float degreeToRadians(float angle)
	{
		return (angle * 3.1415927f) / 180.0f;
	}
	
	public static float getAngleFrom(float startX, float startY, float endX, float endY)
	{
		float angle = (float) Math.toDegrees(Math.atan2(startY - endY, startX - endX));
		if (angle <= 0.0f)
		{
			angle += 360.0f;
		}
		return angle;
	}
	
	public static float headingToDegree(int heading)
	{
		float angle = heading / 182.04445f;
		if (angle == 0.0f)
		{
			angle = 360.0f;
		}
		return angle;
	}
	
	public static float headingToRadians(int heading)
	{
		float angle = heading / 182.04445f;
		if (angle == 0.0f)
		{
			angle = 360.0f;
		}
		return (angle * 3.1415927f) / 180.0f;
	}
	
	public static boolean isInDegree(float x, float y, int heading, float targetX, float targetY, int width)
	{
		boolean flag;
		int angle = (int) Angles.headingToDegree(Angles.calcHeadingTo(x, y, heading, targetX, targetY));
		int degree = (int) Angles.headingToDegree(heading);
		int min = degree - width;
		int max = degree + width;
		if (min < 0)
		{
			min += 360;
		}
		if (max < 0)
		{
			max += 360;
		}
		flag = (angle - degree) > 180;
		if (flag)
		{
			angle -= 360;
		}
		if (angle > max)
		{
			return false;
		}
		if ((angle += 360) > min)
		{
			return true;
		}
		return false;
	}
}
