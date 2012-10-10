package hibernate;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Access(javax.persistence.AccessType.FIELD)
public class Parent {

    @Id
    public Long id;

    @ElementCollection
    @JoinTable(name = "children", joinColumns = {@JoinColumn(name = "parentId")})
    public Set<Child> children = new HashSet<Child>();

    @Column
    public String description;

}
