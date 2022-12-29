package gitlet;

import java.io.File;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     * <p>
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    public static void init() {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        REF_DIR.mkdir();
        BRRANCH_DIR.mkdir();
        OBJ_DIR.mkdir();
        TEMP_DIR.mkdir();
        Branch.createBranch("master", "0");
        Commit initcommit = new Commit(new Date(0), "initial commit");
        initcommit.save();
    }

    public static Branch findHead() {
        File[] fs = BRRANCH_DIR.listFiles();
        for (File f : fs) {
            Branch b = readObject(f, Branch.class);
            if (b.head) {
                return b;
            }
        }
        message("Can't find head");
        return null;
    }

    public static void add(String name) {
        File filename = join(CWD, name);
        if (!filename.exists()) {
            message("File does not exist.");
            System.exit(0);
        }
        File dstname = join(TEMP_DIR, name);
        byte[] b = readContents(filename);
        writeContents(dstname, b);
    }

    public static Commit getCommitById(String id) {
        File f = join(OBJ_DIR, id, "info");
        return readObject(f, Commit.class);
    }

    public static void log() {
        Branch branch = findHead();
        String id = branch.id;
        while (!Objects.equals(id, "0")) {
            Commit c = getCommitById(id);
            System.out.println(c);
            id = c.getParents().get(0);
        }
    }

    public static void checkoutfile(String name) {
        Branch branch = findHead();
        checkoutfile(branch.id, name);
    }

    public static void checkoutfile(String id, String name) {
        if (!join(OBJ_DIR, id).exists()) {
            message("No commit with that id exists.");
            System.exit(0);
        }
        File file = join(OBJ_DIR, id, name);
        if (file.exists()) {
            File dst = join(CWD, name);
            if (dst.exists())
                dst.delete();
            byte[] b = readContents(file);
            writeContents(dst, b);
        } else {
            message("File does not exist in that commit.");
        }
    }

    public static void checkoutBranch(String name) {
        if (Objects.equals(name, findHead().name)) {
            message("No need to checkout the current branch.");
            System.exit(0);
        }
        File branchfile = join(BRRANCH_DIR, name);
        if (!branchfile.exists()) {
            message("No such branch exists.");
            System.exit(0);
        }
        Branch head = findHead();
        Branch b = readObject(branchfile, Branch.class);
        checkforcover(b);
        File[] files = join(OBJ_DIR, b.id).listFiles();
        //change files
        for (File f : files) {
            String filename = f.getName();
            if (filename.equals("info")) {
                continue;
            }
            File dst = join(CWD, filename);
            if (dst.exists())
                dst.delete();
            byte[] bytes = readContents(f);
            writeContents(dst, bytes);
        }
        //delete all in temp
        for (File file : TEMP_DIR.listFiles()) {
            file.delete();
        }
        //change head
        head.head = false;
        writeObject(join(BRRANCH_DIR, head.name), head);
        b.head = true;
        writeObject(join(BRRANCH_DIR, b.name), b);
    }

    public static void rm_branch(String name) {
        if (Objects.equals(findHead().name, name)) {
            message("Cannot remove the current branch.");
            System.exit(0);
        }
        File file = join(BRRANCH_DIR, name);
        if (!file.exists()) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        file.delete();
    }

    public static void reset(String id) {
        if (!join(OBJ_DIR, id).exists()) {
            message("No commit with that id exists.");
            System.exit(0);
        }
        Branch.createBranch("temp", id);
        Branch head = findHead();
        String name = head.name;
        checkoutBranch("temp");
        head = findHead();
        head.name = name;
        join(BRRANCH_DIR, name).delete();
        join(BRRANCH_DIR, "temp").delete();
        writeObject(join(BRRANCH_DIR, name), head);
    }


    public static boolean tracked(String name) {
        Branch b = findHead();
        File f = join(OBJ_DIR, b.id, name);
        if (f.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static void global_log() {
        for (File f : OBJ_DIR.listFiles()) {
            File file = join(f, "info");
            Commit c = readObject(file, Commit.class);
            System.out.println(c);
        }
    }

    public static void find(String msg) {
        boolean flag = true;
        for (File f : OBJ_DIR.listFiles()) {
            File file = join(f, "info");
            Commit c = readObject(file, Commit.class);
            if (Objects.equals(c.getMessage(), msg)) {
                System.out.println(c.getId());
                flag = false;
            }
        }
        if (flag) {
            message("Found no commit with that message.");
        }
    }

    public static void status() {
        message("=== Branches ===");
        for (File f : BRRANCH_DIR.listFiles()) {
            Branch branch = readObject(f, Branch.class);
            if (branch.head) {
                System.out.println("*" + branch.name);
            } else {
                System.out.println(branch);
            }
        }
        message("\n=== Staged Files ===");
        File[] files = join(OBJ_DIR, findHead().id).listFiles();
        for (File f : files) {
            String name = f.getName();
            if (name.equals("info")) {
                continue;
            }
            if (join(CWD, name).exists()) {
                message(name);
            }
        }
        message("\n=== Removed Files ===");
        for (File f : files) {
            String name = f.getName();
            if (name.equals("info")) {
                continue;
            }
            if (!join(CWD, name).exists()) {
                message(name);
            }
        }
        message("\n=== Modifications Not Staged For Commit ===");
        message("\n=== Untracked Files ===\n");
    }

    public static void rm(String name) {
        File f = join(TEMP_DIR, name);
        if (f.exists()) {
            f.delete();
            return;
        }
        Branch b = findHead();
        f = join(OBJ_DIR, b.id, name);
        if (f.exists()) {
            f = join(CWD, name);
            f.delete();
            return;
        }
        message("No reason to remove the file.");
        System.exit(0);
    }

    public static void commit(Date d, String message) {
        Commit commit = new Commit(d, message);
        commit.save();
    }

    public static void merge(String name,String msg) {
        if (!join(BRRANCH_DIR, name).exists()) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        if (TEMP_DIR.listFiles().length != 0) {
            message("You have uncommitted changes.");
            System.exit(0);
        }
        Branch head = findHead();
        Branch given = readObject(join(BRRANCH_DIR, name), Branch.class);
        checkforcover(given);
        if (head.equals(given)) {
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }
        String split_id = find_split(head, given);
        if (split_id.equals(given.id)) {
            return;
        } else if (split_id.equals(head.id)) {
            checkoutBranch(name);
            message("Current branch fast-forwarded.");
            return;
        }
        File[] givenfiles = join(OBJ_DIR, given.id).listFiles();
        File[] headfiles = join(OBJ_DIR, head.id).listFiles();
        List<File> tracks = new ArrayList<>();
        for (File file : givenfiles) {
            String fileName = file.getName();
            if (fileName.equals("info")) {
                continue;
            }
            File splitPath = join(OBJ_DIR, split_id, fileName);
            File headPath = join(OBJ_DIR, head.id, fileName);
            if (splitPath.exists() && headPath.exists()) {
                String givenid = fileaddress(file);
                String splitid = fileaddress(splitPath);
                String headid = fileaddress(headPath);
                if (!givenid.equals(splitid) && givenid.equals(headid)) {
                    //case 3
                    tracks.add(file);
                } else if (givenid.equals(splitid) && !headid.equals(splitid)) {
                    //case 2
                    tracks.add(headPath);
                } else if (!givenid.equals(splitid) && headid.equals(splitid)) {
                    //case 1
                    tracks.add(file);
                } else {
                    //case 8
                    tracks.add(filemerge(headPath, file));
                }
            }
            else if(splitPath.exists()){
                if(!fileaddress(splitPath).equals(fileaddress(file))){
                    //case 8
                    tracks.add(filemerge(null,file));
                }
                //else belong to case 7,just remove this file
            }
            else if(headPath.exists()){
                if(fileaddress(headPath).equals(fileaddress(file))){
                    //case 3
                    tracks.add(file);
                }
                else{
                    //case 8
                    tracks.add(filemerge(headPath, file));
                }
            }
            else{
                //case 5
                tracks.add(file);
            }
        }
        for(File file:headfiles){
            String fileName=file.getName();
            File splitPath = join(OBJ_DIR, split_id, fileName);
            File givenPath = join(OBJ_DIR, head.id, fileName);
            //if given exist the file with same name,
            // all situation have checked before
            if(!givenPath.exists()){
                if(splitPath.exists()){
                    //case 6:if file is the same in split point and head
                    //and not exist in given branch,just remove it
                    if(!fileaddress(splitPath).equals(fileaddress(file))){
                        //case 8
                        tracks.add(filemerge(file,null));
                    }
                }
                else{
                    //case 4
                    tracks.add(file);
                }
            }
        }
        Commit c=new Commit(new java.util.Date(),msg, given.id, tracks);
    }

    //file a from head ,file b from given
    private static File filemerge(File a, File b) {
        byte[] filea=new byte[0];
        if(a!=null)
           filea=readContents(a);
        byte[] fileb=new byte[0];
        if(b!=null)
            fileb=readContents(b);
        String s1="<<<<<<< HEAD\n";
        String s2="=======\n";
        String s3=">>>>>>>\n";

        File file;
        if(a!=null)
            file=join(TEMP_DIR,a.getName());
        else
            file=join(TEMP_DIR,b.getName());
        writeContents(file,s1.getBytes(),filea,s2.getBytes(),fileb,s3.getBytes());
        return file;
    }

    private static void checkforcover(Branch b) {
        Branch head = findHead();
        File[] files = join(OBJ_DIR, b.id).listFiles();
        for (File f : files) {
            String filename = f.getName();
            if (filename.equals("info")) {
                continue;
            }
            File dst = join(CWD, filename);
            if (dst.exists()) {
                if (!tracked(dst.getName())) {
                    message("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
    }

    public static String fileaddress(File file) {
        return sha1(readContents(file));
    }

    private static String find_split(Branch b1, Branch b2) {
        List<String> s1 = commitlist(b1);
        List<String> s2 = commitlist(b2);
        int i;
        for (i = 0; i < s1.size() && i < s2.size(); i++) {
            if (!Objects.equals(s1.get(i), s2.get(i))) {
                return s1.get(i - 1);
            }
        }
        return s1.get(i - 1);
    }

    private static List<String> commitlist(Branch branch) {
        String id = branch.id;
        List<String> list = new ArrayList<>();
        while (!Objects.equals(id, "0")) {
            list.add(id);
            Commit c = getCommitById(id);
            id = c.getParents().get(0);
        }
        Collections.reverse(list);
        return list;
    }

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File REF_DIR = join(GITLET_DIR, "refs");
    public static final File BRRANCH_DIR = join(REF_DIR, "branches");
    public static final File OBJ_DIR = join(GITLET_DIR, "objects");
    public static final File TEMP_DIR = join(GITLET_DIR, "temp");
    /* TODO: fill in the rest of this class. */

}
