package com.mallstudy.gmall.cart;

import java.math.BigDecimal;

public class TestBigDecimal {

    public static void main(String[] args) {

        //初始化
        BigDecimal b1 = new BigDecimal(0.01f);
        BigDecimal b2 = new BigDecimal(0.01d);
        BigDecimal b3 = new BigDecimal("0.01");
        BigDecimal b4 = new BigDecimal("5");
        BigDecimal b5 = new BigDecimal("6");
        System.out.println(b1);
        System.out.println(b2);
        System.out.println(b3);

        //比较
        int i = b1.compareTo(b2);//1大于 0等于 -1小于
        System.out.println(i);

        //运算
            //加
            BigDecimal add = b1.add(b2);
            System.out.println(add);
            //减
            BigDecimal subtract = b2.subtract(b1);
            System.out.println(subtract);
            //乘
            BigDecimal multiply = b4.multiply(b5);
            System.out.println(multiply);
            //除
            BigDecimal divide = b4.divide(b5,3,BigDecimal.ROUND_HALF_DOWN);
            System.out.println(divide);
        //约束
        BigDecimal add1 = b1.add(b2);
        BigDecimal bigDecimal = add1.setScale(3, BigDecimal.ROUND_HALF_DOWN);
        System.out.println(bigDecimal);
    }
}
