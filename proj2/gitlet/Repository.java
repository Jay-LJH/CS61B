package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

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
        Branch.createBranch("master","0");
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
        File f= join(OBJ_DIR,id,"info");
        return readObject(f, Commit.class);
    }
    public static void log(){
        Branch branch=findHead();
        String id=branch.id;
        while (!Objects.equals(id, "0")){
            Commit c=getCommitById(id);
            System.out.println(c);
            id=c.getParents().get(0);
        }
    }
    public void commit(Date d, String message) {
        Commit commit = new Commit(d, message);
        commit.save();
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
