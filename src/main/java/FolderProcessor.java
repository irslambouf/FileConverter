import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class FolderProcessor {
    private final File folder;
    private final Pattern pattern;
    private final ArrayList<FailedRename> failures = new ArrayList<FailedRename>();
    private final ArrayList<SuccessRename> success = new ArrayList<SuccessRename>();
    private String level = null;

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

    public ArrayList<SuccessRename> getSuccess() {
        return success;
    }

    private String getLevel(){
        if (level == null){
            StringBuilder base = new StringBuilder("-");
            File currFolder = folder;

            while(currFolder.getParentFile() != null && !currFolder.getAbsolutePath().equals(Driver.startFolderOrFile.getAbsolutePath())){
                currFolder = currFolder.getParentFile();
                base.append("-");
            }

            level = base.toString();
            return level;
        }
        return level;
    }

    // We want to recursively travel down and process files before renaming folders
    public void process(){
        System.out.println("FOLDER "+getLevel()+" "+folder.getName());

        // Collect info
        File[] fileOrFolderList = folder.listFiles();

        ArrayList<File> files = new ArrayList<File>();
        ArrayList<File> folders = new ArrayList<File>();

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
            success.addAll(folderProcessor.getSuccess());
        }

        // Process files within this folder
        for (File file : files){
            FileRenamer renamer = new FileRenamer(file, level);
            if (!renamer.renameFile()){
                System.err.println("Failed to rename file: "+renamer.getAbsolutePath());
                failures.add(new FailedRename(file, FailedRename.Reason.FAILED_RENAME));
            }else{
                if (!renamer.isSkipped()){
                    System.out.println("->"+renamer.getRe().getFile().getName());
                    System.out.print("->Replaced - ");
                    for( String s : renamer.getRe().getMatches()){
                        System.out.print(s+ " ");
                    }
                    System.out.print("\n");
                    success.add(renamer.getRe());
                }
            }
        }

        // Process itself
        FileRenamer selfRenamer = new FileRenamer(folder);
        if (!selfRenamer.renameFile()){
            System.err.println("Failed to rename file: "+selfRenamer.getAbsolutePath());
            failures.add(new FailedRename(folder, FailedRename.Reason.FAILED_RENAME));
        }else {
            if (!selfRenamer.isSkipped()){
                System.out.println("->"+selfRenamer.getRe().getFile().getName());
                System.out.print("->Replaced - ");
                for( String s : selfRenamer.getRe().getMatches()){
                    System.out.print(s+ " ");
                }
                System.out.print("\n");
            }
        }
    }
}
