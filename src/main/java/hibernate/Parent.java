package hibernate;


import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
//@Access(javax.persistence.AccessType.FIELD)
public class Parent {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ElementCollection
    @JoinTable(name = "children", joinColumns = {@JoinColumn(name = "parentId")})
    @Cascade(value=org.hibernate.annotations.CascadeType.ALL)
    public Set<Child> children = new HashSet<Child>();

    @Column
    public String description;
}
