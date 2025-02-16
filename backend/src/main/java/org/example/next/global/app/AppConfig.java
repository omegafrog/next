package org.example.next.global.app;

public class AppConfig {

    public static boolean isNotProd() {
        return true;
    }

    public static String getSiteFrontUrl(){
        return "http://localhost:3000";
    }

}
