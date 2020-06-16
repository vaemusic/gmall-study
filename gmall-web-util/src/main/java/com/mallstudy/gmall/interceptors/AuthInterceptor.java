package com.mallstudy.gmall.interceptors;

import com.alibaba.fastjson.JSON;
import com.mallstudy.gmall.annotations.LoginRequired;
import com.mallstudy.gmall.util.CookieUtil;
import com.mallstudy.gmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //拦截代码

        //判断被拦截的请求的访问的方法的注解（是否是需要拦截的）
        HandlerMethod hm = (HandlerMethod) handler;
        LoginRequired methodAnnotation = hm.getMethodAnnotation(LoginRequired.class);
        if (methodAnnotation == null) {
            return true;
        }

        boolean loginSuccess = methodAnnotation.loginSuccess();

        String token = "";
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        if (StringUtils.isNotBlank(oldToken)) {
            token = oldToken;
        }
        String newToken = request.getParameter("token");
        if (StringUtils.isNotBlank(newToken)) {
            token = newToken;
        }

        //调用认证中心进行验证
        String success = "fail";
        Map<String,String> successMap=new HashMap<>();
        if(StringUtils.isNotBlank(token)){
            String successJson = HttpclientUtil.doGet("http://passport.gmall.com/verify?token=" + token);

            successMap = JSON.parseObject(successJson, Map.class);

            success = successMap.get("status");
        }

        if (loginSuccess) {
            //必须登录成功才能使用
            if (!success.equals("success")) {
                //重定向回passport登录
                response.sendRedirect("http://passport.gmall.com/index?ReturnUrl=" + request.getRequestURL());
                return false;
            }


            //需要将token携带的用户信息写入
            request.setAttribute("memberId", successMap.get("memberId"));
            request.setAttribute("nickName", successMap.get("nickName"));
            //验证通过，覆盖cookie中的token
            if(StringUtils.isNotBlank(token)){
                CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);

            }

        } else {
            //没有登录也能使用，但是必须验证
            if (success.equals("success")) {
                //需要将token携带的用户信息写入
                request.setAttribute("memberId", successMap.get("memberId"));
                request.setAttribute("nickName", successMap.get("nickName"));
                //验证通过，覆盖cookie中的token
                if(StringUtils.isNotBlank(token)){
                    CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);

                }
            }
        }


        return true;
    }
}
