

import java.net.*;
import java.util.*;

public class ShowInterface {
    

    public static void main ( String arg[] ) {

        try {
            NetworkInterface ifc = 
                NetworkInterface.getByName ("eth1");
        
            System.out.println ( "indx: " + ifc.getIndex() );
            System.out.println ( "Name: " + ifc.getName() );

            byte[] hwb = ifc.getHardwareAddress();
            System.out.println(Arrays.toString(hwb));
            for ( byte b : hwb ) {
                int sh = (b >= 0)? b : b+256;
                System.out.print(Integer.toHexString(sh) + " ");
            }
            System.out.print("\n");
        
            Enumeration<NetworkInterface> subifs = 
                ifc.getSubInterfaces();

            for (; subifs.hasMoreElements(); ) {
                NetworkInterface subif = subifs.nextElement();
                System.out.println("name:"  + subif.getName());

                List<InterfaceAddress> subifAddr = 
                    subif.getInterfaceAddresses();

                InterfaceAddress ifaddr = subifAddr.get(0) ;
                System.out.println("addr:" + 
                                   Arrays.toString(ifaddr.getAddress()
                                                   .getAddress()));
                
            }
        } catch ( SocketException e ) {
            e.printStackTrace();
        }
    }
}