import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Decryptor {

    private static double[] english_freq = new double[]{
            0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015,  // A-G
            0.06094, 0.06966, 0.00153, 0.00772, 0.04025, 0.02406, 0.06749,  // H-N
            0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758,  // O-U
            0.00978, 0.02360, 0.00150, 0.01974, 0.00074                     // V-Z
    };

    String ETAOIN = "ETAOINSHRDLCUMWFGYPBVKJXQZ";


    /**
     *
     * @param encryptedText
     * @return decryptedText
     */
    public String decrypt(String encryptedText) {
        String result;
        result = preprocessEncryptedText(encryptedText);

        // TODO: 10-Nov-18  first substitute whole 1-2 letter words - I, a, of etc.
        result = substituteShortWords(result);

        // TODO: 10-Nov-18 substitute most frequent letters
        // TODO: 10-Nov-18 substitute digrams / trigrams
        return result;
    }


    private String substituteShortWords(String encryptedText) {
        Map<Character, Double> expectedLetterFrequencies = getExpectedLetterFrequencies(english_freq, encryptedText.length());
        Map<Character, Double> encryptedTextLetterFrequencies = getFrequencies(encryptedText);
        double distanceToEnglish = chiSquare(encryptedTextLetterFrequencies, expectedLetterFrequencies);


        String[] tokens = encryptedText.split(" ");

        //if the words are not separated by spaces, just return the encrypted text
        if (tokens.length == 0)
            return encryptedText;

        Arrays.sort(tokens, Comparator.comparingInt(String::length));

        String result = encryptedText;
        String currentText;
        for (String token : tokens) {
            if (token.length() == 1){
                currentText = result.replace(token.charAt(0), 'a');
                double curDistance = chiSquare(getFrequencies(currentText), expectedLetterFrequencies);
                if (curDistance < distanceToEnglish){
                    distanceToEnglish = curDistance;
                    result = currentText;
                }
            }
        }

        return result;
    }

    private Map<Character, Double> getFrequencies(String text) {
        Map<Character, Double> myFreq = new HashMap<>();
        text.chars()
                .mapToObj(item -> (char) item)
                .filter(Character::isLetter)
                .forEach(c -> myFreq.put(c, myFreq.getOrDefault(c, 0.0) + 1));
//            myFreq.keySet().forEach(c -> myFreq.put(c, myFreq.get(c) / text.length()));
        return myFreq;
    }

    private double chiSquare(Map<Character, Double> encryptedTextFreq, Map<Character, Double> expectedLetterFrequency){
        double score = 0.0;
        double letterCount, expectedCount;
        for (Character key : encryptedTextFreq.keySet()){
            if (!expectedLetterFrequency.containsKey(key))
                continue;
            letterCount = encryptedTextFreq.get(key);
            expectedCount = expectedLetterFrequency.get(key);
            score += Math.pow(letterCount- expectedCount, 2)/expectedCount;
        }
        return score;
    }

    private Map<Character, Double> getExpectedLetterFrequencies(double[] english_freq, int encryptedTextLength){
        Map<Character, Double> expectedCounts = new HashMap<>();
        char key = 'a';
        for (int i = 0; i < english_freq.length; i++) {
            expectedCounts.put((char) (key + i) , english_freq[i]*encryptedTextLength);
        }
        return expectedCounts;
    }

    private String preprocessEncryptedText(String encryptedText) {
        encryptedText = encryptedText.toLowerCase();
        //remove punctuation
        encryptedText = encryptedText.replaceAll("\\p{P}", "");

        return encryptedText;
    }
}
