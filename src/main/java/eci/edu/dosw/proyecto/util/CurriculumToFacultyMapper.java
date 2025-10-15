package eci.edu.dosw.proyecto.util;

import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.Faculty;
import org.springframework.stereotype.Component;

@Component
public class CurriculumToFacultyMapper {

    public Faculty map(Curriculum curriculum) {
        if (curriculum == null) {
            return null;
        }

        return switch (curriculum) {
            case ISIS_14, ISIS_15 -> Faculty.INGENIERIA_DE_SISTEMAS;
            case ICIV_09, ICIV_10 -> Faculty.INGENIERIA_CIVIL;
            case IBIO_RO -> Faculty.INGENIERIA_BIOMEDICA;
            case IMEC_03, IMEC_02 -> Faculty.INGENIERIA_MECANICA;
            case MATE_04, MATE_03 -> Faculty.MATEMATICAS;
            case ADMI_04, ADMI_05 -> Faculty.ADMINISTRACION_DE_EMPRESAS;
            case ECON_07, ECON_08 -> Faculty.ECONOMIA;
            case IELN_08, IELN_07 -> Faculty.INGENIERIA_ELECTRONICA;
            case IIND_09, IIND_08 -> Faculty.INGENIERIA_INDUSTRIAL;
            case IELC_14, IELC_13 -> Faculty.INGENIERIA_ELECTRICA;
            case IEST_02, IEST_01 -> Faculty.INGENIERIA_ESTADISTICA;
            case IAMB_02, IAMB_01 -> Faculty.INGENIERIA_AMBIENTAL;
            case ICIB_01 -> Faculty.INGENIERIA_DE_CIBERSEGURIDAD;
            case IDIA_01 -> Faculty.INGENIERIA_DE_INTELIGENCIA_ARTIFICIAL;
            case IBTC_01 -> Faculty.INGENIERIA_DE_BIOTECNOLOGIA;
        };
    }
}
