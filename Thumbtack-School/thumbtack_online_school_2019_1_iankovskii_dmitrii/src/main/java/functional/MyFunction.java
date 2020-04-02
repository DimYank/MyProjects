package functional;

@FunctionalInterface
public interface MyFunction<T, K> {

    K apply(T arg);

    //K apply(T arg1, T arg2);
}
