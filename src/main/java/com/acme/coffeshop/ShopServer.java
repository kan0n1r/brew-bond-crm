package com.acme.coffeshop;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.webapp.WebAppContext;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;

import java.util.Collections;

public final class ShopServer {
    private ShopServer() {
    }

    public static void main(String[] args) throws Exception {
        ShopDatabase.initialize();

        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        Server server = new Server(port);
        WebAppContext web = new WebAppContext();
        web.setContextPath("/");
        web.setWar("target/brew-bond-crm.war");
        web.setParentLoaderPriority(true);
        web.setAttribute("org.eclipse.jetty.containerInitializers",
                Collections.singletonList(new ContainerInitializer(new JettyJasperInitializer(), null)));
        web.addBean(new ServletContainerInitializersStarter(web), true);
        web.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
        server.setHandler(web);
        server.start();
        server.join();
    }
}
