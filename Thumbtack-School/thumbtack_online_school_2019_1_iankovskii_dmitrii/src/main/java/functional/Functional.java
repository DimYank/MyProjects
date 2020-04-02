package functional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.*;

public class Functional {

        //for lambda
        public static <T> List split(T str, MyFunction<T, List> splitter){
            return splitter.apply(str);
        }

        public static <T> Integer count(T list, MyFunction<T,Integer> counter){
            return counter.apply(list);
        }

        //for method-reference
        public static List<String> splitForRef(String str){
            return Arrays.asList(str.split(" "));
        }

        public static Integer countForRef(List<?> list){
            return list.size();
        }

        //andThen, compose
        public static Function<String, List> split = s -> Arrays.asList(s.split(" "));
        public static Function<List, Integer> count = a -> a.size();

        public static Integer splitAndCountA(String str){
            return split.andThen(count).apply(str);
        }
        public static Integer splitAndCountB(String str){
            return count.compose(split).apply(str);
        }
        //----------------


        public static Person create(String str, MyFunction<String, Person> creator){
            return creator.apply(str);
        }

        public static double max (double a, double b, BinaryOperator<Double> operator){
            return operator.apply(a, b);
        }

        public static Date getCurrentDate(Supplier<Date> supplier){
            return supplier.get();
        }

        public static Boolean isEven(Integer a, Predicate<Integer> predicate){
            return predicate.test(a);
        }

        public static Integer areEqual(Integer a, Integer b, BinaryOperator<Integer> operator){
            return operator.apply(a, b);
        }

}
