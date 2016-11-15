package introsde.server.model;

import introsde.server.dao.TOFactory;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@Data
@Entity
@Table(name="Person")
public class Person implements Serializable {

    @Id
    @TableGenerator(name="PERSON_ID_GENERATOR", table="PERSON_SEQUENCES", pkColumnName="PERSON_SEQ_NAME",
            valueColumnName="PERSON_SEQ_NUMBER", pkColumnValue = "PERSON_SEQUENCE", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.TABLE, generator="PERSON_ID_GENERATOR")
    private Double id;

    @Column(name="firstname")
    private String firstname;

    @Column(name="lastname")
    private String lastname;

    @Column(name="birthdate")
    private String birthdate;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="healthProfileId")
    private HealthProfile healthProfile = new HealthProfile();

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="measureHistoryId")
    private MeasureHistory measureHistory = new MeasureHistory();

    public Person(introsde.common.to.Person personTO) {
        this.id = personTO.getId();
        this.firstname = personTO.getFirstname();
        this.lastname = personTO.getLastname();
        this.birthdate = personTO.getBirthdate();
        this.healthProfile = new HealthProfile(personTO.getHealthProfile());
        this.measureHistory = new MeasureHistory(personTO.getMeasureHistory());
    }

    public introsde.common.to.Person buildTO() {
        return TOFactory.buildTO(this);
    }
}
