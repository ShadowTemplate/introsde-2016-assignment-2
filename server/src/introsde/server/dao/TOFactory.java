package introsde.server.dao;

import introsde.server.model.MeasureType;
import introsde.server.model.HealthProfile;
import introsde.server.model.MeasureHistory;
import introsde.server.model.Person;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TOFactory {

    public static introsde.common.to.HealthProfile buildTO(HealthProfile healthProfile) {
        introsde.common.to.HealthProfile healthProfileTO = new introsde.common.to.HealthProfile();
        healthProfileTO.setHealthProfileId(healthProfile.getHealthProfileId());
        Set<introsde.common.to.MeasureType> measureTypes = new HashSet<>();
        for (MeasureType measureType : healthProfile.getMeasureTypes()) {
            measureTypes.add(TOFactory.buildTO(measureType));
        }
        healthProfileTO.setMeasureTypes(measureTypes);
        return healthProfileTO;
    }

    public static introsde.common.to.MeasureType buildTO(MeasureType measureType) {
        introsde.common.to.MeasureType measureTypeTO = new introsde.common.to.MeasureType();
        measureTypeTO.setMid(measureType.getMid());
        measureTypeTO.setMeasure(measureType.getMeasure());
        measureTypeTO.setValue(measureType.getValue());
        measureTypeTO.setCreated(measureType.getCreated());
        return measureTypeTO;
    }

    public static introsde.common.to.MeasureHistory buildTO(MeasureHistory measureHistory) {
        introsde.common.to.MeasureHistory measureHistoryTO = new introsde.common.to.MeasureHistory();
        measureHistoryTO.setMeasureHistoryId(measureHistory.getMeasureHistoryId());
        List<introsde.common.to.MeasureType> measureTypes = new ArrayList<>();
        for (MeasureType measureType : measureHistory.getMeasureTypes()) {
            measureTypes.add(TOFactory.buildTO(measureType));
        }
        measureHistoryTO.setMeasureTypes(measureTypes);
        return measureHistoryTO;
    }

    public static introsde.common.to.Person buildTO(Person person) {
        introsde.common.to.Person personTO = new introsde.common.to.Person();
        personTO.setId(person.getId());
        personTO.setFirstname(person.getFirstname());
        personTO.setLastname(person.getLastname());
        personTO.setBirthdate(person.getBirthdate());
        personTO.setHealthProfile(TOFactory.buildTO(person.getHealthProfile()));
        personTO.setMeasureHistory(TOFactory.buildTO(person.getMeasureHistory()));
        return personTO;
    }
}
