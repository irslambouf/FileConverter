import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Driver {
    public static void main(String[] args){
        if (args.length > 0){
            String startFolderString = args[0];

            File startFolderOrFile = new File(startFolderString);

            if (!startFolderOrFile.exists() || !startFolderOrFile.canRead()){
                System.out.println("Folder or file doesn't exist or can't be read");
                return;
            }

            // Non-ascii
            Pattern nonAscii = Pattern.compile("[^\\x00-\\x7F]");

            if (!startFolderOrFile.isDirectory()){
                // File
                FileRenamer renamer = new FileRenamer(startFolderOrFile, nonAscii);
                if (!renamer.renameFile()){
                    System.out.println("Failed to rename file: "+renamer.getAbsolutePath());
                }
            }else{
                // Folder
                FolderProcessor processor = new FolderProcessor(startFolderOrFile, nonAscii);
                processor.process();

                ArrayList<FailedRename> failures = processor.getFailures();
                if (failures.size() > 0){
                    System.out.println("Some files and folders failed to be renamed");
                    for (FailedRename fail: failures){
                        System.out.println(fail.toString());
                    }
                }
            }
        }else {
            System.out.println("Provide start folder");
        }
    }
}
