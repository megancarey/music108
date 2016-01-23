import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.Object;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/** Using the averages stored in averages.txt and output from getAverages main
 *  function, this program takes in a particular song title that has also been
 *  analyzed by jMIR software and saved to sample.xml in this directory. The
 *  values output for that song are then compared to the averages in averages.txt
 *  and assigned a score accordingly. This score is not saved anywhere; it is
 *  displayed in a pop-up window and output on the command line.
 *  @author Megan Carey */

public class getScore {
    /** Runs the main method, which evaluates the score of a particular song
     *  and prints that score in the terminal. */
    public static void main(String... args) {
        songName = args[0];
        double score = eval();
        /** The following portion allows for the score of a song to be shown in a
         *  pop-up window. It is commented out because it makes the program run
         *  quite a bit slower, so it is not ideal for testing. */
//        JOptionPane.showMessageDialog(null,
//                "The score for " + songName + " is " + score*100 + "/100.",
//                "Song Score",
//                JOptionPane.PLAIN_MESSAGE);
        System.out.println("The score for " + songName + " is " + score*100 + "/100.");
    }

    /** Evaluates the song's score relative to the average values of songs
     *  from averages.txt. */
    static double eval() {
        /** The following code reads the contents of averages.txt. */
        File averagesFile = new File("averages.txt");
        byte[] averages;
        if (!averagesFile.isFile()) {
            throw new IllegalArgumentException("wrong file type");
        }
        try {
            averages = Files.readAllBytes(averagesFile.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
        /** The following code parses the values that are stored in
         *  averages.txt. */
        String averagesContents = new String(averages);
        double[] averageValues = new double[getAverages.num_features];
        int tracker = 0;
        while (averagesContents.contains("Average: ")) {
            int ind = averagesContents.indexOf("Average: ") + 9;
            int fin = averagesContents.indexOf("...");
            String valString = averagesContents.substring(ind, fin);
            double val = Double.parseDouble(valString);
            averageValues[tracker] = val;
            averagesContents = averagesContents.substring(fin + 3);
            tracker += 1;
        }
        /** The following code reads the contents of sample.xml. */
        byte[] featureExtractionsBytes;
        File featureExtractionsFile = new File("sample.xml");
        if (!featureExtractionsFile.isFile()) {
            throw new IllegalArgumentException("wrong file type");
        }
        try {
            featureExtractionsBytes =  Files.readAllBytes(featureExtractionsFile.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
        /** The following code parses the values that are stored in
         *  sample.xml. */
        String featureExtractionsContents = new String(featureExtractionsBytes);
        double[] values = new double[getAverages.num_features];

        if (!featureExtractionsContents.contains(songName)) {
            System.out.println("That is not a valid song name.");
            System.exit(0);
        } else {
            int index = featureExtractionsContents.indexOf(songName);
            String substr = featureExtractionsContents.substring(index);
            int exp;
            for (int i = 0; i < getAverages.num_features; i += 1) {
                int indexBeforeVal = substr.indexOf("<v>") + 3;
                int indexAfterVal = substr.indexOf("</v>");
                if (String.valueOf(substr.charAt(indexAfterVal - 2)).equals("-")) {
                    exp = -1 * Integer.parseInt(String.valueOf(substr.charAt(indexAfterVal - 1)));
                    indexAfterVal = indexAfterVal - 3;
                } else {
                    exp = Integer.parseInt(String.valueOf(substr.charAt(indexAfterVal - 1)));
                    indexAfterVal = indexAfterVal - 2;
                }
                String valString = substr.substring(indexBeforeVal, indexAfterVal);
                double val = Double.parseDouble(valString);
                values[i] += val;
                substr = substr.substring(indexAfterVal + 4);
            }
        }
        /** The following code compares the values in averages.txt to
         *  those in sample.xml and produces a score. */
        double averageScore = 0;
        double thisSongScore = 0;
        for (int i = 0; i < getAverages.num_features; i += 1) {
            averageScore += averageValues[i];
            thisSongScore += values[i];
        }

        double score = 1 - Math.abs((averageScore - thisSongScore)/averageScore);
        return score;
    }

    /** The given songname. NOTE: If there are two songs that begin with the same word,
     *  this program will not know the difference between them. */
    static String songName;

    /** The score of how "average" the song is based on comparison with
     *  the values in averages.txt. For the initial testing purposes, this means
     *  testing the given song's values against the Top 20 songs from the week of
     *  December 7th, 2015. */
    static int score;
}
