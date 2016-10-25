package br.gov.mj.side.web.util.agendamento;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mj.side.web.service.ContratoService;

@Stateless
public class VerificarContratosForaDevigenciaJob {

    @Inject
    private ContratoService contratoService;

    @Schedule(second = "0", minute = "0", hour = "0", persistent = false)
    public void execute() {
        contratoService.alterarStatusContratosForaVigencia();
    }

}