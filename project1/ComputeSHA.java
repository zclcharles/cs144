import java.io.*;
import java.security.*;
public class ComputeSHA {

    public static void main(String[] args) {
        try  {
            FileInputStream in = new FileInputStream(args[0]);
            MessageDigest md = MessageDigest.getInstance("SHA-1");


            byte[] dataBytes = new byte[1024];

            int nread = 0;
            while ((nread = in.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            byte[] mdbytes = md.digest();
            StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
          sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
         System.out.println(sb.toString());
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
