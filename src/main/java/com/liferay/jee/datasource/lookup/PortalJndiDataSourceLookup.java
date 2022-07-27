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

package com.liferay.jee.datasource.lookup;

import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.jndi.JndiTemplate;
import org.springframework.util.ClassUtils;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * JNDI-based {@link DataSourceLookup} implementation suitable for running in a Liferay environment.
 *
 * <p>For specific JNDI configuration, it is recommended to configure
 * the "jndiEnvironment"/"jndiTemplate" properties.
 *
 * @author dnebing
 */
public class PortalJndiDataSourceLookup extends JndiDataSourceLookup {
    private ThreadLocal<ClassLoader> originalClassLoaderThreadLocal = new ThreadLocal<>();

    public PortalJndiDataSourceLookup() {
        super();

        // assign our extension jndi template into the superclass
        setJndiTemplate(new PortalJndiTemplate());
    }

    public class PortalJndiTemplate extends JndiTemplate {
        @Override
        public Context getContext() throws NamingException {
            // get the shielded class loader
            ClassLoader shieldedClassLoader = PortalClassLoaderUtil.getClassLoader();

            // get the webapp class loader from it
            ClassLoader webappClassLoader = shieldedClassLoader.getClass().getClassLoader();

            // replace the current class loader with the webapp class loader.
            ClassLoader originalClassLoader = ClassUtils.overrideThreadContextClassLoader(webappClassLoader);

            originalClassLoaderThreadLocal.set(originalClassLoader);

            try {
                return super.getContext();
            } catch (NamingException | RuntimeException e) {
                if (originalClassLoader != null) {
                    // restore the class loader for the thread context
                    Thread.currentThread().setContextClassLoader(originalClassLoader);
                    originalClassLoaderThreadLocal.set(null);
                }

                throw e;
            }
        }

        @Override
        public void releaseContext(Context ctx) {
            super.releaseContext(ctx);

            ClassLoader originalClassLoader = originalClassLoaderThreadLocal.get();

            if (originalClassLoader != null) {
                // restore the class loader for the thread context
                Thread.currentThread().setContextClassLoader(originalClassLoader);
                originalClassLoaderThreadLocal.set(null);
            }
        }
    }
}
