package functional;

import java.util.Optional;

public class PersonOptional {

    private String firstName;
    private Optional<PersonOptional> father;
    private Optional<PersonOptional> mother;

    public PersonOptional(String firstName, Optional<PersonOptional> father, Optional<PersonOptional> mother){
        this.firstName = firstName;
        this.father = father;
        this.mother = mother;
    }

    public Optional<PersonOptional> getFather() {
        return father;
    }

    public Optional<PersonOptional> getMother() {
        return mother;
    }

    public Optional<PersonOptional> getMothersMotherFather(){
        return mother.get().getMother().get().getFather();
    }
}
