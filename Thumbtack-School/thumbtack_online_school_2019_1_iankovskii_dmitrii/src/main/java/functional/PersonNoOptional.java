package functional;

public class PersonNoOptional {

    private String firstName;
    private PersonNoOptional father;
    private PersonNoOptional mother;

    public PersonNoOptional(String firstName, PersonNoOptional father, PersonNoOptional mother){
        this.firstName = firstName;
        this.father = father;
        this.mother = mother;
    }

    public PersonNoOptional getFather() {
        return father;
    }

    public PersonNoOptional getMother() {
        return mother;
    }

    public PersonNoOptional getMothersMotherFather(){
        if (mother != null){
            if (mother.getMother() !=null){
                return mother.getMother().getFather();
            }
        }
        return null;
    }
}
