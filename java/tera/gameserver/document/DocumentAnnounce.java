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
package tera.gameserver.document;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import tera.gameserver.tasks.AnnounceTask;

import rlib.logging.Loggers;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class DocumentAnnounce
{
	private final File file;
	private Document doc;
	private Array<AnnounceTask> runningAnnouncs;
	private Array<String> startAnnouncs;
	
	/**
	 * Constructor for DocumentAnnounce.
	 * @param f File
	 */
	public DocumentAnnounce(File f)
	{
		file = f;
	}
	
	/**
	 * Method getRuningAnnouncs.
	 * @return Array<AnnounceTask>
	 */
	public final Array<AnnounceTask> getRuningAnnouncs()
	{
		return runningAnnouncs;
	}
	
	/**
	 * Method getStartAnnouncs.
	 * @return Array<String>
	 */
	public final Array<String> getStartAnnouncs()
	{
		return startAnnouncs;
	}
	
	public final void parse()
	{
		try
		{
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			doc = factory.newDocumentBuilder().parse(file);
		}
		catch (SAXException | IOException | ParserConfigurationException e)
		{
			Loggers.warning(this, e);
			return;
		}
		
		parseDocument(doc);
		runningAnnouncs.trimToSize();
		startAnnouncs.trimToSize();
	}
	
	/**
	 * Method parseAnnounce.
	 * @param attrs Node
	 */
	private final void parseAnnounce(Node attrs)
	{
		final NamedNodeMap vals = attrs.getAttributes();
		final Node node = vals.getNamedItem("interval");
		
		if (node == null)
		{
			startAnnouncs.add(attrs.getFirstChild().getNodeValue());
		}
		else
		{
			runningAnnouncs.add(new AnnounceTask(attrs.getFirstChild().getNodeValue(), Integer.parseInt(node.getNodeValue())));
		}
	}
	
	/**
	 * Method parseDocument.
	 * @param doc Document
	 */
	private final void parseDocument(Document doc)
	{
		for (Node lst = doc.getFirstChild(); lst != null; lst = lst.getNextSibling())
		{
			if ("list".equals(lst.getNodeName()))
			{
				for (Node ann = lst.getFirstChild(); ann != null; ann = ann.getNextSibling())
				{
					if ("announce".equals(ann.getNodeName()))
					{
						parseAnnounce(ann);
					}
				}
			}
		}
	}
	
	public void save()
	{
		try (PrintWriter out = new PrintWriter(file))
		{
			out.println("<?xml version='1.0' encoding='utf-8'?>");
			out.println("<list>");
			
			for (String announce : startAnnouncs)
			{
				out.println("	<announce>" + announce + "</announce>");
			}
			
			for (AnnounceTask announce : runningAnnouncs)
			{
				out.println("	<announce interval=\"" + announce.getInterval() + "\" >" + announce.getText() + "</announce>");
			}
			
			out.println("</list>");
		}
		catch (IOException e)
		{
			Loggers.warning(this, e);
		}
	}
	
	/**
	 * Method setRuningAnnouncs.
	 * @param runingAnnouncs Array<AnnounceTask>
	 */
	public final void setRuningAnnouncs(Array<AnnounceTask> runingAnnouncs)
	{
		runningAnnouncs = runingAnnouncs;
	}
	
	/**
	 * Method setStartAnnouncs.
	 * @param sannouncs Array<String>
	 */
	public final void setStartAnnouncs(Array<String> sannouncs)
	{
		startAnnouncs = sannouncs;
	}
}