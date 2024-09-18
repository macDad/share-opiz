import java.text.Normalizer;
import java.util.regex.Pattern;

public class LanguageValidator {

    // Pattern for allowed English and French characters (including accents)
    private static final Pattern ENGLISH_FRENCH_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÖØ-öø-ÿ\\s'-]+$");
    
    // Disallowed accent pattern for other languages (like Czech, German, Spanish, etc.)
    private static final Pattern DISALLOWED_ACCENTS_PATTERN = Pattern.compile("[čďěňřšťůžäëïöüñ]");

    /**
     * Removes accents from a string using Normalizer.
     * @param input the input string
     * @return the string without accents
     */
    public static String removeAccents(String input) {
        if (input == null) {
            return null;
        }
        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    /**
     * Checks if the input string contains only English or French accented characters and no other disallowed accents.
     * @param input the string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isEnglishOrFrench(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        // Check if the string contains only allowed English and French characters
        if (!ENGLISH_FRENCH_PATTERN.matcher(input).matches()) {
            return false;
        }

        // Check if the string contains any disallowed accents
        if (DISALLOWED_ACCENTS_PATTERN.matcher(input).find()) {
            return false; // Contains disallowed accents
        }

        return true;
    }

    public static void main(String[] args) {
        String testString1 = "Bonjour ça va";   // Should return true
        String testString2 = "Hello World";     // Should return true
        String testString3 = "Příliš žluťoučký kůň"; // Should return false (Czech)
        String testString4 = "Café au lait";    // Should return true
        String testString5 = "Über Café";       // Should return false (German 'Ü')

        System.out.println(isEnglishOrFrench(testString1)); // true
        System.out.println(isEnglishOrFrench(testString2)); // true
        System.out.println(isEnglishOrFrench(testString3)); // false
        System.out.println(isEnglishOrFrench(testString4)); // true
        System.out.println(isEnglishOrFrench(testString5)); // false
    }
}
