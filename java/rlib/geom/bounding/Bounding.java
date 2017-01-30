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

public interface Bounding
{
	public boolean contains(float var1, float var2, float var3, VectorBuffer var4);
	
	public boolean contains(Vector var1, VectorBuffer var2);
	
	public float distanceTo(Vector var1);
	
	public BoundingType getBoundingType();
	
	public Vector getCenter();
	
	public Vector getOffset();
	
	public Vector getResultCenter(VectorBuffer var1);
	
	public boolean intersects(Bounding var1, VectorBuffer var2);
	
	public boolean intersects(Ray var1, VectorBuffer var2);
	
	public boolean intersects(Vector var1, Vector var2, VectorBuffer var3);
	
	public void setCenter(Vector var1);
	
	public void update(Rotation var1, VectorBuffer var2);
}
