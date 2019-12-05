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

package io.lzz.demo.rbac.shiro.simple;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

/**
 * @author q1219331697
 */
public class MyRealm extends AuthorizingRealm {

	public static void main(String[] args) {
		// 生成加密后的密码
		String source = "vespa";
		String salt = "vespa";
		SimpleHash simpleHash = new SimpleHash("MD5", source, salt, 1024);
		System.out.println(simpleHash);
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		System.out.println("MyRealm.doGetAuthorizationInfo() 鉴权");
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println("MyRealm.doGetAuthenticationInfo() 认证");

		// token 前台用户
		// 数据库密码
		// String password = "vespa";
		// 数据库中加密后的密码
		String hashedCredentials = "93bfa70669cc83a79d521adb0f10bdcc";
		ByteSource credentialsSalt = ByteSource.Util.bytes("vespa");
		// SimpleAuthenticationInfo info = new
		// SimpleAuthenticationInfo(token.getPrincipal(), password, "");
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(token.getPrincipal(), hashedCredentials, credentialsSalt, getName());
		System.out.println("MyRealm.doGetAuthenticationInfo() realmName: " + getName());
		return info;
	}

	@Override
	public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
		System.out.println("MyRealm.setCredentialsMatcher()");
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
		matcher.setHashAlgorithmName("MD5");
		matcher.setHashIterations(1024);
		super.setCredentialsMatcher(matcher);
	}
}
