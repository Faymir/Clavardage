package Security;

import java.math.BigInteger;
import java.util.Base64;
import java.util.Vector;

public class Cryptography extends Rsa{

    private BigInteger encryptN;
    private BigInteger encryptC;
    private BigInteger decryptN;
    private BigInteger decryptU;

    public Cryptography(String yourPrivateKey, String hisPublicKey) {
        super(null);
        encryptC =  BigInteger.valueOf(Long.parseLong(hisPublicKey.split(" ")[0]));
        encryptN = BigInteger.valueOf(Long.parseLong(hisPublicKey.split(" ")[1]));
        decryptU = BigInteger.valueOf(Long.parseLong(yourPrivateKey.split(" ")[0]));
        decryptN = BigInteger.valueOf(Long.parseLong(yourPrivateKey.split(" ")[1]));
    }

    public String decryptFromString(String message) {
        String [] list = message.split(" ");
        StringBuilder str = new StringBuilder();
        BigInteger bigInteger;
        for (String s : list) {
            bigInteger = new BigInteger(Base64.getDecoder().decode(s));
            str.append((char) bigInteger.modPow(decryptU, decryptN).intValue());
        }

        return str.toString();
    }

    public String encryptToString(String message) throws NullPointerException{
        Vector<String> tab = new Vector<>();
        if(encryptC == null || encryptN == null)
            throw new NullPointerException("You didn't provide a public key so you can't use this method");

        for (int i = 0; i < message.length(); i++) {
            tab.add(Base64.getEncoder().encodeToString(BigInteger.valueOf((int) (message.charAt(i))).modPow(encryptC, encryptN).toByteArray()));
        }
        StringBuilder sb = new StringBuilder();
        tab.forEach(a -> {
            if(sb.length() != 0)
                sb.append(" ").append(a);
            else
                sb.append(a);
        });
        return sb.toString();
    }
}
