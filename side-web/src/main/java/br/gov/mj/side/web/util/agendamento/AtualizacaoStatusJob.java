package br.gov.mj.side.web.util.agendamento;

import java.util.Locale;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mj.side.web.service.PublicizacaoService;

@Stateless
public class AtualizacaoStatusJob {

    @Inject
    PublicizacaoService publicizacaoService;

    @Schedule(second = "0", minute = "0", hour = "0", persistent = false)
    public void execute() {
        Locale.setDefault(new Locale("pt", "BR"));
        publicizacaoService.atualizarStatusProgramas();

    }

}