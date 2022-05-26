package com.codezl.shellcommand.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: code-zl
 * @Date: 2022/04/23/10:13
 * @Description:
 */
public class TestShell {

    public static void main(String[] args) {
        try {
            String[] cmd = new String[]{"/bin/sh", "-c", "ls"};
            Process ps = Runtime.getRuntime().exec(cmd);

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String result = sb.toString();

            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
