package com.icbc.sh.techmg.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.entity.SysUserRole;
import com.icbc.sh.techmg.system.mapper.SysUserMapper;
import com.icbc.sh.techmg.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
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

    @Override
    public void run(String... args) {
        initAdminUser();
    }

    private void initAdminUser() {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserId, "000000000001");
        if (sysUserMapper.selectCount(wrapper) > 0) {
            log.debug("Admin user already exists, skipping.");
            return;
        }

        SysUser admin = new SysUser();
        admin.setUserId("000000000001");
        admin.setUsername("平台管理员");
        admin.setStatus(1);
        sysUserMapper.insert(admin);
        log.info("Created default admin user: userId=000000000001");

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(admin.getId());
        userRole.setRoleId(1L);
        sysUserRoleMapper.insert(userRole);
        log.info("Assigned PLATFORM_ADMIN role to admin user.");
    }
}
