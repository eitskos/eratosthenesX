/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.itskos.eratoshenesx;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author evangelos
 */
public class EratoshenesX {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException,
                                                  ClassNotFoundException {

//        convertToNewFormat();

        for (int i = 0; i < 1000; i++) {
            discoverNextPrimes(70000000);
            System.gc();
            System.gc();
            System.gc();
        }
    }

    static void discoverNextPrimes(long nextNPrimes) throws IOException,
                                                            ClassNotFoundException {
        List<Prime> primes = new LinkedList<>();
        
        long initialNumber = 2;
        boolean resumed = false;

        long started = Calendar.getInstance().getTimeInMillis();
        System.out.println("Loading...");
        File resumeFile = new File("resume.new.dat");
        if (resumeFile.exists()) {
            try (DataInputStream is = new DataInputStream(
                    new FileInputStream(
                    resumeFile))) {
                initialNumber = is.readLong();
                long total = is.readLong();
//                List<Prime> resumePrimes = (ArrayList<Prime>) is.readObject();
                while (is.available() > 0) {
                    primes.add(new Prime(is.readLong(), is.readLong()));
                }
                resumed = true;
            }
        }
        System.out.println("Loaded in " + ((Calendar.getInstance().
                                            getTimeInMillis() - started) /
                                           1000.0) + " sec.");
        System.out.println("Loaded primes: " + primes.size());
        started = Calendar.getInstance().getTimeInMillis();
        
        Map<Long, List<Prime>> nextMultStore = new HashMap<>((int) ((primes.size()+50) *1.10));


//        primes.add(new Prime(1)); // 1 is a prime but not usefulle for our
        // algorithm. We will print it but not use it.
        if (!resumed) {
            primes.add(new Prime(2));
            initialNumber = 2;
        }

         System.out.println("Initializing Store...");
        for (Prime p : primes) {
            add(nextMultStore, p);
        }
        System.out.println("Store Initialized");

        long lastPrint = 0;
        long num;
        for (num = initialNumber + 1; num <= (initialNumber + 1) + nextNPrimes;
             num++) {
            boolean isPrime = true;

            List<Prime> multList = nextMultStore.get(num);
            if (multList != null && !multList.isEmpty()) {
                multList = new ArrayList<>(multList);
                for (Prime p : multList) {
                    if (num == p.getNextProduct()) {
                        nextMultStore.get(p.getNextProduct()).remove(p);
                        if (nextMultStore.get(p.getNextProduct()).isEmpty()) {
                            nextMultStore.remove(p.getNextProduct());
                        }
                        p.moveNextProduct();
                        add(nextMultStore, p);
                        isPrime = false;
                    }
                }
            }

            if (isPrime) {
                Prime p;
                primes.add(p = new Prime(num));
                add(nextMultStore, p);
                long now = System.currentTimeMillis();
                if (now - lastPrint > 5 * 1000) {
                    System.out.println("New prime: " + num + " Total Primes: " +
                                       (primes.size() + 1) + " Primes ratio:" +
                                       ((double) (primes.size() + 1) / num));
                    lastPrint = now;
                    System.gc();
                    System.gc();
                    System.gc();
                }
            }
        }

        long ended = Calendar.getInstance().getTimeInMillis();

        long saveStart = Calendar.getInstance().getTimeInMillis();
        System.out.println("Saving...");
        try (DataOutputStream os = new DataOutputStream(
                new FileOutputStream(
                resumeFile, false))) {
            os.writeLong(num);
            os.writeLong(primes.size());
            for (Prime p : primes) {
                os.writeLong(p.getPrime());
                os.writeLong(p.getNextProduct());
            }
//            os.writeObject(primes);
        }
        System.out.println("Saved in " + ((Calendar.getInstance().
                                           getTimeInMillis() - saveStart) /
                                          1000.0) + " sec.");

        primes.add(0, new Prime(1));


//
//        for (Prime p : primes) {
//            System.out.println(p.getPrime() + "\t" + p.getNextProduct());
//        }
        System.out.println("Total primes: " + primes.size());
        System.out.println("Total primes ratio: " + ((double) primes.size() /
                                                     num));
        System.out.println("Total time in secords: " + ((ended - started) /
                                                        1000.0));
    }

    private static void add(
            Map<Long, List<Prime>> nextMultStore, Prime p) {
        if (!nextMultStore.containsKey(p.getNextProduct())) {
            nextMultStore.put(p.getNextProduct(), new LinkedList<Prime>());
        }
        nextMultStore.get(p.getNextProduct()).add(p);
    }

    private static void convertToNewFormat() throws IOException,
                                                    ClassNotFoundException {
        File resumeFile = new File("resume.data");
        File resumeFileNew = new File("resume.new.dat");
        long initialNumber = 0;
        List<Prime> primes = new ArrayList<>();
        if (resumeFile.exists()) {
            try (ObjectInputStream is = new ObjectInputStream(
                    new FileInputStream(
                    resumeFile))) {
                initialNumber = is.readLong();
                List<Prime> resumePrimes = (ArrayList<Prime>) is.readObject();
                primes.addAll(resumePrimes);

            }
        }

        try (DataOutputStream os = new DataOutputStream(new FileOutputStream(
                resumeFileNew, false))) {
            os.writeLong(initialNumber);
            os.writeLong(primes.size());
            for (Prime p : primes) {
                os.writeLong(p.getPrime());
                os.writeLong(p.getNextProduct());
            }
        }


    }
}
