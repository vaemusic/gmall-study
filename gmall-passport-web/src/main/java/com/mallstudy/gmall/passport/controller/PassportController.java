package com.mallstudy.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.mallstudy.gmall.bean.UmsMember;
import com.mallstudy.gmall.service.UmsMemberService;
import com.mallstudy.gmall.util.JwtUtil;
import com.mallstudy.gmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    UmsMemberService umsMemberService;

    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token,HttpServletRequest request) {
        //通过jwt校验真假
        Map<String,String> map = new HashMap<>();
        String ip = request.getHeader("x-forwarded-for");//通过nginx转发的客户端ip
        if(StringUtils.isBlank(ip)){
            ip = MD5Util.md5Encrypt32Upper(request.getRemoteAddr());//从request中获取ip
            if(StringUtils.isBlank(ip)){
                ip=MD5Util.md5Encrypt32Upper("127.0.0.1");
            }
        }
        Map<String, Object> decode = JwtUtil.decode(token, "2020gmall", ip);
        if(decode!=null){
            map.put("status","success");
            map.put("memberId",(String) decode.get("memberId"));
            map.put("nickname",(String) decode.get("nickname"));
        }else {
            map.put("status","fail");
        }

        return JSON.toJSONString(map);
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request) {
        String token = "";

        //调用用户服务验证用户名和密码
        UmsMember umsMemberLogin = umsMemberService.login(umsMember);

        if(umsMemberLogin!=null){
            //登录成功
            //用jwt生成token
            String memberId = umsMemberLogin.getId();
            String nickname = umsMemberLogin.getNickname();
            Map<String,Object> userMap = new HashMap<>();
            userMap.put("memberId",memberId);
            userMap.put("nickname",nickname);

            String ip = request.getHeader("x-forwarded-for");//通过nginx转发的客户端ip
            if(StringUtils.isBlank(ip)){
                ip = MD5Util.md5Encrypt32Upper(request.getRemoteAddr());//从request中获取ip
                if(StringUtils.isBlank(ip)){
                    ip=MD5Util.md5Encrypt32Upper("127.0.0.1");
                }
            }

            //按照设计的算法对参数进行加密后，生成token
            token = JwtUtil.encode("2020gmall", userMap, ip);

            //将token存入redis一份
            umsMemberService.addUserToken(token,memberId);

        }else {
            //登录失败
            token="fail";
        }

        return token;
    }

    @RequestMapping("index")
    public String index(String ReturnUrl, ModelMap modelMap){
        if(StringUtils.isNotBlank(ReturnUrl)){
            modelMap.put("ReturnUrl",ReturnUrl);
        }
        return "index";
    }
}
