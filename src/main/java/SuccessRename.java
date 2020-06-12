import java.io.File;
import java.util.ArrayList;

public class SuccessRename {
    private ArrayList<String> matches;
    private File file;

    public SuccessRename(ArrayList<String> matches, File file) {
        this.matches = matches;
        this.file = file;
    }

    public ArrayList<String> getMatches() {
        return matches;
    }

    public File getFile() {
        return file;
    }
}
