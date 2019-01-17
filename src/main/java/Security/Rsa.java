package Security;

import Model.Database;

import java.math.BigInteger;
import java.util.Observable;
import java.util.Random;

public class Rsa extends Observable implements Runnable {
    
    protected BigInteger p;
    protected BigInteger q;
    protected BigInteger C;
    protected BigInteger N;
    protected BigInteger U;
    protected boolean keysGenerated = false;
    private String type = Database.FIRSTIME;
    private int bitLength = 2048;
    private Random r = new Random();

    public Rsa(BigInteger P, BigInteger Q) {

        this.p = P;
        this.q = Q;
    }

    public Rsa(String type){
        if(type ==null)
            return;
        this.type = type;
        p = BigInteger.probablePrime(bitLength/2, r);
        q = BigInteger.probablePrime(bitLength/2, r);
    }

    public Rsa() {
        p = BigInteger.probablePrime(bitLength/2, r);
        q = BigInteger.probablePrime(bitLength/2, r);
    }

    public Rsa (String C,String U,String N){
        this.C =  BigInteger.valueOf(Long.parseLong(C));
        this.U = BigInteger.valueOf(Long.parseLong(U));
        this.N = BigInteger.valueOf(Long.parseLong(N));
    }

    public void run() {
        this.N = p.multiply(q);
        BigInteger M = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        this.C = M.divide(BigInteger.valueOf(2));

        do {
            C = new BigInteger(M.bitLength(), r);
        }while (C.compareTo(BigInteger.ONE) <= 0 || C.compareTo(M) >= 0 || !C.gcd(M).equals(BigInteger.ONE));

//        while (M.gcd(C).compareTo(BigInteger.ONE) != 0) {
//            C = C.add(BigInteger.ONE);
//        }

        this.U = C.modInverse(M);
        keysGenerated = true;
        setChanged();
        notifyObservers(type);
    }

    public BigInteger getU() {
        return U;
    }

    public void setU(BigInteger u) {
        U = u;
    }

    public BigInteger getC() {
        return C;
    }

    public BigInteger getN() {
        return N;
    }

    public void setC(BigInteger c) {
        C = c;
    }

    public void setN(BigInteger n) {
        N = n;
    }

    public String getPublicKey(){
        return this.C + " " +this.N;
    }

    public String getPrivateKey(){
        return this.U + " " + this.N;
    }



    // Encrypt message
    public BigInteger[] encrypt(String message) {

        BigInteger[] tab = new BigInteger[message.length()];

        for (int i = 0; i < message.length(); i++) {
            tab[i] = BigInteger.valueOf((int) (message.charAt(i))).modPow(C, N);
        }

        return tab;

    }

    // Decrypt message
    public String decrypt(BigInteger[] message) {

        StringBuilder str = new StringBuilder();

        for (int i = 0; i < message.length; i++) {
            str.append(Character.toString((char) message[i].modPow(U, N).intValue()));
        }

        return str.toString();
    }

    public boolean isKeysGenerated() {
        return keysGenerated;
    }
}