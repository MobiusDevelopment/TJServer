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

import rlib.util.ExtMath;

public class Rotation
{
	private float x;
	private float y;
	private float z;
	private float w;
	private static /* synthetic */ int[] $SWITCH_TABLE$rlib$geom$VectorType;
	
	public static Rotation newInstance()
	{
		return new Rotation();
	}
	
	public static Rotation newInstance(float x, float y, float z, float w)
	{
		return new Rotation(x, y, z, w);
	}
	
	public static Rotation newInstance(float[] vals)
	{
		return new Rotation(vals[0], vals[1], vals[2], vals[3]);
	}
	
	private Rotation()
	{
		this.w = 1.0f;
	}
	
	private Rotation(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public float dot(Rotation q)
	{
		return (this.w * q.w) + (this.x * q.x) + (this.y * q.y) + (this.z * q.z);
	}
	
	public Vector getRotationColumn(VectorType type, Vector store)
	{
		float norm;
		if (store == null)
		{
			store = Vector.newInstance();
		}
		if ((norm = this.norm()) != 1.0f)
		{
			norm = ExtMath.invSqrt(norm);
		}
		float xx = this.x * this.x * norm;
		float xy = this.x * this.y * norm;
		float xz = this.x * this.z * norm;
		float xw = this.x * this.w * norm;
		float yy = this.y * this.y * norm;
		float yz = this.y * this.z * norm;
		float yw = this.y * this.w * norm;
		float zz = this.z * this.z * norm;
		float zw = this.z * this.w * norm;
		switch (Rotation.$SWITCH_TABLE$rlib$geom$VectorType()[type.ordinal()])
		{
			case 1:
			{
				store.setX(1.0f - (2.0f * (yy + zz)));
				store.setY(2.0f * (xy + zw));
				store.setZ(2.0f * (xz - yw));
				break;
			}
			case 2:
			{
				store.setX(2.0f * (xy - zw));
				store.setY(1.0f - (2.0f * (xx + zz)));
				store.setZ(2.0f * (yz + xw));
				break;
			}
			case 3:
			{
				store.setX(2.0f * (xz + yw));
				store.setY(2.0f * (yz - xw));
				store.setZ(1.0f - (2.0f * (xx + yy)));
			}
		}
		return store;
	}
	
	public final float getW()
	{
		return this.w;
	}
	
	public final float getX()
	{
		return this.x;
	}
	
	public final float getY()
	{
		return this.y;
	}
	
	public final float getZ()
	{
		return this.z;
	}
	
	public float norm()
	{
		return (this.w * this.w) + (this.x * this.x) + (this.y * this.y) + (this.z * this.z);
	}
	
	public Rotation set(Rotation rotation)
	{
		this.x = rotation.x;
		this.y = rotation.y;
		this.z = rotation.z;
		this.w = rotation.w;
		return this;
	}
	
	public final void setW(float w)
	{
		this.w = w;
	}
	
	public final void setX(float x)
	{
		this.x = x;
	}
	
	public void setXYZW(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public final void setY(float y)
	{
		this.y = y;
	}
	
	public final void setZ(float z)
	{
		this.z = z;
	}
	
	public void slerp(Rotation q2, float changeAmnt)
	{
		if ((this.x == q2.x) && (this.y == q2.y) && (this.z == q2.z) && (this.w == q2.w))
		{
			return;
		}
		float result = (this.x * q2.x) + (this.y * q2.y) + (this.z * q2.z) + (this.w * q2.w);
		if (result < 0.0f)
		{
			q2.x = -q2.x;
			q2.y = -q2.y;
			q2.z = -q2.z;
			q2.w = -q2.w;
			result = -result;
		}
		float scale0 = 1.0f - changeAmnt;
		float scale1 = changeAmnt;
		if ((1.0f - result) > 0.1f)
		{
			float theta = ExtMath.acos(result);
			float invSinTheta = 1.0f / ExtMath.sin(theta);
			scale0 = ExtMath.sin((1.0f - changeAmnt) * theta) * invSinTheta;
			scale1 = ExtMath.sin(changeAmnt * theta) * invSinTheta;
		}
		this.x = (scale0 * this.x) + (scale1 * q2.x);
		this.y = (scale0 * this.y) + (scale1 * q2.y);
		this.z = (scale0 * this.z) + (scale1 * q2.z);
		this.w = (scale0 * this.w) + (scale1 * q2.w);
	}
	
	public Rotation slerp(Rotation q1, Rotation q2, float t)
	{
		if ((q1.x == q2.x) && (q1.y == q2.y) && (q1.z == q2.z) && (q1.w == q2.w))
		{
			this.set(q1);
			return this;
		}
		float result = (q1.x * q2.x) + (q1.y * q2.y) + (q1.z * q2.z) + (q1.w * q2.w);
		if (result < 0.0f)
		{
			q2.x = -q2.x;
			q2.y = -q2.y;
			q2.z = -q2.z;
			q2.w = -q2.w;
			result = -result;
		}
		float scale0 = 1.0f - t;
		float scale1 = t;
		if ((1.0f - result) > 0.1f)
		{
			float theta = ExtMath.acos(result);
			float invSinTheta = 1.0f / ExtMath.sin(theta);
			scale0 = ExtMath.sin((1.0f - t) * theta) * invSinTheta;
			scale1 = ExtMath.sin(t * theta) * invSinTheta;
		}
		this.x = (scale0 * q1.x) + (scale1 * q2.x);
		this.y = (scale0 * q1.y) + (scale1 * q2.y);
		this.z = (scale0 * q1.z) + (scale1 * q2.z);
		this.w = (scale0 * q1.w) + (scale1 * q2.w);
		return this;
	}
	
	public final Rotation slerp(Rotation start, Rotation end, float done, boolean forceLinear)
	{
		if (start.equals(end))
		{
			this.set(start);
			return this;
		}
		float result = start.dot(end);
		if (result < 0.0f)
		{
			end.negateLocal();
			result = -result;
		}
		float startScale = 1.0f - done;
		float endScale = done;
		if (!forceLinear && ((1.0f - result) > 0.1f))
		{
			float theta = ExtMath.acos(result);
			float invSinTheta = 1.0f / ExtMath.sin(theta);
			startScale = ExtMath.sin((1.0f - done) * theta) * invSinTheta;
			endScale = ExtMath.sin(done * theta) * invSinTheta;
		}
		this.x = (startScale * start.getX()) + (endScale * end.getX());
		this.y = (startScale * start.getY()) + (endScale * end.getY());
		this.z = (startScale * start.getZ()) + (endScale * end.getZ());
		this.w = (startScale * start.getW()) + (endScale * end.getW());
		return this;
	}
	
	public final float toAngleAxis(Vector axisStore)
	{
		float angle;
		float sqrLength = (this.x * this.x) + (this.y * this.y) + (this.z * this.z);
		if (sqrLength == 0.0f)
		{
			angle = 0.0f;
			if (axisStore != null)
			{
				axisStore.setX(1.0f);
				axisStore.setY(0.0f);
				axisStore.setZ(0.0f);
			}
		}
		else
		{
			angle = 2.0f * ExtMath.acos(this.w);
			if (axisStore != null)
			{
				float invLength = 1.0f / ExtMath.sqrt(sqrLength);
				axisStore.setX(this.x * invLength);
				axisStore.setY(this.y * invLength);
				axisStore.setZ(this.z * invLength);
			}
		}
		return angle;
	}
	
	public final Matrix3f toRotationMatrix(Matrix3f result)
	{
		float norm = this.norm();
		float s = norm == 1.0f ? 2.0f : (norm > 0.0f ? 2.0f / norm : 0.0f);
		float x = this.getX();
		float y = this.getY();
		float z = this.getZ();
		float w = this.getW();
		float xs = x * s;
		float ys = y * s;
		float zs = z * s;
		float xx = x * xs;
		float xy = x * ys;
		float xz = x * zs;
		float xw = w * xs;
		float yy = y * ys;
		float yz = y * zs;
		float yw = w * ys;
		float zz = z * zs;
		float zw = w * zs;
		result.set(1.0f - (yy + zz), xy - zw, xz + yw, xy + zw, 1.0f - (xx + zz), yz - xw, xz - yw, yz + xw, 1.0f - (xx + yy));
		return result;
	}
	
	public final Vector multLocal(Vector vector)
	{
		float vectorX = vector.getX();
		float vectorY = vector.getY();
		float vectorZ = vector.getZ();
		float x = this.getX();
		float y = this.getY();
		float z = this.getZ();
		float w = this.getW();
		vector.setX(((((w * w * vectorX) + (2.0f * y * w * vectorZ)) - (2.0f * z * w * vectorY)) + (x * x * vectorX) + (2.0f * y * x * vectorY) + (2.0f * z * x * vectorZ)) - (z * z * vectorX) - (y * y * vectorX));
		vector.setY(((((2.0f * x * y * vectorX) + (y * y * vectorY) + (2.0f * z * y * vectorZ) + (2.0f * w * z * vectorX)) - (z * z * vectorY)) + (w * w * vectorY)) - (2.0f * x * w * vectorZ) - (x * x * vectorY));
		vector.setZ((((((2.0f * x * z * vectorX) + (2.0f * y * z * vectorY) + (z * z * vectorZ)) - (2.0f * w * y * vectorX) - (y * y * vectorZ)) + (2.0f * w * x * vectorY)) - (x * x * vectorZ)) + (w * w * vectorZ));
		return vector;
	}
	
	public final Rotation fromAngles(float[] angles)
	{
		return this.fromAngles(angles[0], angles[1], angles[2]);
	}
	
	public final Rotation fromAngles(float angleX, float yAngle, float zAngle)
	{
		float angle = zAngle * 0.5f;
		float sinZ = ExtMath.sin(angle);
		float cosZ = ExtMath.cos(angle);
		angle = yAngle * 0.5f;
		float sinY = ExtMath.sin(angle);
		float cosY = ExtMath.cos(angle);
		angle = angleX * 0.5f;
		float sinX = ExtMath.sin(angle);
		float cosX = ExtMath.cos(angle);
		float cosYXcosZ = cosY * cosZ;
		float sinYXsinZ = sinY * sinZ;
		float cosYXsinZ = cosY * sinZ;
		float sinYXcosZ = sinY * cosZ;
		this.w = (cosYXcosZ * cosX) - (sinYXsinZ * sinX);
		this.x = (cosYXcosZ * sinX) + (sinYXsinZ * cosX);
		this.y = (sinYXcosZ * cosX) + (cosYXsinZ * sinX);
		this.z = (cosYXsinZ * cosX) - (sinYXcosZ * sinX);
		this.normalizeLocal();
		return this;
	}
	
	public final Rotation normalizeLocal()
	{
		float norm = ExtMath.invSqrt(this.norm());
		this.x *= norm;
		this.y *= norm;
		this.z *= norm;
		this.w *= norm;
		return this;
	}
	
	@Override
	public final int hashCode()
	{
		int prime = 31;
		int result = 1;
		result = (31 * result) + Float.floatToIntBits(this.w);
		result = (31 * result) + Float.floatToIntBits(this.x);
		result = (31 * result) + Float.floatToIntBits(this.y);
		result = (31 * result) + Float.floatToIntBits(this.z);
		return result;
	}
	
	@Override
	public final boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (this.getClass() != obj.getClass())
		{
			return false;
		}
		Rotation other = (Rotation) obj;
		if (Float.floatToIntBits(this.w) != Float.floatToIntBits(other.w))
		{
			return false;
		}
		if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x))
		{
			return false;
		}
		if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y))
		{
			return false;
		}
		if (Float.floatToIntBits(this.z) != Float.floatToIntBits(other.z))
		{
			return false;
		}
		return true;
	}
	
	public final Rotation negateLocal()
	{
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		this.w = -this.w;
		return this;
	}
	
	@Override
	public String toString()
	{
		return "Rotation x = " + this.x + ", y = " + this.y + ", z = " + this.z + ", w = " + this.w;
	}
	
	static /* synthetic */ int[] $SWITCH_TABLE$rlib$geom$VectorType()
	{
		int[] arrn;
		int[] arrn2 = $SWITCH_TABLE$rlib$geom$VectorType;
		if (arrn2 != null)
		{
			return arrn2;
		}
		arrn = new int[VectorType.values().length];
		try
		{
			arrn[VectorType.DIRECTION.ordinal()] = 3;
		}
		catch (NoSuchFieldError noSuchFieldError)
		{
		}
		try
		{
			arrn[VectorType.LEFT.ordinal()] = 1;
		}
		catch (NoSuchFieldError noSuchFieldError)
		{
		}
		try
		{
			arrn[VectorType.UP.ordinal()] = 2;
		}
		catch (NoSuchFieldError noSuchFieldError)
		{
		}
		$SWITCH_TABLE$rlib$geom$VectorType = arrn;
		return $SWITCH_TABLE$rlib$geom$VectorType;
	}
}
