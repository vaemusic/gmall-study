package com.mallstudy.gmall.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestJwt {
    public static void main(String[] args) {
        Map<String,Object> map=new HashMap<>();
        map.put("memberId","1");
        map.put("nickname","zhangsan");
        String ip="192.168.56.101";
        String time=new  SimpleDateFormat("yyyyMMdd HHmm").format(new Date());
        String encode = JwtUtil.encode("2020gmallstudy", map, ip + time);
        System.out.println(encode);
    }
}
