

public class AutoIfDown {
    private String vip;
    private String mask;
    private String ifc;

    AutoIfDown ( String vip , String mask, 
                 String ifc ) throws Exception {

        this.vip = vip;
        this.mask = mask;
        this.ifc = ifc;

        Process process = 
            Runtime.getRuntime().exec ("./autoifdown.sh " + vip + " " + 
                                       mask + " " + ifc + " 1");
        
        process.waitFor();
    }

    public static void main (String arg[] ) {
        try {
            AutoIfDown autoifdown = 
                new AutoIfDown ("192.168.60.33", "255.255.255.0",
                                "eth1");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}