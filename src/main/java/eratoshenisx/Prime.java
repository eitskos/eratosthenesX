/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eratoshenisx;

import java.io.Serializable;

/**
 *
 * @author evangelos
 */
public class Prime implements Serializable {

    private final long prime;
    private long nextProduct;

    public Prime(long prime) {
        this.prime = prime;
        nextProduct = prime + prime;
    }

    Prime(long prime, long nextProduct) {
        this.prime = prime;
        this.nextProduct = nextProduct;
    }

    /**
     * @return the prime
     */
    public long getPrime() {
        return prime;
    }

    /**
     * @return the nextProduct
     */
    public long getNextProduct() {
        return nextProduct;
    }

    public void moveNextProduct() {
        nextProduct += prime;
    }
}
