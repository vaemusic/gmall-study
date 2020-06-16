package com.mallstudy.gmall.service;

import com.mallstudy.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UmsMemberReceiveAddressService {

    List<UmsMemberReceiveAddress> getAllReceiveAddress();

    int deleteReceiveById(String id);

    int insertReceive(UmsMemberReceiveAddress umsMemberReceiveAddress);

    int updateReceiveById(String Id);
}
