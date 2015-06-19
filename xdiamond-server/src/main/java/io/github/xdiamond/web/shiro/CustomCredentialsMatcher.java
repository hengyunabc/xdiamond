package io.github.xdiamond.web.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

public class CustomCredentialsMatcher  extends HashedCredentialsMatcher 
{

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info)
	{
		boolean result=super.doCredentialsMatch(token, info);
		return result;
	}

}