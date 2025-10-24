package com.utp.libretago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

@Theme("default")
@SpringBootApplication
public class LibretagoApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(LibretagoApplication.class, args);
	}

}
