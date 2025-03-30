package org.hisudoku.hisudokuapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HisudokuapiApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(HisudokuapiApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
