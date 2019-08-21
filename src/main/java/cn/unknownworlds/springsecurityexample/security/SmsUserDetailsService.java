package cn.unknownworlds.springsecurityexample.security;

import cn.unknownworlds.springsecurityexample.entity.RoleModel;
import cn.unknownworlds.springsecurityexample.entity.UserModel;
import cn.unknownworlds.springsecurityexample.entity.UserRoleModel;
import cn.unknownworlds.springsecurityexample.service.RoleService;
import cn.unknownworlds.springsecurityexample.service.UserRoleService;
import cn.unknownworlds.springsecurityexample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @ClassName SmsUserDetailsService
 * @Description TODO
 * @Author Administrator
 * @Date 2019/8/21 0021 上午 9:48
 * @Version 1.0
 */
@Service("smsUserDetailsService")
public class SmsUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // 从数据库中取出用户信息
        UserModel user = userService.selectByPhone(phone);

        // 判断用户是否存在
        if(user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }

        // 添加权限
        List<UserRoleModel> userRoles = userRoleService.listByUserId(user.getId());
        for (UserRoleModel userRole : userRoles) {
            RoleModel role = roleService.selectById(userRole.getRoleId());
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        // 返回UserDetails实现类
        return new User(user.getName(), user.getPassword(), authorities);
    }
}
