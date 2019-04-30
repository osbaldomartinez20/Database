package sfsu;

import java.util.*;
import java.util.concurrent.*;

public class Database {
    //the number of semaphores for the get method
    public static final int SEMAPHORE_NUM = 10;
    //the 'database' of the project
    private Hashtable<String, String> mesa;
    //declare the semaphore
    private Semaphore sem;

    //initializes the hash table database with initialSize possible entries
    public Database(int initialSize) {
        this.mesa = new Hashtable<>(initialSize);
        this.sem = new Semaphore(SEMAPHORE_NUM);
    }

    //only one edit at the time to table
    //key is used to place the value in database
    public synchronized void put(String key, String value) {
        if(!(this.mesa.containsKey(key))) {
            this.mesa.put(key, value);
        }
    }

    //only some at the time can read based on SEMAPHORE_NUM
    //With the key it is possible to find the value related to the key.
    public String get(String key) {
        this.sem.tryAcquire();
        String temp = this.mesa.get(key);
        this.sem.release();
        return temp;
    }

    public Hashtable<String, String> getTable() {
        return this.mesa;
    }

    //only one delete at the time
    //With the key it is possible to find the value related to the key and delete it.
    public synchronized void delete(String key) {this.mesa.remove(key);}
}
