package ro.home;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import ro.home.config.AppConfig;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

class Bootstrap implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container)  {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);
        container.addListener(new ContextLoaderListener(context));

        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ServletRegistration.Dynamic restDispatcher = container.addServlet("restDispatcherServlet", dispatcherServlet);
        restDispatcher.setLoadOnStartup(1);
        restDispatcher.addMapping("/*");
        restDispatcher.setAsyncSupported(true);
    }
}