package br.gov.mj.side.web.service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.enums.EnumFormaVerificacaoFormatacao;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumResponsavelPreencherFormatacaoItem;
import br.gov.mj.side.entidades.enums.EnumSituacaoBem;
import br.gov.mj.side.entidades.enums.EnumStatusComunicacaoOrdemFornecimento;
import br.gov.mj.side.entidades.enums.EnumStatusContrato;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaBeneficiario;
import br.gov.mj.side.entidades.enums.EnumStatusOrdemFornecimento;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.enums.EnumTipoObjeto;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoObjetoFornecimento;
import br.gov.mj.side.entidades.programa.licitacao.contrato.HistoricoComunicacaoGeracaoOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensFormatacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.web.dao.OrdemFornecimentoContratoDAO;
import br.gov.mj.side.web.dto.ItensDaOfPorLocalEntregaDto;
import br.gov.mj.side.web.dto.ItensOrdemFornecimentoPorEntidadeDto;
import br.gov.mj.side.web.dto.formatacaoObjetoFornecimento.FormatacaoItensContratoRespostaDto;
import br.gov.mj.side.web.dto.formatacaoObjetoFornecimento.FormatacaoObjetoFornecimentoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class OrdemFornecimentoContratoService {

    @Inject
    private OrdemFornecimentoContratoDAO ordemFornecimentoContratoDAO;

    @Inject
    private FormatacaoItensContratoService formatacaoItensContratoService;

    @Inject
    private IGenericPersister genericPersister;
    
    @Inject
    private ProgramaService programaService;
    
    @Inject
    private PublicizacaoService publicizacaoService;
    
    @Inject
    private ContratoService contratoService;
    
    @Inject
    private NotaRemessaService notaRemessaService;

    public List<FormatacaoObjetoFornecimentoDto> buscarListaFormatacaoObjetoFornecimento(Long idObjetoFornecimentoContrato, EnumPerfilEntidade perfilEntidade) {
        ObjetoFornecimentoContrato objetoFornecimentoContrato = new ObjetoFornecimentoContrato();
        objetoFornecimentoContrato.setId(idObjetoFornecimentoContrato);
        return buscarListaFormatacaoObjetoFornecimento(objetoFornecimentoContrato, perfilEntidade);
    }

    private List<FormatacaoObjetoFornecimentoDto> buscarListaFormatacaoObjetoFornecimento(ObjetoFornecimentoContrato objetoFornecimentoContrato, EnumPerfilEntidade perfilEntidade) {
        if (objetoFornecimentoContrato == null || objetoFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        List<FormatacaoObjetoFornecimentoDto> opcional = ordemFornecimentoContratoDAO.buscarListaFormatacaoObjetoFornecimento(objetoFornecimentoContrato, perfilEntidade, true);
        opcional.addAll(ordemFornecimentoContratoDAO.buscarListaFormatacaoObjetoFornecimento(objetoFornecimentoContrato, perfilEntidade, false));

        return opcional;
    }

    public ObjetoFornecimentoContrato buscarObjetoFornecimentoContrato(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id do objetoFornecimentoContrato não pode ser null");
        }
        return ordemFornecimentoContratoDAO.buscarObjetoFornecimentoContrato(id);
    }
    
    public ObjetoFornecimentoContrato buscarObjetoFornecimentoContratoClicado(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id do objetoFornecimentoContrato não pode ser null");
        }
        return ordemFornecimentoContratoDAO.buscarObjetoFornecimentoContratoClicado(id);
    }
    
    public ObjetoFornecimentoContrato buscarObjetoFornecimentoContratoPeloItemOrdemFornecimento(ItensOrdemFornecimentoContrato item){
        if (item.getId() == null && item.getId() == null) {
            throw new IllegalArgumentException("Parâmetro item do programa não pode ser null");
        }
        return ordemFornecimentoContratoDAO.buscarObjetoFornecimentoContratoPeloItemOrdemFornecimento(item);
    }
    
    public List<ObjetoFornecimentoContrato> buscarTodosObjetosFornecimentoContratoPeloItemOrdemFornecimento(ItensOrdemFornecimentoContrato item){
        if (item.getId() == null && item.getId() == null) {
            throw new IllegalArgumentException("Parâmetro item do programa não pode ser null");
        }
        return ordemFornecimentoContratoDAO.buscarTodosObjetosFornecimentoContratoPeloItemOrdemFornecimento(item);
    }
    
    public List<Long> buscarIdsOrdemFornecimentoComunicadaPorPrograma(Programa programa){
        if (programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do programa não pode ser null");
        }
        List<Long> listaDeIds = ordemFornecimentoContratoDAO.buscarIdsDasOrdensFornecimentoComunicadaPorPrograma(programa);
        return listaDeIds;
    }
    
    public List<Long> buscarIdsOrdemFornecimentoComunicadaPorContrato(Contrato contrato){
        if (contrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do contrato não pode ser null");
        }
        List<Long> listaDeIds = ordemFornecimentoContratoDAO.buscarIdsDasOrdensFornecimentoComunicadaPorContrato(contrato);
        return listaDeIds;
    }
    
    //Busca todas as notas fiscais onde a entrega efetiva ainda não foi realizada
    public List<Long> buscarIdsNotasFiscaisSemEntregaEfetivaPorOrdemFornecimento(OrdemFornecimentoContrato ordemFornecimentoContrato){
        if (ordemFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do contrato não pode ser null");
        }        
        return ordemFornecimentoContratoDAO.buscarIdsNotasFiscaisSemEntregaEfetivaPorOrdemFornecimento(ordemFornecimentoContrato);
    }

    public List<FormatacaoObjetoFornecimento> buscarFormatacoesObjetosPorPerfil(ObjetoFornecimentoContrato ofc, EnumResponsavelPreencherFormatacaoItem perfilResponsavelFormatacao){
        return ordemFornecimentoContratoDAO.buscarFormatacoesObjetosPorPerfil(ofc,perfilResponsavelFormatacao);
    }
    public List<ObjetoFornecimentoContrato> buscarListaObjetoFornecimentoContrato(OrdemFornecimentoContrato ordemFornecimentoContrato, LocalEntregaEntidade localEntregaEntidade,EnumTipoObjeto tipoObjeto) {
        if (ordemFornecimentoContrato == null || ordemFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id para a ordem de fornecimento não pode ser null");
        }
        
        if (tipoObjeto == null) {
            throw new IllegalArgumentException("Parametro 'tipoObjeto' não pode ser nulo, informe se deseja buscar os objetos originais, os objetos devolvidos ou se todos os objetos desta O.F.");
        }
        return ordemFornecimentoContratoDAO.buscarListaObjetoFornecimentoContrato(ordemFornecimentoContrato.getId(), localEntregaEntidade,tipoObjeto);
    }
    
    //Quando um objeto é criado ele contem informações para as formatações do Beneficiário e do Fornecedor, este metodo irá retirar os objetos que não possuem
    // formatações para o perfil informado como parametro.
    public List<ObjetoFornecimentoContrato> retirarObjetosQueNaoSaoDoPerfil(List<ObjetoFornecimentoContrato> listaObjetos, EnumPerfilEntidade perfilDaBusca){
        
        if (listaObjetos == null) {
            throw new IllegalArgumentException("Parâmetro listaObjetos para a ordem de fornecimento não pode ser null");
        }
        
        if (perfilDaBusca == null) {
            throw new IllegalArgumentException("Parâmetro perfilDaBusca para a ordem de fornecimento não pode ser null");
        }
        return ordemFornecimentoContratoDAO.retirarObjetosQueNaoSaoDoPerfil(listaObjetos, perfilDaBusca);
    }
    
    public ItensOrdemFornecimentoContrato buscarItemDaOrdemDeFornecimentoPeloObjetoFornecimento(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        
        if (objetoFornecimentoContrato == null) {
            throw new IllegalArgumentException("Parametro 'objetoFornecimentoContrato' não pode ser nulo.");
        }
        
        return ordemFornecimentoContratoDAO.buscarItemDaOrdemDeFornecimentoPeloObjetoFornecimento(objetoFornecimentoContrato);
    }

    public List<EnumFormaVerificacaoFormatacao> buscarFormaVerificacaoDoItemParaOF(OrdemFornecimentoContrato ordemFornecimentoContrato, Bem item) {

        if (ordemFornecimentoContrato == null || ordemFornecimentoContrato.getContrato() == null) {
            throw new IllegalArgumentException("Parâmetro contrato da ordemFornecimentoContrato não pode ser null");
        }
        if (item == null || item.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id de item não pode ser null");
        }

        boolean existeBem = false;
        Set<EnumFormaVerificacaoFormatacao> setForma = new HashSet<EnumFormaVerificacaoFormatacao>();
        List<FormatacaoContrato> listaFormatacaoContrato = formatacaoItensContratoService.buscarFormatacaoContrato(ordemFornecimentoContrato.getContrato());

        for (FormatacaoContrato formatacaoContrato : listaFormatacaoContrato) {
            existeBem = false;
            for (ItensFormatacao itensFormatacao : formatacaoContrato.getItens()) {
                if (itensFormatacao.getItem().getId().equals(item.getId())) {
                    existeBem = true;
                }
            }

            for (FormatacaoItensContrato formatacaoItensContrato : formatacaoContrato.getListaItensFormatacao()) {
                setForma.add(formatacaoItensContrato.getFormaVerificacao());
            }

            if (existeBem) {
                return new ArrayList<EnumFormaVerificacaoFormatacao>(setForma);
            }

        }

        return new ArrayList<EnumFormaVerificacaoFormatacao>();

    }

    public List<OrdemFornecimentoContrato> buscarOrdemFornecimentoContrato(Contrato contrato) {
        if (contrato == null || contrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return ordemFornecimentoContratoDAO.buscarOrdemFornecimentoContrato(contrato.getId());
    }

    /*
     * A variável 'somenteComunicação setada como 'true' irá trazer o último
     * histórico que tenha sido comunicado e não tenha sido cancelado Se for
     * setada como false irá trazer o último histórico independente de ter sido
     * comunicado ou não
     */
    public HistoricoComunicacaoGeracaoOrdemFornecimentoContrato buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(OrdemFornecimentoContrato ordemFornecimentoContrato, boolean somenteComunicacao) {
        if (ordemFornecimentoContrato == null || ordemFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return ordemFornecimentoContratoDAO.buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ordemFornecimentoContrato.getId(), somenteComunicacao);

    }

    public List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> buscarHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(OrdemFornecimentoContrato ordemFornecimentoContrato, boolean somenteComunicacao) {
        if (ordemFornecimentoContrato == null || ordemFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return ordemFornecimentoContratoDAO.buscarHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ordemFornecimentoContrato.getId(), somenteComunicacao);
    }

    public List<ItensOrdemFornecimentoContrato> buscarItensOrdemFornecimentoContrato(OrdemFornecimentoContrato ordemFornecimentoContrato) {
        if (ordemFornecimentoContrato == null || ordemFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return ordemFornecimentoContratoDAO.buscarItensOrdemFornecimentoContrato(ordemFornecimentoContrato.getId());
    }
    
    public ItensOrdemFornecimentoContrato buscarItensOrdemFornecimentoContratoPeloId(Long idItensOrdemFornecimentoContrato){
        if (idItensOrdemFornecimentoContrato == null) {
            throw new IllegalArgumentException("Parâmetro id do objeto 'idItensOrdemFornecimentoContrato' não pode ser null");
        }
        
        return ordemFornecimentoContratoDAO.buscarItensOrdemFornecimentoContratoPeloId(idItensOrdemFornecimentoContrato);
    }

    public List<ItensOrdemFornecimentoContrato> buscarPaginado(Contrato contrato, int first, int size, EnumOrder order, String propertyOrder) {

        if (contrato == null) {
            throw new IllegalArgumentException("Parâmetro contrato não pode ser null");
        }

        return ordemFornecimentoContratoDAO.buscarPaginado(contrato, first, size, order, propertyOrder);
    }

    public List<ItensOrdemFornecimentoContrato> buscarSemPaginacao(Contrato contrato) {

        if (contrato == null) {
            throw new IllegalArgumentException("Parâmetro contrato não pode ser null");
        }

        return ordemFornecimentoContratoDAO.buscarSemPaginacao(contrato);
    }

    public List<ItensOrdemFornecimentoContrato> buscarSemPaginacaoOrdenado(Contrato contrato, EnumOrder order, String propertyOrder) {

        if (contrato == null) {
            throw new IllegalArgumentException("Parâmetro contrato não pode ser null");
        }

        return ordemFornecimentoContratoDAO.buscarSemPaginacaoOrdenado(contrato, order, propertyOrder);
    }

    public OrdemFornecimentoContrato incluirAlterar(OrdemFornecimentoContrato ordemFornecimentoContrato, String usuarioLogado) {

        OrdemFornecimentoContrato ordemFornecimentoContratoRetorno = null;

        if (ordemFornecimentoContrato == null) {
            throw new IllegalArgumentException("Parâmetro ordemFornecimentoContrato não pode ser null");
        }

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório.");
            throw ex;
        }

        if (ordemFornecimentoContrato.getId() == null) {
            ordemFornecimentoContratoRetorno = ordemFornecimentoContratoDAO.incluir(ordemFornecimentoContrato, usuarioLogado);
        } else {
            ordemFornecimentoContratoRetorno = ordemFornecimentoContratoDAO.alterar(ordemFornecimentoContrato, usuarioLogado);
        }

        return ordemFornecimentoContratoRetorno;
    }

    public void excluir(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe OrdemFornecimentoContrato com esse id cadastrado */

        OrdemFornecimentoContrato f = genericPersister.findByUniqueProperty(OrdemFornecimentoContrato.class, "id", id);
        if (f == null) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("MN051", id);
            throw ex;
        }

        ordemFornecimentoContratoDAO.excluir(f);

    }

    public HistoricoComunicacaoGeracaoOrdemFornecimentoContrato gerarMinuta(OrdemFornecimentoContrato ordemFornecimentoContrato, byte[] minuta) {
        if (ordemFornecimentoContrato == null || ordemFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoComunicacaoGeracaoOrdemFornecimentoContrato = buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ordemFornecimentoContrato, false);
        BusinessException ex = new BusinessException();

        if (historicoComunicacaoGeracaoOrdemFornecimentoContrato != null && historicoComunicacaoGeracaoOrdemFornecimentoContrato.getPossuiComunicado() && !historicoComunicacaoGeracaoOrdemFornecimentoContrato.getPossuiCancelamento()) {
            ex.addErrorMessage("Ordem de fornecimento já foi comunicada.");
            throw ex;
        }
        return ordemFornecimentoContratoDAO.gerarMinuta(ordemFornecimentoContrato, minuta);
    }

    public HistoricoComunicacaoGeracaoOrdemFornecimentoContrato comunicarMinuta(OrdemFornecimentoContrato ordemFornecimentoContrato, String numeroDocumentoSei,String usuarioLogado) {

        if (ordemFornecimentoContrato == null || ordemFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        if (numeroSeiJaEmUso(ordemFornecimentoContrato.getId(), numeroDocumentoSei)) {
            ex.addErrorMessage("Número documento Sei já está em uso.");
            throw ex;
        }

        HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoComunicacaoGeracaoOrdemFornecimentoContrato = buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ordemFornecimentoContrato, false);

        // Se a ordem de fornecimento foi cancelada e for necessário comunicar a
        // mesma ordem de fornecimento
        // então será criado um novo histórico com uma nova data de geração para
        // esta ordem de fornecimento
        if (historicoComunicacaoGeracaoOrdemFornecimentoContrato != null && historicoComunicacaoGeracaoOrdemFornecimentoContrato.getPossuiComunicado() && historicoComunicacaoGeracaoOrdemFornecimentoContrato.getPossuiCancelamento()) {
            historicoComunicacaoGeracaoOrdemFornecimentoContrato = ordemFornecimentoContratoDAO.gerarNovoHistorico(ordemFornecimentoContrato, historicoComunicacaoGeracaoOrdemFornecimentoContrato);
        }

        if (historicoComunicacaoGeracaoOrdemFornecimentoContrato == null || historicoComunicacaoGeracaoOrdemFornecimentoContrato.getMinutaGerada() == null) {
            ex.addErrorMessage("Não existe minuta gerada para esta Ordem de fornecimento.");
            throw ex;
        }

        if (historicoComunicacaoGeracaoOrdemFornecimentoContrato.getPossuiComunicado() && !historicoComunicacaoGeracaoOrdemFornecimentoContrato.getPossuiCancelamento()) {
            ex.addErrorMessage("Ordem de fornecimento já foi comunicada.");
            throw ex;
        }

        // remover Objetos de fornecimento de comunicação enterior
        ordemFornecimentoContratoDAO.removeObjetoFornecimentoDaOrdemFornecimento(ordemFornecimentoContrato.getId());
        // inserir Objetos de fornecimento para nova comunicação
        ordemFornecimentoContratoDAO.inserirObjetoFornecimentoDaOrdemFornecimento(ordemFornecimentoContrato.getId());

        Programa programaMudarFase = programaService.buscarPeloId(ordemFornecimentoContrato.getContrato().getPrograma().getId());
        //Se o status do programa já for "Em execução" não será preciso alterar esta informação novamente.
        if(programaMudarFase.getStatusPrograma() != EnumStatusPrograma.EM_EXECUCAO){
            programaMudarFase.setStatusPrograma(EnumStatusPrograma.EM_EXECUCAO);
            programaService.atualizarInformacoesBasicasPrograma(programaMudarFase, usuarioLogado);
        }
        
        //Se o contrato não estiver em execução irá mudar o status
        if(ordemFornecimentoContrato.getContrato().getStatusContrato() == EnumStatusContrato.NAO_EXECUTADO){
            Contrato contrato = ordemFornecimentoContrato.getContrato();
            contrato.setStatusContrato(EnumStatusContrato.EM_EXECUCAO);
            contratoService.alterarStatusDoContrato(contrato);
        }
        
        //Irá criar um novo histórico da fase do programa
        publicizacaoService.criaHistoricoComunicacaoOrdemFornecimento(programaMudarFase, usuarioLogado);
        
        ordemFornecimentoContrato.setStatusOrdemFornecimento(EnumStatusOrdemFornecimento.EMITIDA);
        ordemFornecimentoContrato.setDataComunicacao(LocalDateTime.now());
        ordemFornecimentoContrato.setStatusComunicacaoOrdemFornecimento(EnumStatusComunicacaoOrdemFornecimento.COMUNICADA);
        ordemFornecimentoContratoDAO.alterarStatusComunicacaoDaOrdemFornecimento(ordemFornecimentoContrato);
        return ordemFornecimentoContratoDAO.comunicarMinuta(historicoComunicacaoGeracaoOrdemFornecimentoContrato.getId(), numeroDocumentoSei);
    }
    
    public HistoricoComunicacaoGeracaoOrdemFornecimentoContrato cancelarComunicacao(OrdemFornecimentoContrato ordemFornecimentoContrato, String motivoCancelamento, String usuarioLogado) {

        if (ordemFornecimentoContrato == null || ordemFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        BusinessException ex = new BusinessException();

        HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoComunicacaoGeracaoOrdemFornecimentoContrato = buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ordemFornecimentoContrato, true);

        if (historicoComunicacaoGeracaoOrdemFornecimentoContrato == null) {
            ex.addErrorMessage("Ordem de fornecimento não possui comunicação.");
            throw ex;
        }

        Programa programaMudarFase = programaService.buscarPeloId(ordemFornecimentoContrato.getContrato().getPrograma().getId());
        //Cria um novo histórico na tabela de publicizacao do programa que armazena o histórico desde o inicio do programa
        publicizacaoService.criaHistoricoCancelamentoOrdemFornecimento(programaMudarFase, usuarioLogado);
        
        // remover Objetos de fornecimento de comunicação enterior
        ordemFornecimentoContratoDAO.removeObjetoFornecimentoDaOrdemFornecimento(ordemFornecimentoContrato.getId());
        
        ordemFornecimentoContrato.setStatusComunicacaoOrdemFornecimento(EnumStatusComunicacaoOrdemFornecimento.CANCELADA);
        ordemFornecimentoContratoDAO.alterarStatusComunicacaoDaOrdemFornecimento(ordemFornecimentoContrato);
        
        if(verificaSeOProgramaDeveraVoltarAFaseAnterior(ordemFornecimentoContrato)){
            programaMudarFase.setStatusPrograma(EnumStatusPrograma.ABERTO_GERACAO_CONTRATO);
            programaService.atualizarInformacoesBasicasPrograma(programaMudarFase, usuarioLogado);
        }
        
        if(verificaSeOContratoDeveraVoltarAFaseAnterior(ordemFornecimentoContrato)){
            Contrato alterar = ordemFornecimentoContrato.getContrato();
            alterar.setStatusContrato(EnumStatusContrato.NAO_EXECUTADO);
            contratoService.alterarStatusDoContrato(alterar);
        }
        
        return ordemFornecimentoContratoDAO.cancelarComunicacao(historicoComunicacaoGeracaoOrdemFornecimentoContrato.getId(), motivoCancelamento);
    }
    
    private boolean verificaSeOProgramaDeveraVoltarAFaseAnterior(OrdemFornecimentoContrato ordemFornecimentoContrato){
        
        //Se este programa não tiver mais O.F's comunicadas deverá retornar a fase anterior.
        List<Long> listaIds = buscarIdsOrdemFornecimentoComunicadaPorPrograma(ordemFornecimentoContrato.getContrato().getPrograma());
        if(listaIds.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    
    public void inserirObjetoFornecimentoEnviadoParaCorrecao(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        ordemFornecimentoContratoDAO.inserirObjetoFornecimentoEnviadoParaCorrecao(objetoFornecimentoContrato);
    }
    
    private boolean verificaSeOContratoDeveraVoltarAFaseAnterior(OrdemFornecimentoContrato ordemFornecimentoContrato){
        
        //Se este contrato não tiver mais O.F's comunicadas deverá retornar a fase anterior.
        List<Long> listaIds = buscarIdsOrdemFornecimentoComunicadaPorContrato(ordemFornecimentoContrato.getContrato());
        if(listaIds.isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    private boolean numeroSeiJaEmUso(Long idOrdemFornecimentoContrato, String numeroDocumentoSei) {

        HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historico = new HistoricoComunicacaoGeracaoOrdemFornecimentoContrato();
        OrdemFornecimentoContrato of = new OrdemFornecimentoContrato();
        of.setId(idOrdemFornecimentoContrato);

        historico.setOrdemFornecimento(of);
        historico.setNumeroDocumentoSei(numeroDocumentoSei);

        return ordemFornecimentoContratoDAO.buscarDuplicado(historico).isEmpty() ? false : true;
    }
    
    public FormatacaoObjetoFornecimento incluirAlterarFormatacaoItensContratoResposta(FormatacaoObjetoFornecimento formatacaoObjetoFornecimento) {

        FormatacaoObjetoFornecimento formatacaoObjetoFornecimentoRetorno = null;

        if (formatacaoObjetoFornecimento.getFormatacaoResposta() == null) {
            throw new IllegalArgumentException("Parâmetro formatacaoItensContratoResposta não pode ser null");
        }
        atualizarStatusDaNotaRemessaRecebendoResposta(formatacaoObjetoFornecimento);
        
        

        if (formatacaoObjetoFornecimento.getFormatacaoResposta().getId() == null) {
            formatacaoObjetoFornecimentoRetorno = ordemFornecimentoContratoDAO.incluir(formatacaoObjetoFornecimento);
        } else {
            formatacaoObjetoFornecimentoRetorno = ordemFornecimentoContratoDAO.alterar(formatacaoObjetoFornecimento);
        }

        return formatacaoObjetoFornecimentoRetorno;

    }
    
    public void atualizarStatusOrdemFornecimentoPeloId(Long id, EnumStatusOrdemFornecimento status){
        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id da Ordem de Fornecimento não pode ser null");
        }
        
        if (status == null) {
            throw new IllegalArgumentException("Parâmetro status da Ordem de Fornecimento não pode ser null");
        }
        ordemFornecimentoContratoDAO.atualizarStatusOrdemFornecimentoPeloId(id, status);
    }
    
    public void alterarStatusObjetoFornecimentoAoInformarPatrimoniamento(ObjetoFornecimentoContrato objetoFornecimentoContrato){
        if (objetoFornecimentoContrato == null || objetoFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro objeto Fornecimento Contrato não pode ser null");
        }
        
        if (objetoFornecimentoContrato.getItemPatrimoniavel() == null) {
            throw new IllegalArgumentException("Parâmetro itemPatrimoniável do objetoFornecimentoContrato não pode ser null");
        }
        
        if(objetoFornecimentoContrato.getMotivoItemNaoPatrimoniavel() == null){
            objetoFornecimentoContrato.setMotivoItemNaoPatrimoniavel(" ");
        }
        ordemFornecimentoContratoDAO.alterarStatusObjetoFornecimentoAoInformarPatrimoniamento(objetoFornecimentoContrato);
    }
    
  //Quando o representante responder o primeiro item da formatação e salvar o status da nota fiscal deste item será alterado para 'EM_ANALISE'
    private void atualizarStatusDaNotaRemessaRecebendoResposta(FormatacaoObjetoFornecimento formatacaoObjetoFornecimento){
        if(formatacaoObjetoFornecimento.getResponsavelFormatacao() == EnumPerfilEntidade.BENEFICIARIO){
            List<BigInteger> listaIds = notaRemessaService.buscarIdsNotaRemessaPorFormatacaoObjetoFornecimento(formatacaoObjetoFornecimento);
            for (BigInteger long1 : listaIds) {
                notaRemessaService.atualizarStatusNotaRemessaBeneficiarioPeloIdEStatus(EnumStatusExecucaoNotaRemessaBeneficiario.EM_ANALISE,long1.longValue());
            }
        }
    }

    public ObjetoFornecimentoContrato incluirAlterarConformidadeItem(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        if (objetoFornecimentoContrato == null) {
            throw new IllegalArgumentException("Parâmetro objetoFornecimentoContrato não pode ser null");
        }
        return ordemFornecimentoContratoDAO.incluirAlterarConformidadeItem(objetoFornecimentoContrato);
    }

    public ObjetoFornecimentoContrato incluirAlterarFormatacaoItensContratoRespostaDesktop(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        if (objetoFornecimentoContrato == null) {
            throw new IllegalArgumentException("Parâmetro objetoFornecimentoContrato não pode ser null");
        }
        return ordemFornecimentoContratoDAO.incluirAlterar(objetoFornecimentoContrato);
    }

    // Este metodo pega todos os itens desta ordem de fornecimento e organiza
    // para que fiquem divididos por endereço de entrega
    public List<ItensOrdemFornecimentoPorEntidadeDto> buscarItensDeOrdemFornecimentoPorLocalEntrega(OrdemFornecimentoContrato ordemFornecimentoContrato) {
        Map<Long, ItensOrdemFornecimentoPorEntidadeDto> mapaItem = new HashMap<Long, ItensOrdemFornecimentoPorEntidadeDto>();

        List<ItensOrdemFornecimentoPorEntidadeDto> listaLocaisEntrega = new ArrayList<ItensOrdemFornecimentoPorEntidadeDto>();

        List<ItensOrdemFornecimentoContrato> lista = buscarItensOrdemFornecimentoContrato(ordemFornecimentoContrato);
        for (ItensOrdemFornecimentoContrato ordem : lista) {
            Long chave = ordem.getLocalEntrega().getId();

            if (!mapaItem.containsKey(chave)) {
                ItensOrdemFornecimentoPorEntidadeDto execucao = new ItensOrdemFornecimentoPorEntidadeDto();
                execucao.setEntidade(ordem.getLocalEntrega().getEntidade());
                execucao.setMunicipio(ordem.getLocalEntrega().getMunicipio());
                execucao.setLocalEntrega(ordem.getLocalEntrega());
                execucao.setOrdemFornecimento(ordem.getOrdemFornecimento());

                ItensDaOfPorLocalEntregaDto itens = new ItensDaOfPorLocalEntregaDto();
                itens.setBem(ordem.getItem());
                itens.setQuantidade(ordem.getQuantidade());

                execucao.getListaItens().add(itens);

                mapaItem.put(chave, execucao);
            } else {
                ItensOrdemFornecimentoPorEntidadeDto execucao = mapaItem.get(chave);

                ItensDaOfPorLocalEntregaDto itens = new ItensDaOfPorLocalEntregaDto();
                itens.setBem(ordem.getItem());
                itens.setQuantidade(ordem.getQuantidade());

                execucao.getListaItens().add(itens);
            }
        }

        listaLocaisEntrega.addAll(mapaItem.values());
        return listaLocaisEntrega;
    }

    public FormatacaoItensContratoRespostaDto buscarFormatacaoItensContratoRespostaDownloadPeloId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return ordemFornecimentoContratoDAO.buscarFormatacaoItensContratoRespostaDownloadPeloId(id);
    }

    public void alterarStatusOrdemFornecimento(OrdemFornecimentoContrato ordemFornecimentoContrato) {
        if (ordemFornecimentoContrato == null || ordemFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id da ordem de fornecimento não pode ser null");
        }
        ordemFornecimentoContratoDAO.alterarStatusDaOrdemFornecimento(ordemFornecimentoContrato);
    }
    
    public void atualizarSituacaoDoItemObjetoFornecimentoContratoPeloId(EnumSituacaoBem situacao, Long idObjetoFornecimentoContrato){
        if (situacao == null) {
            throw new IllegalArgumentException("Parâmetro situacao do objeto fornecimento contrato não pode ser null");
        }
        
        if (idObjetoFornecimentoContrato == null) {
            throw new IllegalArgumentException("Parâmetro situacao do objeto fornecimento contrato não pode ser null");
        }
        
        ordemFornecimentoContratoDAO.atualizarSituacaoDoItemObjetoFornecimentoContratoPeloId(situacao, idObjetoFornecimentoContrato);
    }
}
