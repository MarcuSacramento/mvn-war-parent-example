package br.gov.mj.side.web.util.agendamento;

import java.util.Locale;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mj.side.web.service.GenericEntidadeService;

@Stateless
public class AtualizacaoPessoasJob {

    @Inject
    GenericEntidadeService genericEntidadeService;

    @Schedule(second = "0", minute = "0", hour = "0", persistent = false)
    public void execute() {

        Locale.setDefault(new Locale("pt", "BR"));
        // Retirada momentanea do Job de alterar status de pessoa que tiver o
        // data fim da vigencia expirada.
        // A ideia e de se analisar melhor a funcionalidade para que seja
        // implementada uma funcionalidade definitiva.
        // genericEntidadeService.atualizarStatusPessoas();

    }

}