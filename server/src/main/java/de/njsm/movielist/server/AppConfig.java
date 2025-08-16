package de.njsm.movielist.server;

import java.beans.PropertyVetoException;
import java.util.Properties;
import java.util.function.Function;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.njsm.movielist.server.db.AuthHandler;
import de.njsm.movielist.server.db.ConnectionFactory;
import de.njsm.movielist.server.db.OidcUserService;
import de.njsm.movielist.server.db.UserHandler;
import de.njsm.movielist.server.util.FreeMarkerConfigurer;
import liquibase.integration.spring.SpringLiquibase;

@Configuration
@ComponentScan(basePackages = "de.njsm.movielist")
@Import({ Common.class, Beans.class, Security.class })
@EnableWebSecurity
public class AppConfig {}

@Configuration
@Lazy
@EnableWebSecurity
class Security {

	@Bean
	public SecurityFilterChain filterChain(
		HttpSecurity http,
		OAuth2AuthorizedClientRepository authorizedClientRepository,
		AuthHandler userService,
		OidcUserService oidcUserService
	) throws Exception {
		String loginPage = "/login";
		return http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/login/**").permitAll()
				.requestMatchers("/create_account").permitAll()
				.requestMatchers("/health").permitAll()
				.requestMatchers("/**").authenticated()
			)
			.formLogin(formLogin -> formLogin
				.loginPage(loginPage))
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl(loginPage)
				.deleteCookies("JSESSIONID"))
			.sessionManagement(sessionManagement -> sessionManagement
				.invalidSessionUrl(loginPage))
			.rememberMe(rememberMe -> rememberMe.key("cUnvnUVcgR0WaY1Rbn3TeJaZb0yXbo6MikTbp25FSaWrwokXBFXbGtalZ7IkUUtS698mccPnaMI2J0a2M5rY1gcc"))
			.headers(headers -> headers
				.cacheControl(HeadersConfigurer.CacheControlConfig::disable))
			.oauth2Login(oauth2 -> oauth2
				.authorizedClientRepository(authorizedClientRepository)
				.userInfoEndpoint(userinfo -> userinfo
					.oidcUserService(oidcUserService)))
			.authenticationProvider(userService)
			.build();
	}

	@Bean
	public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
		return new HandlerMappingIntrospector();
	}

	@Bean
	public UserHandler persistentUserBackend(
		@Qualifier("persistentConnectionFactory") ConnectionFactory persistentConnectionFactory,
		@Qualifier("circuitBreakerDatabase") String circuitBreakerDatabase,
		int circuitBreakerTimeout
	) {
		return new UserHandler(
			persistentConnectionFactory,
			circuitBreakerDatabase,
			circuitBreakerTimeout
		);
	}

	@Bean
	public OidcIdTokenDecoderFactory decoder(Function<ClientRegistration, JwsAlgorithm> algorithmResolver) {
		OidcIdTokenDecoderFactory result = new OidcIdTokenDecoderFactory();
		result.setJwsAlgorithmResolver(algorithmResolver);
		return result;
	}

	@Bean
	public ClientRegistrationRepository clientRepository(ClientRegistration client) {
		return new InMemoryClientRegistrationRepository(client);
	}
	@Bean
	public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
		return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
	}
	@Bean
	public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService service) {
		return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(service);
	}
}

@Configuration
@Lazy
class Beans {
	@Bean
	public freemarker.template.Configuration freemarker(FreeMarkerConfigurer freemarkerConfig) {
		return freemarkerConfig.getConfiguration();
	}
}

@Configuration
@Lazy
class Common {

	@Bean
	public String circuitBreakerDatabase() {
		return "database";
	}

	@Bean
	public PropertiesFactoryBean propertiesFile() {
		PropertiesFactoryBean result = new PropertiesFactoryBean();
		result.setLocation(new FileSystemResource("/etc/movie-list.properties"));
		return result;
	}

	@Bean
	public PropertiesFactoryBean freemarkerProperties() {
		PropertiesFactoryBean result = new PropertiesFactoryBean();
		result.setLocation(new ClassPathResource("freemarker.properties"));
		return result;
	}

	@Bean("dbUrl")
	public String getDbUrl(
		@Qualifier("dbAddress") String dbAddress,
		@Qualifier("dbPort") String dbPort,
		@Qualifier("dbName") String dbName
	) {
		return String.format(
			"jdbc:postgresql://%s:%s/%s",
			dbAddress,
			dbPort,
			dbName);
	}

	@Bean("datasource")
	public DataSource getDataSource(
		@Qualifier("dbUrl") String dbUrl,
		@Qualifier("dbConfig") Properties dbProperties
	) throws PropertyVetoException {
		ComboPooledDataSource result = new ComboPooledDataSource();
		setCommonProperties(result, dbUrl, dbProperties);

		result.setAcquireIncrement(3);
		result.setInitialPoolSize(2);
		result.setMaxPoolSize(16);
		result.setMinPoolSize(2);
		result.setCheckoutTimeout(2000);
		result.setUnreturnedConnectionTimeout(15);
		return result;
	}

	@Bean("datasourceForMigrations")
	public DataSource getDatasourceForMigrations(
		@Qualifier("dbUrl") String dbUrl,
		@Qualifier("dbConfig") Properties dbProperties,
		@Qualifier("circuitBreakerTimeout") int checkoutTimeout
	) throws PropertyVetoException {
		ComboPooledDataSource result = new ComboPooledDataSource();
		setCommonProperties(result, dbUrl, dbProperties);

		result.setAcquireIncrement(1);
		result.setInitialPoolSize(1);
		result.setMaxPoolSize(1);
		result.setMinPoolSize(0);
		result.setCheckoutTimeout(checkoutTimeout);
		return result;
	}

	private void setCommonProperties(
		ComboPooledDataSource result,
		String dbUrl,
		Properties dbProperties
	) throws PropertyVetoException {
		result.setDriverClass("org.postgresql.Driver");
		result.setIdleConnectionTestPeriod(60);
		result.setTestConnectionOnCheckin(true);
		result.setMaxIdleTimeExcessConnections(900);
		result.setJdbcUrl(dbUrl);
		result.setProperties(dbProperties);
		result.setContextClassLoaderSource("library");
		result.setPrivilegeSpawnedThreads(true);
	}

	@Bean
	public SpringLiquibase liquibase(
		@Qualifier("datasourceForMigrations") DataSource dataSource,
		@Qualifier("liquibaseContexts") String contexts
	) {
		SpringLiquibase springLiquibase = new SpringLiquibase();
		springLiquibase.setDataSource(dataSource);
		springLiquibase.setChangeLog("classpath:migrations/master.xml");
		springLiquibase.setContexts(contexts);
		return springLiquibase;
	}

	@Bean
	@RequestScope
	public ConnectionFactory connectionFactory(@Qualifier("datasource") DataSource dataSource) {
		return new ConnectionFactory(dataSource);
	}

	@Bean
	public ConnectionFactory persistentConnectionFactory(@Qualifier("datasource") DataSource dataSource) {
		return new ConnectionFactory(dataSource);
	}
}