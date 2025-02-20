/**
 * RandomGeneratorSimu.java
 *
 * This class provides a method to generate pseudo-random double values.
 * It offers two methods for generating random numbers:
 *   1. Using the built-in Math.random() method.
 *   2. Using a custom Random generator initialized with a fixed seed to ensure reproducibility.
 *
 * The choice between the two methods is controlled by the 'randomMath' flag.
 */

 package com.projetzz2.lpwan_colision_simulation;

 import java.util.Random;
 
 public class RandomGeneratorSimu {
     
     // A custom Random generator initialized with a fixed seed for reproducible random numbers.
     private static Random generator = new Random(998877665544332215L);
     
     // Flag to decide whether to use Math.random() or the custom Random generator.
     private static boolean randomMath = false;
 
     /**
      * Generates a random double value in the range [0, 1).
      * <p>
      * If 'randomMath' is set to true, the method returns a value from Math.random().
      * Otherwise, it uses the custom Random generator. The generated integer is converted
      * to a positive double and normalized by dividing by Integer.MAX_VALUE.
      *
      * @return a random double in the range [0, 1)
      */
     public static double random(){
         if(randomMath){
             return Math.random();
         }
         // Generate a random integer, take its absolute value, convert to double, and normalize it.
         return (double)Math.abs(generator.nextInt()) / Integer.MAX_VALUE;
     }
 }
 