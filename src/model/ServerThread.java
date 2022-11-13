package model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class ServerThread implements Runnable {
	
	//
	private DatagramSocket socket;

	@Override
	public void run() {
		try {
			//InetAdress.getByName->Determines the IP address of a host, given the host name.
			socket = new DatagramSocket(6666,InetAddress.getByName("0.0.0.0"));
			//the socket can listen all the traffic UDP
			socket.setBroadcast(true);
			
			while(true) {
				System.out.println("*** The server is ready to receiver broadcast packets ***");
				byte[] receivePacket = new byte[15000];
				DatagramPacket packet = new DatagramPacket(receivePacket,receivePacket.length);
				socket.receive(packet);
				
				
				System.out.println("*** The packet was received from "+packet.getAddress().getHostAddress());
				System.out.println("*** Information received: "+packet.getData().toString());
				
				//trim()->Delete the final trailing spaces and the starting trailing spaces
				String message = new String(packet.getData()).trim();
				
				if(message.equals("DISCOVER_REQUEST")) {
					byte[] sendData = "DISCOVER_RESPONSE".getBytes();
					
					//Send a response
					DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length);
					socket.send(sendPacket);
					
					
					System.out.println("Sent packet to: "+sendPacket.getAddress().getHostAddress());
				}
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static ServerThread getInstance() {
		return ServerThreadHolder.INSTANCE;
	}
	
	private static class ServerThreadHolder{
		private static ServerThread INSTANCE = new ServerThread();
	}

}
