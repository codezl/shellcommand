package com.codezl.shellcommand.controller;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressInUse {
		public static void main(String[] args){
		try {
			InetAddress ip4 = Inet4Address.getLocalHost();
			System.out.println(ip4.getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}				
	}
}

