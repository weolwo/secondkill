package com.poplar.sevice;

import com.poplar.bean.User;
import com.poplar.dao.UserDao;
import com.poplar.redis.RedisHelper;
import com.poplar.redis.UserPrefix;
import com.poplar.util.MD5Util;
import com.poplar.util.UUIDUtil;
import com.poplar.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * by poplar created on 2020/2/3
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    private final static String COOKIE_NAME = "taken";

    @Autowired
    private RedisHelper redisHelper;

    public String login(HttpServletResponse response, LoginVo loginVo) {
        Long mobile = Long.parseLong(loginVo.getMobile());
        String password = loginVo.getPassword();
        User user = userDao.getViaId(mobile);
       /* if (user == null) {
            return false;
        }*/
        String salt = user.getSalt();
        String passwordMd5 = MD5Util.formPassToDBPass(password, salt);
        User login = userDao.login(mobile, passwordMd5);
        /*if (login == null) {
            return false;
        }*/
        //生成分布式session
        String taken = UUIDUtil.uuid();
        addCookie(response, taken, user);
        return taken;
    }

    private void addCookie(HttpServletResponse response, String taken, User user) {
        redisHelper.set(UserPrefix.taken, taken, user);
        Cookie cookie = new Cookie(COOKIE_NAME, taken);
        cookie.setPath("/");
        cookie.setMaxAge(UserPrefix.TAKEN_EXPIRE);
        response.addCookie(cookie);
    }

    public User getTaken(HttpServletResponse response, String taken) {

        if (StringUtils.isEmpty(taken)) {
            return null;
        }
        User user = redisHelper.get(UserPrefix.taken, taken, User.class);
        if (user != null) {
            //延长用户登录时间
            addCookie(response, taken, user);
        }
        return user;
    }
}
