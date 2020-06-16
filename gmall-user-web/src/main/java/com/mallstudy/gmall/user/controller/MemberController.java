package com.mallstudy.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mallstudy.gmall.bean.UmsMember;
import com.mallstudy.gmall.bean.UmsMemberReceiveAddress;
import com.mallstudy.gmall.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MemberController {

    @Reference
    UmsMemberService umsMemberService;

    @RequestMapping("index")
    @ResponseBody
    public String index() {
        return "hello user";
    }

    @RequestMapping("getReceiveAddressByMemberId")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberService.getReceiveAddressByMemberId(memberId);
        return umsMemberReceiveAddresses;
    }

    @RequestMapping("getAllUser")
    @ResponseBody
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMembers = umsMemberService.getAllUser();
        return umsMembers;
    }

    @RequestMapping("deleteMemberById")
    @ResponseBody
    public String deleteMemberById(String memberId) {
        int count = umsMemberService.deleteMemberById(memberId);
        if (count != 0) {
            return "删除成功！";
        } else {
            return "删除失败！";
        }
    }

    @RequestMapping("insertMember")
    @ResponseBody
    public String insertMember(UmsMember umsMember) {
        int count = umsMemberService.insertMember(umsMember);
        if (count != 0) {
            return "添加用户成功！";
        } else {
            return "添加用户失败！";
        }
    }

    @RequestMapping("updateMemberById")
    @ResponseBody
    public String updateMemberById(String memberId) {
        int count = umsMemberService.updateMemberById(memberId);
        if (count != 0) {
            return "修改用户成功！";
        } else {
            return "修改用户失败！";
        }
    }
}
