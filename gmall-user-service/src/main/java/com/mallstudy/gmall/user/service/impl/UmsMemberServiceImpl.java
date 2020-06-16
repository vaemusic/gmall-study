package com.mallstudy.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.mallstudy.gmall.user.mapper.UmsMemberMapper;
import com.mallstudy.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.mallstudy.gmall.bean.UmsMember;
import com.mallstudy.gmall.bean.UmsMemberReceiveAddress;
import com.mallstudy.gmall.service.UmsMemberService;
import com.mallstudy.gmall.util.MD5Util;
import com.mallstudy.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    @Autowired
    private UmsMemberMapper umsMemberMapper;

    @Autowired
    private UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void addUserToken(String token, String memberId) {
        Jedis jedis = redisUtil.getJedis();
        jedis.setex("user:"+memberId+":token",60*60*2,token);
        jedis.close();
    }

    @Override
    public UmsMember login(UmsMember umsMember) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();

            if(jedis!=null){
                String umsMemberStr = jedis.get("user:" + umsMember.getPassword() + ":info");
                if(StringUtils.isNotBlank(umsMemberStr)){
                    //密码正确
                    UmsMember umsMemberCache = JSON.parseObject(umsMemberStr, UmsMember.class);
                    return umsMemberCache;
                }
            }
            //连接redis失败
            //开启数据库
            UmsMember umsMemberFromDb = loginFromDb(umsMember);
            if(umsMemberFromDb!=null){
                jedis.setex("user:" + umsMember.getPassword() + ":info",60*60*24,JSON.toJSONString(umsMemberFromDb));
            }
            return umsMemberFromDb;

        } finally {
            jedis.close();
        }
    }

    private UmsMember loginFromDb(UmsMember umsMember) {
        Example e=new Example(UmsMember.class);
        e.createCriteria().andEqualTo("username",umsMember.getUsername()).andEqualTo("password", MD5Util.md5Encrypt32Lower(umsMember.getPassword()));
        UmsMember umsMembers = umsMemberMapper.selectOneByExample(e);
        UmsMember umsMember1 =null;
        if(umsMembers!=null){
            umsMember1 = umsMembers;
        }
        return umsMember1;
    }

    @Override
    public List<UmsMember> getAllUser() {
        //List<UmsMember> umsMemberList = userMapper.selectAllUser();
        List<UmsMember> umsMemberList = umsMemberMapper.selectAll();
        return umsMemberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {

        //封装的条件参数对象,根据条件查询
        //UmsMemberReceiveAddress umsMemberReceiveAddress =new UmsMemberReceiveAddress();
        //umsMemberReceiveAddress.setMemberId(memberId);

        //List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);

        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId", memberId);

        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(example);
        return umsMemberReceiveAddresses;
    }

    @Override
    public int deleteMemberById(String memberId) {
        int count = umsMemberMapper.deleteByPrimaryKey(memberId);
        return count;
    }

    @Override
    public int insertMember(UmsMember umsMember) {
        umsMember.setMemberLevelId("4");
        umsMember.setUsername("JackChen");
        umsMember.setPassword("e10adc3949ba59abbe56e057f20f883e");
        umsMember.setNickname("JackChen");
        umsMember.setPhone("13888888888");
        umsMember.setStatus(1);
        umsMember.setCreateTime(new Date());

        int count = umsMemberMapper.insert(umsMember);
        return count;
    }

    @Override
    public int updateMemberById(String memberId) {
        UmsMember umsMember = new UmsMember();
        umsMember.setId(memberId);
        umsMember.setUsername("YeWen");
        umsMember.setNickname("YeWen");
        int count = umsMemberMapper.updateByPrimaryKeySelective(umsMember);
        return count;
    }
}
