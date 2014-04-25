package com.tailf.test.maapi1;


import gnu.getopt.Getopt;
import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.maapi.ResultType;
import com.tailf.maapi.ResultTypeString;
import com.tailf.maapi.ResultTypeKeyPath;
import com.tailf.maapi.ResultTypeKeyPathValue;
import com.tailf.maapi.ResultTypeTag;
import com.tailf.conf.ConfException;
import com.tailf.conf.ConfValue;
import com.tailf.conf.ConfPath;
import com.tailf.conf.ConfObject;
import com.tailf.maapi.QueryResult;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.tailf.conf.Conf;
import com.tailf.maapi.MaapiException;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class Rq {

    private static Logger LOGGER = Logger.getLogger(Rq.class);
    int opt_chunk_size = 100;
    int opt_initial_offset = 1;
    int opt_number_of_times = 1;
    int opt_reset_after_first = 0;
    String opt_initial_ctxt;
    int port = Conf.PORT;
    int th = -1;
    InetAddress addr;
    Maapi maapi;
    QueryResult rs;


    public Rq(InetAddress addr,int chunksize, int offset, int numtimes,
              String ctxt){
        this.addr = addr;
        this.opt_chunk_size = chunksize;
        this.opt_initial_offset = offset;
        this.opt_number_of_times = numtimes;
        this.opt_initial_ctxt = ctxt;
    }

    public void maapiSock() {
        try {
            Socket s =
                new Socket(addr, port);
            maapi = new Maapi(s);

        } catch(IOException e) {
            LOGGER.error("",e);

        } catch(ConfException e) {
                 LOGGER.error("",e);
        }
    }

    public void reset() {
        try {
            rs.reset();
        } catch(ConfException e) {
            LOGGER.error("",e);
        } catch(IOException e){
            LOGGER.error("",e);
        }
    }

    public void stop() {
        try{
            rs.stop();
        }catch(ConfException e){
            LOGGER.error("",e);
        }catch(IOException e){
            LOGGER.error("",e);
        }
    }


    public QueryResult queryStart(String query,
                                  List<String> selects,
                                  Class<? extends ResultType> type) {
        try {
            rs =
                maapi.queryStart(th,
                                 query,
                                 this.opt_initial_ctxt,
                                 this.opt_chunk_size,
                                 this.opt_initial_offset,
                                 selects,
                                 type);
        } catch(IOException e) {
            LOGGER.error("",e);
        } catch(ConfException e){
            LOGGER.error("",e);
        }
        return rs;
    }

    public void maapiStartTrans() {
        //int th = -1;

        try {
            th = maapi.startTrans(Conf.DB_RUNNING,
                                  Conf.MODE_READ_WRITE);
        } catch(MaapiException e){
            LOGGER.error("Could not create a transaction" , e);
        } catch(ConfException e){
            LOGGER.info("",e);
        } catch(IOException e){
            LOGGER.info("",e);
        }
    }


    public void maapiUsess() {
        try {
            maapi.startUserSession("admin",
                                   InetAddress.getLocalHost(),
                                   "maapi",
                                   new String[] {"admin"},
                                   MaapiUserSessionFlag.PROTO_TCP);

        } catch(MaapiException e) {
            LOGGER.error("Could not create a session",e);
        } catch(ConfException e) {
            LOGGER.error("Could not create a session",e);
        } catch(UnknownHostException e) {
            LOGGER.error("",e);
        } catch(IOException e) {
            LOGGER.error("",e);
        }
    }



    public static void main(String[] argv) throws UnknownHostException {
        System.out.println("ARGV:" + Arrays.toString(argv));

        String query = argv[0];
        String[] sel = argv[1].split(",");
        List<String> selects = new ArrayList<String>();

        for(String select: sel)  selects.add(select);


        String[] argv2 = new String[argv.length-2];
        System.arraycopy(argv,2,argv2,0,argv2.length);
        System.out.println("ARGV2:" + Arrays.toString(argv2));

        InetAddress addr = InetAddress.getByName("127.0.0.1");
        Class<? extends ResultType> opt_res = ResultTypeString.class;
        int opt_chunk_size = 100;
        int opt_initial_offset = 1;
        int opt_number_of_times = 1;
        int opt_reset_after_first = 0;
        int debuglevel = 0;
        String opt_ctxt_node = null;
        System.out.println("arg.length:" + argv.length);

        //Getopt g = new Getopt("rq", argv, "ab:c::d");
        int o;
        //xString arg;
        Getopt g = new Getopt("rq",argv2, "dp:r:c:o:x:n:t");
        while ((o = g.getopt()) != -1) {
            switch (o) {
            case 'd':
                debuglevel++;
                break;
            case 'p':
                addr = InetAddress.getByName(g.getOptarg());
                break;
            case 'r':
                String arg = g.getOptarg();
                char r = arg.charAt(0);
                switch (r) {
                case 't': opt_res = ResultTypeTag.class; break;
                case 'v': opt_res = ResultTypeKeyPathValue.class; break;
                case 'p': opt_res = ResultTypeKeyPath.class; break;
                case 's':
                default:
                    opt_res = ResultTypeString.class;
                }
                break;
            case 'c':
                opt_chunk_size = Integer.parseInt(g.getOptarg());
                break;
            case 'o':
                opt_initial_offset = Integer.parseInt(g.getOptarg());
                break;
            case 'x':
                opt_ctxt_node = g.getOptarg();
                break;
            case 'n':
                opt_number_of_times = Integer.parseInt(g.getOptarg());
                break;
            case 't':
                opt_reset_after_first = 1;
                break;
            default:
                System.out.println("Eh?\n");
                System.exit(1);
            }
        }

        Logger loggerMaapi = Logger.getLogger("com.tailf.maapi");
        Logger loggerConf  = Logger.getLogger("com.tailf.conf");

        switch(debuglevel){
        case 1:  loggerMaapi.setLevel(Level.INFO);   break;
        case 2: loggerMaapi.setLevel(Level.DEBUG); break;
        case 3:
            loggerMaapi.setLevel(Level.DEBUG);
            loggerConf.setLevel(Level.DEBUG);
            break;

        }

        Rq rq = new Rq(addr,
                       opt_chunk_size,
                       opt_initial_offset,opt_number_of_times,
                       opt_ctxt_node);
        rq.maapiSock();

        rq.maapiUsess();
        rq.maapiStartTrans();

        QueryResult<? extends ResultTypeString> qR1 =
            rq.queryStart(query,selects,opt_res);

        for(int n = 0; n < opt_number_of_times; n++){
            if (n > 0) {
                rq.reset();
            }
            System.out.println("n:" + n);
            for(QueryResult.Entry entry : qR1){
                List<? extends ResultType> rsValue = entry.value();

                for(ResultType typ: rsValue){
                    if(opt_res == ResultTypeTag.class){
                        ResultTypeTag tag =  (ResultTypeTag)typ;

                    }else if(opt_res == ResultTypeKeyPathValue.class){
                        ResultTypeKeyPathValue kpv =
                            (ResultTypeKeyPathValue)typ;
                        System.out.print(new ConfPath(kpv.keyPath())
                                         .toString());
                        System.out.println(" " + kpv.confValue());

                }else if(opt_res == ResultTypeKeyPath.class){
                    ResultTypeKeyPath kpv =
                        (ResultTypeKeyPath)typ;
                    System.out.println(new ConfPath(kpv.keyPath())
                                       .toString());

                    }else if(opt_res == ResultTypeTag.class){
                    ResultTypeTag rst =
                        (ResultTypeTag)typ;
                    System.out.println(rst.tag());
                }
            }
            }
        }
        rq.stop();

        //   //./rq  -c 4 -o 2 -x "/" -r v -d -d -d "/sys/host" "name" "number"
    }

}