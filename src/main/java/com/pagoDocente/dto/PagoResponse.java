package com.pagoDocente.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PagoResponse {
    private double salarioBruto;
    private double descuentoParafiscales;
    private double salarioNeto;
}