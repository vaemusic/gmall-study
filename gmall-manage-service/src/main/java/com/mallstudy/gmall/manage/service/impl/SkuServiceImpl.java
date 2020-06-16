package com.mallstudy.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.mallstudy.gmall.bean.PmsSkuAttrValue;
import com.mallstudy.gmall.bean.PmsSkuImage;
import com.mallstudy.gmall.bean.PmsSkuInfo;
import com.mallstudy.gmall.bean.PmsSkuSaleAttrValue;
import com.mallstudy.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.mallstudy.gmall.manage.mapper.PmsSkuImageMapper;
import com.mallstudy.gmall.manage.mapper.PmsSkuInfoMapper;
import com.mallstudy.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.mallstudy.gmall.service.SkuService;
import com.mallstudy.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        //插入skuInfo
        int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();
        //插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }
        //插入销售属性关联
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }
        //插入图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
    }

    public PmsSkuInfo getSkuByIdFromDb(String skuId){
        PmsSkuInfo pmsSkuInfo=new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        PmsSkuImage pmsSkuImage=new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);

        return skuInfo;
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId,String ip) {
        System.out.println("ip为"+ip+"的用户访问了商品详情的请求,线程号："+Thread.currentThread().getName());
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();

        //连接缓存
        Jedis jedis = redisUtil.getJedis();

        //查询缓存
        String skuKey = "sku:"+skuId+":info";
        String skuJson = jedis.get(skuKey);
        if(StringUtils.isNotBlank(skuJson)) {
            System.out.println("ip为"+ip+"的用户从缓存中获取了商品详情,线程号："+Thread.currentThread().getName());
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);

        }else {
            //如果缓存中没有，查询mysql
            System.out.println("ip为"+ip+"的用户发现缓存中没有,申请缓存的分布式锁查询mysql,锁ID="+"sku:" + skuId + ":lock"+"线程号："+Thread.currentThread().getName());
            //设置分布式锁
            String token = UUID.randomUUID().toString();
            //拿到锁的线程有10秒的过期时间
            String OK = jedis.set("sku:" + skuId + ":lock", token, "nx", "px", 10*1000);
            if(StringUtils.isNotBlank(OK) && OK.equals("OK")){
                //设置成功，有权在10秒的过期时间内访问数据库
                System.out.println("ip为"+ip+"的用户成功拿到锁，有权在10秒的过期时间内访问数据库,锁ID="+"sku:" + skuId + ":lock"+"线程号："+Thread.currentThread().getName());

                pmsSkuInfo = getSkuByIdFromDb(skuId);
                if(pmsSkuInfo!=null){
                    //mysql查询结果存入redis
                    jedis.set("sku:"+skuId+":info",JSON.toJSONString(pmsSkuInfo));
                }else{
                    //数据库中不存在该sku
                    //为了防止缓存穿透，将null值或空字符串设置给redis，过期时间为3分钟
                    jedis.setex("sku:"+skuId+":info",60*3,JSON.toJSONString(""));
                }
                //在访问mysql后，将mysql的分布锁释放
                System.out.println("ip为"+ip+"的用户用完锁,将锁归还,线程号："+Thread.currentThread().getName());
                /*String lockToken = jedis.get("sku:" + skuId + ":lock");
                if(StringUtils.isNotBlank(lockToken) && lockToken.equals(token)){
                    jedis.del("sku:" + skuId + ":lock");//用token确认删除的是自己的sku的锁
                }*/
                //可以用lua脚本，在查询到key的同时删除该key，防止高并发下意外的发生
                String script="if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                jedis.eval(script, Collections.singletonList("sku:" + skuId + ":lock"),Collections.singletonList(token));
            }else {
                //设置失败，自旋（该线程在睡眠几秒后，重新尝试访问本方法）
                System.out.println("ip为"+ip+"的用户没有拿到锁,开始自旋,线程号："+Thread.currentThread().getName());


                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //加上return,保证是同一条线程上，不加return的话，会另外开辟一条孤儿线程执行
                return getSkuById(skuId,ip);
            }
        }


        jedis.close();

        return pmsSkuInfo;
    }


    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfoList;
    }

    @Override
    public List<PmsSkuInfo> getAllSku(String catalog3Id) {
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
            String skuId = pmsSkuInfo.getId();

            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> select = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(select);
        }
        return pmsSkuInfoList;
    }
}

