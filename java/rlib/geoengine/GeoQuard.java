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
package rlib.geoengine;

public final class GeoQuard
{
	private final int x;
	private final int y;
	private float height;
	
	public GeoQuard(int x, int y, float height)
	{
		this.x = x;
		this.y = y;
		this.height = height;
	}
	
	public final float getHeight()
	{
		return height;
	}
	
	public final int getX()
	{
		return x;
	}
	
	public final int getY()
	{
		return y;
	}
	
	public final void setHeight(float height)
	{
		this.height = height;
	}
	
	@Override
	public String toString()
	{
		return "GeoQuard [x = " + x + ", y = " + y + ", height = " + height + "]";
	}
}
