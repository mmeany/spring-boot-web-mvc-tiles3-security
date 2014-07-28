package com.mvmlabs.springboot;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Sources of information for this:
 * 
 * http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-security
 * http://docs.spring.io/spring-security/site/docs/3.2.4.RELEASE/reference/htmlsingle/#jc
 * https://github.com/spring-projects/spring-boot/blob/master/spring-boot-samples/spring-boot-sample-web-secure/
 * http://stackoverflow.com/questions/19530768/configuring-spring-security-with-spring-boot
 * 
 * Have to look in so many places!
 * 
 * @author Mark Meany
 *
 */
@Configuration
public class ConfigurationForSecurity {

    @Bean
    public ApplicationSecurity applicationSecurity() {
        return new ApplicationSecurity();
    }

    @Bean
    public AuthenticationSecurity authenticationSecurity() {
        return new AuthenticationSecurity();
    }

    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Autowired
        private SecurityProperties security;

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring()
                .antMatchers("/static/**")
                .antMatchers(HttpMethod.GET, "/public/**")
                .antMatchers(HttpMethod.GET, "/index.html");
        }
        
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")                                      
                .anyRequest().fullyAuthenticated()
                .and()
                .formLogin().loginPage("/login").failureUrl("/login?error").permitAll()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login");
        }
    }

    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    protected static class AuthenticationSecurity extends GlobalAuthenticationConfigurerAdapter {

        @Autowired
        private UserDetailsService userDetailsService;

/*
        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                .withUser("user").password("password").roles("USER")
                .and()
                .withUser("admin").password("password").roles("USER", "ADMIN");
        }
*/
        
        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService);
        }

    }
    

    private static final GrantedAuthority ADMIN_ROLE = new GrantedAuthority() {
        private static final long serialVersionUID = 8023713081696379175L;
        private Log log = LogFactory.getLog(this.getClass());

        @Override
        public String getAuthority() {
            log.info("Returning ROLE_ADMIN");
            return "ROLE_ADMIN";
        }
    };

    private static final GrantedAuthority USER_ROLE = new GrantedAuthority() {
        private static final long serialVersionUID = 226992918792674250L;
        private Log log = LogFactory.getLog(this.getClass());

        @Override
        public String getAuthority() {
            log.info("Returning ROLE_USER");
            return "ROLE_USER";
        }
    };

    @Bean
    public UserDetailsService userDetailsService() {
        return new MyUserDetailsService();
    }
    
    protected static class MyUserDetailsService implements UserDetailsService {
        private Log log = LogFactory.getLog(this.getClass()); 

        @Override
        public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
            log.info("Getting user details for username: " + username);
            
            final UserDetails user = new UserDetails() {
                private static final long serialVersionUID = 2175883491891532704L;

                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    log.info("asked for authorities of user: " + getUsername());
                    final Collection<GrantedAuthority> ga = new HashSet<GrantedAuthority>();
                    if ("mark".equalsIgnoreCase(username)) {
                        log.info("This user is an administrator!");
                        ga.add(ADMIN_ROLE);
                    }
                    ga.add(USER_ROLE);
                    return ga;
                }

                @Override
                public String getPassword() {
                    return "password1";
                }

                @Override
                public String getUsername() {
                    return username;
                }

                @Override
                public boolean isAccountNonExpired() {
                    return true;
                }

                @Override
                public boolean isAccountNonLocked() {
                    return true;
                }

                @Override
                public boolean isCredentialsNonExpired() {
                    return true;
                }

                @Override
                public boolean isEnabled() {
                    return true;
                }
                
            };

            return user;
        }
    }

}
