package pl.jkuznik;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import javax.sql.DataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.context.annotation.Bean;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@NpmPackage(value = "@fontsource/montserrat", version = "4.5.0")
@Theme(value = "eatt", variant = Lumo.DARK)
@Push
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(DataSource dataSource,
            SqlInitializationProperties properties ) {

        // This bean ensures the database is only initialized when empty
//        properties.setMode(DatabaseInitializationMode.EMBEDDED);
//        properties.setUsername("jkuznik");
//        properties.setPassword("password");
//        DatabaseInitializationMode mode = properties.getMode();
//        String username = properties.getUsername();
//        String password = properties.getPassword();
//        System.out.println("Tryb inicializacji bady danych to: " + mode.toString());
//        System.out.println("Obecny login to: " + username);
//        System.out.println("Obecne hasło to: "+ password);
        return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties) {
            @Override
            public boolean initializeDatabase() {
//                if (repository.count() == 0L) {
                return super.initializeDatabase();
//                }
//                return false;
//            }
            }
        };
    }
}
