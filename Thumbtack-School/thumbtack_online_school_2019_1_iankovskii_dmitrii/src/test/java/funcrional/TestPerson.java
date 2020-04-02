package funcrional;

import functional.PersonNoOptional;
import functional.PersonOptional;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class TestPerson {

    @Test
    public void testGetMothersMotherFatherNoOptional(){
        PersonNoOptional person1 = new PersonNoOptional("Vasya", null, null);
        PersonNoOptional person2 = new PersonNoOptional("Olya", person1, null);
        PersonNoOptional person3 = new PersonNoOptional("Anna", null, person2);
        PersonNoOptional person4 = new PersonNoOptional("Vova", null, person3);
        assertEquals(person1, person4.getMothersMotherFather());
    }

    @Test
    public void testGetMothersMotherFatherOptional(){
        Optional<PersonOptional> person1 = Optional.of(new PersonOptional("Vasya", Optional.empty(), Optional.empty()));
        Optional<PersonOptional> person2 = Optional.of(new PersonOptional("Olya", person1, Optional.empty()));
        Optional<PersonOptional> person3 = Optional.of(new PersonOptional("Anna", Optional.empty(), person2));
        Optional<PersonOptional> person4 = Optional.of(new PersonOptional("Vova", Optional.empty(), person3));
        assertEquals(person1, person4.get().getMothersMotherFather());
    }
}
