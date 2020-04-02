package threads;

import org.junit.Test;

public class TestLesson2 {
    private Task11 pingPong = new Task11();
    private Task10 arrayTheadsLock = new Task10();

    @Test
    public void testPingPong(){
        pingPong.start();
    }

    @Test
    public void testArrayThreads(){
        arrayTheadsLock.startArrayOperations();
    }
}
