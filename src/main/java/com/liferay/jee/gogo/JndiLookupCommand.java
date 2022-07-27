package com.liferay.jee.gogo;

import com.liferay.jee.datasource.lookup.PortalJndiDataSourceLookup;
import com.liferay.jee.jndi.PortalJndiObjectFactoryBean;
import org.osgi.service.component.annotations.Component;
import org.apache.felix.service.command.Descriptor;

import javax.naming.NamingException;
import javax.sql.DataSource;

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

            System.out.println("JNDI name " + jndiName + " found datasource " + jndiObject.getClass().getCanonicalName());
        } catch (NamingException e) {
            System.err.println("Error looking up " + jndiName + ": " + e.getMessage());
        }
    }
}
