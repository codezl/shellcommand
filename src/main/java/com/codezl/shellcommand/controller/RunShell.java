package com.codezl.shellcommand.controller;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class RunShell { 
  public static void main(String[] args){
    String execute = RunShell.execute("dd-driving", "DD-User-2511582", "192.168.4.52",
            22,"nohup /home/dd-driving/testServer >/dev/null 2>&1 &");
    String execute1 = RunShell.execute("dd-driving", "DD-User-2511582", "192.168.4.52",22,
            "lsof -i:16666");
    String execute2 = RunShell.execute("dd-user", "DD-User-2511582", "42.194.198.47",
            20022,"lsof -i:16666");
    System.out.print(execute+">>查看16666端口》"+execute1);
//    try {
//      String shpath="/home/felven/word2vec/demo-classes.sh";
//      Process ps = Runtime.getRuntime().exec("nohup /home/dd-driving/testServer >/dev/null 2>&1 &");
//      ps.waitFor();
//
//      BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
//      StringBuffer sb = new StringBuffer();
//      String line;
//      while ((line = br.readLine()) != null) {
//        sb.append(line).append("\n");
//      }
//      String result = sb.toString();
//      System.out.println(result);
//      }
//    catch (Exception e) {
//      e.printStackTrace();
//      }
  }

  public static String execute(String userName, String password, String ipAddr,int port, String cmd) {
    //返回的结果
    String result = "";
    try {
      if (InetAddress.getByName(ipAddr).isReachable(1500)) {
        Connection conn = new Connection(ipAddr,port);
        conn.connect();
        boolean isAuthed = conn.authenticateWithPassword(userName, password);
        if (isAuthed) {
          //打开一个会话
          Session session = conn.openSession();
          session.execCommand(cmd);
          result = processStdout(session.getStdout(), cmd);
          System.out.print("\n执行结果"+result);
          session.close();
          conn.close();
        } else {
          throw new RuntimeException("执行" + cmd + "命令时失败,连接目标主机失败,用户名密码错误");
        }
      } else {
        throw new RuntimeException("执行" + cmd + "命令时失败,连接目标主机失败,网络不通");
      }
    } catch (IOException | RuntimeException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static String processStdout(InputStream in, String cmd) {
    InputStream stdout = new StreamGobbler(in);
    StringBuilder buffer = new StringBuilder();
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(stdout, StandardCharsets.UTF_8));
      String line;
      while ((line = br.readLine()) != null) {
        buffer.append(line).append("\n");
      }
    } catch (IOException e) {
      throw new RuntimeException("执行" + cmd + "命令时失败,未知原因");
    }
    return buffer.toString();
  }

  public class TestUseSSH {
    private  String ip = "your ip";
    private  int port = 22;
    private  String user = "user";
    private  String pswd = "pswd";


    public  void main(String[] args) {
      try {

        Connection conn = new Connection(ip,port);
        conn.connect();
        boolean isAuthenticated = conn.authenticateWithPassword(user,
                pswd);
        if (isAuthenticated == false)
          throw new IOException("Authentication failed.");
        Session sess = conn.openSession();
        sess.execCommand("sh /test/shell/testForJava");
        InputStream stdout = new StreamGobbler(sess.getStdout());
        BufferedReader br = new BufferedReader(
                new InputStreamReader(stdout));
        while (true) {
          String line = br.readLine();
          if (line == null)
            break;
          System.out.println(line);
        }
        sess.close();
        conn.close();
      } catch (IOException e) {
        e.printStackTrace(System.err);
        System.exit(2);
      }
    }
  }

  public void jsShell() {}
}
