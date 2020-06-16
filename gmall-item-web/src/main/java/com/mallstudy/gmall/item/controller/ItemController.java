package com.mallstudy.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.mallstudy.gmall.bean.PmsProductSaleAttr;
import com.mallstudy.gmall.bean.PmsSkuInfo;
import com.mallstudy.gmall.bean.PmsSkuSaleAttrValue;
import com.mallstudy.gmall.service.SkuService;
import com.mallstudy.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@CrossOrigin
public class ItemController {

    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;

    @RequestMapping("{skuId}.html")
    public ModelAndView item(@PathVariable String skuId, ModelMap modelMap, HttpSession session, HttpServletRequest request){

        String ip="";
        if(request.getHeader("x-forwarded-for")==null){
            ip=request.getRemoteAddr();
        }else {
            ip=request.getHeader("x-forwarded-for");
        }


        ModelAndView mv=new ModelAndView("item");
        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId,ip);
        session.setAttribute("skuId",skuId);
        //sku对象
        modelMap.put("skuInfo",pmsSkuInfo);
        //销售属性列表
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),skuId);
        modelMap.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);

        //查询当前sku的spu的其他sku的集合的hash表
        HashMap<String, String> skuSaleAttrHash = new HashMap<>();
        List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String k = "";
            String v = skuInfo.getId();

            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();

            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                k += pmsSkuSaleAttrValue.getSaleAttrValueId()+"|";
            }
            skuSaleAttrHash.put(k,v);
        }
        //将sku的销售属性hash表放到页面
        String skuSaleAttrHashJsonStr = JSON.toJSONString(skuSaleAttrHash);
        modelMap.put("skuSaleAttrHashJsonStr",skuSaleAttrHashJsonStr);
        return mv;
    }

    @RequestMapping("index")
    public String index(ModelMap modelMap){
        List<String> list=new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("循环数据"+i);
        }
        modelMap.put("list",list);
        modelMap.put("hello","hello hahahahah!!");
        modelMap.put("check","1");

        return "index";
    }
}
