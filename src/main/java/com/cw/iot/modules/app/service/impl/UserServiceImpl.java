package com.cw.iot.modules.app.service.impl;

import cn.hutool.core.util.ObjectUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cw.iot.common.utils.HttpContextUtils;
import com.cw.iot.common.utils.MD5Utils;
import com.cw.iot.modules.app.dao.PermissionDao;
import com.cw.iot.modules.app.dao.RoleDao;
import com.cw.iot.modules.app.dao.UserDao;

import com.cw.iot.modules.app.dto.PermissionDTO;
import com.cw.iot.modules.app.entity.TPermission;
import com.cw.iot.modules.app.entity.TRole;
import com.cw.iot.modules.app.entity.TUser;
import com.cw.iot.modules.app.service.UserService;

import com.google.common.collect.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lulif
 * @program: iot_manage
 * @create 2020-08-15 15:49
 **/
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;

    @Override
    public Integer checkLogin(String userName, String password) {
        LambdaQueryWrapper wrapper = Wrappers.<TUser>lambdaQuery().gt(TUser::getRowState, 0).eq(TUser::getUserName, userName);
        TUser user = userDao.selectOne(wrapper);
        if (ObjectUtil.isNull(user)) {
            return -1;
        }
        if (MD5Utils.check(password, user.getPassword())) {
            return 1;
        }
        return 0;
    }

    @Override
    public List<TUser> getUserList() {
        return userDao.listUsers();
    }

    @Override
    public Integer saveUser(TUser user) {
        user.setPassword(MD5Utils.md5x2(user.getPassword()));
        user.setRowCreater(getCurrentUserName());
        user.setRowCreateTime(new Date());
        user.setRowModifier(getCurrentUserName());
        user.setRowModifierTime(new Date());
        user.setRowState(1);
        return userDao.insert(user);
    }

    @Override
    public Integer updateUser(TUser user) {
        user.setRowModifier(getCurrentUserName());
        user.setRowModifierTime(new Date());
        return userDao.updateById(user);
    }

    @Override
    public Integer removeUser(String userId) {
        TUser user = userDao.selectById(userId);
        if (ObjectUtil.isNotNull(user)) {
            user.setRowState(-1);
            user.setRowModifier(getCurrentUserName());
            user.setRowModifierTime(new Date());
        }
        return userDao.updateById(user);
    }

    private String getCurrentUserName() {
        String userName = (String) HttpContextUtils.getSession().getAttribute("currentUser");
        return userName;
    }

    @Override
    public List<TRole> getAllRoleList() {
        LambdaQueryWrapper wrapper = Wrappers.<TRole>lambdaQuery().ge(TRole::getRowState, 0);
        return roleDao.selectList(wrapper);
    }

    @Override
    public TUser getUserById(String id) {
        return userDao.getUserById(id);
    }

    @Override
    public Integer removeRole(String id) {
        TRole role = roleDao.selectById(id);
        if (ObjectUtils.isNotNull(role)) {
            role.setRowModifierTime(new Date());
            role.setRowState(-1);
        }
        return roleDao.updateById(role);
    }

    @Override
    public List<PermissionDTO> getPermissionList() {
        List<PermissionDTO> res = Lists.newLinkedList();
        //一级权限
        List<TPermission> firstPermission = permissionDao.selectList(Wrappers.<TPermission>lambdaQuery().isNull(TPermission::getParentId));
        List<Integer> firstPermissionIds = firstPermission.stream().map(TPermission::getId).collect(Collectors.toList());
        //二级权限
        List<TPermission> secondPermission = permissionDao.selectList(Wrappers.<TPermission>lambdaQuery().in(TPermission::getParentId, firstPermissionIds));
        List<Integer> secondPermissionIds = secondPermission.stream().map(TPermission::getId).collect(Collectors.toList());
        Map<Integer, List<TPermission>> parId2secondPerMap = secondPermission.stream().collect(Collectors.groupingBy((p) -> p.getParentId()));
        //三级权限
        List<TPermission> thirdPermission = permissionDao.selectList(Wrappers.<TPermission>lambdaQuery().in(TPermission::getParentId, secondPermissionIds));
        Map<Integer, List<TPermission>> parId2thirdPerMap = thirdPermission.stream().collect(Collectors.groupingBy((p) -> p.getParentId()));

        Multimap<Integer, PermissionDTO> parId2secondPerDTOMap = HashMultimap.create();
        Multimap<Integer, PermissionDTO> parId2thirdPerDTOMap = HashMultimap.create();

        parId2thirdPerMap.forEach((k, v) -> {
            v.forEach(p -> {
                PermissionDTO dto = new PermissionDTO();
                BeanUtils.copyProperties(p, dto);
                dto.setChildrens(null);
                parId2thirdPerDTOMap.put(k, dto);
            });
        });
        parId2secondPerMap.forEach((k, v) -> {
            v.forEach(p -> {
                PermissionDTO dto = new PermissionDTO();
                BeanUtils.copyProperties(p, dto);
                dto.setChildrens(new ArrayList<>(parId2thirdPerDTOMap.get(p.getId())));
                parId2secondPerDTOMap.put(k, dto);
            });
        });
        firstPermission.forEach(fp -> {
            PermissionDTO dto = new PermissionDTO();
            BeanUtils.copyProperties(fp, dto);
            dto.setChildrens(new ArrayList<>(parId2secondPerDTOMap.get(fp.getId())));
            res.add(dto);
        });

        return res;
    }

    @Override
    public List<TPermission> getPermissionByRole(String id) {
        return permissionDao.listPermissionByRole(id);
    }
}


