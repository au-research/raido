package raido.util;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

import static java.net.InetAddress.getByName;

public class NetUtil {

  public static boolean isLocalhostPortAvailable(int port) {
    try ( ServerSocket serverSocket = new ServerSocket()) {
      // setReuseAddress(false) is required only on OSX,
      // otherwise the code will not work correctly on that platform
      serverSocket.setReuseAddress(false);
      serverSocket.bind(new InetSocketAddress(getByName("localhost"), port), 1);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

}
