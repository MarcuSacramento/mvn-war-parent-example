package br.gov.mj.side.web.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumAnaliseFinalItem;
import br.gov.mj.side.entidades.enums.EnumBotaoClicadoNotaRemessa;
import br.gov.mj.side.entidades.enums.EnumSituacaoBem;
import br.gov.mj.side.entidades.enums.EnumSituacaoGeracaoTermos;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaBeneficiario;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaContratante;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaFornecedor;
import br.gov.mj.side.entidades.enums.EnumStatusOrdemFornecimento;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoTermoEntrega;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoObjetoFornecimento;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.AnexoNotaRemessa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.ItensNotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.web.dao.NotaRemessaDAO;
import br.gov.mj.side.web.dto.ListaBeneficiariosSemEnvioRelatorioRecebimentoDto;
import br.gov.mj.side.web.dto.NotaRemessaPesquisaDto;
import br.gov.mj.side.web.dto.ObjetoFornecimentoContratoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class NotaRemessaService {

    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

    @Inject
    private NotaRemessaDAO notaRemessaDAO;

    @Inject
    private CodigoVerificacaoService codigoVerificacaoService;
    
    @Inject
    private ObjetoFornecimentoContratoService objetoFornecimentoContratoService;

    public NotaRemessaOrdemFornecimentoContrato buscarPeloId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return notaRemessaDAO.buscarPeloId(id);
    }
    
    public List<ObjetoFornecimentoContrato> buscarObjetoFornecimentoContratoPelaNotaRemessa(NotaRemessaOrdemFornecimentoContrato notaRemessaFornecimentoContrato){
        return notaRemessaDAO.buscarObjetoFornecimentoContratoPelaNotaRemessa(notaRemessaFornecimentoContrato);
    }
    
    //Irá retornar todos os ObjetosFornecimentos de uma nota de remessa
    public List<ObjetoFornecimentoContrato> buscarTodosObjetoFornecimentoContratoPelaNotaRemessa(NotaRemessaOrdemFornecimentoContrato notaRemessaFornecimentoContrato){        
        if (notaRemessaFornecimentoContrato == null) {
            throw new IllegalArgumentException("Parâmetro notaRemessaFornecimentoContrato não pode ser null");
        }        
        return notaRemessaDAO.buscarTodosObjetoFornecimentoContratoPelaNotaRemessa(notaRemessaFornecimentoContrato);
    }

    public List<NotaRemessaOrdemFornecimentoContrato> buscarNotasFiscaisPeloProgramaEBeneficiarioPaginadoOrdenado(Programa programa, Entidade entidade, int first, int size, EnumOrder order, String propertyOrder) {
        return notaRemessaDAO.buscarNotasRemessasPeloProgramaEBeneficiarioPaginadoOrdenado(programa, entidade, first, size, order, propertyOrder);
    }

    public List<NotaRemessaOrdemFornecimentoContrato> buscarPaginado(NotaRemessaPesquisaDto notaRemessaPesquisaDto, int first, int size, EnumOrder order, String propertyOrder) {
        return notaRemessaDAO.buscarPaginado(notaRemessaPesquisaDto, first, size, order, propertyOrder);
    }

    public List<NotaRemessaOrdemFornecimentoContrato> buscarSemPaginacao(NotaRemessaPesquisaDto notaRemessaPesquisaDto) {
        return notaRemessaDAO.buscarSemPaginacao(notaRemessaPesquisaDto);
    }

    public List<NotaRemessaOrdemFornecimentoContrato> buscarSemPaginacaoOrdenado(NotaRemessaPesquisaDto notaRemessaPesquisaDto, EnumOrder order, String propertyOrder) {
        return notaRemessaDAO.buscarSemPaginacao(notaRemessaPesquisaDto);
    }

    public List<NotaRemessaOrdemFornecimentoContrato> buscarListaNotasRemessasCadastradas(OrdemFornecimentoContrato ordemFornecimento) {
        if (ordemFornecimento == null || ordemFornecimento.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return notaRemessaDAO.buscarListaNotasRemessaCadastradas(ordemFornecimento);
    }

    public List<ItensNotaRemessaOrdemFornecimentoContrato> buscarItensNotaRemessa(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato) {
        if (notaRemessaOrdemFornecimentoContrato == null || notaRemessaOrdemFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return notaRemessaDAO.buscarItensNotaRemessa(notaRemessaOrdemFornecimentoContrato);
    }

    public List<NotaRemessaOrdemFornecimentoContrato> buscarNotasRemessasDeItensDevolvidos(List<ObjetoFornecimentoContrato> listaObjetosDevolvidos){
        
        List<NotaRemessaOrdemFornecimentoContrato> listaLocaisEntrega = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
        List<ItensOrdemFornecimentoContrato> listaComTodosItens = new ArrayList<ItensOrdemFornecimentoContrato>();
        OrdemFornecimentoContrato ordemFornecimento = new OrdemFornecimentoContrato();
        for(ObjetoFornecimentoContrato ofo:listaObjetosDevolvidos){
            if(!ofo.getObjetoDevolvido()){
                ItensOrdemFornecimentoContrato itemDevolvido = ordemFornecimentoContratoService.buscarItemDaOrdemDeFornecimentoPeloObjetoFornecimento(ofo);
                listaComTodosItens.add(itemDevolvido);
                
                if(ordemFornecimento.getId() == null){
                    ordemFornecimento = ofo.getOrdemFornecimento();                     
                }
            }
        }        
        listaLocaisEntrega = montarListaDeNotasFiscais(listaComTodosItens);
        for(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato:listaLocaisEntrega){
            notaRemessaOrdemFornecimentoContrato.setNotaDevolucao(true);
        }
        return listaLocaisEntrega;
    }
    
    public List<NotaRemessaOrdemFornecimentoContrato> buscarNotasRemessasRemanecentes(OrdemFornecimentoContrato ordemFornecimentoContrato) {
        Map<Long, NotaRemessaOrdemFornecimentoContrato> mapaItem = new HashMap<Long, NotaRemessaOrdemFornecimentoContrato>();

        List<NotaRemessaOrdemFornecimentoContrato> listaLocaisEntrega = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();

        // Busca todos os itens desta ordem de fornecimento
        List<ItensOrdemFornecimentoContrato> listaComTodosItens = ordemFornecimentoContratoService.buscarItensOrdemFornecimentoContrato(ordemFornecimentoContrato);
        List<NotaRemessaOrdemFornecimentoContrato> listaNFCadastradas = buscarListaNotasRemessasCadastradas(ordemFornecimentoContrato);

        retiraItensJaemUsoDaListaComTodosOsItens(listaComTodosItens, listaNFCadastradas);

        listaLocaisEntrega = montarListaDeNotasFiscais(listaComTodosItens);
        return listaLocaisEntrega;
    }
    
    private List<NotaRemessaOrdemFornecimentoContrato> montarListaDeNotasFiscais(List<ItensOrdemFornecimentoContrato> listaComTodosItens){
        
        Map<Long, NotaRemessaOrdemFornecimentoContrato> mapaItem = new HashMap<Long, NotaRemessaOrdemFornecimentoContrato>();
        List<NotaRemessaOrdemFornecimentoContrato> listaLocaisEntrega = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
        
        for (ItensOrdemFornecimentoContrato ordem : listaComTodosItens) {
            Long chave = ordem.getLocalEntrega().getId();

            if (!mapaItem.containsKey(chave)) {

                NotaRemessaOrdemFornecimentoContrato notaRemessa = new NotaRemessaOrdemFornecimentoContrato();
                notaRemessa.setOrdemFornecimento(ordem.getOrdemFornecimento());

                ItensNotaRemessaOrdemFornecimentoContrato itemNotaRemessa = new ItensNotaRemessaOrdemFornecimentoContrato();
                itemNotaRemessa.setItemOrdemFornecimentoContrato(ordem);

                notaRemessa.getListaItensNotaRemessaOrdemFornecimentoContratos().add(itemNotaRemessa);

                mapaItem.put(chave, notaRemessa);
            } else {
                NotaRemessaOrdemFornecimentoContrato notaRemessa = mapaItem.get(chave);

                ItensNotaRemessaOrdemFornecimentoContrato itemNotaRemessa = new ItensNotaRemessaOrdemFornecimentoContrato();
                itemNotaRemessa.setItemOrdemFornecimentoContrato(ordem);
                notaRemessa.getListaItensNotaRemessaOrdemFornecimentoContratos().add(itemNotaRemessa);
            }
        }

        listaLocaisEntrega.addAll(mapaItem.values());
        return listaLocaisEntrega;
    }

    public List<NotaRemessaOrdemFornecimentoContrato> buscarNotasFiscaisPeloProgramaEBeneficiario(Programa programa, Entidade entidade) {

        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do Programa não pode ser null");
        }

        if (entidade == null || entidade.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id da Entidade não pode ser null");
        }
        return notaRemessaDAO.buscarNotasRemessasPeloProgramaEBeneficiario(programa, entidade);
    }

    public ListaBeneficiariosSemEnvioRelatorioRecebimentoDto buscarNotasFiscaisSemEnvioRelatorioRecebimento() {

        ListaBeneficiariosSemEnvioRelatorioRecebimentoDto notasSemRegistrarRecebimento = notaRemessaDAO.buscarNotasRemessasSemEnvioRelatorioRecebimento();
        return notasSemRegistrarRecebimento;
    }

    public List<BigInteger> buscarIdsNotaRemessaPorFormatacaoObjetoFornecimento(FormatacaoObjetoFornecimento fof) {
        if (fof == null) {
            throw new IllegalArgumentException("A Formatacao objeto fornecimento não pode ser nulo");
        }
        return notaRemessaDAO.buscarIdsNotaRemessaPorFormatacaoObjetoFornecimento(fof);
    }

    public List<BigInteger> buscarIdsNotaRemessaPorFormatacaoObjetoFornecimentoSemStatus(FormatacaoObjetoFornecimento fof) {
        if (fof == null) {
            throw new IllegalArgumentException("A Formatacao objeto fornecimento não pode ser nulo");
        }
        return notaRemessaDAO.buscarIdsNotaRemessaPorFormatacaoObjetoFornecimentoSemStatus(fof);
    }

    public NotaRemessaOrdemFornecimentoContrato incluirAlterar(NotaRemessaOrdemFornecimentoContrato notaRemessa, EnumBotaoClicadoNotaRemessa botaoClicado) {

        NotaRemessaOrdemFornecimentoContrato notaRemessaRetorno = null;

        if (notaRemessa == null) {
            throw new IllegalArgumentException("A nota fiscal não pode ser nula");
        }

        validarCamposObrigatórios(notaRemessa);
        verificarAFaseQueIraANotaRemessa(notaRemessa);

        if (botaoClicado != null && botaoClicado == EnumBotaoClicadoNotaRemessa.EXECUTAR_ENTREGA) {
            notaRemessa.setStatusExecucaoFornecedor(EnumStatusExecucaoNotaRemessaFornecedor.EMITIDA);
            
            List<ObjetoFornecimentoContrato> listaObjetoFornecimento = buscarTodosObjetoFornecimentoContratoPelaNotaRemessa(notaRemessa);
            for (ObjetoFornecimentoContrato objetoFornecimentoContrato : listaObjetoFornecimento) {
                objetoFornecimentoContratoService.setarNotaRemessaNoObjetoFornecimento(objetoFornecimentoContrato,notaRemessa);
            }
        }

        
        
        if (botaoClicado != null && botaoClicado == EnumBotaoClicadoNotaRemessa.REGISTRAR_ENTREGA) {
            notaRemessa.setStatusExecucaoFornecedor(EnumStatusExecucaoNotaRemessaFornecedor.ENTREGUE);
            
            //Irá mudar o status do bem do objetoFornecimentoContrato para entregue
            alterarStatusDosObjetosDaNota(notaRemessa,1);
        }

        if (botaoClicado != null && botaoClicado == EnumBotaoClicadoNotaRemessa.TERMO_RECEBIMENTO_ENVIADO) {
            notaRemessa.setStatusExecucaoContratante(EnumStatusExecucaoNotaRemessaContratante.EM_ANALISE);
            notaRemessa.setStatusExecucaoFornecedor(EnumStatusExecucaoNotaRemessaFornecedor.CONCLUIDA);
            
          //Irá mudar o status do bem do objetoFornecimentoContrato para RECEBIDO
            alterarStatusDosObjetosDaNota(notaRemessa,2);
        }

        if (notaRemessa.getId() == null) {
            notaRemessaRetorno = notaRemessaDAO.incluir(notaRemessa);
            
            List<ObjetoFornecimentoContrato> listaObjetoFornecimento = buscarTodosObjetoFornecimentoContratoPelaNotaRemessa(notaRemessaRetorno);
            for (ObjetoFornecimentoContrato objetoFornecimentoContrato : listaObjetoFornecimento) {
                objetoFornecimentoContratoService.setarNotaRemessaNoObjetoFornecimento(objetoFornecimentoContrato,notaRemessaRetorno);
            }
            
        } else {
            notaRemessaRetorno = notaRemessaDAO.alterar(notaRemessa);
        }
        verificarSeIraAlterarAFaseDaOrdemFornecimento(notaRemessa.getOrdemFornecimento());
        return notaRemessaRetorno;
    }
    
    /*
     * STATUS 1 = entregue
     * STATUS 2 = recebido
     */
    public void alterarStatusDosObjetosDaNota(NotaRemessaOrdemFornecimentoContrato notaRemessa, Integer status){
      //Irá atualizar o status de todos os itens desta nota de remessa para Entregue
        
        ObjetoFornecimentoContratoDto dto = new ObjetoFornecimentoContratoDto();
        ObjetoFornecimentoContrato ofo=new ObjetoFornecimentoContrato();
        ofo.setNotaRemessaOrdemFornecimentoContrato(notaRemessa);
        dto.setObjetoFornecimentoContrato(ofo);
        List<ObjetoFornecimentoContrato> listaAtualizar = objetoFornecimentoContratoService.buscarSemPaginacao(dto);
        
        for (ObjetoFornecimentoContrato objetoFornecimentoContrato : listaAtualizar) {
            
            if(objetoFornecimentoContrato.getSituacaoBem() != null){                    
                
                if(status.intValue() == 1){
                    if(objetoFornecimentoContrato.getSituacaoBem() == EnumSituacaoBem.RECEBIDO){
                      //Se o status for recebido, ou sejA, se o beneficiário já tiver enviado o termo de recebimento não será preciso informar que o status do bem é Entregue pois ele já foi recebido.
                    }else{
                        ordemFornecimentoContratoService.atualizarSituacaoDoItemObjetoFornecimentoContratoPeloId(EnumSituacaoBem.ENTREGUE,objetoFornecimentoContrato.getId());
                    }
                }else{
                    if(status.intValue() == 2){
                        ordemFornecimentoContratoService.atualizarSituacaoDoItemObjetoFornecimentoContratoPeloId(EnumSituacaoBem.RECEBIDO,objetoFornecimentoContrato.getId());
                    }
                }
            }else{
                //Se o status for nulo então não teve nenhuma mudança ainda, então pode ser setado RECEBIDO
                ordemFornecimentoContratoService.atualizarSituacaoDoItemObjetoFornecimentoContratoPeloId(EnumSituacaoBem.ENTREGUE,objetoFornecimentoContrato.getId());
            }
        }
    }

    public void verificarFaseDaOrdemDeFornecimento(OrdemFornecimentoContrato ordemFornecimentoContrato) {
        // Irá verificar se todos os itens desta ordem de fornecimento já
        // possuem o termo de recebimento defintivo
        List<Long> listaIds = buscarIdsItensSemTermoRecebimentoDefinitivoPorOrdemFornecimento(ordemFornecimentoContrato);
        boolean todosItensComTermoDefinitivoEnviado = listaIds != null && listaIds.isEmpty() ? true : false;

        if (todosItensComTermoDefinitivoEnviado) {

            ordemFornecimentoContrato.setStatusOrdemFornecimento(EnumStatusOrdemFornecimento.CONCLUIDA);
            ordemFornecimentoContratoService.alterarStatusOrdemFornecimento(ordemFornecimentoContrato);

        } else {
            // Se não existirem mais notas fiscais a enviaram o termo de
            // recebimento o status da ordem de fornecimento será 'RECEBIDA'
            List<Long> idsQueEnviaramTr = buscarIdsNotasFiscaisAindaNaoEnviadasPorOrdemFornecimento(ordemFornecimentoContrato);
            if (idsQueEnviaramTr != null && idsQueEnviaramTr.isEmpty()) {

                ordemFornecimentoContrato.setStatusOrdemFornecimento(EnumStatusOrdemFornecimento.RECEBIDA);
                ordemFornecimentoContratoService.alterarStatusOrdemFornecimento(ordemFornecimentoContrato);
            } else {

                verificarSeAlteraStatusOrdemFornecimentoParaEntregue(ordemFornecimentoContrato);
            }
        }
    }

    private void verificarSeIraAlterarAFaseDaOrdemFornecimento(OrdemFornecimentoContrato ordemFornecimento) {
        verificarSeAlteraStatusOrdemFornecimentoParaEntregue(ordemFornecimento);

        // Se todas as notas fiscais desta of forem entregues alterar o status
        // da o.f. para 'Concluido'
        verificarSeTodasAsNotasFiscaisJaEstaoComOTREnviados(ordemFornecimento);
    }

    // Se todas as notas fiscais desta O.F. foram efetivamente entregue então o
    // Status da ordem de
    // fornecimento será alterado para Entregue
    private void verificarSeAlteraStatusOrdemFornecimentoParaEntregue(OrdemFornecimentoContrato ordemFornecimento) {

        // Se o status da O.F. já for entregue então não precisa fazer a
        // verificação abaixo
        if (ordemFornecimento.getStatusOrdemFornecimento() != EnumStatusOrdemFornecimento.ENTREGUE) {
            List<Long> idsSemEntrega = ordemFornecimentoContratoService.buscarIdsNotasFiscaisSemEntregaEfetivaPorOrdemFornecimento(ordemFornecimento);
            if (idsSemEntrega.isEmpty()) {
                ordemFornecimento.setStatusOrdemFornecimento(EnumStatusOrdemFornecimento.ENTREGUE);
                ordemFornecimentoContratoService.alterarStatusOrdemFornecimento(ordemFornecimento);
            }
        }
    }

    // Irá buscar todas as notas fiscais desta O.F. e irá atualizar o status da
    // Ordem de Fornecimento para 'Concluida' caso
    // todas tenham enviado o T.R... irá retornar true caso todas as notas
    // enviaram e false caso alguma ainda não enviou
    private boolean verificarSeTodasAsNotasFiscaisJaEstaoComOTREnviados(OrdemFornecimentoContrato ordemFornecimentoContrato) {
        boolean todasNotasEnviadas = true;

        List<Long> listaIds = buscarIdsNotasFiscaisAindaNaoEnviadasPorOrdemFornecimento(ordemFornecimentoContrato);
        if (listaIds.isEmpty()) {
            ordemFornecimentoContrato.setStatusOrdemFornecimento(EnumStatusOrdemFornecimento.CONCLUIDA);
            ordemFornecimentoContratoService.alterarStatusOrdemFornecimento(ordemFornecimentoContrato);
        } else {
            todasNotasEnviadas = false;
        }
        return todasNotasEnviadas;
    }

    public NotaRemessaOrdemFornecimentoContrato executarOrdemFornecimento(NotaRemessaOrdemFornecimentoContrato notaRemessa) {
        notaRemessa.setCodigoGerado(codigoVerificacaoService.geraCodigoAleatorioDeVerificacao().getDescricaoCodigoVerificacao());

        // Se o status desta O.F. não estiver como 'Executada' irá alterar agora
        if (notaRemessa.getOrdemFornecimento().getStatusOrdemFornecimento() != EnumStatusOrdemFornecimento.EXECUTADA) {
            notaRemessa.getOrdemFornecimento().setStatusOrdemFornecimento(EnumStatusOrdemFornecimento.EXECUTADA);
            ordemFornecimentoContratoService.alterarStatusOrdemFornecimento(notaRemessa.getOrdemFornecimento());
        }

        notaRemessa.setStatusExecucaoBeneficiario(EnumStatusExecucaoNotaRemessaBeneficiario.EMITIDA);
        notaRemessaDAO.alterarStatusDaExecucaoNotaRemessaBeneficiario(notaRemessa);

        return incluirAlterar(notaRemessa, EnumBotaoClicadoNotaRemessa.EXECUTAR_ENTREGA);
    }

    public void excluir(NotaRemessaOrdemFornecimentoContrato notaRemessa) {
        if (notaRemessa == null || notaRemessa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        notaRemessaDAO.excluir(notaRemessa);
    }

    // Será chamado sempre que o gestor aceitar / aceitar com ressalvas / não
    // aceitar um item, para alterar o status da nota remessa
    public void verificarObjetosFornecimentoContratoJaAnalisados(NotaRemessaOrdemFornecimentoContrato notaRemessa) {
        if (notaRemessa == null || notaRemessa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        // Todos os Objetos fornecimento contrato desta notaremessa
        List<Long> idsTodosItens = notaRemessaDAO.buscarObjetoFornecimentoPelaNotaRemessaStatusFinal(notaRemessa, null);

        List<Long> idsAceitosComRessalva = notaRemessaDAO.buscarObjetoFornecimentoPelaNotaRemessaStatusFinal(notaRemessa, EnumAnaliseFinalItem.ACEITO_RESSALVA);
        if (idsAceitosComRessalva != null && !idsAceitosComRessalva.isEmpty()) {

            atualizarStatusExecucaoNotaRemessaContratantePeloIdNotaRemessa(EnumStatusExecucaoNotaRemessaContratante.ACEITO_COM_RESALVA, notaRemessa.getId());
            return;
        }

        List<Long> idsAceitos = notaRemessaDAO.buscarObjetoFornecimentoPelaNotaRemessaStatusFinal(notaRemessa, EnumAnaliseFinalItem.ACEITO);
        if (idsTodosItens != null && idsAceitos != null && !idsTodosItens.isEmpty() && !idsAceitos.isEmpty()) {
            if (idsAceitos.size() == idsTodosItens.size()) {

                atualizarStatusExecucaoNotaRemessaContratantePeloIdNotaRemessa(EnumStatusExecucaoNotaRemessaContratante.ACEITO, notaRemessa.getId());
                return;
            }
        }

        List<Long> idsNaoAceitos = notaRemessaDAO.buscarObjetoFornecimentoPelaNotaRemessaStatusFinal(notaRemessa, EnumAnaliseFinalItem.NAO_ACEITO);
        if (idsTodosItens != null && idsNaoAceitos != null && !idsTodosItens.isEmpty() && !idsNaoAceitos.isEmpty()) {
            if (idsNaoAceitos.size() == idsTodosItens.size()) {

                atualizarStatusExecucaoNotaRemessaContratantePeloIdNotaRemessa(EnumStatusExecucaoNotaRemessaContratante.NAO_ACEITO, notaRemessa.getId());
                return;
            }
        }
    }

    public void aceitarAnaliseDoItem(ObjetoFornecimentoContrato ofc) {
        if (ofc == null || ofc.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do Objeto Fornecimento Contrato não pode ser null");
        }
        notaRemessaDAO.atualizarAnaliseFinalObjetoFornecimentoContrato(ofc, EnumAnaliseFinalItem.ACEITO);
        if(ofc.getOrdemFornecimento().getStatusOrdemFornecimento() != EnumStatusOrdemFornecimento.DEVOLVIDA){
            verificarFaseDaOrdemDeFornecimento(ofc.getOrdemFornecimento());
        }
    }

    public void aceitarComRessalvaAnaliseDoItem(ObjetoFornecimentoContrato ofc) {
        if (ofc == null || ofc.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do Objeto Fornecimento Contrato não pode ser null");
        }
        notaRemessaDAO.atualizarAnaliseFinalObjetoFornecimentoContrato(ofc, EnumAnaliseFinalItem.ACEITO_RESSALVA);
        if(ofc.getOrdemFornecimento().getStatusOrdemFornecimento() != EnumStatusOrdemFornecimento.DEVOLVIDA){
            
            //Se a ordem de fornecimento contiver itens devolvidos e os mesmos ainda não tiverem sido executados novamente o status da O.F. continuará como DEVOLVIDA.
            verificarFaseDaOrdemDeFornecimento(ofc.getOrdemFornecimento());
        }
    }

    public void naoAceitarAnaliseDoItem(ObjetoFornecimentoContrato ofc) {
        if (ofc == null || ofc.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do Objeto Fornecimento Contrato não pode ser null");
        }
        notaRemessaDAO.atualizarAnaliseFinalObjetoFornecimentoContrato(ofc, EnumAnaliseFinalItem.NAO_ACEITO);

        // Se um item da nota fiscal não for aceito então o Status da Ordem de
        // Fornecimento passa a ser 'Com Pendência'.
        if(ofc.getOrdemFornecimento().getStatusOrdemFornecimento() != EnumStatusOrdemFornecimento.DEVOLVIDA){
            ordemFornecimentoContratoService.atualizarStatusOrdemFornecimentoPeloId(ofc.getOrdemFornecimento().getId(), EnumStatusOrdemFornecimento.COM_PENDENCIA);
        }
    }

    public void atualizarSituacaoGeracaoTermosObjetoFornecimentoContrato(ObjetoFornecimentoContrato ofc, EnumSituacaoGeracaoTermos analiseFinal) {
        if (ofc == null || ofc.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do Objeto Fornecimento Contrato não pode ser null");
        }
        notaRemessaDAO.atualizarSituacaoGeracaoTermosObjetoFornecimentoContrato(ofc, analiseFinal);
    }

    public void devolverParaAnalise(ObjetoFornecimentoContrato ofc) {
        if (ofc == null || ofc.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do Objeto Fornecimento Contrato não pode ser null");
        }
        notaRemessaDAO.atualizarAnaliseFinalObjetoFornecimentoContrato(ofc, EnumAnaliseFinalItem.NAO_ANALISADO);
        verificarFaseDaOrdemDeFornecimento(ofc.getOrdemFornecimento());
    }
    
    public void atualizarAnaliseFinalObjetoFornecimentoContrato(ObjetoFornecimentoContrato ofc, EnumAnaliseFinalItem status) {
        if (ofc == null || ofc.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do Objeto Fornecimento Contrato não pode ser null");
        }
        notaRemessaDAO.atualizarAnaliseFinalObjetoFornecimentoContrato(ofc, status);
    }

    public void alterarStatusDaExecucaoNotaRemessaFornecedor(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato) {
        if (notaRemessaOrdemFornecimentoContrato == null || notaRemessaOrdemFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id da nota fiscal não pode ser null");
        }
        notaRemessaDAO.alterarStatusDaExecucaoNotaRemessaFornecedor(notaRemessaOrdemFornecimentoContrato);
    }

    public void alterarStatusDaExecucaoNotaRemessaBeneficiario(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato) {
        if (notaRemessaOrdemFornecimentoContrato == null || notaRemessaOrdemFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id da nota fiscal não pode ser null");
        }
        notaRemessaDAO.alterarStatusDaExecucaoNotaRemessaBeneficiario(notaRemessaOrdemFornecimentoContrato);
    }

    public void alterarStatusDaExecucaoNotaRemessaContratante(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato) {
        if (notaRemessaOrdemFornecimentoContrato == null || notaRemessaOrdemFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id da nota fiscal não pode ser null");
        }
        notaRemessaDAO.alterarStatusDaExecucaoNotaRemessaContratante(notaRemessaOrdemFornecimentoContrato);
    }

    // Chamado somente pelo JOB AtualizarStatusNotaRemessaJob
    public void atualizarStatusNotasRemessasPeloJob() {
        notaRemessaDAO.atualizarStatusNotasRemessasPeloJob();
    }

    // Retorna os ids das notas fiscais que ainda não anexaram o Termo de
    // Recebimento assinado, a busca será feita
    // por ordem de fornecimento
    public List<Long> buscarIdsNotasFiscaisAindaNaoEnviadasPorOrdemFornecimento(OrdemFornecimentoContrato ordem) {
        return notaRemessaDAO.buscarIdsNotasRemessasAindaNaoEnviadasPorOrdemFornecimento(ordem);
    }

    public List<Long> buscarIdsItensSemTermoRecebimentoDefinitivoPorOrdemFornecimento(OrdemFornecimentoContrato ordem) {
        if (ordem == null || ordem.getId() == null) {
            throw new IllegalArgumentException("Parâmetro Ordem de Fornecimento não pode ser null");
        }
        return notaRemessaDAO.buscarIdsItensSemTermoRecebimentoDefinitivoPorOrdemFornecimento(ordem);
    }

    public List<Long> buscarIdsNotasFiscaisEnviadasPorOrdemFornecimento(OrdemFornecimentoContrato ordem) {
        return notaRemessaDAO.buscarIdsNotasRemessasAindaEnviadasPorOrdemFornecimento(ordem);
    }

    public List<Object> buscarIdsNotasRemessasPorPrograma(Programa programa) {
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do Programa não pode ser null");
        }

        return notaRemessaDAO.buscarIdsNotasRemessasPorPrograma(programa);
    }

    public Long buscarIdItemNotaRemessaPeloObjetoFornecimento(ObjetoFornecimentoContrato ofc) {

        if (ofc == null || ofc.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do objeto fornecimento contrato não pode ser null");
        }
        return notaRemessaDAO.buscarIdItemNotaRemessaPeloObjetoFornecimento(ofc);
    }

    public Long buscarIdItemOrdemFornecimentoPeloObjetoFornecimentoContrato(ObjetoFornecimentoContrato ofc) {
        if (ofc == null || ofc.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do objeto fornecimento contrato não pode ser null");
        }
        return notaRemessaDAO.buscarIdItemOrdemFornecimentoPeloObjetoFornecimentoContrato(ofc);
    }

    public void atualizarStatusExecucaoNotaRemessaContratantePeloIdNotaRemessa(EnumStatusExecucaoNotaRemessaContratante status, Long id) {
        notaRemessaDAO.atualizarStatusExecucaoNotaRemessaContratantePeloIdNotaRemessa(status, id);
    }

    public void atualizarStatusNotaRemessaBeneficiarioPeloIdEStatus(EnumStatusExecucaoNotaRemessaBeneficiario status, Long id) {
        notaRemessaDAO.atualizarStatusNotaRemessaBeneficiarioPeloIdEStatus(status, id);
    }

    public void atualizarStatusNotaRemessaFornecedorPeloIdEStatus(EnumStatusExecucaoNotaRemessaFornecedor status, Long id) {
        notaRemessaDAO.atualizarStatusNotaRemessaFornecedorPeloIdEStatus(status, id);
    }

    // METODOS PRIVADOS

    private void verificarAFaseQueIraANotaRemessa(NotaRemessaOrdemFornecimentoContrato notaRemessa) {
        verificarSeAlteraStatusNotaRemessaParaRecebido(notaRemessa);
        verificarSeAlteraStatusNotaRemessaParaEnviado(notaRemessa);
    }

    private void verificarSeAlteraStatusNotaRemessaParaRecebido(NotaRemessaOrdemFornecimentoContrato notaRemessa) {
        if (notaRemessa != null && notaRemessa.getDataEfetivaEntrega() != null && notaRemessa.getStatusExecucaoBeneficiario() == EnumStatusExecucaoNotaRemessaBeneficiario.EMITIDA) {
            notaRemessa.setStatusExecucaoBeneficiario(EnumStatusExecucaoNotaRemessaBeneficiario.RECEBIDO);
            
            //Irá atualizar o status de todos os itens desta nota de remessa para Recebido
            List<ObjetoFornecimentoContrato> listaObjetosAtualizar = notaRemessaDAO.buscarObjetoFornecimentoContratoPelaNotaRemessa(notaRemessa);
            for (ObjetoFornecimentoContrato objetoFornecimentoContrato : listaObjetosAtualizar) {
                ordemFornecimentoContratoService.atualizarSituacaoDoItemObjetoFornecimentoContratoPeloId(EnumSituacaoBem.RECEBIDO,objetoFornecimentoContrato.getId());
            }
        }
    }

    private void verificarSeAlteraStatusNotaRemessaParaEnviado(NotaRemessaOrdemFornecimentoContrato notaRemessa) {

        for (AnexoNotaRemessa anf : notaRemessa.getListaAnexosNotaRemessa()) {
            if (anf.getTipoArquivoTermoEntrega() == EnumTipoArquivoTermoEntrega.RELATORIO_RECEBIMENTO_ASSINADO) {
                notaRemessa.setStatusExecucaoBeneficiario(EnumStatusExecucaoNotaRemessaBeneficiario.ENVIADO);
            }
        }
    }

    private void validarCamposObrigatórios(NotaRemessaOrdemFornecimentoContrato notaRemessa) {
        BusinessException ex = new BusinessException();
        boolean erro = false;

        if (notaRemessa.getOrdemFornecimento() == null || notaRemessa.getOrdemFornecimento().getId() == null) {
            ex.addErrorMessage("Escolha da Ordem de Fornecimento é obrigatória.");
            erro = true;
        }

        if (notaRemessa.getDataPrevistaEntrega() == null) {
            ex.addErrorMessage("Data Prevista para a entrega é obrigatória.");
            erro = true;
        }

        if (notaRemessa.getNumeroNotaRemessa() == null || "".equalsIgnoreCase(notaRemessa.getNumeroNotaRemessa())) {
            ex.addErrorMessage("Número da nota de remessa é obrigatório.");
            erro = true;
        }

        if (erro) {
            throw ex;
        }
    }
    
    // baseado nas notas fiscais já cadastradas serão retirados os itens que já
    // estão em uso na lista de itens da ordem de fornecimento
    private void retiraItensJaemUsoDaListaComTodosOsItens(List<ItensOrdemFornecimentoContrato> listaTodosItens, List<NotaRemessaOrdemFornecimentoContrato> listaNotasRemessasCadastradas) {

        for (NotaRemessaOrdemFornecimentoContrato notaRemessa : listaNotasRemessasCadastradas) {

            // Busca todos os itens desta nota fiscal cadastrada
            List<ItensNotaRemessaOrdemFornecimentoContrato> todosItens = buscarItensNotaRemessa(notaRemessa);

            // Itera sobre os itens da NF, ao encontrar o mesmo item na lista de
            // todos com todos os itens, ele será retirado.
            for (ItensNotaRemessaOrdemFornecimentoContrato itemCadastrado : todosItens) {
                for (ItensOrdemFornecimentoContrato itemGeral : listaTodosItens) {
                    if (itemGeral.getId().equals(itemCadastrado.getItemOrdemFornecimentoContrato().getId())) {
                        listaTodosItens.remove(itemGeral);
                        break;
                    }
                }
            }
        }
    }
}
