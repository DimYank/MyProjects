package threads;

import org.junit.jupiter.api.Test;

public class TestThreadLessonOne {

    private Task1and2 basicThreads = new Task1and2();
    private Task3 threeThreads = new Task3();
    private Task4 arrayThreads = new Task4();
    private Task5 arrayThreads2 = new Task5();
    private Task6 synchronizedList = new Task6();
    private Task7 pingPong = new Task7();
    private Task8 readerWriter = new Task8();

    @Test
    void testPrintProps(){
        basicThreads.printProperties();
    }

    @Test
    void testCreateThread(){
        basicThreads.createThread();
    }

    @Test
    void testThreeThreads(){
        threeThreads.createThreeThreads();
    }

    @Test
    void testArrayThreads(){
        arrayThreads.startArrayOperations();
    }
    @Test
    void testArrayThreadsTwo(){
        arrayThreads2.startArrayOperations();
    }
    @Test
    void testSyncList(){
        synchronizedList.start();
    }
    @Test
    void testPingPong(){
        pingPong.start();
    }
    @Test
    void testReaderWriter(){
        readerWriter.start();
    }
}
