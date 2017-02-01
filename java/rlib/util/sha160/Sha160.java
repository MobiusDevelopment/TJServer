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
package rlib.util.sha160;

public class Sha160 extends BaseHash
{
	private static final int[] w = new int[80];
	private int h0;
	private int h1;
	private int h2;
	private int h3;
	private int h4;
	
	private static synchronized int[] sha(int hh0, int hh1, int hh2, int hh3, int hh4, byte[] in, int offset)
	{
		int T;
		int A = hh0;
		int B = hh1;
		int C = hh2;
		int D = hh3;
		int E = hh4;
		int r = 0;
		while (r < 16)
		{
			Sha160.w[r] = (in[offset + (r * 4)] << 24) | ((in[offset + (r * 4) + 1] & 255) << 16) | ((in[offset + (r * 4) + 2] & 255) << 8) | (in[offset + (r * 4) + 3] & 255);
			++r;
		}
		r = 16;
		while (r < 80)
		{
			Sha160.w[r] = w[r - 3] ^ w[r - 8] ^ w[r - 14] ^ w[r - 16];
			++r;
		}
		r = 0;
		while (r < 20)
		{
			T = ((A << 5) | (A >>> 27)) + ((B & C) | (~B & D)) + E + w[r] + 1518500249;
			E = D;
			D = C;
			C = (B << 30) | (B >>> 2);
			B = A;
			A = T;
			++r;
		}
		r = 20;
		while (r < 40)
		{
			T = ((A << 5) | (A >>> 27)) + (B ^ C ^ D) + E + w[r] + 1859775393;
			E = D;
			D = C;
			C = (B << 30) | (B >>> 2);
			B = A;
			A = T;
			++r;
		}
		r = 40;
		while (r < 60)
		{
			T = ((A << 5) | (A >>> 27)) + ((B & C) | (B & D) | (C & D)) + E + w[r] + -1894007588;
			E = D;
			D = C;
			C = (B << 30) | (B >>> 2);
			B = A;
			A = T;
			++r;
		}
		r = 60;
		while (r < 80)
		{
			T = ((A << 5) | (A >>> 27)) + (B ^ C ^ D) + E + w[r] + -899497514;
			E = D;
			D = C;
			C = (B << 30) | (B >>> 2);
			B = A;
			A = T;
			++r;
		}
		return new int[]
		{
			hh0 + A,
			hh1 + B,
			hh2 + C,
			hh3 + D,
			hh4 + E
		};
	}
	
	public Sha160()
	{
		super("sha-160", 20, 64);
	}
	
	private Sha160(Sha160 md)
	{
		this();
		h0 = md.h0;
		h1 = md.h1;
		h2 = md.h2;
		h3 = md.h3;
		h4 = md.h4;
		count = md.count;
		buffer = md.buffer.clone();
	}
	
	@Override
	public Object clone()
	{
		return new Sha160(this);
	}
	
	@Override
	protected byte[] getResult()
	{
		byte[] result = new byte[]
		{
			(byte) h0,
			(byte) (h0 >>> 8),
			(byte) (h0 >>> 16),
			(byte) (h0 >>> 24),
			(byte) h1,
			(byte) (h1 >>> 8),
			(byte) (h1 >>> 16),
			(byte) (h1 >>> 24),
			(byte) h2,
			(byte) (h2 >>> 8),
			(byte) (h2 >>> 16),
			(byte) (h2 >>> 24),
			(byte) h3,
			(byte) (h3 >>> 8),
			(byte) (h3 >>> 16),
			(byte) (h3 >>> 24),
			(byte) h4,
			(byte) (h4 >>> 8),
			(byte) (h4 >>> 16),
			(byte) (h4 >>> 24)
		};
		return result;
	}
	
	@Override
	protected byte[] padBuffer()
	{
		int n = (int) (count % 64);
		int padding = n >= 56 ? 120 - n : 56 - n;
		byte[] result = new byte[padding + 8];
		result[0] = -128;
		long bits = count << 3;
		result[padding++] = (byte) (bits >>> 56);
		result[padding++] = (byte) (bits >>> 48);
		result[padding++] = (byte) (bits >>> 40);
		result[padding++] = (byte) (bits >>> 32);
		result[padding++] = (byte) (bits >>> 24);
		result[padding++] = (byte) (bits >>> 16);
		result[padding++] = (byte) (bits >>> 8);
		result[padding] = (byte) bits;
		return result;
	}
	
	@Override
	protected void resetContext()
	{
		h0 = 1732584193;
		h1 = -271733879;
		h2 = -1732584194;
		h3 = 271733878;
		h4 = -1009589776;
	}
	
	@Override
	protected void transform(byte[] in, int offset)
	{
		int[] result = Sha160.sha(h0, h1, h2, h3, h4, in, offset);
		h0 = result[0];
		h1 = result[1];
		h2 = result[2];
		h3 = result[3];
		h4 = result[4];
	}
}
