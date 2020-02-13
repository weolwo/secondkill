package com.poplar.sevice;

import com.poplar.bean.User;
import com.poplar.dao.UserDao;
import com.poplar.enums.ResultEnum;
import com.poplar.exception.GlobalException;
import com.poplar.redis.RedisHelper;
import com.poplar.redis.UserIdPrefix;
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

    //缓存用户对象到redis
    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        Long mobile = Long.parseLong(loginVo.getMobile());
        String password = loginVo.getPassword();
        User user = getViaId(mobile);
        if (user == null) {
            return false;
        }
        String salt = user.getSalt();
        String passwordMd5 = MD5Util.formPassToDBPass(password, salt);
        User login = userDao.login(mobile, passwordMd5);
        if (login == null) {
            return false;
        }
        //生成分布式session
        String taken = UUIDUtil.uuid();
        addCookie(response, taken, user);
        return true;
    }

    //缓存用户对象到redis https://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
    public User getViaId(Long userId) {
        User user = redisHelper.get(UserIdPrefix.getByUserId, "" + userId, User.class);
        if (user != null) {
            return user;
        }
        user = userDao.getViaId(userId);
        if (user == null) {
            throw new GlobalException(ResultEnum.USER_NOT_EXIST);
        }
        redisHelper.set(UserIdPrefix.getByUserId, "" + userId, user);
        return user;
    }

    /**
     * 更新密码
     *
     * @param token
     * @param newPassword
     * @return
     */
    public boolean update(String token, String newPassword, Long userId) {
        User user = getViaId(userId);
        if (user == null) {
            throw new GlobalException(ResultEnum.USER_NOT_EXIST);
        }

        //只更新需要更新的字段
        User u = new User();
        u.setPassword(MD5Util.formPassToDBPass(newPassword, user.getSalt()));
        userDao.update(u);
        //删除缓存中的旧数据
        user.setPassword(u.getPassword());
        redisHelper.delete(UserIdPrefix.getByUserId, "" + userId);
        redisHelper.set(UserPrefix.taken, token, user);
        return true;
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
