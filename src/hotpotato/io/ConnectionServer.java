/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.io;

import java.io.*;
import java.net.*;

public abstract class ConnectionServer {
	public static final int SLEEP_DELAY = 25;
	private ServerSocket server;
	private Thread listener;
	private volatile boolean isRunning;
	private final String name;
	private int count = 0;
	int port;

	public ConnectionServer(int port, String name) {
		isRunning = true;
		this.name = name;
		this.port = port;
		listener = new Thread(new SocketListener(), name);
	}

	public void start() throws IOException {
		server = new ServerSocket(port);
		listener.start();
		pause();
	}

	abstract protected void acceptConnection(Socket s) throws IOException;

	public int getPort() {
		if (server == null)
			return port;
		return server.getLocalPort();
	}

	public InetAddress getInetAddress() {
		return server.getInetAddress();
	}

	public void shutdown() throws IOException {
		isRunning = false;
		listener.interrupt();
		if (server != null)
			server.close();
		pause();
	}

	private void pause() {
		try {
			Thread.sleep(ConnectionServer.SLEEP_DELAY);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	private class SocketListener implements Runnable {
		public IOException caught = null;
		public void run() {
			try {
				runIO();
			} catch (IOException e) {
				caught = e;
				if (toRuntime(e)) {
					throw new RuntimeException(e);
				}
			}
		}
		protected void runIO() throws IOException {
			while (true) {
				String nextName = name + "[" + count++ +"]";
				Socket socket;
				socket = server.accept();
				ConnectionAcceptor acceptor = new ConnectionAcceptor(socket);
				new Thread(acceptor, nextName).start();
			}
		}
		protected boolean toRuntime(IOException e) {
			String msg = e.getMessage();
			return !(
				"Socket closed".equals(msg) || "Socket is closed".equals(msg));
		}
	}

	private class ConnectionAcceptor implements Runnable {
		private Socket socket;

		public ConnectionAcceptor(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				acceptConnection(socket);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			try {
				socket.close();
			} catch (IOException e1) { //
			}
		}
	}
}