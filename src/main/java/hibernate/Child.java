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

        if (description != null ? !description.equals(child.description) : child.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return description != null ? description.hashCode() : 0;
    }
}
