package threads;

import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Task12<K, V> {

    private HashMap<K, V> map = new HashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public void put(K key, V value){
        lock.writeLock().lock();
        map.put(key, value);
        lock.writeLock().unlock();
    }

    public V get(K key){
        lock.readLock().lock();
        V val = map.get(key);
        lock.readLock().unlock();
        return val;

    }

    public void remove(K key){
        lock.writeLock().lock();
        map.remove(key);
        lock.writeLock().lock();
    }
}
