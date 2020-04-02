package functional;

import java.util.*;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Streams {

    public static IntStream transform(IntStream stream, IntUnaryOperator op){
        return stream.map(op);
    }

    public static IntStream transformParallel(IntStream stream, IntUnaryOperator op){
        return stream.parallel().map(op);
    }

    public static List<Person> sortPersonList(List<Person> personList){
        Stream<Person> personStream = personList.stream();
        return personStream.filter(p-> p.getAge() >= 30).sorted(Comparator.comparingInt(c -> c.getFirstName().length())).collect(Collectors.toList());
    }

    public static Map<String, Long> setOfUniqueNames(List<Person> personList){
        /*Stream<Person> personStream = personList.stream();
        Map<String, Long> map = personStream.filter(p-> p.getAge() >= 30)
                .collect(Collectors.groupingBy(Person::getFirstName ,Collectors.counting()));

        List<Map.Entry<String, Long>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Map.Entry.comparingByValue());

        List<String> names = new ArrayList<>();
        for (Map.Entry<String, Long> entry : entries){
            names.add(entry.getKey());
        }
        return names;*/

        return personList.stream().filter(p-> p.getAge() >= 30)
                .collect(Collectors.groupingBy(Person::getFirstName ,Collectors.counting()));
    }

    public static int sum(List<Integer> list){
        return list.stream().reduce((o1,o2)-> o1+o2).get();
    }

    public static int product(List<Integer> list){
        return list.stream().reduce((o1,o2)-> o1*o2).get();
    }
}
