package com.poplar.config;

import com.poplar.bean.User;
import com.poplar.sevice.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * by poplar created on 2020/2/6
 * 用于解析参数
 */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> parameterType = methodParameter.getParameterType();
        return parameterType == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        //获取request和response
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        String paramTaken = request.getParameter("taken");
        String cookieTaken = getCookieValue(request, "taken");
        if (StringUtils.isEmpty(paramTaken) && StringUtils.isEmpty(cookieTaken)) {
            return null;
        }
        String taken = StringUtils.isEmpty(paramTaken) ? cookieTaken : paramTaken;
        return userService.getTaken(response, taken);
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
