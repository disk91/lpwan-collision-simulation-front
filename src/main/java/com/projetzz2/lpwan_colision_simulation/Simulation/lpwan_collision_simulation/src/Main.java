/*import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ModelRunner m = new ModelRunner();
        RadioModel r = new MiotyModel(MiotyModel.MODE_EU1);
        RadioModel s = new SigfoxModel();
        RadioModel l = new LoRaWanModel();
        String resultFile = "output-5.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile, false))) {
            for ( int i = 2 ; i < 250 ; i+=1) {
                // Run mioty
                long t = 0;
                long c = 0;
                for ( int exp = 0 ; exp < 30 ; exp++ ) {
                    m.runStep(r, i);
                    t+=m.getTotalFrames();
                    c+=m.getTotalCollisions();
                }
                int colisionPercentMioty = (int)Math.floor((100.0*c/(double)t));

                // Run sigfox
                t = 0; c = 0;
                for ( int exp = 0 ; exp < 30 ; exp++ ) {
                    m.runStep(s, i);
                    t+=m.getTotalFrames();
                    c+=m.getTotalCollisions();
                }
                int colisionPercentSigfox = (int)Math.floor((100.0*c/(double)t));

                // Run LoRaWan
                t = 0; c = 0;
                for ( int exp = 0 ; exp < 30 ; exp++ ) {
                    m.runStep(l, i);
                    t+=m.getTotalFrames();
                    c+=m.getTotalCollisions();
                }
                int colisionPercentLoRaWan = (int)Math.floor((100.0*c/(double)t));


                System.out.println("With "+i+"/s get "+colisionPercentMioty+"% / "+colisionPercentSigfox+"% / "+colisionPercentLoRaWan+"% frame lost" );
                writer.write(i+";"+colisionPercentMioty+";"+colisionPercentSigfox+";"+colisionPercentLoRaWan);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            System.err.println("File issue");
        }

    }
}*/