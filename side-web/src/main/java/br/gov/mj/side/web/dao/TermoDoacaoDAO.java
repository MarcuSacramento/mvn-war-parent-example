package br.gov.mj.side.web.dao;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumSituacaoBem;
import br.gov.mj.side.entidades.enums.EnumSituacaoGeracaoTermos;
import br.gov.mj.side.entidades.enums.EnumStatusGeracaoTermoDoacao;
import br.gov.mj.side.entidades.enums.EnumStatusTermoDoacao;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.TermoDoacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.ItensNotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.web.dto.TermoDoacaoDto;
import br.gov.mj.side.web.dto.TermoRecebimentoDefinitivoDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.DoacoesConcluidasDto;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.NotaRemessaService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.service.TermoRecebimentoDefinitivoService;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class TermoDoacaoDAO {

    @Inject
    private EntityManager em;

    @Inject
    private NotaRemessaService notaRemessaService;
    
    @Inject
    private GenericEntidadeService genericEntidadeService;
    
    @Inject
    private TermoRecebimentoDefinitivoService termoRecebimentoDefinitivoService;
    
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

    public List<TermoDoacao> buscarPaginado(TermoDoacaoDto termoDoacaoDto, int first, int size, EnumOrder order, String propertyOrder) {

        List<TermoDoacao> lista1 = buscarSemPaginacao(termoDoacaoDto, order, propertyOrder);

        // filtra paginado
        List<TermoDoacao> listaRetorno = new ArrayList<TermoDoacao>();
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

    public List<TermoDoacao> buscarSemPaginacao(TermoDoacaoDto termoDoacaoDto) {
        return buscarSemPaginacao(termoDoacaoDto, EnumOrder.ASC, "id");
    }

    public List<TermoDoacao> buscarSemPaginacaoOrdenado(TermoDoacaoDto termoDoacaoDto, EnumOrder order, String propertyOrder) {
        return buscarSemPaginacao(termoDoacaoDto, order, propertyOrder);
    }

    private List<TermoDoacao> buscarSemPaginacao(TermoDoacaoDto termoDoacaoDto, EnumOrder order, String propertyOrder) {
        
        List<TermoDoacao> todosTermosDoacaoPorPrograma = new ArrayList<TermoDoacao>();

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        
        Root<ObjetoFornecimentoContrato> rootOfc = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        Join<ObjetoFornecimentoContrato, OrdemFornecimentoContrato> rootJoinOrdeFornecimento = rootOfc.join("ordemFornecimento");
        Join<OrdemFornecimentoContrato, Contrato> rootJoinContrato = rootJoinOrdeFornecimento.join("contrato");
        Join<Contrato, Programa> rootJoinPrograma = rootJoinContrato.join("programa");
        
        Join<ObjetoFornecimentoContrato, TermoDoacao> joinTermoDoacao = rootOfc.join("termoDoacao");
        
        Predicate idPrograma = criteriaBuilder.equal(rootJoinPrograma.get("id"), termoDoacaoDto.getPrograma().getId());
        Predicate termoNaoNulo = criteriaBuilder.isNotNull(rootOfc.get("termoDoacao").get("id"));

        criteriaQuery.select(rootOfc.get("termoDoacao").get("id")).where(criteriaBuilder.and(idPrograma,termoNaoNulo));
        
        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        //Busca o id dos Termos de doação
        List<Long> listaIdsTermos = query.getResultList();
        
        //A partir da lista de id's do termo de doação serão montados todos os termos
        todosTermosDoacaoPorPrograma = montarListaDeTermoDeDoacao(listaIdsTermos,criteriaBuilder);
        
        List<TermoDoacao> lista = new ArrayList<TermoDoacao>();
        lista = organizarTermosPorEntidade(todosTermosDoacaoPorPrograma);
        
        int ordem = order.isAscOrder()?1:-1;
        Collections.sort(lista,TermoDoacao.getComparator(ordem,propertyOrder));        
        return lista;
    }
    
    public void atualizarNumeroProcessoSeiPorTermoDoacao(TermoDoacaoDto termoDoacaoDto){
        
        //Irá buscar todos os termos de doação
        List<BigInteger> listaIdsTermo = buscarIdsTermosDoacaoPorProgramaEEntidade(termoDoacaoDto);
        
        for(BigInteger id:listaIdsTermo){
            String sqlString = " UPDATE "+
                    " side.tb_tda_termo_doacao "+
                    " SET "+
                    " tda_nu_numero_processo_sei=:numeroProcessoSei "+
                    " WHERE "+
                    " tda_id_termo_doacao=:idTermoDoacao ";
            Query query = em.createNativeQuery(sqlString);
            query.setParameter("numeroProcessoSei", termoDoacaoDto.getTermoDoacao().getNumeroProcessoSEI());
            query.setParameter("idTermoDoacao", id.longValue());
            query.executeUpdate();
        }
    }
    
  //Irá retornar os ids dos termos de doacao de uma determinada entidade e em um determinado programa
    private List<BigInteger> buscarIdsTermosDoacaoPorProgramaEEntidade(TermoDoacaoDto termoDoacaoDto){

        String queryString ="SELECT "+
                " tda.tda_id_termo_doacao "+
                " FROM "+
                " side.tb_ofo_objeto_fornecimento_contrato ofo, "+ 
                " side.tb_ofc_ordem_fornecimento_contrato ofc, "+
                " side.tb_con_contrato con, "+
                " side.tb_prg_programa prg, "+
                " side.tb_tda_termo_doacao tda, "+ 
                " side.tb_ent_entidade ent "+
                " WHERE "+
                " ofo.ofo_fk_ofc_id_ordem_fornecimento_contrato = ofc.ofc_id_ordem_fornecimento_contrato AND "+
                " ofo.ofo_fk_trd_id_termo_doacao = tda.tda_id_termo_doacao AND "+
                " ofc.ofc_fk_con_id_contrato = con.con_id_contrato AND "+
                " con.con_fk_prg_id_programa = prg.prg_id_programa AND "+
                " tda.tda_fk_ent_id_entidade = ent.ent_id_entidade AND "+
                " ent.ent_id_entidade=:idEntidade AND "+
                " prg.prg_id_programa=:idPrograma";
        Query query = em.createNativeQuery(queryString);
        query.setParameter("idEntidade", termoDoacaoDto.getTermoDoacao().getEntidade().getId());
        query.setParameter("idPrograma", termoDoacaoDto.getPrograma().getId());
        
        List<BigInteger> listaIdsTermosDoacao = query.getResultList();
        return listaIdsTermosDoacao;
                
    }
    
    //Este metodo irá receber a lista de ids dos termos de doação e monta-los somente com as informações
    //necessárias para a visualização
    private List<TermoDoacao> montarListaDeTermoDeDoacao(List<Long> listaIdsTermos, CriteriaBuilder criteriaBuilder){
        List<TermoDoacao> todosTermosDoacaoPorPrograma = new ArrayList<TermoDoacao>();
        
        for (Long o : listaIdsTermos) {
            
            CriteriaBuilder criteriaTermo = em.getCriteriaBuilder();
            CriteriaQuery<Object> criteriaQueryTermo = criteriaBuilder.createQuery(Object.class);
            
            Long idDoTermo = o;

            Root<TermoDoacao> rootTermo = criteriaQueryTermo.from(TermoDoacao.class);
            Predicate idTermo = criteriaBuilder.equal(rootTermo.get("id"), idDoTermo);
            
            criteriaQueryTermo.multiselect(rootTermo.get("id"),rootTermo.get("nomeAnexo"),
                    rootTermo.get("nomeBeneficiario"),rootTermo.get("dataGeracao"),rootTermo.get("numeroCnpj"),rootTermo.get("numeroProcessoSEI"),rootTermo.get("numeroDocumentoSei"),
                    rootTermo.get("entidade").get("id"),rootTermo.get("entidade").get("email")).where(criteriaBuilder.and(idTermo));
            
            TypedQuery<Object> queryTermo = em.createQuery(criteriaQueryTermo);
            //Busca o id dos Termos de doação
            List<Object> objsTermo = queryTermo.getResultList();
            
            for(Object objTermo:objsTermo){
                
                Object[] objeto = (Object[]) objTermo;
                Long idDoTermoNovo = (Long) objeto[0];
                String nomeAnexo = (String) objeto[1];
                String nomeBeneficiario = (String) objeto[2];
                LocalDateTime data = (LocalDateTime) objeto[3];
                String cnpj = (String) objeto[4];
                String numeroProcessoSei = (String) objeto[5];
                String numeroDocumentoSei = objeto[6] == null?"":(String) objeto[6];
                
                Entidade entidade = new Entidade();
                entidade.setId((Long)objeto[7]);
                entidade.setEmail((String)objeto[8]);

                // Monta o termo de doação
                TermoDoacao termo = new TermoDoacao();
                termo.setId(idDoTermoNovo);
                termo.setNomeAnexo(nomeAnexo);
                termo.setNomeBeneficiario(nomeBeneficiario);
                termo.setNumeroCnpj(cnpj);
                termo.setDataGeracao(data);
                termo.setNumeroProcessoSEI(numeroProcessoSei);
                termo.setNumeroDocumentoSei(numeroDocumentoSei);
                termo.setEntidade(entidade);

                List<ObjetoFornecimentoContrato> listaObjeto = new ArrayList<ObjetoFornecimentoContrato>();

                // irá montar os itens que fazem parte deste termo de doação.
                List<Object> listaDeItens = buscarItensPeloIdTermoDoacao(idDoTermo);
                for (Object objItem : listaDeItens) {

                    Object[] objetoItem = (Object[]) objItem;
                    Long idObjeto = (Long) objetoItem[0];
                    String nomeBem = (String) objetoItem[1];

                    ObjetoFornecimentoContrato ofc = new ObjetoFornecimentoContrato();
                    ofc.setId(idObjeto);

                    Bem bem = new Bem();
                    bem.setNomeBem(nomeBem);
                    ofc.setItem(bem);
                    listaObjeto.add(ofc);
                }
                termo.setObjetosFornecimentoContrato(listaObjeto);
                todosTermosDoacaoPorPrograma.add(termo);
            }
        }        
        return todosTermosDoacaoPorPrograma;
    }
    
    //Irá pegar a lista com todos os termos definitivos e separa-los por beneficiario
    //Todos os termos de recebimento do mesmo beneficiário irá ser salvos como o mesmo termo de doação
    private List<TermoDoacao> organizarTermosPorEntidade(List<TermoDoacao> todosTermosDoacaoPorPrograma){
        Map<Long,TermoDoacao> map = new HashMap<Long,TermoDoacao>();
        
        for(TermoDoacao trd:todosTermosDoacaoPorPrograma){
            List<ObjetoFornecimentoContrato> listaObjetos = trd.getObjetosFornecimentoContrato();
            
            if(!map.containsKey(trd.getId())){
                TermoDoacao td = new TermoDoacao();
                
                td.setDataGeracao(trd.getDataGeracao());
                td.setId(trd.getId());
                td.setNomeAnexo(trd.getNomeAnexo());
                td.setNomeBeneficiario(trd.getNomeBeneficiario());
                td.setNumeroCnpj(trd.getNumeroCnpj());
                td.setObjetosFornecimentoContrato(listaObjetos);
                td.setNumeroDocumentoSei(trd.getNumeroDocumentoSei());
                td.setNumeroProcessoSEI(trd.getNumeroProcessoSEI());
                td.setEntidade(trd.getEntidade());
                
                map.put(trd.getId(), td);
            }else{
                TermoDoacao td = map.get(trd.getId());
                map.put(trd.getId(), td);
            }
       }
        
        List<TermoDoacao> lista = new ArrayList<TermoDoacao>();
        lista.addAll( (Collection<? extends TermoDoacao>) map.values());
        return lista;
    }

    public TermoDoacao incluir(TermoDoacaoDto termoDoacaoDto) {
        
        TermoDoacao termoDoacao =termoDoacaoDto.getTermoDoacao();
        String usuarioLogado = termoDoacaoDto.getUsuarioLogado();
                
        List<ObjetoFornecimentoContrato> objetos = termoDoacao.getObjetosFornecimentoContrato();
        termoDoacao.setUsuarioCriacao(usuarioLogado);
        termoDoacao.setDataGeracao(LocalDateTime.now());
        termoDoacao.setNomeAnexo("TD-"+ termoDoacao.getNomeBeneficiario());
        termoDoacao.setStatusTermoDoacao(EnumStatusTermoDoacao.DOACAO_NAO_CONCLUIDA);
        em.persist(termoDoacao);
        em.flush();
        
        //termoDoacao.setNomeAnexo("TD-"+ termoDoacao.getNomeBeneficiario());
        //em.merge(termoDoacao);

        for (ObjetoFornecimentoContrato ofc : objetos) {
            
            setarTermoDoacaoAObjetoFornecimentoContrato(termoDoacao, ofc.getId());

            // Atualiza o status do item que é do tipo ObjetoFornecimentoContrato
            notaRemessaService.atualizarSituacaoGeracaoTermosObjetoFornecimentoContrato(ofc, EnumSituacaoGeracaoTermos.TERMO_DOACAO_GERADO);
            
         // Atualiza o status do termo de recebimento definitivo
            termoRecebimentoDefinitivoService.atualizarStatusGeracaoTermoDefinitivoPeloId(EnumStatusGeracaoTermoDoacao.TERMO_DOACAO_GERADO, ofc.getTermoRecebimentoDefinitivo().getId());
        }
        return termoDoacao;
    }

    public TermoDoacao buscarPeloId(Long id) {
        return em.find(TermoDoacao.class, id);
    }

    public boolean concluirDoacao(TermoDoacao termoDoacao){
        boolean termoDoacaoAtualizado = true;
        boolean objetoFornecimentoAtualizado = true;
        
        //Irá atualizar o status do termo de doação
        termoDoacao.setStatusTermoDoacao(EnumStatusTermoDoacao.DOACAO_CONCLUIDA);
        termoDoacaoAtualizado = alterar(termoDoacao);
        
        for(ObjetoFornecimentoContrato td:termoDoacao.getObjetosFornecimentoContrato()){
            objetoFornecimentoAtualizado = atualizarSituacaoDaGeracaoDosTermosDoObjetoFornecimentoPeloId(EnumSituacaoGeracaoTermos.DOACAO_CONCLUIDA,td.getId());
            ordemFornecimentoContratoService.atualizarSituacaoDoItemObjetoFornecimentoContratoPeloId(EnumSituacaoBem.DOADO, td.getId());
            if(!objetoFornecimentoAtualizado){
                break;
            }
        }        
        boolean atualizadoSucesso = termoDoacaoAtualizado && objetoFornecimentoAtualizado?true:false;
        return atualizadoSucesso;
    }
    
    // Ira retornar os ids local de entrega, ids ordem fornecimento e ids bens de todos os itens da nota
    // fiscal
    public List<Object> buscarListItensPorTermoDoacao(Long idTermo) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);

        Root<ItensNotaRemessaOrdemFornecimentoContrato> rootItens = criteriaQuery.from(ItensNotaRemessaOrdemFornecimentoContrato.class);
        Join<ItensNotaRemessaOrdemFornecimentoContrato, TermoDoacao> rootJoinTermo = rootItens.join("termoDoacao");
        Join<ItensNotaRemessaOrdemFornecimentoContrato, ItensNotaRemessaOrdemFornecimentoContrato> rootJoinItensOF = rootItens.join("itemOrdemFornecimentoContrato");

        Predicate idTermoRecebimento = criteriaBuilder.equal(rootJoinTermo.get("id"), idTermo);

        criteriaQuery.multiselect(rootJoinItensOF.get("localEntrega").get("id"), rootJoinItensOF.get("item").get("id"), rootJoinItensOF.get("ordemFornecimento").get("id"), rootJoinItensOF.get("item").get("nomeBem")).where(criteriaBuilder.and(idTermoRecebimento));

        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        List<Object> lista = query.getResultList();
        return lista;
    }

    // Ira retornar os ids local de entrega, ids ordem fornecimento e ids bens de todos os itens da nota
    // fiscal
    public List<Object> buscarListObjetoFornecimentoPeloTermoDoacao(Long idTermo) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);

        Root<ObjetoFornecimentoContrato> rootItens = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        Join<ObjetoFornecimentoContrato, TermoDoacao> rootJoinTermo = rootItens.join("termoDoacao");

        Predicate idTermoRecebimento = criteriaBuilder.equal(rootJoinTermo.get("id"), idTermo);

        criteriaQuery.multiselect(rootItens.get("item").get("nomeBem"), rootItens.get("id")).where(criteriaBuilder.and(idTermoRecebimento));

        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        List<Object> lista = query.getResultList();
        return lista;
    }

    public List<TermoDoacao> buscarTermosDoacaoPorPrograma(Programa programa) {
        List<TermoDoacao> todosTermosDoacaoPorPrograma = new ArrayList<TermoDoacao>();

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        
        Root<ObjetoFornecimentoContrato> rootOfc = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        Join<ObjetoFornecimentoContrato, OrdemFornecimentoContrato> rootJoinOrdeFornecimento = rootOfc.join("ordemFornecimento");
        Join<OrdemFornecimentoContrato, Contrato> rootJoinContrato = rootJoinOrdeFornecimento.join("contrato");
        Join<Contrato, Programa> rootJoinPrograma = rootJoinContrato.join("programa");

        Predicate idPrograma = criteriaBuilder.equal(rootJoinPrograma.get("id"), programa.getId());
        Predicate termoNaoNulo = criteriaBuilder.isNotNull(rootOfc.get("termoDoacao").get("id"));

        criteriaQuery.select(rootOfc.get("termoDoacao").get("id")).where(criteriaBuilder.and(idPrograma,termoNaoNulo));

        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        //Busca o id dos Termos de doação
        List<Long> listaIdsTermos = query.getResultList();
        
        
        //A partir da lista de id's do termo de doação serão montados todos os termos
        for (Long o : listaIdsTermos) {
            
            CriteriaBuilder criteriaTermo = em.getCriteriaBuilder();
            CriteriaQuery<Object> criteriaQueryTermo = criteriaBuilder.createQuery(Object.class);
            
            Long idDoTermo = o;

            Root<TermoDoacao> rootTermo = criteriaQueryTermo.from(TermoDoacao.class);
            Predicate idTermo = criteriaBuilder.equal(rootTermo.get("id"), idDoTermo);
            
            criteriaQueryTermo.multiselect(rootTermo.get("id"),rootTermo.get("nomeAnexo"),
                    rootTermo.get("nomeBeneficiario"),rootTermo.get("dataGeracao"),rootTermo.get("numeroCnpj")).where(criteriaBuilder.and(idTermo));

            TypedQuery<Object> queryTermo = em.createQuery(criteriaQueryTermo);
            //Busca o id dos Termos de doação
            List<Object> objsTermo = queryTermo.getResultList();
            
            for(Object objTermo:objsTermo){
                
                Object[] objeto = (Object[]) objTermo;
                Long idDoTermoNovo = (Long) objeto[0];
                String nomeAnexo = (String) objeto[1];
                String nomeBeneficiario = (String) objeto[2];
                LocalDateTime data = (LocalDateTime) objeto[3];
                String cnpj = (String) objeto[4];

                // Monta o termo de doação
                TermoDoacao termo = new TermoDoacao();
                termo.setId(idDoTermoNovo);
                termo.setNomeAnexo(nomeAnexo);
                termo.setNomeBeneficiario(nomeBeneficiario);
                termo.setNumeroCnpj(cnpj);
                termo.setDataGeracao(data);

                List<ObjetoFornecimentoContrato> listaObjeto = new ArrayList<ObjetoFornecimentoContrato>();

                // irá montar os itens que fazem parte deste termo de doação.
                List<Object> listaDeItens = buscarItensPeloIdTermoDoacao(idDoTermo);
                for (Object objItem : listaDeItens) {

                    Object[] objetoItem = (Object[]) objItem;
                    Long idObjeto = (Long) objetoItem[0];
                    String nomeBem = (String) objetoItem[1];

                    ObjetoFornecimentoContrato ofc = new ObjetoFornecimentoContrato();
                    ofc.setId(idObjeto);

                    Bem bem = new Bem();
                    bem.setNomeBem(nomeBem);
                    ofc.setItem(bem);
                    listaObjeto.add(ofc);
                }
                termo.setObjetosFornecimentoContrato(listaObjeto);
                todosTermosDoacaoPorPrograma.add(termo);
                
            }
        }
        
        Map<Long,TermoDoacao> map = new HashMap<Long,TermoDoacao>();
        
        //Irá pegar a lista com todos os termos definitivos e separa-los por beneficiario
        //Todos os termos de recebimento do mesmo beneficiário irá ser salvos como o mesmo termo de doação
        for(TermoDoacao trd:todosTermosDoacaoPorPrograma){
            List<ObjetoFornecimentoContrato> listaObjetos = trd.getObjetosFornecimentoContrato();
            
            if(!map.containsKey(trd.getId())){
                TermoDoacao td = new TermoDoacao();
                
                td.setDataGeracao(trd.getDataGeracao());
                td.setId(trd.getId());
                td.setNomeAnexo(trd.getNomeAnexo());
                td.setNomeBeneficiario(trd.getNomeBeneficiario());
                td.setNumeroCnpj(trd.getNumeroCnpj());
                td.setObjetosFornecimentoContrato(listaObjetos);
                
                map.put(trd.getId(), td);
            }else{
                TermoDoacao td = map.get(trd.getId());
                map.put(trd.getId(), td);
            }
       }
        
        List<TermoDoacao> lista = new ArrayList<TermoDoacao>();
        lista.addAll( (Collection<? extends TermoDoacao>) map.values());
        return lista;        
    }

    private List<Object> buscarItensPeloIdTermoDoacao(Long idTermoDoacao) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);

        Root<ObjetoFornecimentoContrato> rootObjetoTermo = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        Join<ObjetoFornecimentoContrato, Bem> rootJoinBem = rootObjetoTermo.join("item");

        Predicate idTermo = criteriaBuilder.equal(rootObjetoTermo.get("termoDoacao").get("id"), idTermoDoacao);

        criteriaQuery.multiselect(rootObjetoTermo.get("id"), rootJoinBem.get("nomeBem")).where(criteriaBuilder.and(idTermo));

        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        List<Object> lista = query.getResultList();

        return lista;
    }
    
    //Irá retornar a quantidade de itens que foram entregues e doados divididos por entidade
    public List<DoacoesConcluidasDto> buscarTodosItensDoados(TermoDoacaoDto dto) {
        List<Entidade> todasEntidades = genericEntidadeService.buscarTodosBeneficiariosPeloPrograma(dto.getPrograma());
        
        List<DoacoesConcluidasDto> listaDoacoes = new ArrayList<DoacoesConcluidasDto>();
        for (Entidade entidade : todasEntidades) {
            
            DoacoesConcluidasDto doacao = new DoacoesConcluidasDto();
            doacao.setEntidade(entidade);
            doacao.setNumeroProcessoSei(dto.getNumeroProcessoSei());
            
            //Irá buscar a quantidade de itens que possuem o termo de recebimento gerado
            TermoRecebimentoDefinitivoDto termoRecebimentoEntregueDto = new TermoRecebimentoDefinitivoDto();
            termoRecebimentoEntregueDto.setPrograma(dto.getPrograma());
            //termoRecebimentoEntregueDto.setSituacaoGeracaoTermos(EnumSituacaoGeracaoTermos.TERMO_RECEBIMENTO_DEFINITIVO_GERADO);
            termoRecebimentoEntregueDto.setEntidade(entidade);
            termoRecebimentoEntregueDto.setBuscarSomenteOsOfosComTermosJaGerados(true);
            List<ObjetoFornecimentoContrato> listaItens = montarObjetoFornecimentoContrato(termoRecebimentoEntregueDto);
            doacao.setListaItensEntregues(listaItens);
            doacao.setQuantidadeEntregue(listaItens.size());
            
            //Irá buscar a quantidade de itens que já foram doados
            TermoRecebimentoDefinitivoDto termoRecebimentoDto = new TermoRecebimentoDefinitivoDto();
            termoRecebimentoDto.setPrograma(dto.getPrograma());
            termoRecebimentoDto.setSituacaoGeracaoTermos(EnumSituacaoGeracaoTermos.DOACAO_CONCLUIDA);
            termoRecebimentoDto.setEntidade(entidade);
            List<ObjetoFornecimentoContrato> listaItensDoados = montarObjetoFornecimentoContrato(termoRecebimentoDto);
            doacao.setListaItensDoados(listaItensDoados);
            doacao.setQuantidadeDoado(listaItensDoados.size());
            
            listaDoacoes.add(doacao);
        }
        
        //Ordena a lista
        if(dto.getOrder() != null && dto.getPropertyOrder() != null){
            int ordem = dto.getOrder().isAscOrder()?1:-1;
            Collections.sort(listaDoacoes,DoacoesConcluidasDto.getComparator(ordem,dto.getPropertyOrder()));
        }
        
        return listaDoacoes;
    }
    
    private List<ObjetoFornecimentoContrato> montarObjetoFornecimentoContrato(TermoRecebimentoDefinitivoDto termoRecebimentoDto){
        
        List<Object> listaItens = new ArrayList<Object>();
        listaItens = termoRecebimentoDefinitivoService.retornaObjetoFornecimentoContratoPorTermoRecebimento(termoRecebimentoDto);
        
        //Com a lista dos ObjetosFornecimentoContrado a lista será montada
          List<ObjetoFornecimentoContrato> listaObjetosAdicionar = new ArrayList<ObjetoFornecimentoContrato>();
          for (Object object : listaItens) {
              Object[] o = (Object[]) object;
              
              Long idOfc = (Long) o[0];
              Long idBem = (Long) o[1];
              String nomeBem = (String) o[2];
              
              Bem bem = new Bem();
              bem.setId(idBem);
              bem.setNomeBem(nomeBem);
              
              ObjetoFornecimentoContrato ofc = new ObjetoFornecimentoContrato();
              ofc.setId(idOfc);
              ofc.setItem(bem);
              
              listaObjetosAdicionar.add(ofc);
          }
          return listaObjetosAdicionar;
    }

    public List<ObjetoFornecimentoContrato> buscarTodosOsObjetosFornecimentoContratoPeloIdTermo(Long idTermoDoacao) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ObjetoFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ObjetoFornecimentoContrato.class);

        Root<ObjetoFornecimentoContrato> rootItens = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        Predicate idTermoRecebimento = criteriaBuilder.equal(rootItens.get("termoDoacao").get("id"), idTermoDoacao);
        criteriaQuery.select(criteriaQuery.getSelection()).where(idTermoRecebimento);

        TypedQuery<ObjetoFornecimentoContrato> query = em.createQuery(criteriaQuery);
        List<ObjetoFornecimentoContrato> lista = query.getResultList();

        return lista;
    }
    
    private boolean alterar(TermoDoacao termoDoacao){
        String queryString = "UPDATE side.tb_tda_termo_doacao "+
                " SET "+ 
                " tda_st_status_termo_doacao=:status, "+
                " tda_nu_numero_documento_sei=:numeroDocumentoSei "+
                " WHERE "+
                " tda_id_termo_doacao=:idTermoDoacao";
        Query query = em.createNativeQuery(queryString);
        query.setParameter("status", termoDoacao.getStatusTermoDoacao().getValor());
        query.setParameter("numeroDocumentoSei", termoDoacao.getNumeroDocumentoSei());
        query.setParameter("idTermoDoacao", termoDoacao.getId());
        
        try{
            query.executeUpdate();
            return true;
        }catch(Exception ex){
            return false;
        }
    }
    
    //Atualiza somente o status do termo de doação
    private boolean atualizarStatusDoTermoDeDoacaoPeloId(EnumStatusTermoDoacao status, Long idTermoDoacao){
        String queryString = "UPDATE side.tb_tda_termo_doacao "+
                " SET "+ 
                " tda_st_status_termo_doacao=:status "+
                " WHERE "+
                " tda_id_termo_doacao=:idTermoDoacao";
        Query query = em.createNativeQuery(queryString);
        query.setParameter("status", status.getValor());
        query.setParameter("idTermoDoacao", idTermoDoacao);
        
        try{
            query.executeUpdate();
            return true;
        }catch(Exception ex){
            return false;
        }
        
    }

    // Este metodo irá setar o valor do id do termo de recebimento salvo no item da nota fiscal passado como parametro
    private void setarTermoDoacaoAObjetoFornecimentoContrato(TermoDoacao termo,Long idObjetoFornecimentoContrato){
        String queryString = "UPDATE side.tb_ofo_objeto_fornecimento_contrato "
                +"SET "
                + "     ofo_fk_trd_id_termo_doacao=:idTermoDoacao "
                +"WHERE "
                + "     ofo_id_objeto_fornecimento_contrato=:idObjetoFornecimentoContrato ";
        
        Query query = em.createNativeQuery(queryString);
        query.setParameter("idTermoDoacao", termo.getId());
        query.setParameter("idObjetoFornecimentoContrato", idObjetoFornecimentoContrato);
        query.executeUpdate();
        em.flush();
    }
    
    //Atualiza o status da geração dos termos do objeto fornecimento contrato
    private boolean atualizarSituacaoDaGeracaoDosTermosDoObjetoFornecimentoPeloId(EnumSituacaoGeracaoTermos status,Long idObjetoFornecimentoContrato){
        String queryString = "UPDATE side.tb_ofo_objeto_fornecimento_contrato "
                +"SET "
                + "     ofo_st_situacao_geracao_termo=:status "
                +"WHERE "
                + "     ofo_id_objeto_fornecimento_contrato=:idObjetoFornecimentoContrato ";
        
        Query query = em.createNativeQuery(queryString);
        query.setParameter("status", status.getValor());
        query.setParameter("idObjetoFornecimentoContrato", idObjetoFornecimentoContrato);
        
        try{
            query.executeUpdate();
            return true;
        }catch(Exception ex){
            return false;
        }
        
    }
    
    public void setarTermoDoacaoParaObjetoFornecimentoContrato(Long idTermo, Long idOfo){
        String queryString = "UPDATE side.tb_ofo_objeto_fornecimento_contrato "
                +"SET "
                + "     ofo_fk_trd_id_termo_doacao=:idTermo "
                +"WHERE "
                + "     ofo_id_objeto_fornecimento_contrato=:idOfo ";
        
        Query query = em.createNativeQuery(queryString);
        query.setParameter("idTermo", idTermo);
        query.setParameter("idOfo", idOfo);
        query.executeUpdate();
        em.flush();
    }
    
    
    
}
