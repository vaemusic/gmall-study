package com.mallstudy.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mallstudy.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.mallstudy.gmall.bean.UmsMemberReceiveAddress;
import com.mallstudy.gmall.service.UmsMemberReceiveAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UmsMemberReceiveAddressServiceImpl implements UmsMemberReceiveAddressService {

    @Autowired
    private UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Override
    public List<UmsMemberReceiveAddress> getAllReceiveAddress() {
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectAll();
        return umsMemberReceiveAddresses;
    }

    @Override
    public int deleteReceiveById(String id) {
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("id", id);
        int count = umsMemberReceiveAddressMapper.deleteByExample(example);
        return count;
    }

    @Override
    public int insertReceive(UmsMemberReceiveAddress umsMemberReceiveAddress) {
        umsMemberReceiveAddress.setMemberId("1");
        umsMemberReceiveAddress.setName("大梨");
        umsMemberReceiveAddress.setPhoneNumber("18033441849");
        umsMemberReceiveAddress.setDefaultStatus(0);
        umsMemberReceiveAddress.setPostCode("474150");
        umsMemberReceiveAddress.setProvince("河南省");
        umsMemberReceiveAddress.setCity("南阳市");
        umsMemberReceiveAddress.setRegion("邓州市");
        umsMemberReceiveAddress.setDetailAddress("花洲街道");
        int count = umsMemberReceiveAddressMapper.insert(umsMemberReceiveAddress);
        return count;
    }

    @Override
    public int updateReceiveById(String id) {
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("id", id);
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setName("小黑");
        int count = umsMemberReceiveAddressMapper.updateByExampleSelective(umsMemberReceiveAddress, example);
        return count;
    }
}
