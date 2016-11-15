package introsde.common.to;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Person implements Serializable {

    private Double id;
    private String firstname;
    private String lastname;
    private String birthdate;
    private HealthProfile healthProfile;
    private MeasureHistory measureHistory;

}
