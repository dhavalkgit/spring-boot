/*
 * Copyright 2012-present the original author or authors.
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

package smoketest.security.method;

import jakarta.servlet.DispatcherType;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.security.autoconfigure.actuate.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;

@SpringBootApplication
@EnableMethodSecurity(securedEnabled = true)
public class SampleMethodSecurityApplication implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/access").setViewName("access");
	}

	public static void main(String[] args) {
		new SpringApplicationBuilder(SampleMethodSecurityApplication.class).run(args);
	}

	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Configuration(proxyBeanMethods = false)
	protected static class AuthenticationSecurity {

		@SuppressWarnings("deprecation")
		@Bean
		public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
			return new InMemoryUserDetailsManager(
					User.withDefaultPasswordEncoder()
						.username("admin")
						.password("admin")
						.roles("ADMIN", "USER", "ACTUATOR")
						.build(),
					User.withDefaultPasswordEncoder().username("user").password("user").roles("USER").build());
		}

	}

	@Configuration(proxyBeanMethods = false)
	protected static class ApplicationSecurity {

		@Bean
		SecurityFilterChain configure(HttpSecurity http) throws Exception {
			http.csrf(CsrfConfigurer::disable);
			http.authorizeHttpRequests((requests) -> {
				requests.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll();
				requests.anyRequest().fullyAuthenticated();
			});
			http.httpBasic(withDefaults());
			http.formLogin((form) -> form.loginPage("/login").permitAll());
			http.exceptionHandling((exceptions) -> exceptions.accessDeniedPage("/access"));
			return http.build();
		}

	}

	@Configuration(proxyBeanMethods = false)
	@Order(1)
	protected static class ActuatorSecurity {

		@Bean
		SecurityFilterChain actuatorSecurity(HttpSecurity http) throws Exception {
			http.csrf(CsrfConfigurer::disable);
			http.securityMatcher(EndpointRequest.toAnyEndpoint());
			http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
			http.httpBasic(withDefaults());
			return http.build();
		}

	}

	@Controller
	protected static class HomeController {

		@GetMapping("/")
		@Secured("ROLE_ADMIN")
		public String home() {
			return "home";
		}

	}

}
