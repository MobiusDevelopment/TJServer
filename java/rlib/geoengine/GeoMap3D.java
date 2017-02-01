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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

public final class GeoMap3D implements GeoMap
{
	private final int offsetX;
	private final int offsetY;
	private final int quardSize;
	private final int quardHeight;
	private final int split;
	private GeoQuard[][][] quards;
	private int size;
	
	public GeoMap3D(GeoConfig config)
	{
		quardSize = config.getQuardSize();
		quardHeight = config.getQuardHeight();
		split = config.getSplit();
		offsetX = config.getOffsetX();
		offsetY = config.getOffsetY();
		quards = new GeoQuard[0][][];
	}
	
	@Override
	public void addQuard(GeoQuard quard)
	{
		GeoQuard[][] yQuards;
		if (quard.getX() >= quards.length)
		{
			quards = Arrays.copyOf(quards, quard.getX() + 1);
		}
		yQuards = quards[quard.getX()];
		if (yQuards == null)
		{
			yQuards = new GeoQuard[quard.getY() + 1][];
			quards[quard.getX()] = yQuards;
		}
		else if (quard.getY() >= yQuards.length)
		{
			yQuards = Arrays.copyOf(yQuards, (quard.getY() + 1) - yQuards.length);
			quards[quard.getX()] = yQuards;
		}
		GeoQuard[] zQuards = yQuards[quard.getY()];
		if (zQuards == null)
		{
			zQuards = new GeoQuard[]
			{
				quard
			};
			yQuards[quard.getY()] = zQuards;
			++size;
		}
		else
		{
			int z = (int) quard.getHeight() / quardHeight;
			int i = 0;
			int length = zQuards.length;
			while (i < length)
			{
				GeoQuard target = zQuards[i];
				int targetZ = (int) target.getHeight() / quardHeight;
				if (z == targetZ)
				{
					if (target.getHeight() > quard.getHeight())
					{
						return;
					}
					zQuards[i] = quard;
					return;
				}
				++i;
			}
			yQuards[quard.getY()] = Arrays.addToArray(zQuards, quard, GeoQuard.class);
			++size;
		}
	}
	
	@Override
	public void exportTo(File file)
	{
		try
		{
			FileOutputStream out = new FileOutputStream(file);
			try
			{
				FileChannel channel = out.getChannel();
				ByteBuffer buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
				byte split = (byte) getSplit();
				int x = 0;
				int lengthX = quards.length;
				while (x < lengthX)
				{
					GeoQuard[][] yQuards = quards[x];
					if (yQuards != null)
					{
						int y = 0;
						int lengthY = yQuards.length;
						while (y < lengthY)
						{
							GeoQuard[] zQuards = yQuards[y];
							if (zQuards != null)
							{
								int z = 0;
								int lengthZ = zQuards.length;
								while (z < lengthZ)
								{
									GeoQuard quard = zQuards[z];
									buffer.clear();
									buffer.put(split);
									buffer.putInt(quard.getX());
									buffer.putInt(quard.getY());
									buffer.putFloat(quard.getHeight());
									buffer.flip();
									channel.write(buffer);
									++z;
								}
							}
							++y;
						}
					}
					++x;
				}
			}
			finally
			{
				out.close();
			}
		}
		catch (IOException e)
		{
			Loggers.warning(this, e);
		}
	}
	
	@Override
	public Array<GeoQuard> getAllQuards(Array<GeoQuard> container)
	{
		int x = 0;
		int lengthX = quards.length;
		while (x < lengthX)
		{
			GeoQuard[][] yQuards = quards[x];
			if (yQuards != null)
			{
				int y = 0;
				int lengthY = yQuards.length;
				while (y < lengthY)
				{
					GeoQuard[] zQuards = yQuards[y];
					if (zQuards != null)
					{
						int z = 0;
						int lengthZ = zQuards.length;
						while (z < lengthZ)
						{
							container.add(zQuards[z]);
							++z;
						}
					}
					++y;
				}
			}
			++x;
		}
		return container;
	}
	
	@Override
	public GeoQuard getGeoQuard(float x, float y, float z)
	{
		int newY;
		int newX = toIndex(x) + offsetX;
		GeoQuard[] quards = getQuards(newX, newY = toIndex(y) + offsetY);
		if (quards == null)
		{
			int i = -2;
			while (i <= 2)
			{
				int j = -2;
				while (j <= 2)
				{
					if (((i != 0) || (j != 0)) && ((quards = getQuards(Math.max(newX + i, 0), Math.max(newY + j, 0))) != null))
					{
						break;
					}
					++j;
				}
				++i;
			}
		}
		if (quards == null)
		{
			return null;
		}
		if (quards.length == 1)
		{
			return quards[0];
		}
		GeoQuard target = null;
		float min = 0.0f;
		int i = 0;
		int length = quards.length;
		while (i < length)
		{
			GeoQuard quard = quards[i];
			if (target == null)
			{
				target = quard;
				min = Math.abs(quard.getHeight() - z);
			}
			else
			{
				float diff = Math.abs(quard.getHeight() - z);
				if (diff < min)
				{
					min = diff;
					target = quard;
				}
			}
			++i;
		}
		return target;
	}
	
