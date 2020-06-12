import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Driver {
    public static File startFolderOrFile;

    public static void main(String[] args){
        if (args.length > 0){
            String startFolderString = args[0];

            System.out.println("+Input arg detected: "+startFolderString);

            startFolderOrFile = new File(startFolderString);

            if (!startFolderOrFile.exists() || !startFolderOrFile.canRead()){
                System.err.println("Folder or file doesn't exist or can't be read");
                return;
            }

            // Non-ascii
            Pattern nonAscii = Pattern.compile("[^\\x00-\\x7F]");

            if (!startFolderOrFile.isDirectory()){
                // File
                System.out.println("+Input is file - processing");

                FileRenamer renamer = new FileRenamer(startFolderOrFile, nonAscii);
                if (!renamer.renameFile()){
                    System.err.println("Failed to rename file: "+renamer.getAbsolutePath());
                }else{
                    if (!renamer.isSkipped()){
                        System.out.println("->"+renamer.getName());
                    }
                }
            }else{
                // Folder
                System.out.println("+Input is folder - processing");

                FolderProcessor processor = new FolderProcessor(startFolderOrFile, nonAscii);
                processor.process();

                ArrayList<FailedRename> failures = processor.getFailures();
                if (failures.size() > 0){
                    System.err.println("Some files and folders failed to be renamed");
                    for (FailedRename fail: failures){
                        System.err.println(fail.toString());
                    }
                }

                ArrayList<SuccessRename> success = processor.getSuccess();
                if (success.size() > 0){
                    System.out.println("RENAMED "+success.size()+" FILES");
                }
            }
        }else {
            System.out.println("Provide start folder or file");
        }
    }
}
