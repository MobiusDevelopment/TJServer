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

public final class Matrix3f
{
	public static final Matrix3f ZERO = new Matrix3f(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
	public static final Matrix3f IDENTITY = new Matrix3f();
	protected float val_0_0;
	protected float val_0_1;
	protected float val_0_2;
	protected float val_1_0;
	protected float val_1_1;
	protected float val_1_2;
	protected float val_2_0;
	protected float val_2_1;
	protected float val_2_2;
	
	public static Matrix3f newInstance(float val_0_0, float val_0_1, float val_0_2, float val_1_0, float val_1_1, float val_1_2, float val_2_0, float val_2_1, float val_2_2)
	{
		return new Matrix3f(val_0_0, val_0_1, val_0_2, val_1_0, val_1_1, val_1_2, val_2_0, val_2_1, val_2_2);
	}
	
	public static Matrix3f newInstance()
	{
		return new Matrix3f();
	}
	
	private Matrix3f(float val_0_0, float val_0_1, float val_0_2, float val_1_0, float val_1_1, float val_1_2, float val_2_0, float val_2_1, float val_2_2)
	{
		this.val_0_0 = val_0_0;
		this.val_0_1 = val_0_1;
		this.val_0_2 = val_0_2;
		this.val_1_0 = val_1_0;
		this.val_1_1 = val_1_1;
		this.val_1_2 = val_1_2;
		this.val_2_0 = val_2_0;
		this.val_2_1 = val_2_1;
		this.val_2_2 = val_2_2;
	}
	
	public Matrix3f()
	{
		val_2_1 = 0.0f;
		val_2_0 = 0.0f;
		val_1_2 = 0.0f;
		val_1_0 = 0.0f;
		val_0_2 = 0.0f;
		val_0_1 = 0.0f;
		val_2_2 = 1.0f;
		val_1_1 = 1.0f;
		val_0_0 = 1.0f;
	}
	
	public void absoluteLocal()
	{
		val_0_0 = Math.abs(val_0_0);
		val_0_1 = Math.abs(val_0_1);
		val_0_2 = Math.abs(val_0_2);
		val_1_0 = Math.abs(val_1_0);
		val_1_1 = Math.abs(val_1_1);
		val_1_2 = Math.abs(val_1_2);
		val_2_0 = Math.abs(val_2_0);
		val_2_1 = Math.abs(val_2_1);
		val_2_2 = Math.abs(val_2_2);
	}
	
	public Vector mult(Vector vector, Vector result)
	{
		float x = vector.x;
		float y = vector.y;
		float z = vector.z;
		result.x = (val_0_0 * x) + (val_0_1 * y) + (val_0_2 * z);
		result.y = (val_1_0 * x) + (val_1_1 * y) + (val_1_2 * z);
		result.z = (val_2_0 * x) + (val_2_1 * y) + (val_2_2 * z);
		return result;
	}
	
	public Matrix3f set(Rotation rotation)
	{
		return rotation.toRotationMatrix(this);
	}
	
	public void set(float val_0_0, float val_0_1, float val_0_2, float val_1_0, float val_1_1, float val_1_2, float val_2_0, float val_2_1, float val_2_2)
	{
		this.val_0_0 = val_0_0;
		this.val_0_1 = val_0_1;
		this.val_0_2 = val_0_2;
		this.val_1_0 = val_1_0;
		this.val_1_1 = val_1_1;
		this.val_1_2 = val_1_2;
		this.val_2_0 = val_2_0;
		this.val_2_1 = val_2_1;
		this.val_2_2 = val_2_2;
	}
}
