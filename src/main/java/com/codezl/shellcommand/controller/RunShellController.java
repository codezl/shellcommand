package com.codezl.shellcommand.controller;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.rmi.runtime.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: code-zl
 * @Date: 2022/04/21/13:48
 * @Description:
 */
@RestController
@RequestMapping("shell")
public class RunShellController {

    @GetMapping("restartPush")
    public String runshell() {
        try {
            //Process exec = Runtime.getRuntime().exec("nohup /home/dd-driving/testServer >/dev/null 2>&1 &");
            Process exec = Runtime.getRuntime().exec("nohup /opt/midware-bak/testServer >/dev/null 2>&1 &");
            int i = exec.waitFor();
            exec.exitValue();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    @GetMapping("restartPushDd")
    public String ddPushRestart() {
        // 前面后续需要添加逻辑（连接）去判断服务是否挂了
        String[] cmds = {"dd-user","DD-User-2511582","cd /opt/midware-bak/"
                ,"nohup ./testServer >/dev/null 2>&1 &"};
        return JavaSsh.runcmd("42.194.198.47", 20022,cmds);
    }

    @GetMapping("pushState")
    public String pushState() {
        String[] cmds = {"dd-user","DD-User-2511582","lsof -i:16666"};
        return JavaSsh.runcmd( "42.194.198.47", 20022,
                cmds);
    }

    @GetMapping("checkPort")
    public String checkPort() {
        List<String> cmds = new ArrayList<>();
        cmds.add("lsof -i:16666");
        return execute("dd-user", "DD-User-2511582", "42.194.198.47", 20022,
                cmds);
    }

    public static String execute(String userName, String password, String ipAddr,int port, List<String> cmds) {
        //返回的结果
        String result = "";
        try {
            if (InetAddress.getByName(ipAddr).isReachable(1500)) {
                Connection conn = new Connection(ipAddr,port);
                conn.connect();
                boolean isAuthed = conn.authenticateWithPassword(userName, password);
                if (isAuthed) {
                    for (String cmd:cmds) {
                        //打开一个会话
                        Session session = conn.openSession();
                        session.execCommand(cmd);
                        session.getStdin();
                        result = RunShell.processStdout(session.getStdout(), cmds.get(0));
                        System.out.print("\n执行结果" + result);

                        BufferedReader br = new BufferedReader(new InputStreamReader(session.getStdout()));
                        // 这里也可以输出文本日志

                        OutputStream out = session.getStdin();
                        InputStream inputStream = session.getStdout();
                        byte[] data = new byte[inputStream.available()];
                        int nLen = inputStream.read(data);
                        if (nLen < 0) {
                            throw new Exception("network errro");
                        }
                        String temp = new String(data, 0, nLen, StandardCharsets.UTF_8);
                        System.out.print("获取：" + nLen + ">temp>>" + temp);
                        String lineStr;
                        while ((lineStr = br.readLine()) != null) {
                            result = lineStr;
                        }
                        System.out.println("\n---" + result);
                        session.close();
                    }
                    conn.close();
                } else {
                    throw new RuntimeException("执行" + cmds + "命令时失败,连接目标主机失败,用户名密码错误");
                }
                conn.close();
            } else {
                throw new RuntimeException("执行" + cmds + "命令时失败,连接目标主机失败,网络不通");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    }
