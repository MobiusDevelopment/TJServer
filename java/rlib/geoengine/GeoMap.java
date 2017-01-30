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

import java.io.File;

import rlib.util.array.Array;

public interface GeoMap
{
	public void addQuard(GeoQuard var1);
	
	public void addQuard(int var1, int var2, float var3);
	
	public void exportTo(File var1);
	
	public Array<GeoQuard> getAllQuards(Array<GeoQuard> var1);
	
	public GeoQuard getGeoQuard(float var1, float var2, float var3);
	
	public float getHeight(float var1, float var2, float var3);
	
	public GeoMap importTo(File var1);
	
	public int size();
}
