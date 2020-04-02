package funcrional;

import functional.Functional;
import functional.Person;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class TestFunctional {

    @Test
    public void testSplit(){
        List<String> expected = new ArrayList();
        expected.add("one");
        expected.add("two");
        expected.add("three");
        assertEquals(expected, Functional.split("one two three", s->Arrays.asList(s.split(" "))));
    }

    @Test
    public void testCount(){
        List<String> test = new ArrayList();
        test.add("one");
        test.add("two");
        test.add("three");
        assertEquals(new Integer(3), Functional.count(test, c->c.size()));
    }

    @Test
    public void testMethodRef(){
        List<String> arr = new ArrayList();
        arr.add("one");
        arr.add("two");
        arr.add("three");
        assertEquals(arr, Functional.split("one two three", Functional::splitForRef));
        assertEquals(new Integer(3), Functional.count(arr, Functional::countForRef));
    }

    @Test
    public void testSplitAndCount(){
        assertEquals(new Integer(3), Functional.count.apply(Functional.split.apply("one two three")));
        assertEquals(new Integer(3), Functional.splitAndCountA("one two three"));
        assertEquals(new Integer(3), Functional.splitAndCountB("one two three"));
    }

    @Test
    public void testCreatePerson(){
        Person expected = new Person("Vasya");
        assertEquals(expected.getFirstName(), Functional.create("Vasya", Person::new).getFirstName());
    }

    @Test
    public void testMax(){
        assertEquals(4.2, Functional.max(4.2, 3, Math::max), 0);
    }

    @Test
    public void testGetCurrentDate(){
        assertEquals(new Date(System.currentTimeMillis()).getYear(),
                Functional.getCurrentDate(()->new Date(System.currentTimeMillis())).getYear());
    }

    @Test
    public void testIsEven(){
        assertTrue(Functional.isEven(22, p-> p % 2 == 0));
        assertFalse(Functional.isEven(23, p-> p % 2 == 0));
    }

    @Test
    public void testAreEqual(){
        assertEquals(new Integer(0), Functional.areEqual(22, 22, (o1,o2)-> o1 - o2 ));
        assertNotEquals(new Integer(1), Functional.areEqual(122, 22, (o1,o2)-> o1 / o2 ));
    }
}
