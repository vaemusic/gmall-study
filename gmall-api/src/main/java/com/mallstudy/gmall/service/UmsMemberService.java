package com.mallstudy.gmall.service;

import com.mallstudy.gmall.bean.UmsMember;
import com.mallstudy.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UmsMemberService {
    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);

    int deleteMemberById(String memberId);

    int insertMember(UmsMember umsMember);

    int updateMemberById(String memberId);

    UmsMember login(UmsMember umsMember);

    void addUserToken(String token, String memberId);
}
