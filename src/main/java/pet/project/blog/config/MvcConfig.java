package pet.project.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    public void addViewControllers(ViewControllerRegistry registry) {

        // Register and Login + Main page
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/login").setViewName("login");

        // Profile
        registry.addViewController("/index/profile/**").setViewName("profile");
        registry.addViewController("/index/other_profile/**").setViewName("profile");

        // Publication
        registry.addViewController("/index/publication").setViewName("publication");
        registry.addViewController("/index/publication_edit").setViewName("publication_edit");

        // Admin
        registry.addViewController("/index/admin/**").setViewName("admin");
        registry.addViewController("/index/admin/users").setViewName("users");
    }

}