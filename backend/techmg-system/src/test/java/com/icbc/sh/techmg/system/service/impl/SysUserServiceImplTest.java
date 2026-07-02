package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.icbc.sh.techmg.system.entity.SysRole;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.entity.SysUserBranch;
import com.icbc.sh.techmg.system.mapper.SysRoleMapper;
import com.icbc.sh.techmg.system.mapper.SysUserBranchMapper;
import com.icbc.sh.techmg.system.mapper.SysUserMapper;
import com.icbc.sh.techmg.system.mapper.SysUserRoleMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysUserServiceImpl 单元测试 — syncUserInfo / getByUserId
 * 使用 Spy 绕开 MyBatis-Plus TableInfo 依赖，不连真实数据库
 */
@RunWith(MockitoJUnitRunner.class)
public class SysUserServiceImplTest {

    @Mock private SysUserMapper sysUserMapper;
    @Mock private SysUserRoleMapper sysUserRoleMapper;
    @Mock private SysUserBranchMapper sysUserBranchMapper;
    @Mock private SysRoleMapper sysRoleMapper;

    @Spy
    @InjectMocks
    private SysUserServiceImpl sysUserService;

    private Map<String, Object> mockExtInfo;

    @Before
    public void setUp() {
        mockExtInfo = Map.of(
            "userId", "000000000001",
            "username", "平台管理员",
            "branchId", "12092342",
            "branchName", "上海技术部",
            "notesId", "admin@sdc.com",
            "branchIdList", List.of(Map.of("branchId", "12092342", "branchName", "上海技术部"))
        );

        // GUEST role
        SysRole guestRole = new SysRole();
        guestRole.setId(5L);
        guestRole.setRoleCode("GUEST");
        when(sysRoleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(guestRole);

        // MyBatis-Plus baseMapper
        ReflectionTestUtils.setField(sysUserService, "baseMapper", sysUserMapper);

        // Spy: 桩 saveOrUpdate 绕开 TableInfo 依赖
        doReturn(true).when(sysUserService).saveOrUpdate(any(SysUser.class));
        // Spy: 桩 save 绕开 TableInfo
        doReturn(true).when(sysUserService).save(any(SysUser.class));
    }

    @Test
    public void shouldUpdateExistingUser() {
        SysUser existingUser = new SysUser();
        existingUser.setId(1L);
        existingUser.setUserId("000000000001");
        existingUser.setUsername("旧名称");
        doReturn(existingUser).when(sysUserService).getByUserId("000000000001");

        SysUser result = sysUserService.syncUserInfo(mockExtInfo);

        assertNotNull(result);
        assertEquals("000000000001", result.getUserId());
        assertEquals("平台管理员", result.getUsername());
        assertEquals("12092342", result.getBranchId());
        assertNotNull(result.getLastLoginTime());
        // 已存在用户不应分配 GUEST 角色
        verify(sysUserRoleMapper, never()).insert(any());
    }

    @Test
    public void shouldCreateNewUserWithGuestRole() {
        doReturn(null).when(sysUserService).getByUserId("000000000001");

        SysUser result = sysUserService.syncUserInfo(mockExtInfo);

        assertNotNull(result);
        assertEquals("000000000001", result.getUserId());
        // 新用户应分配 GUEST 角色
        verify(sysUserRoleMapper).insert(any());
    }

    @Test
    public void shouldSyncBranchList() {
        SysUser existingUser = new SysUser();
        existingUser.setId(1L);
        existingUser.setUserId("000000000001");
        doReturn(existingUser).when(sysUserService).getByUserId("000000000001");

        sysUserService.syncUserInfo(mockExtInfo);

        // 删除旧 branch + 插入新的
        verify(sysUserBranchMapper).delete(any(LambdaQueryWrapper.class));
        verify(sysUserBranchMapper).insert(any(SysUserBranch.class));
    }
}
