package org.example;

import org.example.controller.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
        private static final Logger logger =
                LoggerFactory.getLogger(Main.class);

        public static void main(String[] args) {

            logger.info("Starting RevPassword Manager Application");

            MainController controller = new MainController();
            controller.start();


            logger.info("Application terminated");
        }
}