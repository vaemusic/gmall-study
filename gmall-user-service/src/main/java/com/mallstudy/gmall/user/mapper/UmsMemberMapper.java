package com.mallstudy.gmall.user.mapper;


import com.mallstudy.gmall.bean.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UmsMemberMapper extends Mapper<UmsMember> {
    List<UmsMember> selectAllUser();
}
