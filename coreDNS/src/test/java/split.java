public class split {

    public static void main(String[] args) {
        String str = "a.dnstest.moliplayer.com.    IN  A   118.190.26.52";
        String[] list = str.split("\\s+");
        for (String s : list) {
            System.out.println(s);
        }
    }
}
