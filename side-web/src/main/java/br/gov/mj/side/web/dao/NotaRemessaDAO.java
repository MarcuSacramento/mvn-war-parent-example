package br.gov.mj.side.web.dao;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.enums.EnumAnaliseFinalItem;
import br.gov.mj.side.entidades.enums.EnumSituacaoGeracaoTermos;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaBeneficiario;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaContratante;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaFornecedor;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoTermoEntrega;
import br.gov.mj.side.entidades.enums.EnumTipoRetornoObjeto;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoObjetoFornecimento;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.AnexoNotaRemessa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.ItensNotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.TermoRecebimentoDefinitivo;
import br.gov.mj.side.web.dto.ListaBeneficiariosSemEnvioRelatorioRecebimentoDto;
import br.gov.mj.side.web.dto.NotaRemessaPesquisaDto;
import br.gov.mj.side.web.dto.ObjetoFornecimentoContratoDto;
import br.gov.mj.side.web.service.ObjetoFornecimentoContratoService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class NotaRemessaDAO {

    @Inject
    private EntityManager em;

    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;
    
    @Inject
    private ObjetoFornecimentoContratoService objetoFornecimentoContratoService;
    
    public List<NotaRemessaOrdemFornecimentoContrato> buscarNotasRemessasPeloProgramaEBeneficiarioPaginadoOrdenado(Programa programa, Entidade entidade, int first, int size, EnumOrder order, String propertyOrder) {

        List<NotaRemessaOrdemFornecimentoContrato> listaTemp = buscarNotasRemessasPeloProgramaEBeneficiarioOrdenado(programa, entidade, order, propertyOrder);

        List<NotaRemessaOrdemFornecimentoContrato> listaRetorno = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
        if (!listaTemp.isEmpty()) {
            int inicio = first;
            int fim = first + size;

            if (fim > listaTemp.size()) {
                fim = listaTemp.size();
            }
            for (int i = inicio; i < fim; i++) {
                listaRetorno.add(listaTemp.get(i));
            }
        }
        return listaRetorno;
    }

    public List<NotaRemessaOrdemFornecimentoContrato> buscarPaginado(NotaRemessaPesquisaDto notaRemessaPesquisaDto, int first, int size, EnumOrder order, String propertyOrder) {

        List<NotaRemessaOrdemFornecimentoContrato> lista1 = buscarSemPaginacaoOrdenado(notaRemessaPesquisaDto, order, propertyOrder);
        // filtra paginado
        List<NotaRemessaOrdemFornecimentoContrato> listaRetorno = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
        if (!lista1.isEmpty()) {
            int inicio = first;
            int fim = first + size;

            if (fim > lista1.size()) {
                fim = lista1.size();
            }
            for (int i = inicio; i < fim; i++) {
                listaRetorno.add(lista1.get(i));
            }
        }

        return listaRetorno;
    }

    public List<NotaRemessaOrdemFornecimentoContrato> buscarSemPaginacao(NotaRemessaPesquisaDto notaRemessaPesquisaDto) {
        return buscarSemPaginacaoOrdenado(notaRemessaPesquisaDto, EnumOrder.ASC, "id");
    }

    public List<NotaRemessaOrdemFornecimentoContrato> buscarListaNotasRemessaCadastradas(OrdemFornecimentoContrato ordemFornecimento) {
        NotaRemessaPesquisaDto notaRemessaDto = new NotaRemessaPesquisaDto();
        notaRemessaDto.setNotaRemessa(new NotaRemessaOrdemFornecimentoContrato());
        notaRemessaDto.getNotaRemessa().setOrdemFornecimento(ordemFornecimento);
        return buscarSemPaginacao(notaRemessaDto);
    }

    public List<ItensNotaRemessaOrdemFornecimentoContrato> buscarItensNotaRemessa(NotaRemessaOrdemFornecimentoContrato notaRemessa) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ItensNotaRemessaOrdemFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ItensNotaRemessaOrdemFornecimentoContrato.class);
        Root<ItensNotaRemessaOrdemFornecimentoContrato> root = criteriaQuery.from(ItensNotaRemessaOrdemFornecimentoContrato.class);
        List<Predicate> predicates = new ArrayList<>();
        if (notaRemessa.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("notaRemessaOrdemFornecimento").get("id"), notaRemessa.getId()));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ItensNotaRemessaOrdemFornecimentoContrato> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }
    
    //Uma nota de remessa pode ter varios objetosFornecimentoContrato, este serviço irá retornar todos os objetos
    public List<ObjetoFornecimentoContrato> buscarObjetoFornecimentoContratoPelaNotaRemessa(NotaRemessaOrdemFornecimentoContrato notaRemessaFornecimentoContrato){
        
        //Busca todos os itens da nota de remessa
        List<ItensNotaRemessaOrdemFornecimentoContrato> listaItens = buscarItensNotaRemessa(notaRemessaFornecimentoContrato);
        
        List<ObjetoFornecimentoContrato> listaObjetoRetornar = new ArrayList<ObjetoFornecimentoContrato>();
        for(ItensNotaRemessaOrdemFornecimentoContrato item:listaItens){
            ObjetoFornecimentoContrato ofc = ordemFornecimentoContratoService.buscarObjetoFornecimentoContratoPeloItemOrdemFornecimento(item.getItemOrdemFornecimentoContrato());
            listaObjetoRetornar.add(ofc);
        }
        
        return listaObjetoRetornar;
    }
    
    //Uma nota de remessa pode ter varios objetosFornecimentoContrato, este serviço irá retornar todos os objetos
    public List<ObjetoFornecimentoContrato> buscarTodosObjetoFornecimentoContratoPelaNotaRemessa(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato){
        
        //Busca todos os itens da nota de remessa
        List<ItensNotaRemessaOrdemFornecimentoContrato> listaItens = buscarItensNotaRemessa(notaRemessaOrdemFornecimentoContrato);
        
        List<ObjetoFornecimentoContrato> listaObjetoRetornar = new ArrayList<ObjetoFornecimentoContrato>();
        
        
            for(ItensNotaRemessaOrdemFornecimentoContrato item:listaItens){
                ObjetoFornecimentoContratoDto dto = new ObjetoFornecimentoContratoDto();
                
                ObjetoFornecimentoContrato obj = new ObjetoFornecimentoContrato();
                obj.setItem(item.getItemOrdemFornecimentoContrato().getItem());
                obj.setLocalEntrega(item.getItemOrdemFornecimentoContrato().getLocalEntrega());
                obj.setOrdemFornecimento(item.getItemOrdemFornecimentoContrato().getOrdemFornecimento());
                //obj.setNotaRemessaOrdemFornecimentoContrato(notaRemessaOrdemFornecimentoContrato);
                
                //Se a nota não tiver sido executada então os objetos ainda não possuem id da nota setada para ela
                if(notaRemessaOrdemFornecimentoContrato.getStatusExecucaoFornecedor() == EnumStatusExecucaoNotaRemessaFornecedor.EMITIDA ||
                   notaRemessaOrdemFornecimentoContrato.getStatusExecucaoFornecedor() == EnumStatusExecucaoNotaRemessaFornecedor.ENTREGUE ||
                   notaRemessaOrdemFornecimentoContrato.getStatusExecucaoFornecedor() == EnumStatusExecucaoNotaRemessaFornecedor.CONCLUIDA){
                    
                    obj.setNotaRemessaOrdemFornecimentoContrato(notaRemessaOrdemFornecimentoContrato);
                }
                
                dto.setObjetoFornecimentoContrato(obj);
                listaObjetoRetornar.addAll(objetoFornecimentoContratoService.buscarSemPaginacao(dto));
        }
        
        /*if(tipoRetorno == EnumTipoRetornoObjeto.TODOS){
            for(ItensNotaRemessaOrdemFornecimentoContrato item:listaItens){
                List<ObjetoFornecimentoContrato> ofc = ordemFornecimentoContratoService.buscarTodosObjetosFornecimentoContratoPeloItemOrdemFornecimento(item.getItemOrdemFornecimentoContrato());
                
                
                listaObjetoRetornar.addAll(ofc);
            }
        }else{
            ObjetoFornecimentoContratoDto dto = new ObjetoFornecimentoContratoDto();
            ObjetoFornecimentoContrato obj = new ObjetoFornecimentoContrato();
            obj.setNotaRemessaOrdemFornecimentoContrato(notaRemessaOrdemFornecimentoContrato);
            dto.setObjetoFornecimentoContrato(obj);
            
            listaObjetoRetornar = objetoFornecimentoContratoService.buscarSemPaginacao(dto);
        }*/
        return listaObjetoRetornar;
    }

    public List<NotaRemessaOrdemFornecimentoContrato> buscarSemPaginacaoOrdenado(NotaRemessaPesquisaDto notaRemessaPesquisaDto, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<NotaRemessaOrdemFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(NotaRemessaOrdemFornecimentoContrato.class);
        Root<NotaRemessaOrdemFornecimentoContrato> root = criteriaQuery.from(NotaRemessaOrdemFornecimentoContrato.class);

        Predicate[] predicates = extractPredicates(notaRemessaPesquisaDto.getNotaRemessa(), criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        }

        TypedQuery<NotaRemessaOrdemFornecimentoContrato> query = em.createQuery(criteriaQuery);
        return retornaListaFiltrada(notaRemessaPesquisaDto, query);
    }

    private List<NotaRemessaOrdemFornecimentoContrato> retornaListaFiltrada(NotaRemessaPesquisaDto notaRemessaPesquisaDto, TypedQuery<NotaRemessaOrdemFornecimentoContrato> query) {
        List<NotaRemessaOrdemFornecimentoContrato> programasFiltradosPorPredicates = query.getResultList();
        return retornaListaFiltrada(programasFiltradosPorPredicates, notaRemessaPesquisaDto);

    }

    public NotaRemessaOrdemFornecimentoContrato buscarPeloId(Long id) {
        return em.find(NotaRemessaOrdemFornecimentoContrato.class, id);
    }

    private List<NotaRemessaOrdemFornecimentoContrato> retornaListaFiltrada(List<NotaRemessaOrdemFornecimentoContrato> notaRemessaFiltradasPorPredicates, NotaRemessaPesquisaDto notaRemessaPesquisaDto) {

        List<NotaRemessaOrdemFornecimentoContrato> notaRemessaFiltradasFinal = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();

        boolean possuiNumeroNotaRemessa = false;
        boolean possuiCodigoGerado = false;
        boolean possuiItemNotaRemessa = false;
        boolean possuiOrdemDeFornecimento = false;

        NotaRemessaOrdemFornecimentoContrato notaRemessaPesquisar = notaRemessaPesquisaDto.getNotaRemessa();

        if (notaRemessaPesquisar.getCodigoGerado() != null || !notaRemessaPesquisar.getListaItensNotaRemessaOrdemFornecimentoContratos().isEmpty() || notaRemessaPesquisar.getNumeroNotaRemessa() != null) {

            for (NotaRemessaOrdemFornecimentoContrato notaRemessa : notaRemessaFiltradasPorPredicates) {

                // Busca pelo número da nota remessa
                if (notaRemessaPesquisar.getNumeroNotaRemessa() == null || existeNumeroNotaRemessa(notaRemessa, notaRemessaPesquisar.getNumeroNotaRemessa())) {
                    possuiNumeroNotaRemessa = true;
                }

                // Busca pelo código gerado
                if (notaRemessaPesquisar.getCodigoGerado() == null || existeCodigoGerado(notaRemessa, notaRemessaPesquisar.getCodigoGerado())) {
                    possuiCodigoGerado = true;
                }

                // Pesquisar por item
                if (notaRemessaPesquisaDto.getItemNotaRemessa() == null || existeItemNaNotaRemessa(notaRemessa.getListaItensNotaRemessaOrdemFornecimentoContratos(), notaRemessaPesquisaDto.getItemNotaRemessa())) {
                    possuiItemNotaRemessa = true;
                }

                if (notaRemessaPesquisar.getOrdemFornecimento() == null || existeOrdemDeFornecimento(notaRemessa.getOrdemFornecimento(), notaRemessaPesquisar.getOrdemFornecimento())) {
                    possuiOrdemDeFornecimento = true;
                }

                if (possuiCodigoGerado && possuiItemNotaRemessa && possuiNumeroNotaRemessa && possuiOrdemDeFornecimento) {
                    notaRemessaFiltradasFinal.add(notaRemessa);
                }
            }

            possuiNumeroNotaRemessa = false;
            possuiCodigoGerado = false;
            possuiItemNotaRemessa = false;
            possuiOrdemDeFornecimento = false;

        } else {
            return notaRemessaFiltradasPorPredicates;
        }
        return notaRemessaFiltradasFinal;
    }

    private boolean existeNumeroNotaRemessa(NotaRemessaOrdemFornecimentoContrato notaRemessa, String numeroNotaRemessa) {
        return UtilDAO.removerAcentos(notaRemessa.getNumeroNotaRemessa().toLowerCase()).contains(UtilDAO.removerAcentos(numeroNotaRemessa));
    }

    private boolean existeCodigoGerado(NotaRemessaOrdemFornecimentoContrato notaRemessa, String codigoGerado) {
        return UtilDAO.removerAcentos(notaRemessa.getCodigoGerado().toLowerCase()).contains(UtilDAO.removerAcentos(codigoGerado));
    }

    private boolean existeItemNaNotaRemessa(List<ItensNotaRemessaOrdemFornecimentoContrato> listaItens, ItensNotaRemessaOrdemFornecimentoContrato itemNotaRemessa) {
        for (ItensNotaRemessaOrdemFornecimentoContrato itemLista : listaItens) {
            if (itemLista.getId().equals(itemNotaRemessa.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean existeOrdemDeFornecimento(OrdemFornecimentoContrato ordemFornecimentoSalvo, OrdemFornecimentoContrato ordemPesquisa) {
        if (ordemFornecimentoSalvo != null && ordemPesquisa != null) {
            if (ordemFornecimentoSalvo.getId().equals(ordemFornecimentoSalvo.getId())) {
                return true;
            }
        }
        return false;
    }

    private Predicate[] extractPredicates(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        // Pesquisa as Notas de Remessas a partir do id da ordem de fornecimento
        if (notaRemessaOrdemFornecimentoContrato != null && notaRemessaOrdemFornecimentoContrato.getOrdemFornecimento() != null && notaRemessaOrdemFornecimentoContrato.getOrdemFornecimento().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("ordemFornecimento").get("id"), notaRemessaOrdemFornecimentoContrato.getOrdemFornecimento().getId()));
        }

        // Nota Remessa
        if (notaRemessaOrdemFornecimentoContrato != null && StringUtils.isNotBlank(notaRemessaOrdemFornecimentoContrato.getNumeroNotaRemessa())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("numeroNotaRemessa"))), "%" + UtilDAO.removerAcentos(notaRemessaOrdemFornecimentoContrato.getNumeroNotaRemessa().toLowerCase()) + "%"));
        }

        // Código gerado
        if (notaRemessaOrdemFornecimentoContrato != null && StringUtils.isNotBlank(notaRemessaOrdemFornecimentoContrato.getCodigoGerado())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("codigoGerado"))), "%" + UtilDAO.removerAcentos(notaRemessaOrdemFornecimentoContrato.getCodigoGerado().toLowerCase()) + "%"));
        }
        return predicates.toArray(new Predicate[] {});
    }

    public NotaRemessaOrdemFornecimentoContrato incluir(NotaRemessaOrdemFornecimentoContrato notaRemessa) {

        notaRemessa.setStatusExecucaoBeneficiario(EnumStatusExecucaoNotaRemessaBeneficiario.NAO_EMITIDA);
        notaRemessa.setStatusExecucaoFornecedor(EnumStatusExecucaoNotaRemessaFornecedor.EM_PREPARACAO);
        notaRemessa.setStatusExecucaoContratante(EnumStatusExecucaoNotaRemessaContratante.AGUARDANDO_RELATORIO_RECEBIMENTO);

        for (AnexoNotaRemessa anexo : notaRemessa.getListaAnexosNotaRemessa()) {
            anexo.setNotaRemessaOrdemFornecimento(notaRemessa);
        }

        for (ItensNotaRemessaOrdemFornecimentoContrato itens : notaRemessa.getListaItensNotaRemessaOrdemFornecimentoContratos()) {
            itens.setItemOrdemFornecimentoContrato(em.find(ItensOrdemFornecimentoContrato.class, itens.getItemOrdemFornecimentoContrato().getId()));
            itens.setNotaRemessaOrdemFornecimento(notaRemessa);
        }
        em.persist(notaRemessa);
        return notaRemessa;
    }

    public NotaRemessaOrdemFornecimentoContrato alterar(NotaRemessaOrdemFornecimentoContrato notaRemessa) {
        NotaRemessaOrdemFornecimentoContrato notaParaMerge = buscarPeloId(notaRemessa.getId());

        notaParaMerge.setCodigoGerado(notaRemessa.getCodigoGerado());
        notaParaMerge.setDataEfetivaEntrega(notaRemessa.getDataEfetivaEntrega());
        notaParaMerge.setDataPrevistaEntrega(notaRemessa.getDataPrevistaEntrega());
        notaParaMerge.setNumeroNotaRemessa(notaRemessa.getNumeroNotaRemessa());
        notaParaMerge.setOrdemFornecimento(notaRemessa.getOrdemFornecimento());
        notaParaMerge.setStatusExecucaoFornecedor(notaRemessa.getStatusExecucaoFornecedor());
        notaParaMerge.setStatusExecucaoBeneficiario(notaRemessa.getStatusExecucaoBeneficiario());
        notaParaMerge.setStatusExecucaoContratante(notaRemessa.getStatusExecucaoContratante());
        notaParaMerge.setCodigoInformadoPeloFornecedor(notaRemessa.getCodigoInformadoPeloFornecedor());

        sincronizarAnexos(notaRemessa.getListaAnexosNotaRemessa(), notaParaMerge);
        sincronizarItensDaNotaRemessa(notaRemessa.getListaItensNotaRemessaOrdemFornecimentoContratos(), notaParaMerge);
        return em.merge(notaParaMerge);
    }

    public NotaRemessaOrdemFornecimentoContrato alterarStatusDaExecucaoNotaRemessaFornecedor(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato) {
        NotaRemessaOrdemFornecimentoContrato notaAlterar = em.find(NotaRemessaOrdemFornecimentoContrato.class, notaRemessaOrdemFornecimentoContrato.getId());
        notaAlterar.setStatusExecucaoFornecedor(notaRemessaOrdemFornecimentoContrato.getStatusExecucaoFornecedor());
        return em.merge(notaAlterar);
    }

    public NotaRemessaOrdemFornecimentoContrato alterarStatusDaExecucaoNotaRemessaBeneficiario(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato) {
        NotaRemessaOrdemFornecimentoContrato notaAlterar = em.find(NotaRemessaOrdemFornecimentoContrato.class, notaRemessaOrdemFornecimentoContrato.getId());
        notaAlterar.setStatusExecucaoBeneficiario(notaRemessaOrdemFornecimentoContrato.getStatusExecucaoBeneficiario());
        return em.merge(notaAlterar);
    }

    public NotaRemessaOrdemFornecimentoContrato alterarStatusDaExecucaoNotaRemessaContratante(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato) {
        NotaRemessaOrdemFornecimentoContrato notaAlterar = em.find(NotaRemessaOrdemFornecimentoContrato.class, notaRemessaOrdemFornecimentoContrato.getId());
        notaAlterar.setStatusExecucaoContratante(notaRemessaOrdemFornecimentoContrato.getStatusExecucaoContratante());
        return em.merge(notaAlterar);
    }

    // Este metodo será chamado pelo job AtualizarStatusNotaRemessaJob
    // Irá verificar Notas Remessas que estão em atraso do envio do relatório de
    // recebimento
    public void atualizarStatusNotasRemessasPeloJob() {
        atualizarNotasRemessasComRelatoriosEnviados();
    }

    // Metodo chamado somente pelo Job 'AtualizarStatusNotaRemessaJob' para
    // atualizar
    // o status da nota remessa para 'ATRASO' ou 'INADIPLENTE' quando não for
    // anexado o Termo de recebimento.
    private void atualizarNotasRemessasComRelatoriosEnviados() {
        String sqlEnviar = "select nrc.id, nrc.dataEfetivaEntrega from NotaRemessaOrdemFornecimentoContrato as nrc " + "where ( nrc.dataEfetivaEntrega is not null ) " + "and ( nrc.statusExecucaoBeneficiario<>'ENVI' ) "
                + "and ( ( nrc.dataEfetivaEntrega=:atraso10dias ) or ( nrc.dataEfetivaEntrega=:atraso30dias ) )";
        Query queryEnviar = em.createQuery(sqlEnviar);
        queryEnviar.setParameter("atraso10dias", LocalDate.now().minusDays(11));
        queryEnviar.setParameter("atraso30dias", LocalDate.now().minusDays(31));
        List<Object> listaIds = queryEnviar.getResultList();
        for (Object obj : listaIds) {

            Object[] o = (Object[]) obj;
            Long id = (Long) o[0];
            LocalDate atrasoBanco = (LocalDate) o[1];

            LocalDate atraso10Dias = LocalDate.now().minusDays(11);
            LocalDate atraso30Dias = LocalDate.now().minusDays(31);

            if (atrasoBanco.isEqual(atraso10Dias)) {
                atualizarStatusNotaRemessaBeneficiarioPeloIdEStatus(EnumStatusExecucaoNotaRemessaBeneficiario.EM_ATRASO, id);
                continue;
            }

            if (atrasoBanco.isEqual(atraso30Dias)) {
                atualizarStatusNotaRemessaBeneficiarioPeloIdEStatus(EnumStatusExecucaoNotaRemessaBeneficiario.INADIPLENTE, id);
            }
        }
    }

    public List<Long> buscarObjetoFornecimentoPelaNotaRemessaStatusFinal(NotaRemessaOrdemFornecimentoContrato notaRemessa, EnumAnaliseFinalItem analise) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        Root<ObjetoFornecimentoContrato> rootOfo = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        Join<ObjetoFornecimentoContrato, TermoRecebimentoDefinitivo> joinTermoRecebimento = rootOfo.join("termoRecebimentoDefinitivo");
        Join<TermoRecebimentoDefinitivo, NotaRemessaOrdemFornecimentoContrato> joinNotaRemessa = joinTermoRecebimento.join("notaRemessaOrdemFornecimentoContrato");

        Predicate predNota = criteriaBuilder.equal(joinNotaRemessa.get("id"), notaRemessa.getId());

        Predicate predStatusOfo = null;
        if (analise != null) {
            predStatusOfo = criteriaBuilder.equal(rootOfo.get("analiseFinalItem"), analise);
            criteriaQuery.select(rootOfo.get("id")).where(criteriaBuilder.and(predNota, predStatusOfo));
        } else {
            criteriaQuery.select(rootOfo.get("id")).where(criteriaBuilder.and(predNota));
        }

        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        List<Long> ids = query.getResultList();

        return ids;

    }

    // Chamado principalmente pelo Job 'AtualizarStatusNotaRemessaJob'
    public void atualizarStatusExecucaoNotaRemessaContratantePeloIdNotaRemessa(EnumStatusExecucaoNotaRemessaContratante status, Long id) {
        String sqlAtualizar = "UPDATE side.tb_nrc_nota_remessa_ordem_fornecimento_contrato " + "SET nrc_st_status_execucao_contratante=:status " + "WHERE nrc_id_nota_remessa_ordem_fornecimento_contrato=:idNotaRemessa";
        Query query = em.createNativeQuery(sqlAtualizar);
        query.setParameter("status", status.getValor());
        query.setParameter("idNotaRemessa", id);
        query.executeUpdate();
    }

    public void atualizarAnaliseFinalObjetoFornecimentoContrato(ObjetoFornecimentoContrato ofc, EnumAnaliseFinalItem status) {
        String sqlAtualizar = "UPDATE side.tb_ofo_objeto_fornecimento_contrato " + "SET " + " ofo_st_analise_final_item=:status, " + " ofo_ds_motivo=:motivo " + " WHERE " + " ofo_id_objeto_fornecimento_contrato=:idOfo";
        Query query = em.createNativeQuery(sqlAtualizar);
        query.setParameter("status", status.getValor());
        query.setParameter("idOfo", ofc.getId());
        query.setParameter("motivo", ofc.getMotivo());
        query.executeUpdate();
    }

    public void atualizarSituacaoGeracaoTermosObjetoFornecimentoContrato(ObjetoFornecimentoContrato ofc, EnumSituacaoGeracaoTermos status) {
        String sqlAtualizar = "UPDATE side.tb_ofo_objeto_fornecimento_contrato " + "SET " + " ofo_st_situacao_geracao_termo=:status, " + " ofo_ds_motivo=:motivo " + " WHERE " + " ofo_id_objeto_fornecimento_contrato=:idOfo";
        Query query = em.createNativeQuery(sqlAtualizar);
        query.setParameter("status", status.getValor());
        query.setParameter("idOfo", ofc.getId());
        query.setParameter("motivo", ofc.getMotivo());
        query.executeUpdate();
    }

    // Chamado principalmente pelo Job 'AtualizarStatusNotaRemessaJob'
    public void atualizarStatusNotaRemessaBeneficiarioPeloIdEStatus(EnumStatusExecucaoNotaRemessaBeneficiario status, Long id) {
        String sqlAtualizar = "UPDATE side.tb_nrc_nota_remessa_ordem_fornecimento_contrato " + "SET nrc_st_status_execucao_beneficiario=:status " + "WHERE nrc_id_nota_remessa_ordem_fornecimento_contrato=:idNotaRemessa";
        Query query = em.createNativeQuery(sqlAtualizar);
        query.setParameter("status", status.getValor());
        query.setParameter("idNotaRemessa", id);
        query.executeUpdate();
    }

    // Chamado principalmente pelo Job 'AtualizarStatusNotaRemessaJob'
    public void atualizarStatusNotaRemessaFornecedorPeloIdEStatus(EnumStatusExecucaoNotaRemessaFornecedor status, Long id) {
        String sqlAtualizar = "UPDATE side.tb_nrc_nota_remessa_ordem_fornecimento_contrato " + "SET nrc_st_status_execucao_fornecedor=:status " + "WHERE nrc_id_nota_remessa_ordem_fornecimento_contrato=:idNotaRemessa";
        Query query = em.createNativeQuery(sqlAtualizar);
        query.setParameter("status", status.getValor());
        query.setParameter("idNotaRemessa", id);
        query.executeUpdate();
    }

    public List<BigInteger> buscarIdsNotaRemessaPorFormatacaoObjetoFornecimento(FormatacaoObjetoFornecimento formatacaoObjetoFornecimento) {

        String sqlPesquisar = "SELECT" + 
                " nrc.nrc_id_nota_remessa_ordem_fornecimento_contrato " + 
                " FROM " + 
                " side.tb_iof_itens_ordem_fornecimento_contrato iof," + 
                " side.tb_inr_item_nota_remessa_of_contrato inr, " + 
                " side.tb_nrc_nota_remessa_ordem_fornecimento_contrato nrc " + 
                " WHERE "+
                " inr.inr_fk_iof_id_itens_ordem_fornecimento_contrato = iof.iof_id_itens_ordem_fornecimento_contrato AND " +
                " inr.inr_fk_nrc_id_nota_remessa_ordem_fornecimento_contrato = nrc.nrc_id_nota_remessa_ordem_fornecimento_contrato AND " +
                " iof.iof_fk_lee_id_local_entrega_entidade = :idLocalEntrega AND " +
                " iof.iof_fk_ofc_id_ordem_fornecimento_contrato = :idOf AND " +
                " nrc.nrc_st_status_execucao_beneficiario != 'ANAL' AND " +
                " nrc.nrc_st_status_execucao_beneficiario != 'ENVI' AND "+
                " iof.iof_fk_bem_id_bem = :idBem ";

        Query query = em.createNativeQuery(sqlPesquisar);
        query.setParameter("idLocalEntrega", formatacaoObjetoFornecimento.getObjetoFornecimento().getLocalEntrega().getId());
        query.setParameter("idOf", formatacaoObjetoFornecimento.getObjetoFornecimento().getOrdemFornecimento().getId());
        query.setParameter("idBem", formatacaoObjetoFornecimento.getObjetoFornecimento().getItem().getId());

        List<BigInteger> idsNotas = query.getResultList();
        return idsNotas;
    }

    public List<NotaRemessaOrdemFornecimentoContrato> buscarNotasRemessasPeloProgramaEBeneficiario(Programa programa, Entidade entidade) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ItensNotaRemessaOrdemFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ItensNotaRemessaOrdemFornecimentoContrato.class);
        Root<ItensNotaRemessaOrdemFornecimentoContrato> root = criteriaQuery.from(ItensNotaRemessaOrdemFornecimentoContrato.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("notaRemessaOrdemFornecimento").get("ordemFornecimento").get("contrato").get("programa").get("id"), programa.getId()));
        predicates.add(criteriaBuilder.equal(root.get("itemOrdemFornecimentoContrato").get("localEntrega").get("entidade").get("id"), entidade.getId()));

        Predicate[] predicatePronto = predicates.toArray(new Predicate[] {});

        criteriaQuery.select(criteriaQuery.getSelection()).where(predicatePronto);

        TypedQuery<ItensNotaRemessaOrdemFornecimentoContrato> query = em.createQuery(criteriaQuery);
        List<ItensNotaRemessaOrdemFornecimentoContrato> notaRemessaFiltradoPorPredicates = query.getResultList();

        return montarNotasRemessasPorEntidadeEPrograma(notaRemessaFiltradoPorPredicates);
    }

    public List<BigInteger> buscarIdsNotaRemessaPorFormatacaoObjetoFornecimentoSemStatus(FormatacaoObjetoFornecimento formatacaoObjetoFornecimento) {

        String sqlPesquisar = "SELECT" +
                " nrc.nrc_id_nota_remessa_ordem_fornecimento_contrato" +
                " FROM " +
                " side.tb_iof_itens_ordem_fornecimento_contrato iof," +
                " side.tb_inr_item_nota_remessa_of_contrato inr, " +
                " side.tb_nrc_nota_remessa_ordem_fornecimento_contrato nrc" +
                " WHERE "+
                " inr.inr_fk_iof_id_itens_ordem_fornecimento_contrato = iof.iof_id_itens_ordem_fornecimento_contrato AND" +
                " inr.inr_fk_nrc_id_nota_remessa_ordem_fornecimento_contrato = nrc.nrc_id_nota_remessa_ordem_fornecimento_contrato AND" +
                " iof.iof_fk_lee_id_local_entrega_entidade = :idLocalEntrega AND " +
                " iof.iof_fk_ofc_id_ordem_fornecimento_contrato = :idOf AND" +
                " iof.iof_fk_bem_id_bem = :idBem";

        Query query = em.createNativeQuery(sqlPesquisar);
        query.setParameter("idLocalEntrega", formatacaoObjetoFornecimento.getObjetoFornecimento().getLocalEntrega().getId());
        query.setParameter("idOf", formatacaoObjetoFornecimento.getObjetoFornecimento().getOrdemFornecimento().getId());
        query.setParameter("idBem", formatacaoObjetoFornecimento.getObjetoFornecimento().getItem().getId());

        List<BigInteger> idsNotas = query.getResultList();
        return idsNotas;
    }

    public List<NotaRemessaOrdemFornecimentoContrato> buscarNotasRemessasPeloProgramaEBeneficiarioOrdenado(Programa programa, Entidade entidade, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ItensNotaRemessaOrdemFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ItensNotaRemessaOrdemFornecimentoContrato.class);
        Root<ItensNotaRemessaOrdemFornecimentoContrato> root = criteriaQuery.from(ItensNotaRemessaOrdemFornecimentoContrato.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("notaRemessaOrdemFornecimento").get("ordemFornecimento").get("contrato").get("programa").get("id"), programa.getId()));
        predicates.add(criteriaBuilder.equal(root.get("itemOrdemFornecimentoContrato").get("localEntrega").get("entidade").get("id"), entidade.getId()));

        Predicate[] predicatePronto = predicates.toArray(new Predicate[] {});

        criteriaQuery.select(criteriaQuery.getSelection()).where(predicatePronto);

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        }

        TypedQuery<ItensNotaRemessaOrdemFornecimentoContrato> query = em.createQuery(criteriaQuery);
        List<ItensNotaRemessaOrdemFornecimentoContrato> notaRemessaFiltradoPorPredicates = query.getResultList();

        List<NotaRemessaOrdemFornecimentoContrato> listaNotasRemessas = montarNotasRemessasPorEntidadeEPrograma(notaRemessaFiltradoPorPredicates);
        Collections.sort(listaNotasRemessas, NotaRemessaOrdemFornecimentoContrato.getComparator(order.isAscOrder() ? 1 : -1, propertyOrder));

        return listaNotasRemessas;
    }

    // Somente utilizado pelo JOB VerificarEnvioRelatorioPeloRepresentanteJob
    public ListaBeneficiariosSemEnvioRelatorioRecebimentoDto buscarNotasRemessasSemEnvioRelatorioRecebimento() {
        ListaBeneficiariosSemEnvioRelatorioRecebimentoDto listaNotasRetornar = new ListaBeneficiariosSemEnvioRelatorioRecebimentoDto();

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<NotaRemessaOrdemFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(NotaRemessaOrdemFornecimentoContrato.class);
        Root<NotaRemessaOrdemFornecimentoContrato> root = criteriaQuery.from(NotaRemessaOrdemFornecimentoContrato.class);

        Predicate[] pred = extractPredicatesParaEnvioEmailRelatoriosRecebimentoNaoEnviado(criteriaBuilder, root);
        criteriaQuery.select(root).where(criteriaBuilder.and(pred));

        TypedQuery<NotaRemessaOrdemFornecimentoContrato> query = em.createQuery(criteriaQuery);

        listaNotasRetornar = montarListaDeRepresentantesSemEnvioRelatorioRecebimento(query.getResultList());
        return listaNotasRetornar;
    }

    // Irá trazer os ids de todas as Notas Remessas que não enviaram o Termo de
    // Recebimento
    public List<Long> buscarIdsNotasRemessasAindaNaoEnviadasPorOrdemFornecimento(OrdemFornecimentoContrato ordem) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        Root<NotaRemessaOrdemFornecimentoContrato> notaRemessa = criteriaQuery.from(NotaRemessaOrdemFornecimentoContrato.class);
        Join<NotaRemessaOrdemFornecimentoContrato, OrdemFornecimentoContrato> ordemRoot = notaRemessa.join("ordemFornecimento");

        Predicate idsNotasNaoEnviadas = criteriaBuilder.notEqual(notaRemessa.get("statusExecucaoBeneficiario"), EnumStatusExecucaoNotaRemessaBeneficiario.ENVIADO);
        Predicate idsOrdemFornecimento = criteriaBuilder.equal(ordemRoot.get("id"), ordem.getId());

        criteriaQuery.select(notaRemessa.get("id")).where(criteriaBuilder.and(idsNotasNaoEnviadas, idsOrdemFornecimento));

        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        List<Long> lista = query.getResultList();

        return lista;
    }

    // Ira devolver quais os id's dos itens de uma determinada Ordem de
    // Fornecimento que ainda não receberam o Termo de Recebimento Definitivo
    public List<Long> buscarIdsItensSemTermoRecebimentoDefinitivoPorOrdemFornecimento(OrdemFornecimentoContrato ordem) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        Root<ObjetoFornecimentoContrato> rootOfc = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        Join<ObjetoFornecimentoContrato, OrdemFornecimentoContrato> joinOfc = rootOfc.join("ordemFornecimento");

        Predicate semTermoRecebimentoDefinitivo = criteriaBuilder.isNull(rootOfc.get("termoRecebimentoDefinitivo"));
        Predicate idsOrdemFornecimento = criteriaBuilder.equal(joinOfc.get("id"), ordem.getId());

        criteriaQuery.select(rootOfc.get("id")).where(criteriaBuilder.and(semTermoRecebimentoDefinitivo, idsOrdemFornecimento));

        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        List<Long> lista = query.getResultList();

        return lista;
    }

    public List<Long> buscarIdsNotasRemessasAindaEnviadasPorOrdemFornecimento(OrdemFornecimentoContrato ordem) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        Root<NotaRemessaOrdemFornecimentoContrato> notaRemessas = criteriaQuery.from(NotaRemessaOrdemFornecimentoContrato.class);
        Join<NotaRemessaOrdemFornecimentoContrato, OrdemFornecimentoContrato> ordemRoot = notaRemessas.join("ordemFornecimento");

        Predicate idsNotasNaoEnviadas = criteriaBuilder.equal(notaRemessas.get("statusExecucaoBeneficiario"), EnumStatusExecucaoNotaRemessaBeneficiario.ENVIADO);
        Predicate idsOrdemFornecimento = criteriaBuilder.equal(ordemRoot.get("id"), ordem.getId());

        criteriaQuery.select(notaRemessas.get("id")).where(criteriaBuilder.and(idsNotasNaoEnviadas, idsOrdemFornecimento));

        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        List<Long> lista = query.getResultList();

        return lista;
    }

    public Long buscarIdItemNotaRemessaPeloObjetoFornecimento(ObjetoFornecimentoContrato ofc) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        Root<ItensNotaRemessaOrdemFornecimentoContrato> rootItens = criteriaQuery.from(ItensNotaRemessaOrdemFornecimentoContrato.class);
        Join<ItensNotaRemessaOrdemFornecimentoContrato, ItensOrdemFornecimentoContrato> rootJoin = rootItens.join("itemOrdemFornecimentoContrato");

        Predicate idLocalEntrega = criteriaBuilder.equal(rootJoin.get("localEntrega").get("id"), ofc.getLocalEntrega().getId());
        Predicate idBem = criteriaBuilder.equal(rootJoin.get("item").get("id"), ofc.getItem().getId());
        Predicate idOrdemFornecimento = criteriaBuilder.equal(rootJoin.get("ordemFornecimento").get("id"), ofc.getOrdemFornecimento().getId());

        criteriaQuery.select(rootItens.get("id")).where(criteriaBuilder.and(idLocalEntrega, idBem, idOrdemFornecimento));

        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        List<Long> lista = query.getResultList();
        if (lista.isEmpty()) {
            return null;
        } else {
            return lista.get(0);
        }
    }

    public Long buscarIdItemOrdemFornecimentoPeloObjetoFornecimentoContrato(ObjetoFornecimentoContrato ofc) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        Root<ItensOrdemFornecimentoContrato> rootItens = criteriaQuery.from(ItensOrdemFornecimentoContrato.class);

        Predicate idLocalEntrega = criteriaBuilder.equal(rootItens.get("localEntrega").get("id"), ofc.getLocalEntrega().getId());
        Predicate idBem = criteriaBuilder.equal(rootItens.get("item").get("id"), ofc.getItem().getId());
        Predicate idOrdemFornecimento = criteriaBuilder.equal(rootItens.get("ordemFornecimento").get("id"), ofc.getOrdemFornecimento().getId());

        criteriaQuery.select(rootItens.get("id")).where(criteriaBuilder.and(idLocalEntrega, idBem, idOrdemFornecimento));

        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        List<Long> lista = query.getResultList();
        if (lista.isEmpty()) {
            return null;
        } else {
            return lista.get(0);
        }
    }

    private ItensNotaRemessaOrdemFornecimentoContrato buscarItemNotaRemessaPeloId(Long id) {
        ItensNotaRemessaOrdemFornecimentoContrato item = em.find(ItensNotaRemessaOrdemFornecimentoContrato.class, id);
        return item;
    }

    public List<Object> buscarIdsNotasRemessasPorPrograma(Programa programa) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);

        Root<ItensNotaRemessaOrdemFornecimentoContrato> rootItensNotaRemessa = criteriaQuery.from(ItensNotaRemessaOrdemFornecimentoContrato.class);

        Join<ItensNotaRemessaOrdemFornecimentoContrato, ItensOrdemFornecimentoContrato> rootJoinItensOf = rootItensNotaRemessa.join("itemOrdemFornecimentoContrato");
        Join<ItensOrdemFornecimentoContrato, LocalEntregaEntidade> rootJoinLocalEntrega = rootJoinItensOf.join("localEntrega");
        Join<LocalEntregaEntidade, Entidade> rootJoinEntidade = rootJoinLocalEntrega.join("entidade");

        Join<ItensNotaRemessaOrdemFornecimentoContrato, NotaRemessaOrdemFornecimentoContrato> rootJoinNotaRemessa = rootItensNotaRemessa.join("notaRemessaOrdemFornecimento");
        Join<NotaRemessaOrdemFornecimentoContrato, OrdemFornecimentoContrato> rootJoinOF = rootJoinNotaRemessa.join("ordemFornecimento");
        Join<OrdemFornecimentoContrato, Contrato> rootJoinContrato = rootJoinOF.join("contrato");
        Join<Contrato, Programa> rootJoinPrograma = rootJoinContrato.join("programa");

        Predicate idPrograma = criteriaBuilder.equal(rootJoinPrograma.get("id"), programa.getId());

        criteriaQuery.multiselect(rootItensNotaRemessa.get("id"), rootJoinEntidade.get("nomeEntidade"), rootJoinEntidade.get("id"), rootJoinNotaRemessa.get("id")).where(criteriaBuilder.and(idPrograma));

        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        List<Object> lista = query.getResultList();
        return lista;
    }

    private ListaBeneficiariosSemEnvioRelatorioRecebimentoDto montarListaDeRepresentantesSemEnvioRelatorioRecebimento(List<NotaRemessaOrdemFornecimentoContrato> lista) {

        ListaBeneficiariosSemEnvioRelatorioRecebimentoDto notaRemessaSemAnexo = new ListaBeneficiariosSemEnvioRelatorioRecebimentoDto();
        for (NotaRemessaOrdemFornecimentoContrato nota : lista) {

            boolean registroDeRecebimentoAnexado = false;
            for (AnexoNotaRemessa anf : nota.getListaAnexosNotaRemessa()) {
                if (anf.getTipoArquivoTermoEntrega() == EnumTipoArquivoTermoEntrega.RELATORIO_RECEBIMENTO_ASSINADO) {
                    registroDeRecebimentoAnexado = true;
                    break;
                }
            }

            if (!registroDeRecebimentoAnexado) {
                LocalDate dataAtual = LocalDate.now();
                LocalDate diasPassadosSemEnvioRecebimento = nota.getDataEfetivaEntrega();
                long diasPassados = ChronoUnit.DAYS.between(diasPassadosSemEnvioRecebimento, dataAtual);

                if (diasPassados == 5) {
                    notaRemessaSemAnexo.getLista5Dias().add(nota);
                } else {
                    if (diasPassados == 10) {
                        notaRemessaSemAnexo.getLista10Dias().add(nota);
                    } else {
                        if (diasPassados == 15) {
                            notaRemessaSemAnexo.getLista15Dias().add(nota);
                        } else {
                            if (diasPassados == 20) {
                                notaRemessaSemAnexo.getLista20Dias().add(nota);
                            } else {
                                if (diasPassados == 25) {
                                    notaRemessaSemAnexo.getLista25Dias().add(nota);
                                } else {
                                    notaRemessaSemAnexo.getLista30Dias().add(nota);
                                }
                            }
                        }
                    }
                }
            }
        }

        return notaRemessaSemAnexo;
    }

    private Predicate[] extractPredicatesParaEnvioEmailRelatoriosRecebimentoNaoEnviado(CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.isNotNull(root.get("dataEfetivaEntrega")));
        Predicate dia5 = criteriaBuilder.equal(root.get("dataEfetivaEntrega"), LocalDate.now().minusDays(5));
        Predicate dia10 = criteriaBuilder.equal(root.get("dataEfetivaEntrega"), LocalDate.now().minusDays(10));
        Predicate dia15 = criteriaBuilder.equal(root.get("dataEfetivaEntrega"), LocalDate.now().minusDays(15));
        Predicate dia20 = criteriaBuilder.equal(root.get("dataEfetivaEntrega"), LocalDate.now().minusDays(20));
        Predicate dia25 = criteriaBuilder.equal(root.get("dataEfetivaEntrega"), LocalDate.now().minusDays(25));
        Predicate dia30 = criteriaBuilder.equal(root.get("dataEfetivaEntrega"), LocalDate.now().minusDays(30));

        predicates.add(criteriaBuilder.or(dia5, dia10, dia15, dia20, dia25, dia30));
        return predicates.toArray(new Predicate[] {});
    }

    private List<NotaRemessaOrdemFornecimentoContrato> montarNotasRemessasPorEntidadeEPrograma(List<ItensNotaRemessaOrdemFornecimentoContrato> itensNotaRemessaDoBanco) {

        Map<Long, NotaRemessaOrdemFornecimentoContrato> listaHash = new HashMap<Long, NotaRemessaOrdemFornecimentoContrato>();
        List<NotaRemessaOrdemFornecimentoContrato> listaRetornar = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();

        for (ItensNotaRemessaOrdemFornecimentoContrato itens : itensNotaRemessaDoBanco) {
            Long chave = itens.getNotaRemessaOrdemFornecimento().getId();

            if (!listaHash.containsKey(chave)) {
                listaHash.put(chave, itens.getNotaRemessaOrdemFornecimento());
            }
        }

        listaRetornar.addAll(listaHash.values());
        return listaRetornar;

    }

    public void excluir(NotaRemessaOrdemFornecimentoContrato notaRemessa) {

        // Como estes itens contem um relacionamento com a tabela de 'itens da
        // ordem de fornecimento' irei retirar esta ligação
        // antes de apagar o item propriamente dito da lista de itens da nota
        // remessa
        for (ItensNotaRemessaOrdemFornecimentoContrato item : notaRemessa.getListaItensNotaRemessaOrdemFornecimentoContratos()) {
            ItensNotaRemessaOrdemFornecimentoContrato itemApagar = em.find(ItensNotaRemessaOrdemFornecimentoContrato.class, item.getId());
            itemApagar.setItemOrdemFornecimentoContrato(null);
            em.merge(itemApagar);
            
            //Irá retirar a referencia dos Objetos a nota de remessa que esta sendo apagada
            ObjetoFornecimentoContratoDto objDto = new ObjetoFornecimentoContratoDto();
            ObjetoFornecimentoContrato obj = new ObjetoFornecimentoContrato();
            obj.setItem(item.getItemOrdemFornecimentoContrato().getItem());
            obj.setLocalEntrega(item.getItemOrdemFornecimentoContrato().getLocalEntrega());
            obj.setOrdemFornecimento(item.getItemOrdemFornecimentoContrato().getOrdemFornecimento());
            obj.setNotaRemessaOrdemFornecimentoContrato(notaRemessa);
            objDto.setObjetoFornecimentoContrato(obj);
            
            List<ObjetoFornecimentoContrato> objeto = objetoFornecimentoContratoService.buscarSemPaginacao(objDto);
            if(objeto != null){
                for(ObjetoFornecimentoContrato objLimpar:objeto){
                    objetoFornecimentoContratoService.retirarNotaRemessaNoObjetoFornecimento(objLimpar);
                }
            }
        }

        NotaRemessaOrdemFornecimentoContrato notaRemessaApagar = em.find(NotaRemessaOrdemFornecimentoContrato.class, notaRemessa.getId());
        em.remove(notaRemessaApagar);
    }

    private void sincronizarAnexos(List<AnexoNotaRemessa> anexos, NotaRemessaOrdemFornecimentoContrato entityAtual) {
        // remover os excluidos
        List<AnexoNotaRemessa> anexosAux = new ArrayList<AnexoNotaRemessa>(entityAtual.getListaAnexosNotaRemessa());
        for (AnexoNotaRemessa anexoAtual : anexosAux) {
            if (!anexos.contains(anexoAtual)) {
                entityAtual.getListaAnexosNotaRemessa().remove(anexoAtual);
            }
        }

        // adiciona os novos
        for (AnexoNotaRemessa anexoNovo : anexos) {
            if (anexoNovo.getId() == null) {
                anexoNovo.setNotaRemessaOrdemFornecimento(entityAtual);
                // anexoNovo.setDataCadastro(LocalDateTime.now());
                // anexoNovo.setTamanho(new
                // Long(anexoNovo.getConteudo().length));
                entityAtual.getListaAnexosNotaRemessa().add(anexoNovo);
            }
        }

    }

    private void sincronizarItensDaNotaRemessa(List<ItensNotaRemessaOrdemFornecimentoContrato> novaListaItens, NotaRemessaOrdemFornecimentoContrato notaRemessa) {
        List<ItensNotaRemessaOrdemFornecimentoContrato> listaItensAtualizar = new ArrayList<ItensNotaRemessaOrdemFornecimentoContrato>();

        // Selecionará somente os anexos que foram mantidos
        for (ItensNotaRemessaOrdemFornecimentoContrato inf : notaRemessa.getListaItensNotaRemessaOrdemFornecimentoContratos()) {
            if (novaListaItens.contains(inf)) {
                listaItensAtualizar.add(inf);
            }
        }

        // Limpa a lista de itens que já foram persistidos
        notaRemessa.getListaItensNotaRemessaOrdemFornecimentoContratos().clear();

        // Irá adicionar a lista de itens novos.
        for (ItensNotaRemessaOrdemFornecimentoContrato inf : novaListaItens) {
            if (inf.getId() == null) {
                inf.setNotaRemessaOrdemFornecimento(notaRemessa);
                notaRemessa.getListaItensNotaRemessaOrdemFornecimentoContratos().add(inf);
            }
        }

        // A lista de itens novos já foi adicionada a nota remessa que será
        // persistida
        // Agora será necessário adicionar a lista de itens que já estavam
        // salvas e não foram removidas.
        notaRemessa.getListaItensNotaRemessaOrdemFornecimentoContratos().addAll(listaItensAtualizar);
    }

}
