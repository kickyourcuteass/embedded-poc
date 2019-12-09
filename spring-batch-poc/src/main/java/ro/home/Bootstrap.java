package ro.home;

import org.h2.server.web.WebServlet;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import ro.home.config.WebConfig;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

class Bootstrap implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(WebConfig.class);
        container.addListener(new ContextLoaderListener(context));

        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ServletRegistration.Dynamic restDispatcher = container.addServlet("restDispatcherServlet", dispatcherServlet);
        restDispatcher.setLoadOnStartup(1);
        restDispatcher.addMapping("/rest/*");
        restDispatcher.setAsyncSupported(true);

        WebServlet h2Servlet = new WebServlet();
        ServletRegistration.Dynamic h2Dispatcher = container.addServlet("h2Servlet", h2Servlet);
        h2Dispatcher.setLoadOnStartup(2);
        h2Dispatcher.setInitParameter("webAllowOthers", "true");
        h2Dispatcher.addMapping("/h2console/*");
    }
}