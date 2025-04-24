package com.pagoDocente.service.servicieImpl;

import com.pagoDocente.dto.PagoRequest;
import com.pagoDocente.dto.PagoResponse;
import com.pagoDocente.entity.DocentePago;
import com.pagoDocente.repository.DocentePagoRepository;
import com.pagoDocente.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class PagoServiceImpl implements PagoService {

    private final DocentePagoRepository pagoRepository;

    public PagoServiceImpl(DocentePagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    public PagoResponse calcularPago(PagoRequest request) {
        double valorHoraDiurna = 0;
        double valorHoraNocturna = 0;
        double valorHoraDominical = 0;
        double valorHoraFestiva = 0;
        if (request.getHorasDiurnas() < 0 || request.getHorasNocturnas() < 0 ||
                request.getHorasDominicales() < 0 || request.getHorasFestivas() < 0) {
            throw new IllegalArgumentException("Las horas trabajadas no pueden ser negativas");
        }


        if ("tiempo_completo".equalsIgnoreCase(request.getTipoContrato())) {
            valorHoraDiurna = 20000;
            valorHoraNocturna = 30000;
            valorHoraDominical = 35000;
            valorHoraFestiva = 40000;
        } else if ("medio_tiempo".equalsIgnoreCase(request.getTipoContrato())) {
            valorHoraDiurna = 12000;
            valorHoraNocturna = 18000;
            valorHoraDominical = 21000;
            valorHoraFestiva = 24000;
        } else {
            throw new IllegalArgumentException("Tipo de contrato no válido");
        }

        double bruto = request.getHorasDiurnas() * valorHoraDiurna
                + request.getHorasNocturnas() * valorHoraNocturna
                + request.getHorasDominicales() * valorHoraDominical
                + request.getHorasFestivas() * valorHoraFestiva;

        double parafiscales = bruto * 0.08; // 4% salud + 4% pensión
        double neto = bruto - parafiscales;

        // Guardar en BD
        DocentePago entity = DocentePago.builder()
                .tipoContrato(request.getTipoContrato())
                .horasDiurnas(request.getHorasDiurnas())
                .horasNocturnas(request.getHorasNocturnas())
                .horasDominicales(request.getHorasDominicales())
                .horasFestivas(request.getHorasFestivas())
                .salarioBruto(bruto)
                .descuentoParafiscales(parafiscales)
                .salarioNeto(neto)
                .fechaRegistro(LocalDateTime.now())
                .build();

        pagoRepository.save(entity);

        return new PagoResponse(bruto, parafiscales, neto);
    }
}