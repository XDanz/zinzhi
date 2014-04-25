/**
 * Copyright 2005 Tail-F Systems AB
 */

import com.tailf.conf.Conf;

public class DbServerCLI extends Thread {


    public void run() {

        boolean run = true;
        int dbname = Conf.DB_RUNNING;

        try {
            // read from stdin
            while (run) {
                System.out.print("> ");
                java.io.BufferedReader stdin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
                String line = stdin.readLine();

                // echo line
                System.out.println(line);

                // parse line
                if (line.startsWith("db ")) {
                    line = line.substring(3);
                    if (line.equals("running"))
                        dbname = Conf.DB_RUNNING;
                    else if (line.equals("candidate"))
                        dbname = Conf.DB_CANDIDATE;
                    else if (dbname == Conf.DB_RUNNING)
                        System.out.println("Using the running DB");
                    else
                        System.out.println("Using the candidate DB");
                }
                else if (line.equals("show")) {
                    DbServer.show(dbname);
                }
                else {
                    System.out.println("Unrecognized command");
                    System.out.println("Hint: ");
                    System.out.println("  running  -- switch to running DB");
                    System.out.println("  candidate  -- switch to candidate DB");
                    System.out.println("  show  -- show content of current DB");
                }

            }
        } catch (Exception e) {
            System.out.println(e); }
    }

}