	@Override
	public float getHeight(float x, float y, float z)
	{
		GeoQuard quard = getGeoQuard(x, y, z);
		if (quard == null)
		{
			return z;
		}
		return Math.abs(quard.getHeight() - z) > quardHeight ? z : quard.getHeight();
	}
	
	public GeoQuard[] getQuards(int x, int y)
	{
		if (quards.length <= x)
		{
			return null;
		}
		GeoQuard[][] yQuards = quards[x];
		if ((yQuards == null) || (yQuards.length <= y))
		{
			return null;
		}
		return yQuards[y];
	}
	
	private int getSplit()
	{
		return split;
	}
	
	@Override
	public GeoMap3D importTo(File file)
	{
		block18:
		{
			try
			{
				FileInputStream in = new FileInputStream(file);
				try
				{
					FileChannel channel = in.getChannel();
					if (channel.size() < 100000000)
					{
						Loggers.info(this, "start fast import " + file.getName());
						ByteBuffer buffer = ByteBuffer.allocate((int) channel.size()).order(ByteOrder.LITTLE_ENDIAN);
						byte split = (byte) getSplit();
						buffer.clear();
						channel.read(buffer);
						buffer.flip();
						while (buffer.remaining() > 12)
						{
							byte val = buffer.get();
							if (val != split)
							{
								Loggers.warning(this, "incorrect import file.");
								break block18;
							}
							GeoQuard quard = new GeoQuard(buffer.getInt(), buffer.getInt(), buffer.getFloat());
							if ((quard.getX() < 0) || (quard.getY() < 0))
							{
								Loggers.warning(this, "incorrect quard " + quard);
								continue;
							}
							addQuard(quard);
						}
						break block18;
					}
					Loggers.info(this, "start slow import " + file.getName());
					ByteBuffer buffer = ByteBuffer.allocate(13).order(ByteOrder.LITTLE_ENDIAN);
					byte split = (byte) getSplit();
					while ((channel.size() - channel.position()) > 12)
					{
						buffer.clear();
						channel.read(buffer);
						buffer.flip();
						byte val = buffer.get();
						if (val != split)
						{
							Loggers.warning(this, "incorrect import file.");
							break;
						}
						GeoQuard quard = new GeoQuard(buffer.getInt(), buffer.getInt(), buffer.getFloat());
						if ((quard.getX() < 0) || (quard.getY() < 0))
						{
							Loggers.warning(this, "incorrect quard " + quard);
							continue;
						}
						addQuard(quard);
					}
				}
				finally
				{
					in.close();
				}
			}
			catch (Exception e)
			{
				Loggers.warning(this, e);
			}
		}
		Loggers.info(this, "import ended. load " + size() + " quards.");
		return this;
	}
	
	public void remove(GeoQuard quard)
	{
		if (quards.length <= quard.getX())
		{
			return;
		}
		GeoQuard[][] yQuards = quards[quard.getX()];
		if ((yQuards == null) || (yQuards.length <= quard.getY()))
		{
			return;
		}
		GeoQuard[] quards = yQuards[quard.getY()];
		if ((quards == null) || (quards.length < 2))
		{
			yQuards[quard.getY()] = null;
		}
		else
		{
			GeoQuard[] result = new GeoQuard[quards.length - 1];
			int i = 0;
			int j = 0;
			int length = quards.length;
			while (i < length)
			{
				GeoQuard item = quards[i];
				if (item != quard)
				{
					result[j++] = item;
				}
				++i;
			}
			yQuards[quard.getY()] = result;
		}
	}
	
	@Override
	public int size()
	{
		return size;
	}
	
	private int toIndex(float coord)
	{
		return (int) coord / quardSize;
	}
	
	@Override
	public void addQuard(int x, int y, float height)
	{
		GeoQuard[][] yQuards;
		if (x >= quards.length)
		{
			quards = Arrays.copyOf(quards, x + 1);
		}
		yQuards = quards[x];
		if (yQuards == null)
		{
			yQuards = new GeoQuard[y + 1][];
			quards[x] = yQuards;
		}
		else if (y >= yQuards.length)
		{
			yQuards = Arrays.copyOf(yQuards, (y + 1) - yQuards.length);
			quards[x] = yQuards;
		}
		GeoQuard[] zQuards = yQuards[y];
		if (zQuards == null)
		{
			zQuards = new GeoQuard[]
			{
				new GeoQuard(x, y, height)
			};
			yQuards[y] = zQuards;
			++size;
		}
		else
		{
			int z = (int) height / quardHeight;
			int i = 0;
			int length = zQuards.length;
			while (i < length)
			{
				GeoQuard target = zQuards[i];
				int targetZ = (int) target.getHeight() / quardHeight;
				if (z == targetZ)
				{
					if (target.getHeight() > height)
					{
						return;
					}
					zQuards[i] = new GeoQuard(x, y, height);
					return;
				}
				++i;
			}
			yQuards[y] = Arrays.addToArray(zQuards, new GeoQuard(x, y, height), GeoQuard.class);
			++size;
		}
	}
}
