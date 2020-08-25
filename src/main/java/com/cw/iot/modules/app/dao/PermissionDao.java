package com.cw.iot.modules.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cw.iot.modules.app.entity.TPermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @program: iot_manage
 * @author: llf9093
 * @create: 2020-08-25 11:05
 **/
@Mapper
public interface PermissionDao extends BaseMapper<TPermission> {
    List<TPermission> listPermissionByRole(String id);
}
