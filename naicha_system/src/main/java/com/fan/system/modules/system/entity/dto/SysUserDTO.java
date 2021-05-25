package com.fan.system.modules.system.entity.dto;

import com.fan.common.entity.system.SysUser;
import lombok.Data;

import java.util.Set;

@Data
public class SysUserDTO extends SysUser {

    private String roleName;
    private String token;
    private Set<String> permissions;
}
