package introsde.common.to;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class MeasureType implements Serializable {

    private Double mid;
    private String measure;
    private Double value;
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
}
