package com.projetzz2.lpwan_colision_simulation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerLog {
    
    private static DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    static public void printLog(String msg){
        LocalDateTime dateHeureActuelle = LocalDateTime.now();
        System.out.println("[" + dateHeureActuelle.format(formater) + "]  " + msg);
    }

    static public void printErrorLog(String msg){
        LocalDateTime dateHeureActuelle = LocalDateTime.now();
        System.err.println("[" + dateHeureActuelle.format(formater) + "]  " + msg);
    }

}
