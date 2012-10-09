package hibernate;


import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Parent {

    @Id
    public Long id;

    @OneToMany(cascade= CascadeType.ALL)
    public Set<Child> children = new HashSet<Child>();

    @Column
    public String description;

    public void add(Child... children) {
        Collections.addAll(this.children, children);
    }
}
