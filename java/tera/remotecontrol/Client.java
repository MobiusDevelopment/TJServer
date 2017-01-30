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
package tera.remotecontrol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import tera.gameserver.ServerThread;

import rlib.logging.GameLogger;
import rlib.logging.GameLoggers;
import rlib.logging.Logger;
import rlib.logging.Loggers;

/**
 * @author Ronn
 * @created 26.03.2012
 */
public class Client extends ServerThread
{
	private static final Logger log = Loggers.getLogger("RemoteClient");
	private static final GameLogger gamelog = GameLoggers.getLogger("RemoteClient");
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	
	/**
	 * Constructor for Client.
	 * @param socket Socket
	 * @throws IOException
	 */
	public Client(Socket socket) throws IOException
	{
		setName("RemoteClient");
		setSocket(socket);
		setOutput(new ObjectOutputStream(socket.getOutputStream()));
		setInput(new ObjectInputStream(socket.getInputStream()));
		log.info("accept new client " + socket.getInetAddress().getHostAddress());
	}
	
	/**
	 * Method getAnswer.
	 * @param packet Packet
	 * @return Packet
	 */
	public Packet getAnswer(Packet packet)
	{
		gamelog.write("read packet " + packet);
		return HandlerManager.getHandler(packet.getType()).processing(packet);
	}
	
	/**
	 * Method getInput.
	 * @return ObjectInputStream
	 */
	protected final ObjectInputStream getInput()
	{
		return input;
	}
	
	/**
	 * Method getOutput.
	 * @return ObjectOutputStream
	 */
	protected final ObjectOutputStream getOutput()
	{
		return output;
	}
	
	/**
	 * Method getSocket.
	 * @return Socket
	 */
	protected final Socket getSocket()
	{
		return socket;
	}
	
	@Override
	public void interrupt()
	{
		try
		{
			socket.close();
		}
		catch (Exception e)
		{
			log.warning(e);
		}
		
		super.interrupt();
	}
	
	/**
	 * Method isClosed.
	 * @return boolean
	 */
	public boolean isClosed()
	{
		try
		{
			return socket.isClosed();
		}
		catch (Exception e)
		{
			return true;
		}
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		try
		{
			while (socket.isConnected())
			{
				final Packet packet = (Packet) input.readObject();
				final Packet answer = getAnswer(packet);
				output.writeObject(answer);
			}
		}
		catch (ClassNotFoundException | IOException ex)
		{
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
				log.warning(e);
			}
		}
	}
	
	/**
	 * Method setInput.
	 * @param input ObjectInputStream
	 */
	protected final void setInput(ObjectInputStream input)
	{
		this.input = input;
	}
	
	/**
	 * Method setOutput.
	 * @param output ObjectOutputStream
	 */
	protected final void setOutput(ObjectOutputStream output)
	{
		this.output = output;
	}
	
	/**
	 * Method setSocket.
	 * @param socket Socket
	 */
	protected final void setSocket(Socket socket)
	{
		this.socket = socket;
	}
}