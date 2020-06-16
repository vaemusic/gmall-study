package com.mallstudy.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mallstudy.gmall.bean.PmsBaseAttrInfo;
import com.mallstudy.gmall.bean.PmsBaseAttrValue;
import com.mallstudy.gmall.bean.PmsBaseSaleAttr;
import com.mallstudy.gmall.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class AttrController {

    @Reference
    AttrService attrService;

    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        List<PmsBaseAttrInfo> baseAttrInfos = attrService.attrInfoList(catalog3Id);
        return baseAttrInfos;
    }

    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        List<PmsBaseAttrValue> pmsBaseAttrValues = attrService.getAttrValueList(attrId);
        return pmsBaseAttrValues;
    }

    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo) {
        String status = attrService.saveAttrInfo(pmsBaseAttrInfo);
        if (status.equals("success")) {
            return "success";
        } else {
            return "error";
        }
    }


    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        List<PmsBaseSaleAttr> pmsBaseSaleAttrs = attrService.baseSaleAttrList();
        return pmsBaseSaleAttrs;
    }
}
