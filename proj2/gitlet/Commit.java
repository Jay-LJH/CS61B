package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
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

    public Commit(Date date, String message, String id, List<File> files) {
        this.date = date;
        this.message = message;
        this.parents = new ArrayList<>();
        b = findHead();
        parents.add(b.id);
        parents.add(id);
        for (File f : files) {
            tracks.add(readContents(f));
        }
        this.id = generateId();

        File f = join(OBJ_DIR, this.id);
        f.mkdir();
        for (File t : files) {
            byte[] b = readContents(t);
            File dstname = join(f, t.getName());
            writeContents(dstname, b);
        }
        for (File file : TEMP_DIR.listFiles()) {
            file.delete();
        }
        //create info file about commit message
        File fi = join(f, "info");
        try {
            fi.createNewFile();
            writeObject(fi, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //change head
        b.id = id;
        File branchfile = join(BRRANCH_DIR, b.name);
        branchfile.delete();
        writeObject(branchfile, b);
    }

    private String generateId() {
        if (Objects.equals(b.id, "0"))
            return sha1(date.toString(), parents.toString(), message);
        return sha1(date.toString(), parents.toString(), message, tracks.toString());
    }

    public void save() {
        boolean flag = true;
        if (!b.id.equals("0")) {
            for (File file : join(OBJ_DIR, parents.get(0)).listFiles()) {
                String name = file.getName();
                File f = join(CWD, name);
                if (!f.exists() || !Repository.fileaddress(file).equals(Repository.fileaddress(f)))
                    flag = false;
            }
        }
        if (TEMP_DIR.listFiles().length == 0 && !Objects.equals(b.id, "0") && flag) {
            message("No changes added to the commit.");
            System.exit(0);
        }
        //copy all files from temp to dst
        File f = join(OBJ_DIR, id);
        f.mkdir();
        File[] fs = TEMP_DIR.listFiles();
        for (File t : fs) {
            byte[] b = readContents(t);
            File dstname = join(f, t.getName());
            writeContents(dstname, b);
            t.delete();
        }
        //create info file about commit message
        File fi = join(f, "info");
        try {
            fi.createNewFile();
            writeObject(fi, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //copy files from head
        if (!Objects.equals(parents.get(0), "0")) {
            fs = join(OBJ_DIR, parents.get(0)).listFiles();
            for (File t : fs) {
                String name = t.getName();
                if (join(f, name).exists()) {
                    continue;
                }
                File cwd = join(CWD, t.getName());
                if (cwd.exists() && Repository.fileaddress(cwd).equals(Repository.fileaddress(t))) {
                    byte[] b = readContents(t);
                    File dstname = join(f, t.getName());
                    writeContents(dstname, b);
                } else if (cwd.exists()) {
                    byte[] b = readContents(cwd);
                    File dstname = join(f, t.getName());
                    writeContents(dstname, b);
                }

            }
        }
        //change head
        b.id = id;
        File branchfile = join(BRRANCH_DIR, b.name);
        branchfile.delete();
        writeObject(branchfile, b);
    }

    @Override
    public String toString() {
        SimpleDateFormat DateFor = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy z");
        String stringDate = DateFor.format(date);
        String s = "===\n";
        s += "commit " + id + "\n";
        if (parents.size() > 1) {
            Iterator<String> i = parents.iterator();
            s += "Merge: " + i.next().substring(0, 6) + " " + i.next().substring(0, 6) + "\n";
        }
        s += "Date: " + stringDate + "\n";
        s += message + "\n";
        return s;
    }

    public List<String> getParents() {
        return parents;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public static void main(String[] args) {
        System.out.println(findHead().id);
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
