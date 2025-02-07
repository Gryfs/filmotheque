package fr.eni.tp.filmotheque.configuration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/*").permitAll()
				.requestMatchers("/css/*").permitAll()
				.requestMatchers("/img/*").permitAll()
				.requestMatchers("/films").permitAll()
				.requestMatchers("/detail").permitAll()
				.requestMatchers("/login").permitAll()
				.requestMatchers("/logout").permitAll()
				.requestMatchers("/register").permitAll()
				.requestMatchers("/session").authenticated()
				.requestMatchers("/creer").hasRole("ADMIN")
				.requestMatchers("/avis").hasRole("MEMBRE")
				.requestMatchers(HttpMethod.GET, "/creer").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/creer").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/avis").hasRole("MEMBRE")
				.requestMatchers(HttpMethod.POST, "/avis").hasRole("MEMBRE")	
				.anyRequest().denyAll()
			)
			.httpBasic(Customizer.withDefaults())
			.formLogin((formLogin) ->
				formLogin
					.loginPage("/login")
					.defaultSuccessUrl("/session")
			)
			.logout((logout) ->
				logout
					.invalidateHttpSession(true)
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
					.logoutSuccessUrl("/")
			);



		return http.build();
	}

	// @Bean
	public UserDetailsService userDetailsService() {
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		String passwordChiffre = passwordEncoder.encode("Pa$$w0rd");

		UserDetails userDetails = User.builder().username("user").password("password").roles("USER").build();

		UserDetails stephane = User.builder().username("sgobin@campus-eni.fr").password(passwordChiffre)
				.roles("FORMATEUR", "Admin").build();

		return new InMemoryUserDetailsManager(userDetails, stephane);
	}

	@Bean
	UserDetailsManager users(DataSource dataSource) {

		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
		jdbcUserDetailsManager.setUsersByUsernameQuery(
				"select email, password, 'true' as enabled from membre where email = ?");
		jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
			    "SELECT m.email, r.ROLE FROM membre m JOIN roles r ON m.admin = r.IS_ADMIN WHERE m.email = ?"
			);

		
		return jdbcUserDetailsManager;
	}

}
