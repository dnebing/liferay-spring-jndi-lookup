package com.liferay.jee.gogo;

import com.liferay.jee.jndi.PortalJndiObjectFactoryBean;
import org.osgi.service.component.annotations.Component;
import org.apache.felix.service.command.Descriptor;

import javax.naming.NamingException;

@Component(
        immediate = true,
        property = {
                "osgi.command.function=lookup",
                "osgi.command.scope=jndi"
        },
        service = JndiLookupCommand.class
)
public class JndiLookupCommand {

    @Descriptor("Get page definition JSON for a given layout by its PLID")
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
            e.printStackTrace(System.err);
        }
    }
}
