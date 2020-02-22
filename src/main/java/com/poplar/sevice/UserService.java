package com.poplar.sevice;

import com.poplar.bean.User;
import com.poplar.dao.UserDao;
import com.poplar.enums.ResultEnum;
import com.poplar.exception.GlobalException;
import com.poplar.redis.RedisHelper;
import com.poplar.redis.UserIdKey;
import com.poplar.redis.UserKey;
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

    private final static String COOKIE_NAME = "token";

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
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return true;
    }

    //缓存用户对象到redis https://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
    public User getViaId(Long userId) {
        User user = redisHelper.get(UserIdKey.getByUserId, "" + userId, User.class);
        if (user != null) {
            return user;
        }
        user = userDao.getViaId(userId);
        if (user == null) {
            throw new GlobalException(ResultEnum.USER_NOT_EXIST);
        }
        redisHelper.set(UserIdKey.getByUserId, "" + userId, user);
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
        redisHelper.delete(UserIdKey.getByUserId, "" + userId);
        redisHelper.set(UserKey.token, token, user);
        return true;
    }

    private void addCookie(HttpServletResponse response, String token, User user) {
        redisHelper.set(UserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setPath("/");
        cookie.setMaxAge(UserKey.TAKEN_EXPIRE);
        response.addCookie(cookie);
    }

    public User getToken(HttpServletResponse response, String token) {

        if (StringUtils.isEmpty(token)) {
            return null;
        }
        User user = redisHelper.get(UserKey.token, token, User.class);
        if (user != null) {
            //延长用户登录时间
            addCookie(response, token, user);
        }
        return user;
    }
}
