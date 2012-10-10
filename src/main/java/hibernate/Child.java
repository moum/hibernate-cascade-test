package hibernate;

import javax.persistence.*;

@Embeddable
public class Child {

    //    @Id
    //    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column
    public String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Child child = (Child) o;

        if (!description.equals(child.description)) return false;
        if (!id.equals(child.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }
}
