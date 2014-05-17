
import java.net.NetworkInterface;

public class GArpSend {

    static {
        System.loadLibrary("garp");
    }

    public native void reply ( NetworkInterface iface ) throws 
        GArpSendException;

    public static void main (String arg[] ) {
        try {
        GArpSend garp = 
            new GArpSend ();
        garp.reply(NetworkInterface.getByName("eth1"));
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}