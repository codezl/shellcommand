package com.codezl.shellcommand.controller;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
 
public class MyClass {
	public static void main(String[] args) throws Exception {
 
		Enumeration<NetworkInterface> IFaces = NetworkInterface.getNetworkInterfaces();
 
		while (IFaces.hasMoreElements()) {
			NetworkInterface fInterface = IFaces.nextElement();
			if (!fInterface.isVirtual() && !fInterface.isLoopback() && fInterface.isUp()) {
				Enumeration<InetAddress> adds = fInterface.getInetAddresses();
				while (adds.hasMoreElements()) {
					InetAddress address = adds.nextElement();
					byte[] bs = address.getAddress();
					if (bs.length == 4)
						System.out.println(address.getHostAddress());
				}
			}
		}
	}
}