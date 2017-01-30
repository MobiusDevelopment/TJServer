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
package rlib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.Enumeration;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.Table;
import rlib.util.table.Tables;

public class Files
{
	private static final Logger log = Loggers.getLogger("Files");
	private static final Table<String, String> cache = Tables.newObjectTable();
	private static final Table<String, File> cacheFiles = Tables.newObjectTable();
	
	public static void clean()
	{
		cache.clear();
	}
	
	public static boolean containsFormat(String[] formats, File file)
	{
		return Files.containsFormat(formats, file.getName());
	}
	
	public static boolean containsFormat(String[] formats, String path)
	{
		int i = 0;
		int length = formats.length;
		while (i < length)
		{
			if (path.endsWith(formats[i]))
			{
				return true;
			}
			++i;
		}
		return false;
	}
	
	public static boolean copyFile(String pathSource, String pathDest)
	{
		FileInputStream instream = null;
		FileOutputStream outstream = null;
		
		try
		{
			File infile = new File(pathSource);
			File outfile = new File(pathDest);
			
			instream = new FileInputStream(infile);
			outstream = new FileOutputStream(outfile);
			
			byte[] buffer = new byte[1024];
			
			int length;
			/*
			 * copying the contents from input stream to output stream using read and write methods
			 */
			while ((length = instream.read(buffer)) > 0)
			{
				outstream.write(buffer, 0, length);
			}
			
			// Closing the input/output file streams
			instream.close();
			outstream.close();
			
			return true;
			
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		return false;
	}
	
	public static File[] getFiles(File dir)
	{
		return Files.getFiles(dir, Strings.EMPTY_ARRAY);
	}
	
	public static /* varargs */ File[] getFiles(File dir, String... formats)
	{
		Array<File> array = Arrays.toArray(File.class);
		File[] files = dir.listFiles();
		int i = 0;
		int length = files.length;
		while (i < length)
		{
			File file = files[i];
			if (file.isDirectory())
			{
				array.addAll(Files.getFiles(file, formats));
			}
			else if ((formats == Strings.EMPTY_ARRAY) || Files.containsFormat(formats, file))
			{
				array.add(file);
			}
			++i;
		}
		array.trimToSize();
		return array.array();
	}
	
	public static File[] getFiles(Package pckg)
	{
		return Files.getFiles(pckg, Strings.EMPTY_ARRAY);
	}
	
	public static /* varargs */ File[] getFiles(Package pckg, String... formats)
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> urls = null;
		try
		{
			urls = classLoader.getResources(pckg.getName().replace('.', '/'));
		}
		catch (IOException e)
		{
			Loggers.warning(Files.class, e);
		}
		if (urls == null)
		{
			return new File[0];
		}
		Array<File> files = Arrays.toArray(File.class);
		while (urls.hasMoreElements())
		{
			File file;
			URL next = urls.nextElement();
			String path = next.getFile();
			if (path.contains("%20"))
			{
				path = path.replace("%20", " ");
			}
			if ((file = new File(path)).isDirectory())
			{
				files.addAll(Files.getFiles(file, formats));
				continue;
			}
			if ((formats != Strings.EMPTY_ARRAY) && !Files.containsFormat(formats, path))
			{
				continue;
			}
			files.add(file);
		}
		files.trimToSize();
		return files.array();
	}
	
	public static long lastModified(String name)
	{
		if (name == null)
		{
			return 0;
		}
		File file = cacheFiles.get(name);
		if (file == null)
		{
			file = new File("./" + name);
			cacheFiles.put(name, file);
		}
		return file.lastModified();
	}
	
	public static String read(String path)
	{
		StringBuilder content;
		if (path == null)
		{
			return null;
		}
		if (cache.containsKey(path))
		{
			return cache.get(path);
		}
		File file = cacheFiles.get(path);
		if (file == null)
		{
			file = new File(path);
			cacheFiles.put(path, file);
		}
		if (!file.exists())
		{
			return null;
		}
		content = new StringBuilder();
		try
		{
			FileReader in = new FileReader(file);
			try
			{
				CharBuffer buffer = CharBuffer.allocate(512);
				while (in.ready())
				{
					buffer.clear();
					in.read(buffer);
					buffer.flip();
					content.append(buffer.array(), 0, buffer.limit());
				}
			}
			finally
			{
				if (in != null)
				{
					in.close();
				}
			}
		}
		catch (IOException e)
		{
			log.warning(e);
		}
		cache.put(path, content.toString());
		return content.toString();
	}
}
