package org.hisudoku.hisudokuapi;

import org.springframework.boot.SpringApplication;

public class TestHisudokuapiApplication {

	public static void main(String[] args) {
		SpringApplication.from(HisudokuapiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
