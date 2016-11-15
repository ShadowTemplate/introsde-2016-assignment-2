package introsde.server.model;

import introsde.server.dao.TOFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.PrivateOwned;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
@Entity
@Table(name="MeasureHistory")
public class MeasureHistory implements Serializable {

    @Id
    @TableGenerator(name="MH_ID_GENERATOR", table="MH_SEQUENCES", pkColumnName="MH_SEQ_NAME",
            valueColumnName="MH_SEQ_NUMBER", pkColumnValue = "MH_SEQUENCE", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.TABLE, generator="MH_ID_GENERATOR")
    private Double measureHistoryId;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="mid")
    @PrivateOwned
    private List<MeasureType> measureTypes = new ArrayList<>();

    public MeasureHistory(introsde.common.to.MeasureHistory measureHistoryTO) {
        this.measureHistoryId = measureHistoryTO.getMeasureHistoryId();
        this.measureTypes = measureHistoryTO.getMeasureTypes().stream().map(MeasureType::new).collect(Collectors.toList());
    }

    public introsde.common.to.MeasureHistory buildTO() {
        return TOFactory.buildTO(this);
    }
}
