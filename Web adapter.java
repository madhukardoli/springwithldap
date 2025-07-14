import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Order(1)  // Ensures this configuration takes precedence to avoid conflicts
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
            .userDnPatterns("uid={0},ou=people,dc=example,dc=com")
            .groupSearchBase("ou=groups,dc=example,dc=com")
            .contextSource()
            .url("ldap://localhost:8389/dc=example,dc=com")
            .ldif("classpath:users.ldif")  // For testing; remove in production
            .and()
            .passwordCompare()
            .passwordAttribute("userPassword");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/dashboard/**").hasAnyRole("VIEW", "CREATE", "MANAGE")
                .antMatchers("/management/**").hasRole("MANAGE")
                .antMatchers("/resources/**", "/static/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
            .and()
            .logout()
                .permitAll()
            .and()
            .httpBasic()
            .and()
            .csrf().disable();  // Disable CSRF for testing; enable in production
    }
}
