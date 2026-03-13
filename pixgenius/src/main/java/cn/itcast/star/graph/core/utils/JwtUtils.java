package cn.hx.pix.genius.core.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.jwt.JWT;
import cn.hx.pix.genius.core.pojo.User;

public class JwtUtils {

    static final String key = "itheima.com";

    /**
     * 生成一个token
     * @param user
     * @return
     */
    public static String genToekn(User user){
        return JWT.create()
                .setPayload("uid",user.getId())
                .setPayload("uname",user.getUsername())
                .setExpiresAt(DateTime.now().offsetNew(DateField.YEAR,1))
                .setKey(key.getBytes())
                .sign();
    }

//    /**
//     * 验证一个token是否有效
//     * @param token
//     * @return
//     */
//    public static boolean verifyToekn(String token){
//        return JWT.of(token)
//                .setKey(key.getBytes())
//                .verify();
//    }

    /**
     * 验证一个token是否有效
     * @param token
     * @return
     */
    public static User getToekn(String token){
        try {
            JWT jwt = JWT.of(token).setKey(key.getBytes());
            User user = new User();
            user.setId(Long.valueOf(jwt.getPayload("uid") + ""));
            user.setUsername(jwt.getPayload("uname") + "");
            return user;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
