package com.cw.iot.modules.app.dto;

import lombok.Data;

import java.util.List;

/**
 * @program: iot_manage
 * @author: llf9093
 * @create: 2020-08-25 13:08
 **/
@Data
public class PermissionDTO {
    private Integer id;
    private String permissionName;
    private List<PermissionDTO> childrens;
    private Integer permissionLevel;
    private Integer parentId;
}
