package db2025.DB2025Team05_poppop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DataBaseClass2025Application {

	public static void main(String[] args) {
		SpringApplication.run(DataBaseClass2025Application.class, args);
	}

}
