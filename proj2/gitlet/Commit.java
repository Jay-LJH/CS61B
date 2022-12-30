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

        List<File> files = new ArrayList<>();
        files.addAll(Arrays.asList(TEMP_DIR.listFiles()));
        Boolean flag = files.size() == 0;
        Branch head = findHead();
        if (head.id.equals("0"))
            init(date, message, null, files);
        else {
            File[] tracks = join(OBJ_DIR, head.id).listFiles();
            for (File f : tracks) {
                if (f.getName().equals("info"))
                    continue;
                String name = f.getName();
                File cwd = join(CWD, name);
                if (!cwd.exists()) {
                    flag = false;
                } else if (Repository.fileaddress(cwd).equals(Repository.fileaddress(f))) {
                    files.add(f);
                } else {
                    files.add(cwd);
                    flag = false;
                }
            }
            if (flag) {
                message("No changes added to the commit.");
                System.exit(0);
            }
            init(date, message, null, files);
        }
    }

    public Commit(Date date, String message, String id, List<File> files) {
        init(date, message, id, files);
    }

    public void init(Date date, String message, String id, List<File> files) {
        this.date = date;
        this.message = message;
        this.parents = new ArrayList<>();
        Branch head = findHead();
        parents.add(head.id);
        if (id != null)
            parents.add(id);
        List<byte[]> tracks = new ArrayList<>();
        for (File f : files) {
            tracks.add(readContents(f));
        }

        this.id = sha1(date.toString(), parents.toString(), message, tracks.toString());
        join(OBJ_DIR, this.id).mkdir();
        for (File f : files) {
            String name = f.getName();
            File dst = join(OBJ_DIR, this.id, name);
            byte[] bytes = readContents(f);
            writeContents(dst, bytes);
        }
        File dstinfo = join(OBJ_DIR, this.id, "info");
        writeObject(dstinfo, this);
        for (File f : join(TEMP_DIR).listFiles()) {
            f.delete();
        }
        head.id = this.id;
        writeObject(join(BRRANCH_DIR, head.name), head);
    }

    @Override
    public String toString() {
        SimpleDateFormat DateFor = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
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

    /**
     * The message of this Commit.
     */
    private String message;
    private Date date;
    private String id;
    private List<String> parents;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File REF_DIR = join(GITLET_DIR, "refs");
    public static final File BRRANCH_DIR = join(REF_DIR, "branches");
    public static final File TEMP_DIR = join(GITLET_DIR, "temp");
    public static final File OBJ_DIR = join(GITLET_DIR, "objects");
    /* TODO: fill in the rest of this class. */
}
