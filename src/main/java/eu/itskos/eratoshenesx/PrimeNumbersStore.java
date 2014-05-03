/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.itskos.eratoshenesx;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author evangelos
 */
public class PrimeNumbersStore {

    private final List<Prime> primes;
    private Map<Long, List<Prime>> primeProductsCache;
    private long lastCheckedNumber;

    public PrimeNumbersStore() {
        primes = new LinkedList<>();
        primeProductsCache = null;
        lastCheckedNumber = 2;
    }

    void addNoCache(Prime prime) {
        primes.add(prime);
    }

    int size() {
        return primes.size();
    }

    void add(Prime prime) {
        addNoCache(prime);
        addToCahce(prime);
    }

    void initializeCache(final long lastCheckedNumber) {
        this.lastCheckedNumber = lastCheckedNumber;
        primeProductsCache = new HashMap<>((int) ((primes.size() + 50) * 1.10));
        for (Prime p : primes) {
            addToCahce(p);
        }
    }

    private void addToCahce(Prime p) {
        if (!primeProductsCache.containsKey(p.getNextProduct())) {
            primeProductsCache.put(p.getNextProduct(), new LinkedList<Prime>());
        }
        primeProductsCache.get(p.getNextProduct()).add(p);
    }

    boolean isPrime(long primeProdcut) {
        lastCheckedNumber = primeProdcut;
        return !primeProductsCache.containsKey(primeProdcut);
    }

    void advanceToNextProduct(long num) {
        final List<Prime> multList = primeProductsCache.remove(num);
        for (Prime p : multList) {
            p.moveNextProduct();
            addToCahce(p);
        }
    }

    Iterable<Prime> primesList() {
        return primes;
    }

    long lastCheckedNumber() {
        return lastCheckedNumber;
    }

}
