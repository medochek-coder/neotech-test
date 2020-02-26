package com.kulygina.neotechdev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.kulygina.neotechdev.service.TimeStorageService;
import com.kulygina.neotechdev.service.impl.TimeStorageServiceImpl;

@SpringBootApplication
public class NeotechDevApplication {

	private static final String PRINT_PARAM = "-p";

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(NeotechDevApplication.class, args);
		TimeStorageService service = applicationContext.getBean(TimeStorageServiceImpl.class);

		if (args.length > 0 && args[0].equals(PRINT_PARAM)) {
			service.printAll();
		} else {
			service.saveDataEverySecond();
		}
	}
}
