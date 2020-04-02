package funcrional;

import functional.Person;
import functional.Streams;
import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class TestStreams {

    @Test
    public void testTransform(){
        IntStream stream = Streams.transform(IntStream.of(11, 22, 33, 44), o-> o/2);
        stream.forEach(System.out::println);
    }

    @Test
    public void testTransformParallel(){
        IntStream stream = Streams.transformParallel(IntStream.of(11, 22, 33, 44), o-> o/2);
        stream.forEach(System.out::println);
    }

    @Test
    public void testSortPersonList(){
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Ivan", 23));
        persons.add(new Person("Vladimir", 31));
        persons.add(new Person("Olga", 30));
        persons.add(new Person("Robert", 41));

        List<Person> expected = new ArrayList<>();
        expected.add(new Person("Olga", 30));
        expected.add(new Person("Robert", 41));
        expected.add(new Person("Vladimir", 31));

        List<Person> actual = Streams.sortPersonList(persons);

        assertEquals(expected.get(1).getFirstName(), actual.get(1).getFirstName());
    }

    @Test
    public void testSetOfUniqueName(){
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Ivan", 33));
        persons.add(new Person("Vladimir", 31));
        persons.add(new Person("Olga", 30));
        persons.add(new Person("Robert", 41));
        persons.add(new Person("Alex", 21));
        persons.add(new Person("Ivan", 41));
        persons.add(new Person("Robert", 43));
        persons.add(new Person("Robert", 34));

        Map<String, Long> expected = new HashMap<>();
        expected.put("Olga", 1L);
        expected.put("Vladimir", 1L);
        expected.put("Ivan", 2L);
        expected.put("Robert", 3L);

        assertEquals(expected, Streams.setOfUniqueNames(persons));
    }

    @Test
    public void testSum(){
        List<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(44);
        ints.add(323);
        ints.add(12);

        assertEquals(1 + 44 + 323 + 12, Streams.sum(ints));
    }

    @Test
    public void testProduct(){
        List<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(44);
        ints.add(323);
        ints.add(12);

        assertEquals(1 * 44 * 323 * 12, Streams.product(ints));
    }
}
