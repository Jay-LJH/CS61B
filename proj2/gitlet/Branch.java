package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Branch implements Serializable {
    public Branch(String name, String id) {
        if (name == "master") {
            head = true;
        }
        this.name = name;
        this.id = id;
    }
    public static void createBranch(String name, String id){
        File f = join(BRRANCH_DIR, name);
        boolean successed;
        try {
            successed = f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!successed) {
            message("A branch with that name already exists.");
            System.exit(0);
        }
        else
        {
            Branch b=new Branch(name,id);
            b.save();
        }
    }
    public void save(){
        File f = join(BRRANCH_DIR, name);
        writeObject(f, this);
    }
    @Override
    public String toString(){
        return name+" at "+id+" head:"+head;
    }
    public String name;
    public String id ;
    public boolean head = false;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File REF_DIR = join(GITLET_DIR, "refs");
    public static final File BRRANCH_DIR = join(REF_DIR, "branches");
}