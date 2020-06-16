package com.mallstudy.gmall.user.controller;

import com.mallstudy.gmall.bean.UmsMemberReceiveAddress;
import com.mallstudy.gmall.service.UmsMemberReceiveAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MemberReceiveAddressController {

    @Autowired
    private UmsMemberReceiveAddressService umsMemberReceiveAddressService;


    @RequestMapping("getAllReceiveAddress")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getAllReceiveAddress() {
        List<UmsMemberReceiveAddress> UmsMemberReceiveAddressReceiveAddresses = umsMemberReceiveAddressService.getAllReceiveAddress();
        return UmsMemberReceiveAddressReceiveAddresses;
    }

    @RequestMapping("deleteReceiveById")
    @ResponseBody
    public String deleteReceiveById(String id) {
        int count = umsMemberReceiveAddressService.deleteReceiveById(id);
        if (count != 0) {
            return "删除成功！";
        } else {
            return "删除失败！";
        }
    }

    @RequestMapping("insertReceive")
    @ResponseBody
    public String insertReceive(UmsMemberReceiveAddress UmsMemberReceiveAddress) {
        int count = umsMemberReceiveAddressService.insertReceive(UmsMemberReceiveAddress);
        if (count != 0) {
            return "添加收货地址成功！";
        } else {
            return "添加收货地址失败！";
        }
    }

    @RequestMapping("updateReceiveById")
    @ResponseBody
    public String updateReceiveById(String id) {
        int count = umsMemberReceiveAddressService.updateReceiveById(id);
        if (count != 0) {
            return "修改收货地址成功！";
        } else {
            return "修改收货地址失败！";
        }
    }
}
