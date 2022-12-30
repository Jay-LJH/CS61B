package gitlet;

import java.io.File;
import java.util.Date;

import static gitlet.Utils.join;
import static gitlet.Utils.message;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author TODO
 */
public class Main {
    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            message("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                if (args.length != 1) {
                    message("Incorrect operands.");
                }
                Repository.init();
                break;
            case "add":
                validateNumArgs("add", args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                validateNumArgs("commit", args, 2);
                if(args[1].equals("")){
                    message("Please enter a commit message.");
                    System.exit(0);
                }
                Commit c = new Commit(new Date(), args[1]);
                break;
            case "log":
                validateNumArgs("log", args, 1);
                Repository.log();
                break;
            case "global-log":
                validateNumArgs("global-log", args, 1);
                Repository.global_log();
                break;
            case "find":
                validateNumArgs("find", args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                validateNumArgs("status", args, 1);
                Repository.status();
                break;
            case "rm":
                validateNumArgs("rm", args, 2);
                Repository.rm(args[1]);
                break;
            case "branch":
                validateNumArgs("branch", args, 2);
                Branch.createBranch(args[1], Repository.findHead().id);
                break;
            case "checkout":
                if (!GITLET_DIR.exists()) {
                    message("Not in an initialized Gitlet directory.");
                }
                if (args.length == 3) {
                    if(!args[1].equals("--")){
                        message("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.checkoutfile(args[2]);
                } else if(args.length == 4) {
                    if(!args[2].equals("--")){
                        message("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.checkoutfile(args[1], args[3]);
                }
                else if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                } else {
                    message("Incorrect operands.");
                }
                break;
            case "rm-branch":
                validateNumArgs("rm-branch", args, 2);
                Repository.rm_branch(args[1]);
                break;
            case "reset":
                validateNumArgs("reset", args, 2);
                Repository.reset(args[1]);
                break;
            case "merge":
                validateNumArgs("merge", args, 2);
                Repository.merge(args[1]);
                break;
            default:
                message("No command with that name exists.");
        }
    }

    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        if (args.length != n) {
            message("Incorrect operands.");
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
}
