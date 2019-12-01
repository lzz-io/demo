/*
 * Copyright (C) qq:1219331697
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.lzz.demo.rbac.shiro.config;

import java.util.Arrays;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import io.lzz.demo.rbac.shiro.entity.User;
import io.lzz.demo.rbac.shiro.service.UserService;

/**
 * 自定义realm
 * 
 * @author q1219331697
 *
 */
public class CustomRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		System.out.println("CustomRealm.doGetAuthorizationInfo() 授权");
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		// 添加授权
		// info.addStringPermission("user:add");
		// 从数据库去权限
		Subject subject = SecurityUtils.getSubject();
		User user = (User) subject.getPrincipal();
		User dbUser = userService.findById(user.getId());
		info.addStringPermissions(Arrays.asList(dbUser.getPerms().split(";")));
		System.out.println(info.getStringPermissions());
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println("CustomRealm.doGetAuthenticationInfo() 认证");
		// 模拟数据
		// String username = "username";
		// String password = "password";

		// 1、判断用户名
		// 强转token为UsernamePasswordToken
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
		// if (!Objects.equals(username, usernamePasswordToken.getUsername())) {
		// // return null,底层会自动抛出 UnknownAccountException
		// return null;
		// }
		User user = userService.findByUsername(usernamePasswordToken.getUsername());
		if (user == null) {
			// return null,底层会自动抛出 UnknownAccountException
			// 也可以 throw new UnknownAccountException();
			return null;
		}

		// 2、判断密码交个shiro完成
		return new SimpleAuthenticationInfo(user, user.getPassword(), "");
	}

}
