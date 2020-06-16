package com.mallstudy.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mallstudy.gmall.bean.PmsBaseAttrInfo;
import com.mallstudy.gmall.bean.PmsBaseAttrValue;
import com.mallstudy.gmall.bean.PmsBaseSaleAttr;
import com.mallstudy.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.mallstudy.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.mallstudy.gmall.manage.mapper.PmsBaseSaleAttrMapper;
import com.mallstudy.gmall.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Autowired
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Override
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);
        for (PmsBaseAttrInfo baseAttrInfo : pmsBaseAttrInfos) {
            List<PmsBaseAttrValue> pmsBaseAttrValues = new ArrayList<>();
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(baseAttrInfo.getId());
            pmsBaseAttrValues = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
            baseAttrInfo.setAttrValueList(pmsBaseAttrValues);
        }
        return pmsBaseAttrInfos;
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        return pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
    }

    @Override
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        String infoId = pmsBaseAttrInfo.getId();
        if (StringUtils.isBlank(infoId)) {
            //保存操作
            //保存属性
            //insert:是不是null值都插入数据库
            //insertSelective:是否将null插入数据库
            pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);
            //保存属性值
            List<PmsBaseAttrValue> attrValues = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValues) {
                pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
            }
        } else {
            //id不为空,执行修改操作

            //修改属性
            Example example = new Example(PmsBaseAttrInfo.class);
            example.createCriteria().andEqualTo("id", pmsBaseAttrInfo.getId());
            pmsBaseAttrInfoMapper.updateByExampleSelective(pmsBaseAttrInfo, example);

            //修改属性值
            List<PmsBaseAttrValue> attrValues = pmsBaseAttrInfo.getAttrValueList();
            //按照属性id删除所有属性值
            PmsBaseAttrValue pmsBaseAttrValueDel = new PmsBaseAttrValue();
            pmsBaseAttrValueDel.setAttrId(pmsBaseAttrInfo.getId());
            pmsBaseAttrValueMapper.delete(pmsBaseAttrValueDel);

            //删除后将新的属性值插入
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValues) {
                pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
            }
        }

        return "success";
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        return pmsBaseSaleAttrMapper.selectAll();
    }

    @Override
    public List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> valueIdSet) {
        String valueIdStr = StringUtils.join(valueIdSet, ",");//41,45,46
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectAttrValueListByValueId(valueIdStr);
        return pmsBaseAttrInfos;
    }
}
