package com.mallstudy.gmall.service;

import com.mallstudy.gmall.bean.PmsSearchParam;
import com.mallstudy.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
