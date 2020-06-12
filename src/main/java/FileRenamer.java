import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileRenamer {
    private File file;
    private final Pattern pattern;
    private final String level;
    private boolean skipped = false;
    private SuccessRename re;

    public FileRenamer(File file, Pattern pattern, String level) {
        this.file = file;
        this.pattern = pattern;
        this.level = level;
    }

    public FileRenamer(File file, String level) {
        this.file = file;
        this.pattern = Pattern.compile("[^\\x00-\\x7F]");
        this.level = level;
    }

    public FileRenamer(File file, Pattern pattern) {
        this.file = file;
        this.pattern = pattern;
        this.level = "-";
    }

    // Default to non ascii pattern
    public FileRenamer(File file) {
        this.file = file;
        this.pattern = Pattern.compile("[^\\x00-\\x7F]");
        this.level = "-";
    }

    // Return success status of rename operation
    public boolean renameFile() {
        if (file.isDirectory()) {
            System.out.print("FOLDER FINISHED - RENAME SELF\nFOLDER " + level + " ");
        } else {
            System.out.print("FILE   " + level + " ");
        }

        String fileName = file.getName();

        Matcher matcher = pattern.matcher(fileName);

        // We have non ascii
        if (matcher.find()) {
            System.out.println("RENAMED " + file.getName());

            // Collect all matches
            ArrayList<String> matches = new ArrayList<String>();
            matches.add(matcher.group());
            while(matcher.find()){
                matches.add(matcher.group());
            }

            String baseDir = file.getParent();

            File newFile = new File(baseDir, StringUtils.asciiFileName(fileName));

            boolean success = file.renameTo(newFile);

            file = newFile;

            re = new SuccessRename(matches, file);

            return success;
        }

        System.out.println("SKIPPED " + file.getName());

        // Nothing to changed
        skipped = true;
        return true;
    }

    public SuccessRename getRe() {
        return re;
    }

    public boolean isSkipped() {
        return skipped;
    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    public String getName() {
        return file.getName();
    }
}
