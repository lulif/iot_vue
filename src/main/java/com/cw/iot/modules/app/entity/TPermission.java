package com.cw.iot.modules.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @program: iot_manage
 * @author: llf9093
 * @create: 2020-08-25 10:59
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("T_PERMISSION")
public class TPermission {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String permissionName;
    private Date rowModifierTime;
    private Integer parentId;
    private Date rowCreateTime;
    private String remark;
    private Integer permissionLevel;
}
