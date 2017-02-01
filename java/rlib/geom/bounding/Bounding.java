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
	boolean contains(float var1, float var2, float var3, VectorBuffer var4);
	
	boolean contains(Vector var1, VectorBuffer var2);
	
	float distanceTo(Vector var1);
	
	BoundingType getBoundingType();
	
	Vector getCenter();
	
	Vector getOffset();
	
	Vector getResultCenter(VectorBuffer var1);
	
	boolean intersects(Bounding var1, VectorBuffer var2);
	
	boolean intersects(Ray var1, VectorBuffer var2);
	
	boolean intersects(Vector var1, Vector var2, VectorBuffer var3);
	
	void setCenter(Vector var1);
	
	void update(Rotation var1, VectorBuffer var2);
}
