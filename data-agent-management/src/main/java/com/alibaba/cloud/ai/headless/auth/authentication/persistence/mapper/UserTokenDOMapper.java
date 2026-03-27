package com.alibaba.cloud.ai.headless.auth.authentication.persistence.mapper;


import com.alibaba.cloud.ai.headless.auth.authentication.persistence.dataobject.UserTokenDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserTokenDOMapper extends BaseMapper<UserTokenDO> {

}
