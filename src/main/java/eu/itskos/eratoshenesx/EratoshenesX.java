/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.itskos.eratoshenesx;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author evangelos
 */
public class EratoshenesX {

    private final static String RESUME_FILE_NAME = "resume.new.dat";

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        final PrimeNumbersStore datastore = initDatastore(RESUME_FILE_NAME);

        for (int i = 0; i < 1000; i++) {
            final long started = Calendar.getInstance().getTimeInMillis();
            discoverNextPrimes(datastore, 700000);
            final long ended = Calendar.getInstance().getTimeInMillis();
            persistDatastore(datastore, RESUME_FILE_NAME);
            System.gc();
            System.gc();
            System.gc();

            Logger.getLogger(EratoshenesX.class.getName()).log(Level.INFO, "Total primes: {0}", datastore.size());
            Logger.getLogger(EratoshenesX.class.getName()).log(Level.INFO, "Total primes ratio: {0}", ((double) datastore.size() / datastore.lastCheckedNumber()));
            Logger.getLogger(EratoshenesX.class.getName()).log(Level.INFO, "Total time in secords: {0}", ((ended - started) / 1000.0));
        }
    }

    static void discoverNextPrimes(PrimeNumbersStore datastore, long nextNPrimes) throws IOException,
            ClassNotFoundException {

        final long initialNumber = datastore.lastCheckedNumber();

        long lastPrint = 0;
        long num;
        for (num = initialNumber + 1; num <= (initialNumber + 1) + nextNPrimes; num++) {

            if (datastore.isPrime(num)) {
                datastore.add(new Prime(num));
                long now = System.currentTimeMillis();
                if (now - lastPrint > 5 * 1000) {
                    System.out.println("New prime: " + num + " Total Primes: "
                            + (datastore.size() + 1) + " Primes ratio:"
                            + ((double) (datastore.size() + 1) / num));
                    lastPrint = now;
                    System.gc();
                    System.gc();
                    System.gc();
                }
            } else {
                datastore.advanceToNextProduct(num);
            }
        }
    }

    private static PrimeNumbersStore initDatastore(String resumeFileName) throws FileNotFoundException, IOException {
        Logger.getLogger(EratoshenesX.class.getName()).log(Level.INFO, "Initializing datastore.");
        final PrimeNumbersStore datastore = new PrimeNumbersStore();

        final File resumeFile = new File(resumeFileName);
        if (resumeFile.exists()) {
            long started = Calendar.getInstance().getTimeInMillis();
            Logger.getLogger(EratoshenesX.class.getName()).log(Level.INFO, "Found resume file [{0}]. Loading...", resumeFileName);
            try (DataInputStream is = new DataInputStream(
                    new FileInputStream(resumeFile))) {
                final long initialNumber = is.readLong();
                is.readLong(); // Total number of primes (we don't need this number since it is computed.
                while (is.available() > 0) {
                    datastore.addNoCache(new Prime(is.readLong(), is.readLong()));
                }
                datastore.initializeCache(initialNumber);
            }
            final double duration = ((Calendar.getInstance().getTimeInMillis() - started) / 1000.0);
            Logger.getLogger(EratoshenesX.class.getName())
                    .log(Level.INFO, "Loaded {0} primes in {1} seconds.",
                            new Object[]{datastore.size(), duration});
        } else {
            datastore.addNoCache(new Prime(2));
            datastore.initializeCache(2);
        }
        return datastore;
    }

    private static void persistDatastore(PrimeNumbersStore datastore, String resumeFileName) throws FileNotFoundException, IOException {
        final File resumeFile = new File(resumeFileName);
        final long saveStart = Calendar.getInstance().getTimeInMillis();
        Logger.getLogger(EratoshenesX.class.getName())
                .log(Level.INFO, "Persisting datastore in resume file [{0}]", resumeFileName);
        try (DataOutputStream os = new DataOutputStream(
                new FileOutputStream(resumeFile, false))) {
            os.writeLong(datastore.lastCheckedNumber());
            os.writeLong(datastore.size());
            for (Prime p : datastore.primesList()) {
                os.writeLong(p.getPrime());
                os.writeLong(p.getNextProduct());
            }
        }
        final double duration = (Calendar.getInstance().getTimeInMillis() - saveStart) / 1000.0;
        Logger.getLogger(EratoshenesX.class.getName()).log(Level.INFO, "Persisted in {0}", duration);
    }

}
