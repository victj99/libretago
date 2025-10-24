package com.utp.libretago.classes.dto;

import java.util.List;

import org.jspecify.annotations.NonNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelValidadoDTO<T> {
    String archivoId;

    List<@NonNull T> datosCargados;

    public ExcelValidadoDTO() {
    }

    public ExcelValidadoDTO(String archivoId, List<@NonNull T> datosCargados) {
        this.archivoId = archivoId;
        this.datosCargados = datosCargados;
    }

}
