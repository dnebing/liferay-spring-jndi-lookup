/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.jee.jndi;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.naming.Context;
import javax.naming.NamingException;

import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.jndi.*;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Okay, this is an extension of Spring's JndiObjectFactoryBean. The only thing we need to do is
 * override the JndiTemplate class that our base class is using in order to change the class loader
 * during the actual jndi lookup.
 *
 * @author dnebing
 */
public class PortalJndiObjectFactoryBean extends JndiObjectFactoryBean
		implements FactoryBean<Object>, BeanFactoryAware, BeanClassLoaderAware, InitializingBean {

	@Override
	public void afterPropertiesSet() throws IllegalArgumentException, NamingException {
		// get the shielded class loader
		ClassLoader shieldedClassLoader = PortalClassLoaderUtil.getClassLoader();

		// get the webapp class loader from it
		ClassLoader webappClassLoader = shieldedClassLoader.getClass().getClassLoader();

		super.setBeanClassLoader(webappClassLoader);

		// let the superclass do its thing, it's ready to start looking things up...
		super.afterPropertiesSet();
	}
}
