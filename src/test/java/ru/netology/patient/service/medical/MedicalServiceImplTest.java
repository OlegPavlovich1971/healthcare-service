package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;

public class MedicalServiceImplTest {
    final String id = "1";
    final PatientInfo patientInfo = new PatientInfo(id, "Иван", "Петров",
            LocalDate.of(1980, 11, 26), new HealthInfo(new BigDecimal("36.6"),
            new BloodPressure(120, 80)));
    final BloodPressure currentPressure = new BloodPressure(60, 120);
    final BloodPressure normalPressure = new BloodPressure(120, 80); // см. BloodPressure в patientInfo
    final BigDecimal currentTemperature = new BigDecimal("39.0");
    final String message = "Warning, patient with id: " + id + ", need help";

    @Test
    public void testCheckBloodPressureNotNormalValue() {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById(id))
                .thenReturn(patientInfo);
        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        medicalService.checkBloodPressure(id, currentPressure);
        Mockito.verify(sendAlertService, Mockito.times(1))
                .send(argumentCaptor.capture());
        Assertions.assertEquals(message, argumentCaptor.getValue());
    }

    @Test
    public void testCheckTemperatureNotNormalValue() {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById(id))
                .thenReturn(patientInfo);
        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
//        Если я правильно понял работу сервиса, то метод checkTemperature должен был предупреждать о превышении
//        температуры пациента на 1,5 от нормы. В ходе тестирования выявилось, что метод работает некорректно,
//        в связи с чем мне пришлось его отредактировать: заменить ">" на "<" при оценке результатов метода toCompare
//        для нормальной и текущей температур. Кроме того, метод checkTemperature выводил сообщение самостоятельно, до
//        вызова метода send.
        medicalService.checkTemperature(id, currentTemperature);
        Mockito.verify(sendAlertService, Mockito.times(1))
                .send(argumentCaptor.capture());
        Assertions.assertEquals(message, argumentCaptor.getValue());
    }

    @Test
    public void testCheckBloodPressureNormalValue() {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById(id))
                .thenReturn(patientInfo);
        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        medicalService.checkBloodPressure(id, normalPressure);
        Mockito.verify(sendAlertService, Mockito.times(0))
                .send(any());
    }

}
