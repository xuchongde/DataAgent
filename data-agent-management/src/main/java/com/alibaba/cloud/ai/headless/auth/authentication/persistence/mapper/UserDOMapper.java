package com.alibaba.cloud.ai.headless.auth.authentication.persistence.mapper;

import com.alibaba.cloud.ai.headless.auth.authentication.persistence.dataobject.UserDO;
import com.alibaba.cloud.ai.headless.auth.authentication.persistence.dataobject.UserDOExample;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDOMapper extends BaseMapper<UserDO> {

    List<UserDO> selectByExample(UserDOExample example);

    void updateByPrimaryKey(UserDO userDO);
}
