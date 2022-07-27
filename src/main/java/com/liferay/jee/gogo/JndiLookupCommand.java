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

package com.liferay.jee.gogo;

import com.liferay.jee.datasource.lookup.PortalJndiDataSourceLookup;
import com.liferay.jee.jndi.PortalJndiObjectFactoryBean;
import org.osgi.service.component.annotations.Component;
import org.apache.felix.service.command.Descriptor;

import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * This is a Gogo command registering class to test the jndi code in the Gogo shell.
 *
 * @author dnebing
 */
@Component(
        immediate = true,
        property = {
                "osgi.command.function=lookup",
                "osgi.command.function=datasource",
                "osgi.command.scope=jndi"
        },
        service = JndiLookupCommand.class
)
public class JndiLookupCommand {

    @Descriptor("Looks up a JNDI resource using the given name")
    public void lookup(String jndiName) {

        // create a new bean to perform the lookup
        PortalJndiObjectFactoryBean bean = new PortalJndiObjectFactoryBean();

        // set the name of the jndi thingy we want to load
        bean.setJndiName(jndiName);

        try {
            // initiate afterPropertiesSet() to perform the lookup
            bean.afterPropertiesSet();

            // object should have been retrieved, get it now
            Object jndiObject = bean.getObject();

            if (jndiObject == null) {
                System.out.println("JNDI name " + jndiName + " returns null object.");
                return;
            }

            System.out.println("JNDI name " + jndiName + " found object class " + jndiObject.getClass().getCanonicalName());
        } catch (NamingException e) {
            System.err.println("Error looking up " + jndiName + ": " + e.getMessage());
        }
    }

    @Descriptor("Looks up a JNDI datasource using the given name")
    public void datasource(String jndiName) {

        // create a new bean to perform the lookup
        PortalJndiDataSourceLookup lookup = new PortalJndiDataSourceLookup();

        try {

            // object should have been retrieved, get it now
            DataSource ds = lookup.getDataSource(jndiName);

            if (ds == null) {
                System.out.println("JNDI name " + jndiName + " returns null object.");
                return;
            }

            System.out.println("JNDI name " + jndiName + " found datasource " + ds.getClass().getCanonicalName());
        } catch (Exception e) {
            System.err.println("Error looking up " + jndiName + ": " + e.getMessage());
        }
    }
}
