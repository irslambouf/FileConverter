import java.io.File;

public class FailedRename {
    private final File file;
    private final Reason reason;

    public FailedRename(File file, Reason reason) {
        this.file = file;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "Reason: "+reason+"\n"+file.getAbsolutePath();
    }

    public enum Reason{
        NO_READ_ACCESS,
        DOES_NOT_EXIST,
        FAILED_RENAME
    }
}
