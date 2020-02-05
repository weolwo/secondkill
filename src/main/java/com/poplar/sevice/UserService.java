package com.poplar.sevice;

import com.poplar.bean.User;
import com.poplar.dao.UserDao;
import com.poplar.util.MD5Util;
import com.poplar.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * by poplar created on 2020/2/3
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public boolean login(LoginVo loginVo) {
        Long mobile = Long.parseLong(loginVo.getMobile());
        String password = loginVo.getPassword();
        User user = userDao.getViaId(mobile);
        if (user ==null){
            return false;
        }
        String salt = user.getSalt();
        String passwordMd5 = MD5Util.formPassToDBPass(password, salt);
        User login = userDao.login(mobile, password);
        if (login == null){
            return false;
        }
        return true;
    }
}
