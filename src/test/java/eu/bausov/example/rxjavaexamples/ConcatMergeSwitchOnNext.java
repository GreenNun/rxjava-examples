package eu.bausov.example.rxjavaexamples;

import com.sun.tools.javac.util.Pair;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConcatMergeSwitchOnNext {
    private final Observable<String> alice = speak("Вечером на лавке Курнули мы с тобой Я поплелся провожать Через мост тебя домой", 110L);
    private final Observable<String> bob = speak("Птицы громко пели А ещё бурчал живот Утром бабка Афанася Кислый мне дала компот", 100L);
    private final Observable<String> jane = speak("И крепился, и держался И хотелось пердануть Слева речка, справа пруд Мне домой не дотянуть", 90L);

    @Test
    public void concat() throws InterruptedException {
        final Disposable subscribe = Observable.concat(
                alice.map(w -> "Alice: " + w),
                bob.map(w -> "Bob: " + w),
                jane.map(w -> "Jane: " + w)
        ).subscribe(System.out::println);

        TimeUnit.SECONDS.sleep(25);
    }

    @Test
    public void merge() throws InterruptedException {
        final Disposable subscribe = Observable.merge(
                alice.map(w -> "Alice: " + w),
                bob.map(w -> "Bob: " + w),
                jane.map(w -> "Jane: " + w)
        ).subscribe(System.out::println);

        TimeUnit.SECONDS.sleep(25);
    }

    @Test
    public void switchOnNext() throws InterruptedException {
        final Random random = new Random();

        final Observable<Observable<String>> quotes = Observable.just(
                alice.map(w -> "Alice: " + w),
                bob.map(w -> "Bob: " + w),
                jane.map(w -> "Jane: " + w))
                .flatMap(innerObj -> Observable.just(innerObj)
                        .delay(random.nextInt(5), TimeUnit.SECONDS));

        final Disposable subscribe = Observable.switchOnNext(quotes)
                .subscribe(System.out::println);

        TimeUnit.SECONDS.sleep(25);
    }

    private Observable<String> speak(String quotes, long ms) {
        final String[] tokens = quotes.replaceAll("[:,]", "").split(" ");
        final Observable<String> words = Observable.fromArray(tokens);
        final Observable<Long> absoluteDelay = words
                .map(String::length)
                .map(len -> len * ms)
                .scan(Long::sum);

        return words
                .zipWith(absoluteDelay.startWith(0L), Pair::of)
                .flatMap(pair -> Observable.just(pair.fst)
                        .delay(pair.snd, TimeUnit.MILLISECONDS));
    }

}
