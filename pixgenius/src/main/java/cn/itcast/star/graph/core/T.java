package cn.hx.pix.genius.core;

import cn.hutool.crypto.digest.DigestUtil;

public class T {
    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            String password= "123456";
            if(i%3==1){
                password = "654321";
            }
            String temp = DigestUtil.md5Hex(password+i);
            System.out.println(i+temp);
        }
        String save = "0d8e423a9d5eb97da9e2d58cd57b92808";
        String user1 = "123456";
        String temp = DigestUtil.md5Hex(user1+save.substring(0,1));
        System.out.println("ttttt:"+temp);
    }
}
