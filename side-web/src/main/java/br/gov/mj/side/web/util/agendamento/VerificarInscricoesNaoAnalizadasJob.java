package br.gov.mj.side.web.util.agendamento;

import java.util.List;
import java.util.Locale;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.MailService;

@Stateless
public class VerificarInscricoesNaoAnalizadasJob {

    @Inject
    GenericEntidadeService genericEntidadeService;

    @Inject
    MailService mailService;

    @Schedule(second = "0", minute = "0", hour = "*", persistent = false)
    public void execute() {
        Locale.setDefault(new Locale("pt", "BR"));
        List<Entidade> lista = genericEntidadeService.atualizarInscricoesNaoAnalizadasNoPrazo();

        if (lista != null) {
            for (Entidade entidade : lista) {
                mailService.enviarEmailEntidadeNaoAnalisadaEmTempoHabil(entidade);
            }
        }

    }

}