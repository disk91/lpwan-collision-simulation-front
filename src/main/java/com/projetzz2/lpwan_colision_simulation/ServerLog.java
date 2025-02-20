/**
 * ServerLog.java
 *
 * This class provides static methods for logging messages with a timestamp.
 * It utilizes a specific date-time format and directs output to either the standard
 * output stream (for regular log messages) or the standard error stream (for error messages).
 *
 * Usage:
 *   ServerLog.printLog("This is a log message.");
 *   ServerLog.printErrorLog("This is an error log message.");
 */

 package com.projetzz2.lpwan_colision_simulation;

 import java.time.LocalDateTime;
 import java.time.format.DateTimeFormatter;
 
 public class ServerLog {
     
     // Formatter for date and time in the pattern: "year-month-day hours:minutes:seconds.milliseconds"
     private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
 
     /**
      * Prints a log message to the standard output with the current timestamp.
      *
      * @param msg The message to be logged.
      */
     static public void printLog(String msg){
         // Obtain the current date and time
         LocalDateTime currentDateTime = LocalDateTime.now();
         // Format and print the message with the timestamp to the console
         System.out.println("[" + currentDateTime.format(formatter) + "]  " + msg);
     }
 
     /**
      * Prints an error log message to the standard error with the current timestamp.
      *
      * @param msg The error message to be logged.
      */
     static public void printErrorLog(String msg){
         // Obtain the current date and time
         LocalDateTime currentDateTime = LocalDateTime.now();
         // Format and print the error message with the timestamp to the error output stream
         System.err.println("[" + currentDateTime.format(formatter) + "]  " + msg);
     }
 
 }
