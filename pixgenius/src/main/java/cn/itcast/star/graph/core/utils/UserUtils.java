package cn.hx.pix.genius.core.utils;

import cn.hx.pix.genius.core.pojo.User;

public class UserUtils {

    static ThreadLocal<User> local = new ThreadLocal<>();

    public static void saveUser(User user) {
        local.set(user);
    }

    public static User getUser() {
        return local.get();
    }

    public static void removeUser() {
        local.remove();
    }
}
