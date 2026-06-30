package com.icbc.sh.techmg.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.entity.SysUserRole;
import com.icbc.sh.techmg.system.mapper.SysUserMapper;
import com.icbc.sh.techmg.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器 — 启动时检查并创建默认管理员用户
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initAdminUser();
    }

    private void initAdminUser() {
        // 检查是否已存在 admin 用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getAuthNo, "admin");
        if (sysUserMapper.selectCount(wrapper) > 0) {
            log.debug("Admin user already exists, skipping creation.");
            return;
        }

        // 创建平台管理员用户
        SysUser admin = new SysUser();
        admin.setAuthNo("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setUsername("admin");
        admin.setRealName("平台管理员");
        admin.setStatus(1);
        sysUserMapper.insert(admin);
        log.info("Created default admin user: authNo=admin, password=admin123");

        // 分配 PLATFORM_ADMIN 角色 (role_id=1)
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(admin.getId());
        userRole.setRoleId(1L);
        sysUserRoleMapper.insert(userRole);
        log.info("Assigned PLATFORM_ADMIN role to admin user.");
    }
}
