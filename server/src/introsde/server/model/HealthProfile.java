package introsde.server.model;

import introsde.server.dao.TOFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.PrivateOwned;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
@Entity
@Table(name="HealthProfile")
public class HealthProfile implements Serializable {

    @Id
    @TableGenerator(name="HP_ID_GENERATOR", table="HP_SEQUENCES", pkColumnName="HP_SEQ_NAME",
            valueColumnName="HP_SEQ_NUMBER", pkColumnValue = "HP_SEQUENCE", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.TABLE, generator="HP_ID_GENERATOR")
    private Double healthProfileId;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="mid")
    @PrivateOwned
    private Set<MeasureType> measureTypes = new HashSet<>();

    public HealthProfile(introsde.common.to.HealthProfile healthProfileTO) {
        this.healthProfileId = healthProfileTO.getHealthProfileId();
        this.measureTypes = healthProfileTO.getMeasureTypes().stream().map(MeasureType::new).collect(Collectors.toSet());
    }

    public introsde.common.to.HealthProfile buildTO() {
        return TOFactory.buildTO(this);
    }
}
