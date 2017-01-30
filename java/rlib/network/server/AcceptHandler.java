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
package rlib.network.server;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import rlib.logging.Logger;
import rlib.logging.Loggers;

public abstract class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>
{
	protected static final Logger log = Loggers.getLogger(AcceptHandler.class);
	
	@Override
	public void completed(AsynchronousSocketChannel result, AsynchronousServerSocketChannel serverChannel)
	{
		serverChannel.accept(serverChannel, this);
		this.onAccept(result);
	}
	
	@Override
	public void failed(Throwable exc, AsynchronousServerSocketChannel serverChannel)
	{
		serverChannel.accept(serverChannel, this);
		this.onFailed(exc);
	}
	
	protected abstract void onAccept(AsynchronousSocketChannel var1);
	
	protected abstract void onFailed(Throwable var1);
}
