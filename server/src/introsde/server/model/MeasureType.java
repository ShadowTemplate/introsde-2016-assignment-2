package introsde.server.model;

import introsde.server.dao.TOFactory;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name="MeasureType")
public class MeasureType implements Serializable {

    @Id
    @TableGenerator(name="MT_ID_GENERATOR", table="MT_SEQUENCES", pkColumnName="MT_SEQ_NAME",
            valueColumnName="MT_SEQ_NUMBER", pkColumnValue = "MT_SEQUENCE", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.TABLE, generator="MT_ID_GENERATOR")
    private Double mid;

    @Column(name="measure")
    private String measure;

    @Column(name="value")
    private Double value;

    @Column(name="created")
    private String created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeasureType that = (MeasureType) o;
        return !(that.getMid() == null || this.getMid() == null) && measure.equals(that.measure);
    }

    @Override
    public int hashCode() {
        return mid != null ? mid.hashCode() : 0;
    }

    public MeasureType(introsde.common.to.MeasureType measureTypeTO) {
        this.mid = measureTypeTO.getMid();
        this.measure = measureTypeTO.getMeasure();
        this.value = measureTypeTO.getValue();
        this.created = measureTypeTO.getCreated();
    }

    public introsde.common.to.MeasureType buildTO() {
        return TOFactory.buildTO(this);
    }
}
