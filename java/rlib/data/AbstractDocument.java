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
package rlib.data;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import rlib.logging.Logger;
import rlib.logging.Loggers;

public abstract class AbstractDocument<C> implements DocumentXML<C>
{
	protected static final Logger log = Loggers.getLogger("DocumentXML");
	protected File file;
	protected Document doc;
	protected C result;
	
	public AbstractDocument(File file)
	{
		this.file = file;
	}
	
	protected abstract C create();
	
	@Override
	public C parse()
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			this.doc = factory.newDocumentBuilder().parse(this.file);
		}
		catch (IOException | ParserConfigurationException | SAXException e)
		{
			log.warning(this, "incorrect file " + this.file);
			log.warning(this, e);
		}
		this.result = this.create();
		try
		{
			this.parse(this.doc);
		}
		catch (Exception e)
		{
			log.warning(this, "incorrect file " + this.file);
			log.warning(this, e);
		}
		return this.result;
	}
	
	protected abstract void parse(Document var1);
}
