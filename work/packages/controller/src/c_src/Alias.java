

public class Alias {
    static {
        System.out.println (System.getProperty("java.library.path"));
        System.loadLibrary ("alias");
    }

    public native void addAlias (String interfaceName, 
                                  String ipAddress ) throws AliasException;

    public native void removeAlias ( String interfaceName) 
        throws AliasException;


    public static void main (String arg[] ) {
        try {
        if (arg[0].equals ( "add") ) {
            Alias alias = new Alias();
            alias.addAlias(arg[1],arg[2]);
        }
        else if (arg[0].equals("del")) {
            Alias alias = new Alias();
            alias.removeAlias ( arg[1] );
        } else {
            System.err.println("heh?");
        }
        } catch ( AliasException e ) {
            e.printStackTrace();
        }
    }
}