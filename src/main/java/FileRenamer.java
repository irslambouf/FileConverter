import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileRenamer {
    private final File file;
    private final Pattern pattern;

    public FileRenamer(File file, Pattern pattern) {
        this.file = file;
        this.pattern = pattern;
    }

    // Default to non ascii pattern
    public FileRenamer(File file){
        this.file = file;
        this.pattern = Pattern.compile("[^\\x00-\\x7F]");
    }

    // Return success status of rename operation
    public boolean renameFile(){
        String fileName = file.getName();

        Matcher matcher = pattern.matcher(fileName);

        // We have non ascii
        if (matcher.find()){
            String baseDir = file.getParent();

            File newFile = new File(baseDir, StringUtils.asciiFileName(fileName));

            return file.renameTo(newFile);
        }

        // Nothing to change-*
         return true;
    }

    public String getAbsolutePath(){
        return file.getAbsolutePath();
    }
}
