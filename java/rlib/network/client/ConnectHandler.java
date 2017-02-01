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
package rlib.network.client;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public abstract class ConnectHandler implements CompletionHandler<Void, AsynchronousSocketChannel>
{
	@Override
	public void completed(Void result, AsynchronousSocketChannel attachment)
	{
		onConnect(attachment);
	}
	
	@Override
	public void failed(Throwable exc, AsynchronousSocketChannel attachment)
	{
		onFailed(exc);
	}
	
	protected abstract void onConnect(AsynchronousSocketChannel var1);
	
	protected abstract void onFailed(Throwable var1);
}
