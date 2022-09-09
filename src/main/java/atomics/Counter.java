package atomics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
  // Language spec allows access to long & double variables
  // to be non-atomic unless they are labeled volatile
//  public static /*volatile*/ long count = 0;

  public static void main(String[] args) throws Throwable {
    final LongAccumulator count = new LongAccumulator((a, b) -> a + b, 0);
//    final LongAdder count = new LongAdder();
//    final AtomicLong count = new AtomicLong(0);
//    final Object rendezvous = new Object();
//    ReentrantLock lock = new ReentrantLock();
    Runnable r = () -> {
      for (int i = 0; i < 100_000; i++) {
//        synchronized (rendezvous) { // uncontended lock, pretty fast
//        lock.lock();
//        try {
//          count++;  // read modify write cycle
//        } finally {
//          lock.unlock();
//        }
//        count.incrementAndGet();
//        count.increment();
        count.accumulate(1);
      }

    };

    System.out.println("count before " + count.get());

    long start = System.nanoTime();

    List<Thread> lt = new ArrayList<>();
    for (int i = 0; i < 10_000; i++) {
      Thread t = new Thread(r);
      lt.add(t);
      t.start();
    }

    for (Thread t : lt) {
      t.join();
    }
    long time = System.nanoTime() - start;
//    System.out.println("count after " + count);
//    System.out.println("count after " + count.sum());
    System.out.println("count after " + count.get());
    System.out.println("time was " + (time / 1_000_000_000.0));
  }
}