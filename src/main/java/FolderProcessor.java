import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class FolderProcessor {
    private final File folder;
    private final Pattern pattern;
    private final ArrayList<FailedRename> failures = new ArrayList<>();

    public FolderProcessor(File folder) {
        this.folder = folder;
        this.pattern = Pattern.compile("[^\\x00-\\x7F]");
    }

    public FolderProcessor(File folder, Pattern pattern) {
        this.folder = folder;
        this.pattern = pattern;
    }

    public ArrayList<FailedRename> getFailures(){
        return failures;
    }

    // We want to recursively travel down and process files before renaming folders
    public void process(){
        // Collect info
        File[] fileOrFolderList = folder.listFiles();

        ArrayList<File> files = new ArrayList<>();
        ArrayList<File> folders = new ArrayList<>();

        if (fileOrFolderList != null){
            for( File file : fileOrFolderList){
                if (file.exists() && file.canRead()){
                    if (file.isDirectory()){
                        folders.add(file);
                    }else {
                        files.add(file);
                    }
                }else{
                    System.out.println("Folder or file doesn't exist or can't be read");

                    FailedRename failure;
                    if (!file.exists()){
                        failure = new FailedRename(file, FailedRename.Reason.DOES_NOT_EXIST);
                    }else{
                        failure = new FailedRename(file, FailedRename.Reason.NO_READ_ACCESS);
                    }

                    failures.add(failure);
                }
            }
        }

        // Recursively go down the folders
        for (File folder : folders){
            FolderProcessor folderProcessor = new FolderProcessor(folder, pattern);
            folderProcessor.process();

            // Aggregate failures upwards
            failures.addAll(folderProcessor.getFailures());
        }

        // Process files within this folder
        for (File file : files){
            FileRenamer renamer = new FileRenamer(file);
            if (!renamer.renameFile()){
                System.out.println("Failed to rename file: "+renamer.getAbsolutePath());
                failures.add(new FailedRename(file, FailedRename.Reason.FAILED_RENAME));
            }
        }

        // Process itself
        FileRenamer selfRenamer = new FileRenamer(folder);
        selfRenamer.renameFile();
        if (!selfRenamer.renameFile()){
            System.out.println("Failed to rename file: "+selfRenamer.getAbsolutePath());
            failures.add(new FailedRename(folder, FailedRename.Reason.FAILED_RENAME));
        }
    }
}
