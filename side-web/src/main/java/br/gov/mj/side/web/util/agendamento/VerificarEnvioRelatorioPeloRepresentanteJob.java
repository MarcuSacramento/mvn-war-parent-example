package br.gov.mj.side.web.util.agendamento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.ItensNotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.web.dto.ListaBeneficiariosSemEnvioRelatorioRecebimentoDto;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.MailService;
import br.gov.mj.side.web.service.NotaRemessaService;
import br.gov.mj.side.web.service.ParametroService;

@Stateless
public class VerificarEnvioRelatorioPeloRepresentanteJob {
	
	public static final String PAGINA_INSCRICAO = "inscricao";

    @Inject
    NotaRemessaService notaRemessaService;

    @Inject
    ParametroService parametroService;

    @Inject
    MailService mailService;
    
	@Inject
	private InscricaoProgramaService inscricaoProgramaService;
    
    private List<InscricaoPrograma> listaInscricaoPrograma = new ArrayList<InscricaoPrograma>();

    @Schedule(second = "0", minute = "0", hour = "0", persistent = false)
    public void execute() {
        Locale.setDefault(new Locale("pt", "BR"));
        
        ListaBeneficiariosSemEnvioRelatorioRecebimentoDto listaPessoasEnviarEmail = notaRemessaService.buscarNotasFiscaisSemEnvioRelatorioRecebimento();
        
        dispararEmailGenerico(listaPessoasEnviarEmail.getLista5Dias(),5);
        dispararEmailGenerico(listaPessoasEnviarEmail.getLista10Dias(),10);
        dispararEmailGenerico(listaPessoasEnviarEmail.getLista15Dias(),15);
        dispararEmailGenerico(listaPessoasEnviarEmail.getLista20Dias(),20);
        dispararEmailGenerico(listaPessoasEnviarEmail.getLista25Dias(),25);
        dispararEmailGenerico(listaPessoasEnviarEmail.getLista30Dias(),30);
    }
    
    private void dispararEmailGenerico(List<NotaRemessaOrdemFornecimentoContrato> notaRemessaGenerica, int dias) {
    	List<NotaRemessaOrdemFornecimentoContrato> listaTemp = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
    	listaTemp.addAll(notaRemessaGenerica);
    	String url = parametroService.getUrlBase(PAGINA_INSCRICAO);
    	for(NotaRemessaOrdemFornecimentoContrato notaRemessa : listaTemp){
        	listaInscricaoPrograma = new ArrayList<InscricaoPrograma>();
        	Programa programa = new Programa();
        	Entidade entidade = new Entidade();
        	LocalDate dataEvetivaEntrega = notaRemessa.getDataEfetivaEntrega();
        	
        	programa = notaRemessa.getOrdemFornecimento().getContrato().getPrograma();
            for (ItensNotaRemessaOrdemFornecimentoContrato itensNotaRemessa : notaRemessa.getListaItensNotaRemessaOrdemFornecimentoContratos()){
        		entidade = itensNotaRemessa.getItemOrdemFornecimentoContrato().getLocalEntrega().getEntidade();
        		break;
        	}
        	
        	listaInscricaoPrograma = inscricaoProgramaService.buscarInscricaoProgramaPeloProgramaEEntidade(programa,entidade);
        	
        	if(dias == 5){
        		mailService.enviarEmail5dias(dataEvetivaEntrega, listaInscricaoPrograma,url);
        	}else if(dias == 10 || dias == 15 || dias == 20 || dias == 25){
        		mailService.enviarEmailAtraso(dataEvetivaEntrega, listaInscricaoPrograma,url,entidade);
        	}else if(dias == 30){
        		mailService.enviarEmailFinalPrazo(dataEvetivaEntrega, listaInscricaoPrograma,url,entidade);
        	}
        }
		
	}

}