import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.Object;
import java.util.regex.Pattern;

/** This takes the information gathered by jMIR software's analysis and written into
 *  a file featureResults.xml and creates an average value for each of the features
 *  input. There is a variable at the bottom of the file that should be changed if the
 *  number of features analyzed are changed, or if the number of songs input to be
 *  averaged is changed. The output of the main function is a set of averages titled
 *  with what they represent, and it is written into a file averages.txt in this same
 *  directory.
 *  @author Megan Carey */

public class getAverages {
    /** Retrieves a byte array of the contents of the file of features extracted. */
    static byte[] featuresExtracted() {
        File file = new File("featureResults.xml");
        if (!file.isFile()) {
            throw new IllegalArgumentException("wrong file type");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Writes the average score for each feature category into a file. */
    static void writeInAverage() {
        /** Initializing variables. */
        double[] sums = new double[num_features];
        String[] featureNames = new String[num_features];
        byte[] featuresBytes = featuresExtracted();
        String features = new String(featuresBytes);
        int indexFirst = features.indexOf("<data_set>");
        int indexLast = features.indexOf("</data_set>");
        String firstFeatures = features.substring(indexFirst, indexLast);
        Pattern featureNameStatement = Pattern.compile("[a-zA-Z_0-9]*[ \\t]*");
        Pattern featureValStatement = Pattern.compile("[a-zA-Z_0-9]*[ \\t]*");
        /** The following initializes the file averages.txt if it does not
         *  already exist, and empties it if it does. */
        File newFile = new File("averages.txt");
        try {
            newFile.createNewFile();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        writeContents(newFile, "".getBytes());
        byte[] currentContents;
        /** The following collects the data stored in featureResults.xml. */
        while (features.contains("<data_set>")) {
            int cutoff = features.indexOf("<data_set>") + 10;
            features = features.substring(cutoff);
            int innercutoff = features.indexOf("</data_set>");
            int exp;
            String theseFeatures = features.substring(0, innercutoff);
            for (int i = 0; i < num_features; i += 1) {
                int indexBeforeVal = theseFeatures.indexOf("<v>") + 3;
                int indexAfterVal = theseFeatures.indexOf("</v>");
                if (String.valueOf(theseFeatures.charAt(indexAfterVal - 2)).equals("-")) {
                    exp = -1 * Integer.parseInt(String.valueOf(theseFeatures.charAt(indexAfterVal - 1)));
                    indexAfterVal = indexAfterVal - 3;
                } else {
                    exp = Integer.parseInt(String.valueOf(theseFeatures.charAt(indexAfterVal - 1)));
                    indexAfterVal = indexAfterVal - 2;
                }
                String valString = theseFeatures.substring(indexBeforeVal, indexAfterVal);
                double val = Double.parseDouble(valString);
                sums[i] += val;
                theseFeatures = theseFeatures.substring(indexAfterVal + 4);
            }
        }

        /** The following code produces averages for each of the categories and
         *  prints them into the file averages.txt. */
        for (int i = 0; i < num_features; i += 1) {
            sums[i] = sums[i]/num_songs;
        }

        int counter = 0;
        while (firstFeatures.contains("<name>")) {
            try {
                currentContents = Files.readAllBytes(newFile.toPath());
            } catch (IOException excp) {
                throw new IllegalArgumentException(excp.getMessage());
            }
            int index0 = firstFeatures.indexOf("<name>");
            firstFeatures = firstFeatures.substring(index0 + 6);
            int index1 = firstFeatures.indexOf("</name");
            byte[] toFile = (firstFeatures.substring(0, index1) + " Average: " + ((Double) sums[counter]).toString() + "..." + ("\n")).getBytes(); //ADD SUMS
            StringBuilder textToFile = new StringBuilder();
            textToFile.append(new String(currentContents));
            textToFile.append(new String(toFile));
            writeContents(newFile, textToFile.toString().getBytes());
            counter += 1;
        }
    }

    /** Write the entire contents of the given byte array to the specified file,
     *  creating or overwriting it as needed.
     *  Throws IllegalArgumentException in case of problems. */
    static void writeContents(File file, byte[] bytes) {
        try {
            if (file.isDirectory()) {
                throw new IllegalArgumentException("wrong file type");
            }
            Files.write(file.toPath(), bytes);
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Runs the main method, which writes text into a file. */
    public static void main(String... args) {
        writeInAverage();
    }

    /** The number of features being tested. Should be edited if user wishes to
     *  test a different number of features. */
    public static final int num_features = 7;

    /** The number of songs being tested. Should be edited if user wishes to
     *  test a different number of songs. */
    public static final int num_songs = 20;
}
