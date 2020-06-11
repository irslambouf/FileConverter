import java.text.Normalizer;

public class StringUtils {
    public static String asciiFileName(String original){
        original = Normalizer.normalize(original, Normalizer.Form.NFD);
        return original.replaceAll("[^\\x00-\\x7F]", "");
    }
}
