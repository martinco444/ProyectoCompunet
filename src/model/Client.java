package model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Client implements Runnable{
	
	private DatagramSocket clientSocket; 
	@Override
	public void run() {
		
		try {
			clientSocket = new DatagramSocket();
			clientSocket.setBroadcast(true);
			
			byte[] sendData = "DISCOVER_REQUEST".getBytes();
			
			try {
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getByName("255.255.255.255"), 6666);
				clientSocket.send(sendPacket);
				System.out.println("request client packet");
	
			}catch(Exception e) {
				e.printStackTrace();
			}
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if(networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue; 
				}
				
				for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if(broadcast == null) {
						continue;
					}
					try {
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,broadcast,6666);
						clientSocket.send(sendPacket);
					}catch(Exception e) {
						e.printStackTrace();
					}
					System.out.println("request client packet" + broadcast.getHostAddress() + " interface: " + networkInterface.getDisplayName());
				}
			}
			System.out.println();
			byte[] reciveBuf = new byte[15000];
			DatagramPacket receivePacket = new DatagramPacket(reciveBuf,reciveBuf.length);
			clientSocket.receive(receivePacket);
			
			System.out.println("Broadcast response from server:" + receivePacket.getAddress().getHostAddress());
			
			String message = new String(receivePacket.getData()).trim();
			if(message.equals("DISCOVER_RESPONSE")) {
				System.out.println("server response done");
			}
			clientSocket.close();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}

}
