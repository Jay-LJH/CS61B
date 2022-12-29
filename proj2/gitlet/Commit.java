package gitlet;

// TODO: any imports you need here

import net.sf.saxon.trans.SymbolicName;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.*;

import static gitlet.Repository.findHead;
import static gitlet.Utils.*;


/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     * <p>
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    public Commit(Date date, String message) {
        this.date = date;
        this.message = message;
        this.parents = new ArrayList<>();
        b = findHead();
        parents.add(b.id);
        tracks = new ArrayList<>();
        File[] fs = TEMP_DIR.listFiles();
        for (File f : fs) {
            tracks.add(readContents(f));
        }
        id = generateId();
    }

    private String generateId() {
        if (Objects.equals(b.id, "0"))
            return sha1(date.toString(), parents.toString(), message);
        return sha1(date.toString(), parents.toString(), message, tracks.toString());
    }

    public void save() {
        if (TEMP_DIR.listFiles().length==0 && !Objects.equals(b.id, "0")) {
            message("No changes added to the commit.");
            System.exit(0);
        }
        File f = join(OBJ_DIR, id);
        f.mkdir();
        File[] fs = TEMP_DIR.listFiles();
        for (File t : fs) {
            byte[] b = readContents(t);
            File dstname = join(f, t.getName());
            writeContents(dstname, b);
            t.delete();
        }
        File fi = join(f, "info");
        try {
            fi.createNewFile();
            writeObject(fi, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        b.id=id;
        File branchfile=join(BRRANCH_DIR,b.name);
        branchfile.delete();
        writeObject(branchfile,b);
    }

    @Override
    public String toString() {
        String s = "===\n";
        s += "commit " + id + "\n";
        if (parents.size() > 1) {
            Iterator<String> i = parents.iterator();
            s += "Merge: " + i.next().substring(0, 6) + " " + i.next().substring(0, 6) + "\n";
        }
        s += "Date: " + date.toString() + "\n";
        s += message + "\n";
        return s;
    }
    public List<String> getParents(){
        return parents;
    }
    public static void main(String[] args) {
    System.out.println(TEMP_DIR.listFiles()==null);
    }

    /**
     * The message of this Commit.
     */
    private String message;
    private Date date;
    private String id;
    private List<String> parents;
    private Branch b;
    private List<Object> tracks;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File REF_DIR = join(GITLET_DIR, "refs");
    public static final File BRRANCH_DIR = join(REF_DIR, "branches");
    public static final File TEMP_DIR = join(GITLET_DIR, "temp");
    public static final File OBJ_DIR = join(GITLET_DIR, "objects");
    /* TODO: fill in the rest of this class. */
}
