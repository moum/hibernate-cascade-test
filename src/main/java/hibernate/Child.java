package hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Child {

    @Column
    public String description;

}
