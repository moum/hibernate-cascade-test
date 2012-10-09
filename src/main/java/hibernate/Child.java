package hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Child {

    @Id
    public Long id;

    @Column
    public String description;

}
