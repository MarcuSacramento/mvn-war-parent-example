package br.gov.mj.side.web.util.agendamento;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mj.side.web.service.NotaRemessaService;

/*
 * Este JOB irá atualizar o Status da Nota Remessa após ela ter sido entregue pelo Fornecedor
 * Quando o Fornecedor executa a O.F. o Status da Nota é 'Emitida'
 * Quando o Fornecedor faz a entrega efetiva o Status é 'Recebido'
 * O Status Recebido fica assim até 10 caso não tenha iniciado o cadastro de nenhum item
 * Se até 10 dias não enviar o relatório de recebimento mas tiver cadastrado algum item o Status será Analise
 * Após os 10 dias do recebimento o status é 'Atraso'
 * Após 30 dias o status ´'Inadiplente'
 * Quando enviar o Relatório de recebimento o status será 'Enviado'
 * 
 */

@Stateless
public class AtualizarStatusNotaRemessaJob {

    @Inject
    private NotaRemessaService notaRemessaService;

    @Schedule(second = "0", minute = "0", hour = "0", persistent = false)
    public void execute() {
        notaRemessaService.atualizarStatusNotasRemessasPeloJob();
    }

}