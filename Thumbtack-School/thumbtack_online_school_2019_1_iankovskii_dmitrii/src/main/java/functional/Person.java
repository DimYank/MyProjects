package functional;

public class Person {
    private String firstName;
    private int age;

    public Person(String firstName){
        this.firstName= firstName;
    }

    public Person(String firstName, int age){
        this(firstName);
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getAge() {
        return age;
    }
}
