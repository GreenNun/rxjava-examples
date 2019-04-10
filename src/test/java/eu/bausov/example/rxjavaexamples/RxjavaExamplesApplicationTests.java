package eu.bausov.example.rxjavaexamples;

import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;


@SuppressWarnings("all")
@RunWith(SpringRunner.class)
@SpringBootTest
public class RxjavaExamplesApplicationTests {

    @Test // 25
    public void sync() {
        Observable.create(s -> {
            s.onNext("Hello World!");
            s.onComplete();
        }).subscribe(hello -> System.out.println(hello));
    }

    @Test // 26
    public void cache() {
        final String SOME_KEY = "a";
        final HashMap<String, String> cache = new HashMap<>();
        cache.put(SOME_KEY, "Hello World!");

        Observable.create(s -> { // is not needed to be acync if takes from cache, fast operation
            s.onNext(cache.get(SOME_KEY));
            s.onComplete();
        }).subscribe(value -> System.out.println(value));
    }

    @Test
    public void sync2() {
        Observable<Integer> o = Observable.create(s -> {
            s.onNext(1);
            s.onNext(2);
            s.onNext(3);
            s.onComplete();
        });

        o.map(i -> "Number " + i)
                .subscribe(s -> System.out.println(s));
    }

    @Test
    public void merge() {
        Observable.create(s -> {
            new Thread(() -> {
                s.onNext(1);
                s.onNext(2);
                s.onNext(3);
                s.onComplete();
            }).start();
        }).subscribe(s -> System.out.println(s));

        // don't do that
        Observable.create(s -> {
            new Thread(() -> {
                s.onNext(1);
                s.onNext(2);
            }).start();
            new Thread(() -> {
                s.onNext(3);
                s.onNext(4);
            }).start();
        }).subscribe(s -> System.out.println(s));

        // correct
        final Observable<Object> a = Observable.create(s -> {
            new Thread(() -> {
                s.onNext(1);
                s.onNext(2);
            }).start();
        });

        final Observable<Object> b = Observable.create(s -> {
            new Thread(() -> {
                s.onNext(3);
                s.onNext(4);
            }).start();
        });

        Observable.merge(a, b)
                .subscribe(s -> System.out.println(s));
    }
//    @Test
//    public void test() {
//    }
//    @Test
//    public void test() {
//    }
//    @Test
//    public void test() {
//    }
//    @Test
//    public void test() {
//    }
//    @Test
//    public void test() {
//    }
//    @Test
//    public void test() {
//    }
//    @Test
//    public void test() {
//    }
//    @Test
//    public void test() {
//    }

}
