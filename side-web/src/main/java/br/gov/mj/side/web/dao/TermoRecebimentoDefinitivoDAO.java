package br.gov.mj.side.web.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.enums.EnumSituacaoGeracaoTermos;
import br.gov.mj.side.entidades.enums.EnumStatusGeracaoTermoDoacao;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.HistoricoComunicacaoGeracaoOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.TermoDoacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.ItensNotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.TermoRecebimentoDefinitivo;
import br.gov.mj.side.web.dto.TermoRecebimentoDefinitivoDto;
import br.gov.mj.side.web.service.NotaRemessaService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class TermoRecebimentoDefinitivoDAO {

    @Inject
    private EntityManager em;
    
    @Inject
    private NotaRemessaService notaRemessaService;
    
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

    public void excluir(TermoRecebimentoDefinitivo termoRecebimentoDefinitivo) {
        
        for(ItensNotaRemessaOrdemFornecimentoContrato item:termoRecebimentoDefinitivo.getItensNotaRemessaOrdemFornecimentoContrato()){
            retirarTermoDeReferenciaDeUmItemNotaRemessaContrato(item.getId());
        }
        em.remove(termoRecebimentoDefinitivo);
    }

    public List<TermoRecebimentoDefinitivo> buscarPaginado(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto, int first, int size, EnumOrder order, String propertyOrder) {

        List<TermoRecebimentoDefinitivo> lista1 = buscarSemPaginacao(termoRecebimentoDefinitivoDto, order, propertyOrder);

        // filtra paginado
        List<TermoRecebimentoDefinitivo> listaRetorno = new ArrayList<TermoRecebimentoDefinitivo>();
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

    public List<TermoRecebimentoDefinitivo> buscarSemPaginacao(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto) {
        return buscarSemPaginacao(termoRecebimentoDefinitivoDto, EnumOrder.ASC, "id");
    }

    public TermoRecebimentoDefinitivo incluir(TermoRecebimentoDefinitivo termoRecebimentoDefinitivo, List<ObjetoFornecimentoContrato> bensAdicionados, String usuarioLogado) {
        
        termoRecebimentoDefinitivo.setUsuarioAlteracao(usuarioLogado);
        termoRecebimentoDefinitivo.setUsuarioCriacao(usuarioLogado);
        termoRecebimentoDefinitivo.setDataGeracao(LocalDateTime.now());
        termoRecebimentoDefinitivo.setStatusTermoRecebimento(EnumStatusGeracaoTermoDoacao.TERMO_DOACAO_NAO_GERADO);
        em.persist(termoRecebimentoDefinitivo);
        em.flush();
        
        int flag = 0;
        for (ObjetoFornecimentoContrato ofc : bensAdicionados) {
            setarTermoRecebimentoDefinitoAObjetoFornecimentoContrato(termoRecebimentoDefinitivo,ofc.getId());
            
            //Atualiza o status do item que é do tipo ObjetoFornecimentoContrato 
            notaRemessaService.atualizarSituacaoGeracaoTermosObjetoFornecimentoContrato(ofc,EnumSituacaoGeracaoTermos.TERMO_RECEBIMENTO_DEFINITIVO_GERADO);
            
            if(flag == 0){
                Long idItem = notaRemessaService.buscarIdItemOrdemFornecimentoPeloObjetoFornecimentoContrato(ofc);
                ItensOrdemFornecimentoContrato objetoItem = ordemFornecimentoContratoService.buscarItensOrdemFornecimentoContratoPeloId(idItem);
                String nomeBeneficiario = objetoItem.getLocalEntrega().getEntidade().getNomeEntidade();
                termoRecebimentoDefinitivo.setNomeBeneficiario(nomeBeneficiario);
                termoRecebimentoDefinitivo.setEntidade(objetoItem.getLocalEntrega().getEntidade());
                flag ++;
            }
        }
        termoRecebimentoDefinitivo.setNomeAnexo(SideUtil.adicionarZerosAString(termoRecebimentoDefinitivo.getId().toString(), 7));
        em.merge(termoRecebimentoDefinitivo);
        return termoRecebimentoDefinitivo;
    }

    public TermoRecebimentoDefinitivo atualizar(TermoRecebimentoDefinitivo termoRecebimentoDefinitivo){
        
        TermoRecebimentoDefinitivo termoMerge = buscarPeloId(termoRecebimentoDefinitivo.getId());
        termoMerge.setNotaFiscal(termoRecebimentoDefinitivo.getNotaFiscal());
        termoMerge.setNumeroNotaFiscal(termoRecebimentoDefinitivo.getNumeroNotaFiscal());
        termoMerge.setNomeAnexoNotaFiscal(termoRecebimentoDefinitivo.getNomeAnexoNotaFiscal());
        termoMerge.setTamanhoAnexoNotaFiscal(termoRecebimentoDefinitivo.getTamanhoAnexoNotaFiscal());
        return em.merge(termoMerge); 
    }
    
    public TermoRecebimentoDefinitivo buscaPeloIdHibernate(Long id){
        
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TermoRecebimentoDefinitivo> criteriaQuery = criteriaBuilder.createQuery(TermoRecebimentoDefinitivo.class);
        
        Root<TermoRecebimentoDefinitivo> rootTermo = criteriaQuery.from(TermoRecebimentoDefinitivo.class);
        rootTermo.alias("trd"); 
        
        Predicate predicate = criteriaBuilder.equal(rootTermo.get("id"), id);
        
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicate);
        TypedQuery<TermoRecebimentoDefinitivo> query = em.createQuery(criteriaQuery);
        List<TermoRecebimentoDefinitivo> listaTermos = query.getResultList();
        
        TermoRecebimentoDefinitivo objetoRetornar = new TermoRecebimentoDefinitivo();
        if(listaTermos != null && listaTermos.size()>0){
            objetoRetornar = listaTermos.get(0);
        }
        return objetoRetornar;
    }
    
    public TermoRecebimentoDefinitivo alterar(TermoRecebimentoDefinitivo termoRecebimentoDefinitivo) {
        
        TermoRecebimentoDefinitivo termoParaMerge = buscaPeloIdHibernate(termoRecebimentoDefinitivo.getId());
        termoParaMerge.setDataGeracao(termoRecebimentoDefinitivo.getDataGeracao());
        termoParaMerge.setNomeAnexo(termoRecebimentoDefinitivo.getNomeAnexo());
        termoParaMerge.setNomeAnexoNotaFiscal(termoRecebimentoDefinitivo.getNomeAnexoNotaFiscal());
        termoParaMerge.setNomeBeneficiario(termoRecebimentoDefinitivo.getNomeBeneficiario());
        
        if(termoRecebimentoDefinitivo.getNotaFiscal() != null){
            termoParaMerge.setNotaFiscal(termoRecebimentoDefinitivo.getNotaFiscal());
        }
        
        termoParaMerge.setNumeroDocumentoSei(termoRecebimentoDefinitivo.getNumeroDocumentoSei());
        termoParaMerge.setNumeroNotaFiscal(termoRecebimentoDefinitivo.getNumeroNotaFiscal());
        termoParaMerge.setNumeroProcessoSEI(termoRecebimentoDefinitivo.getNumeroProcessoSEI());
        termoParaMerge.setStatusTermoRecebimento(termoRecebimentoDefinitivo.getStatusTermoRecebimento());
        termoParaMerge.setTamanhoAnexoNotaFiscal(termoRecebimentoDefinitivo.getTamanhoAnexoNotaFiscal());
        if(termoRecebimentoDefinitivo.getTermoRecebimentoDefinitivoGerado() != null){
            termoParaMerge.setTermoRecebimentoDefinitivoGerado(termoRecebimentoDefinitivo.getTermoRecebimentoDefinitivoGerado());
        }
        termoParaMerge.setUsuarioAlteracao(termoRecebimentoDefinitivo.getUsuarioAlteracao());
        return em.merge(termoParaMerge);
    }
    
    private TermoRecebimentoDefinitivoDto montarDtoTermoDoacao(TermoRecebimentoDefinitivo termo){
        
        TermoRecebimentoDefinitivoDto termoDto = new TermoRecebimentoDefinitivoDto();
        
        termoDto.setIdTermoDoacao(termo.getId());
        termoDto.setDataGeracao(termo.getDataGeracao());
        String numeroTermo = SideUtil.adicionarZerosAString(termo.getId().toString(),6);        
        termoDto.setNumeroTermoDoacao(numeroTermo);
        
        return termoDto;
    }
    
    public TermoRecebimentoDefinitivo buscarPeloId(Long id) {
        return em.find(TermoRecebimentoDefinitivo.class, id);
    }

    public List<TermoRecebimentoDefinitivo> buscarSemPaginacaoOrdenado(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto, EnumOrder order, String propertyOrder) {
        return buscarSemPaginacao(termoRecebimentoDefinitivoDto, order, propertyOrder);
    }
    
    public List<TermoRecebimentoDefinitivo> buscarSemPaginacao(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
        
        //Inicialmente irá filtrar os termos de doação pelo programa
        Root<ObjetoFornecimentoContrato> rootOfc = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        rootOfc.alias("ofo");
        
        Join<ObjetoFornecimentoContrato,TermoRecebimentoDefinitivo> joinTermo = rootOfc.join("termoRecebimentoDefinitivo");
        joinTermo.alias("trd");
        
        Join<TermoRecebimentoDefinitivo,NotaRemessaOrdemFornecimentoContrato> joinNotaRemessa = joinTermo.join("notaRemessaOrdemFornecimentoContrato");
        joinNotaRemessa.alias("nrc");
        
        Join<NotaRemessaOrdemFornecimentoContrato,OrdemFornecimentoContrato> joinOf = joinNotaRemessa.join("ordemFornecimento");
        joinOf.alias("ofc");
        
        Join<OrdemFornecimentoContrato,Contrato> rootJoinContrato = joinOf.join("contrato");
        rootJoinContrato.alias("cont");
        
        Join<Contrato,Programa> rootJoinPrograma = rootJoinContrato.join("programa");
        rootJoinPrograma.alias("prg");
        
        //Irá buscar até o local de entrega da entidade
        Join<ObjetoFornecimentoContrato,Bem> joinBem = rootOfc.join("item");
        joinBem.alias("bem");
        
        Join<ObjetoFornecimentoContrato,LocalEntregaEntidade> joinLocalEntrega = rootOfc.join("localEntrega");
        joinLocalEntrega.alias("local");
        
        Join<LocalEntregaEntidade,Entidade> joinEntidade = joinLocalEntrega.join("entidade");
        joinEntidade.alias("entidade");
        
        Join<Entidade,Municipio> joinMunicipio = joinEntidade.join("municipio");
        joinMunicipio.alias("municipio");
        
        Join<Municipio,Uf> joinUf = joinMunicipio.join("uf");
        joinUf.alias("uf");
        
        
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates = extractPredicatesSemPaginacao2(termoRecebimentoDefinitivoDto, criteriaBuilder, rootJoinPrograma,joinNotaRemessa,joinTermo,rootOfc,joinBem,joinUf,joinEntidade);
        
        criteriaQuery.multiselect(
                joinTermo.get("id"),joinTermo.get("nomeBeneficiario"),
                joinTermo.get("dataGeracao"),joinTermo.get("nomeAnexo"),
                joinTermo.get("numeroProcessoSEI"),joinTermo.get("numeroDocumentoSei"),
                joinTermo.get("numeroNotaFiscal"),joinTermo.get("nomeAnexoNotaFiscal"),
                joinTermo.get("tamanhoAnexoNotaFiscal"),joinTermo.get("statusTermoRecebimento"),
                rootOfc.get("id"), rootOfc.get("motivo"),rootOfc.get("motivoNaoConformidade"),
                rootOfc.get("estadoDeNovo"),rootOfc.get("funcionandoDeAcordo"), rootOfc.get("configuradoDeAcordo"),
                rootOfc.get("descricaoNaoConfiguradoDeAcordo"),rootOfc.get("descricaoNaoFuncionandoDeAcordo"),
                joinBem.get("id"),joinBem.get("nomeBem"),
                joinNotaRemessa.get("id"),joinNotaRemessa.get("dataPrevistaEntrega"),
                joinNotaRemessa.get("dataEfetivaEntrega"),joinNotaRemessa.get("numeroNotaRemessa"),
                joinNotaRemessa.get("codigoGerado"),joinNotaRemessa.get("codigoInformadoPeloFornecedor"),
                joinEntidade.get("id"),joinEntidade.get("numeroCnpj"),
                joinEntidade.get("email"),joinEntidade.get("numeroProcessoSEI"),
                joinEntidade.get("nomeEntidade"),
                joinLocalEntrega.get("id"),joinLocalEntrega.get("nomeEndereco"),
                joinLocalEntrega.get("descricaoEndereco"),joinLocalEntrega.get("numeroEndereco"),
                joinLocalEntrega.get("complementoEndereco")).where(predicates.toArray(new Predicate[] {}));
        
        //Irá buscar todos os ids dos termos de recebimento deste programa
        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        List<Object> listaTermos = query.getResultList();
        List<TermoRecebimentoDefinitivo> listaRetornar = montaAListaComOsTermosDeRecebimento(listaTermos);
        return listaRetornar;
    }
    
    private List<TermoRecebimentoDefinitivo> montaAListaComOsTermosDeRecebimento(List<Object> listaTermos)
    {
        Map<Long,TermoRecebimentoDefinitivo> map = new HashMap<Long,TermoRecebimentoDefinitivo>();
        
        for (Object termos : listaTermos) {
            
            List<ObjetoFornecimentoContrato> listaObjetosAdicionar = new ArrayList<ObjetoFornecimentoContrato>();
            
            Object[] objTermo = (Object[]) termos;
            
            Long idTermo = (Long)objTermo[0];
            
            Bem bem = new Bem();
            bem.setId(objTermo[18] == null?0:(Long) objTermo[18]);
            bem.setNomeBem(objTermo[19] == null?"":(String) objTermo[19]);

            Entidade entidade = new Entidade();
            entidade.setId((Long)objTermo[26] == null?0:(Long) objTermo[26]);
            entidade.setNumeroCnpj(objTermo[27] == null?"":(String) objTermo[27]);
            entidade.setEmail(objTermo[28] == null?"":(String) objTermo[28]);
            entidade.setNumeroProcessoSEI(objTermo[29] == null?"":(String) objTermo[29]);
            entidade.setNomeEntidade(objTermo[30] == null?"":(String) objTermo[30]);
            
            LocalEntregaEntidade localEntrega = new LocalEntregaEntidade();
            localEntrega.setId((Long)objTermo[31] == null?0:(Long) objTermo[31]);
            localEntrega.setNomeEndereco(objTermo[32] == null?"":(String) objTermo[32]);
            localEntrega.setDescricaoEndereco(objTermo[33] == null?"":(String) objTermo[33]);
            localEntrega.setNumeroEndereco(objTermo[34] == null?"":(String) objTermo[34]);
            localEntrega.setComplementoEndereco(objTermo[35] == null?"":(String) objTermo[35]);
            localEntrega.setEntidade(entidade);
            
            ObjetoFornecimentoContrato ofc = new ObjetoFornecimentoContrato();
            ofc.setId(objTermo[10] == null?0:(Long) objTermo[10]);
            ofc.setItem(bem);
            ofc.setMotivo(objTermo[11] == null?"":(String) objTermo[11]);
            ofc.setMotivoNaoConformidade(objTermo[12] == null?"":(String) objTermo[12]);
            ofc.setEstadoDeNovo(objTermo[13] == null?null:(Boolean) objTermo[13]);
            ofc.setFuncionandoDeAcordo(objTermo[14] == null?null:(Boolean) objTermo[14]);
            ofc.setConfiguradoDeAcordo(objTermo[15] == null?null:(Boolean) objTermo[15]);
            ofc.setDescricaoNaoFuncionandoDeAcordo(objTermo[16] == null?"":(String) objTermo[16]);
            ofc.setDescricaoNaoConfiguradoDeAcordo(objTermo[17] == null?"":(String) objTermo[17]);
            ofc.setItem(bem);
            ofc.setLocalEntrega(localEntrega);
            
            if(!map.containsKey(idTermo)){
                
                //Cria um termo de recebimento novo
                TermoRecebimentoDefinitivo trd = new TermoRecebimentoDefinitivo();
                trd.setId(idTermo);
                trd.setNomeBeneficiario((String) objTermo[1] == null?"":(String) objTermo[1]);
                trd.setDataGeracao((LocalDateTime) objTermo[2]== null?null:(LocalDateTime) objTermo[2]);
                String nomeAnexo = (String)objTermo[3]== null?"":(String) objTermo[3];
                trd.setNomeAnexo(nomeAnexo);
                trd.setNumeroProcessoSEI(objTermo[4] == null?"":(String) objTermo[4]);
                trd.setNumeroDocumentoSei(objTermo[5] == null?"":(String) objTermo[5]);
                trd.setNumeroNotaFiscal(objTermo[6] == null?"":(String) objTermo[6]);
                trd.setNomeAnexoNotaFiscal(objTermo[7] == null?"":(String) objTermo[7]);
                trd.setTamanhoAnexoNotaFiscal(objTermo[8] == null?0:(Long) objTermo[8]);
                trd.setStatusTermoRecebimento(objTermo[9] == null?null:(EnumStatusGeracaoTermoDoacao) objTermo[9]);
                
                //Recebe a nota de remessa vinculada a este termo de recebimento
                NotaRemessaOrdemFornecimentoContrato notaRemessa = new NotaRemessaOrdemFornecimentoContrato();
                notaRemessa.setId((Long)objTermo[20] == null?0:(Long) objTermo[20]);
                notaRemessa.setDataPrevistaEntrega((LocalDate)objTermo[21] == null?null:(LocalDate) objTermo[21]);
                notaRemessa.setDataEfetivaEntrega((LocalDate)objTermo[22] == null?null:(LocalDate) objTermo[22]);
                notaRemessa.setNumeroNotaRemessa(objTermo[23] == null?"":(String) objTermo[23]);
                notaRemessa.setCodigoGerado(objTermo[24] == null?"":(String) objTermo[24]);
                notaRemessa.setCodigoInformadoPeloFornecedor(objTermo[25] == null?"":(String) objTermo[25]);
                trd.setNotaRemessaOrdemFornecimentoContrato(notaRemessa);                
                trd.setEntidade(entidade);
                
                ofc.setTermoRecebimentoDefinitivo(trd);
                //Pega o item vinculado a este termo e adiciona a lista
                listaObjetosAdicionar.add(ofc);
                trd.setObjetosFornecimentoContrato(listaObjetosAdicionar);
                
                map.put(trd.getId(), trd);
            }else{
                
                //Se o termo de recebimento já estiver no map será necessário apenas adicionar este item ao termo
                TermoRecebimentoDefinitivo td = map.get(idTermo);
                ofc.setTermoRecebimentoDefinitivo(td);
                td.getObjetosFornecimentoContrato().add(ofc);
                map.put(idTermo, td);
            }
        }
        
        List<TermoRecebimentoDefinitivo> lista = new ArrayList<TermoRecebimentoDefinitivo>();
        lista.addAll( (Collection<? extends TermoRecebimentoDefinitivo>) map.values());        
        return lista;
    }
    
    private List<TermoRecebimentoDefinitivo> buscarSemPaginacao2(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
        
        //Inicialmente irá filtrar os termos de doação pelo programa
        Root<TermoRecebimentoDefinitivo> rootTermo = criteriaQuery.from(TermoRecebimentoDefinitivo.class);
        rootTermo.alias("trd");
        
        Join<TermoRecebimentoDefinitivo,NotaRemessaOrdemFornecimentoContrato> joinNotaRemessa = rootTermo.join("notaRemessaOrdemFornecimentoContrato");
        joinNotaRemessa.alias("nrc");
        
        Join<NotaRemessaOrdemFornecimentoContrato,OrdemFornecimentoContrato> joinOf = joinNotaRemessa.join("ordemFornecimento");
        joinOf.alias("ofc");
        
        Join<OrdemFornecimentoContrato,Contrato> rootJoinContrato = joinOf.join("contrato");
        rootJoinContrato.alias("cont");
        
        Join<Contrato,Programa> rootJoinPrograma = rootJoinContrato.join("programa");
        rootJoinPrograma.alias("prg");
        
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates = extractPredicatesSemPaginacao(termoRecebimentoDefinitivoDto, criteriaBuilder, rootJoinPrograma,joinNotaRemessa,rootTermo);
        
        criteriaQuery.multiselect(rootTermo.get("id"),rootTermo.get("nomeBeneficiario"),
                rootTermo.get("dataGeracao"),rootTermo.get("nomeAnexo"),
                rootTermo.get("numeroProcessoSEI"),rootTermo.get("numeroDocumentoSei"),
                rootTermo.get("numeroNotaFiscal"),rootTermo.get("nomeAnexoNotaFiscal"),
                rootTermo.get("tamanhoAnexoNotaFiscal")
                ).where(predicates.toArray(new Predicate[] {}));

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, rootTermo)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, rootTermo)));
        }
        
        //Irá buscar todos os ids dos termos de recebimento deste programa
        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        List<Object> listaTermos = query.getResultList();
        
        List<TermoRecebimentoDefinitivo> listaRetornar = montarObjetosDoTermoDeRecebimento(listaTermos,termoRecebimentoDefinitivoDto);
        return listaRetornar;
    }
    
    private List<TermoRecebimentoDefinitivo> montarObjetosDoTermoDeRecebimento(List<Object> listaTermos,TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto){
        List<TermoRecebimentoDefinitivo> listaRetornar = new ArrayList<TermoRecebimentoDefinitivo>();
        
        //Lista com todos os termos de recebimento deste programa
        for (Object termos : listaTermos) {
            Object[] objTermo = (Object[]) termos;
            
            Long idTermo = (Long)objTermo[0];
            String nomeTermo = (String) objTermo[1];
            LocalDateTime dataGeracao = (LocalDateTime) objTermo[2];
            String nomeAnexo = (String)objTermo[3];
            String numeroProcessoSei = objTermo[4] == null?"":(String) objTermo[4];
            String numeroDocumentoSei = objTermo[5] == null?"":(String) objTermo[5];
            String numeroNotaFiscal = objTermo[6] == null?"":(String) objTermo[6];
            String nomeAnexoNotaFiscal = objTermo[7] == null?"":(String) objTermo[7];
            Long tamanhoAnexoNotaFiscal = objTermo[8] == null?0:(Long) objTermo[8];
            
            TermoRecebimentoDefinitivo trd = new TermoRecebimentoDefinitivo();
            trd.setId(idTermo);
            trd.setNomeBeneficiario(nomeTermo);
            trd.setDataGeracao(dataGeracao);
            trd.setNomeAnexo(nomeAnexo);
            trd.setNumeroProcessoSEI(numeroProcessoSei);
            trd.setNumeroDocumentoSei(numeroDocumentoSei);
            trd.setNumeroDocumentoSei(numeroNotaFiscal);
            trd.setNomeAnexoNotaFiscal(nomeAnexoNotaFiscal);
            trd.setTamanhoAnexoNotaFiscal(tamanhoAnexoNotaFiscal);
            termoRecebimentoDefinitivoDto.setTermoRecebimentoDefinitivo(trd);
            
            // Irá buscar os itens que respeitam a regra do filtro e que estão vinculados a este termo que esta
            // sendo iterado
            List<Object> listaObjetos = retornaObjetoFornecimentoContratoPorTermoRecebimento(termoRecebimentoDefinitivoDto);
            
            //Com a lista dos ObjetosFornecimentoContrado a lista será montada
            List<ObjetoFornecimentoContrato> listaObjetosAdicionar = new ArrayList<ObjetoFornecimentoContrato>();
            for (Object object : listaObjetos) {
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
            
            if(listaObjetosAdicionar != null && !listaObjetosAdicionar.isEmpty()){
                trd.setObjetosFornecimentoContrato(listaObjetosAdicionar);
                listaRetornar.add(trd);
            }            
        }        
        return listaRetornar;
    }
    
    //ira devolver o id dos objetoFornecimentoContratos além do id e nome do bem deste Ofc vinculados ao Termo de Recebimento passado como parametro
    public List<Object> retornaObjetoFornecimentoContratoPorTermoRecebimento(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto){
        
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
        
        Root<ObjetoFornecimentoContrato> rootOfc = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        
        Join<ObjetoFornecimentoContrato,TermoRecebimentoDefinitivo> joinTermo = rootOfc.join("termoRecebimentoDefinitivo");
        joinTermo.alias("trd");
        
        Join<ObjetoFornecimentoContrato,Bem> joinBem = rootOfc.join("item");
        joinBem.alias("bem");
        
        Join<ObjetoFornecimentoContrato,LocalEntregaEntidade> joinLocalEntrega = rootOfc.join("localEntrega");
        joinLocalEntrega.alias("local");
        
        Join<LocalEntregaEntidade,Entidade> joinEntidade = joinLocalEntrega.join("entidade");
        joinEntidade.alias("entidade");
        
        Join<Entidade,Municipio> joinMunicipio = joinEntidade.join("municipio");
        joinMunicipio.alias("municipio");
        
        Join<Municipio,Uf> joinUf = joinMunicipio.join("uf");
        joinUf.alias("uf");
        
        //Join para programa
        Join<ObjetoFornecimentoContrato,OrdemFornecimentoContrato> joinOf = rootOfc.join("ordemFornecimento");
        joinOf.alias("ofc");
        
        Join<OrdemFornecimentoContrato,Contrato> rootJoinContrato = joinOf.join("contrato");
        rootJoinContrato.alias("cont");
        
        Join<Contrato,Programa> rootJoinPrograma = rootJoinContrato.join("programa");
        rootJoinPrograma.alias("prg");
        
        List<Predicate> predicates = new ArrayList<Predicate>();
        if(termoRecebimentoDefinitivoDto != null){
            predicates = extractPredicates(termoRecebimentoDefinitivoDto, criteriaBuilder,joinEntidade,joinUf,joinBem,rootOfc,rootJoinPrograma);
        }
        
        criteriaQuery.multiselect(rootOfc.get("id"),joinBem.get("id"),joinBem.get("nomeBem"),rootOfc.get("situacaoGeracaoTermos")).where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));

        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        List<Object> listaObjetos = query.getResultList();
        
        return listaObjetos;
    }
    
   public List<TermoRecebimentoDefinitivo> buscarTodosOsTermosRecebimentoDefinitivoPorNotaRemessa(Long idNotaRemessa){

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TermoRecebimentoDefinitivo> criteriaQuery = criteriaBuilder.createQuery(TermoRecebimentoDefinitivo.class);
        
        Root<TermoRecebimentoDefinitivo> rootTermo = criteriaQuery.from(TermoRecebimentoDefinitivo.class);
        Predicate predNotaRemessa = criteriaBuilder.equal(rootTermo.get("notaRemessaOrdemFornecimentoContrato").get("id"), idNotaRemessa);
        
        criteriaQuery.select(criteriaQuery.getSelection()).where(criteriaBuilder.and(predNotaRemessa));
        
        TypedQuery<TermoRecebimentoDefinitivo> query = em.createQuery(criteriaQuery);
        List<TermoRecebimentoDefinitivo> listaTermo = query.getResultList();
        return listaTermo;
    }
    
    public List<Object> buscarDadosBasicosTermosRecebimentoPorNotaRemessa(NotaRemessaOrdemFornecimentoContrato nota){
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
        
        Root<TermoRecebimentoDefinitivo> rootTermo = criteriaQuery.from(TermoRecebimentoDefinitivo.class);
        Join<TermoRecebimentoDefinitivo,NotaRemessaOrdemFornecimentoContrato> rootJoinNota = rootTermo.join("notaRemessaOrdemFornecimentoContrato");
        
        Predicate idNotaRemessa = criteriaBuilder.equal(rootJoinNota.get("id"), nota.getId());
        
        criteriaQuery.multiselect(rootTermo.get("id"), rootTermo.get("nomeAnexo"),rootTermo.get("dataGeracao")).where(criteriaBuilder.and(idNotaRemessa));

        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        List<Object> lista = query.getResultList();
        return lista;
    }
    
    //Ira retornar os ids local de entrega, ids ordem fornecimento e ids bens de todos os itens da nota fiscal
    public List<Object> buscarListItensPorTermoRecebimentoDefinitivo(Long idTermo){
        
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
        
        Root<ItensNotaRemessaOrdemFornecimentoContrato> rootItens = criteriaQuery.from(ItensNotaRemessaOrdemFornecimentoContrato.class);
        Join<ItensNotaRemessaOrdemFornecimentoContrato,TermoRecebimentoDefinitivo> rootJoinTermo = rootItens.join("termoRecebimentoDefinitivo");
        Join<ItensNotaRemessaOrdemFornecimentoContrato,ItensNotaRemessaOrdemFornecimentoContrato> rootJoinItensOF = rootItens.join("itemOrdemFornecimentoContrato");
        
        Predicate idTermoRecebimento = criteriaBuilder.equal(rootJoinTermo.get("id"), idTermo);
        
        criteriaQuery.multiselect(rootJoinItensOF.get("localEntrega").get("id"),rootJoinItensOF.get("item").get("id"),
                rootJoinItensOF.get("ordemFornecimento").get("id"),rootJoinItensOF.get("item").get("nomeBem")
                ).where(criteriaBuilder.and(idTermoRecebimento));

        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        List<Object> lista = query.getResultList();
        return lista;
    }
    
  //Ira retornar os ids local de entrega, ids ordem fornecimento e ids bens de todos os itens da nota fiscal
    public List<Object> buscarListObjetoFornecimentoPeloTermoRecebimentoDefinitivo(Long idTermo){
        
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
        
        Root<ObjetoFornecimentoContrato> rootItens = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        Join<ObjetoFornecimentoContrato,TermoRecebimentoDefinitivo> rootJoinTermo = rootItens.join("termoRecebimentoDefinitivo");
        
        Predicate idTermoRecebimento = criteriaBuilder.equal(rootJoinTermo.get("id"), idTermo);
        
        criteriaQuery.multiselect(rootItens.get("item").get("nomeBem"),rootItens.get("id")).where(criteriaBuilder.and(idTermoRecebimento));

        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        List<Object> lista = query.getResultList();
        return lista;
    }
    
    public List<TermoRecebimentoDefinitivo> buscarTermosRecebimentoDefinitivoPorPrograma(Programa programa){
        List<Object> idsNotaRemessa = notaRemessaService.buscarIdsNotasRemessasPorPrograma(programa);
        List<TermoRecebimentoDefinitivo> todosTermosDefinitivosPrograma = new ArrayList<TermoRecebimentoDefinitivo>();
        
        Map<Long,Long> mapaNotasRemessas = new HashMap<Long,Long>();
        
        for (Object notasRemessas : idsNotaRemessa) {
            Object[] o = (Object[]) notasRemessas;
            Long idDaNotaRemessa = (Long) o[3];
            
            if(!mapaNotasRemessas.containsKey(idDaNotaRemessa)){
                mapaNotasRemessas.put(idDaNotaRemessa, idDaNotaRemessa);
                
                List<TermoRecebimentoDefinitivo> todosTermos = new ArrayList<TermoRecebimentoDefinitivo>();
                todosTermos = buscarTodosOsTermosRecebimentoDefinitivoPorNotaRemessa(idDaNotaRemessa);
                if(todosTermos != null && !todosTermos.isEmpty()){
                    todosTermosDefinitivosPrograma.addAll(todosTermos);
                }
            }
            
        }
        return todosTermosDefinitivosPrograma;
    }
    
    public List<ObjetoFornecimentoContrato> buscarTodosOsObjetosFornecimentoContratoPeloIdTermo(Long idTermoRecebimentoDefinitivo){
        
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ObjetoFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ObjetoFornecimentoContrato.class);
        
        Root<ObjetoFornecimentoContrato> rootItens = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        Predicate idTermoRecebimento = criteriaBuilder.equal(rootItens.get("termoRecebimentoDefinitivo").get("id"), idTermoRecebimentoDefinitivo);
        criteriaQuery.select(criteriaQuery.getSelection()).where(idTermoRecebimento);

        TypedQuery<ObjetoFornecimentoContrato> query = em.createQuery(criteriaQuery);
        List<ObjetoFornecimentoContrato> lista = query.getResultList();
        
        return lista;
    }
    
    //O número do documento Sei é único
    public boolean verificarSeNumeroDocumentoSeiEstaEmUso(String numeroSei){
        
        if(numeroSeiEmUsoNoHistoricoComunicacao(numeroSei)){
            return true;
        }
        if(numeroSeiEmUsoNoTermoDoacao(numeroSei)){
            return true;
        }
        
        if(numeroSeiEmUsoNoTermoRecebimentoDefinitivo(numeroSei)){
            return true;
        }        
        return false;
    }
    
    //Irá atualizar o número do processo sei para todas as entidades que estão neste programa
    public void atualizarNumeroProcessoSeiPorTermoDeRecebimento(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto){
        
        List<Long> listaIdsTermo = buscarIdsTermosRecebimentoPorProgramaEEntidade(termoRecebimentoDefinitivoDto);
        
        for(Long id:listaIdsTermo){
            String sqlString = "UPDATE side.tb_trd_termo_recebimento_definitivo "+
                    " SET trd_nu_numero_processo_sei=:numeroSei "+
                    " WHERE trd_id_termo_recebimento_definitivo=:idTermo ";
            Query query = em.createNativeQuery(sqlString);
            query.setParameter("numeroSei", termoRecebimentoDefinitivoDto.getNumeroProcessoSei());
            query.setParameter("idTermo", id);
            query.executeUpdate();
        }
    }
    
    //Irá retornar os ids dos termos de recebimento de uma determinada entidade e em um determinado programa
    private List<Long> buscarIdsTermosRecebimentoPorProgramaEEntidade(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto){

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        
        //Inicialmente irá filtrar os termos de doação pelo programa
        Root<TermoRecebimentoDefinitivo> rootTermo = criteriaQuery.from(TermoRecebimentoDefinitivo.class);
        rootTermo.alias("trd");
        
        Join<TermoRecebimentoDefinitivo,NotaRemessaOrdemFornecimentoContrato> joinNotaRemessa = rootTermo.join("notaRemessaOrdemFornecimentoContrato");
        joinNotaRemessa.alias("nrc");
        
        Join<NotaRemessaOrdemFornecimentoContrato,OrdemFornecimentoContrato> joinOf = joinNotaRemessa.join("ordemFornecimento");
        joinOf.alias("ofc");
        
        Join<OrdemFornecimentoContrato,Contrato> rootJoinContrato = joinOf.join("contrato");
        rootJoinContrato.alias("cont");
        
        Join<Contrato,Programa> rootJoinPrograma = rootJoinContrato.join("programa");
        rootJoinPrograma.alias("prg");
        
        //Irá filtrar pela Entidade
        Join<TermoRecebimentoDefinitivo,Entidade> joinEntidade = rootTermo.join("entidade");
        
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.equal(rootJoinPrograma.get("id"), termoRecebimentoDefinitivoDto.getPrograma().getId()));
        predicates.add(criteriaBuilder.equal(joinEntidade.get("id"), termoRecebimentoDefinitivoDto.getEntidade().getId()));
        
        criteriaQuery.select(rootTermo.get("id")).where(predicates.toArray(new Predicate[] {}));
        
        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        List<Long> idsTermos = query.getResultList();
        return idsTermos;
    }
    
    
    private boolean numeroSeiEmUsoNoHistoricoComunicacao(String numeroSei){
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        
        Root<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> rootItens = criteriaQuery.from(HistoricoComunicacaoGeracaoOrdemFornecimentoContrato.class);
        Predicate predNumeroSei = criteriaBuilder.equal(rootItens.get("numeroDocumentoSei"), numeroSei);
        criteriaQuery.select(rootItens.get("id")).where(predNumeroSei);
        
        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        List<Long> idsHistoricoComunicacao = query.getResultList();
        
        if(idsHistoricoComunicacao != null && !idsHistoricoComunicacao.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    
    private boolean numeroSeiEmUsoNoTermoDoacao(String numeroSei){
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        
        Root<TermoDoacao> rootItens = criteriaQuery.from(TermoDoacao.class);
        Predicate predNumeroSei = criteriaBuilder.equal(rootItens.get("numeroDocumentoSei"), numeroSei);
        criteriaQuery.select(rootItens.get("id")).where(predNumeroSei);

        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        List<Long> idsTermoDoacao = query.getResultList();
        
        if(idsTermoDoacao != null && !idsTermoDoacao.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    
    private boolean numeroSeiEmUsoNoTermoRecebimentoDefinitivo(String numeroSei){
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        
        Root<TermoRecebimentoDefinitivo> rootItens = criteriaQuery.from(TermoRecebimentoDefinitivo.class);
        Predicate predNumeroSei = criteriaBuilder.equal(rootItens.get("numeroDocumentoSei"), numeroSei);
        criteriaQuery.select(rootItens.get("id")).where(predNumeroSei);

        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        List<Long> idsTermoRecebimento = query.getResultList();
        
        if(idsTermoRecebimento != null && !idsTermoRecebimento.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    
    private List<TermoRecebimentoDefinitivo> retornaListaFiltrada(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto, TypedQuery<TermoRecebimentoDefinitivo> query) {
        List<TermoRecebimentoDefinitivo> contratoFiltradosPorPredicates = query.getResultList();
        return retornaListaFiltrada(contratoFiltradosPorPredicates, termoRecebimentoDefinitivoDto);
    }

    private List<TermoRecebimentoDefinitivo> retornaListaFiltrada(List<TermoRecebimentoDefinitivo> termoFiltradoPorPredicates, TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto) {
        List<TermoRecebimentoDefinitivo> termosFiltradosFinal = new ArrayList<TermoRecebimentoDefinitivo>();

        boolean possuiNomeAnexo = false;

        if (termoRecebimentoDefinitivoDto.getTermoRecebimentoDefinitivo() != null || termoRecebimentoDefinitivoDto.getTermoRecebimentoDefinitivo().getNomeAnexo() != null) {

            for (TermoRecebimentoDefinitivo termoRecebimentoDefinitivo : termoFiltradoPorPredicates) {

                possuiNomeAnexo = true;
                
                if (possuiNomeAnexo) {
                    termosFiltradosFinal.add(termoRecebimentoDefinitivo);
                }
                
                possuiNomeAnexo = false;

            }
        } else {
            return termoFiltradoPorPredicates;
        }

        return termosFiltradosFinal;
    }
    
    // Este metodo irá setar o valor do id do termo de recebimento salvo no item da nota remessa passado como parametro
    private void setarTermoRecebimentoDefinitoAUmItemNotaRemessaContrato(TermoRecebimentoDefinitivo termo,Long idItemNotaRemessa){
        String queryString = "UPDATE side.tb_inr_item_nota_remessa_of_contrato "
                +"SET "
                + "     inr_fk_trd_id_termo_recebimento_definitivo=:idTermoRecebimento "
                +"WHERE "
                + "     inr_id_item_nota_remessa_of_contrato=:idItemNotaRemessa ";
        
        Query query = em.createNativeQuery(queryString);
        query.setParameter("idTermoRecebimento", termo.getId());
        query.setParameter("idItemNotaRemessa", idItemNotaRemessa);
        query.executeUpdate();
    }
    
    // Este metodo irá setar o valor do id do termo de recebimento salvo no item da nota remessa passado como parametro
    private void setarTermoRecebimentoDefinitoAObjetoFornecimentoContrato(TermoRecebimentoDefinitivo termo,Long idObjetoFornecimentoContrato){
        String queryString = "UPDATE side.tb_ofo_objeto_fornecimento_contrato "
                +"SET "
                + "     ofo_fk_trd_id_termo_recebimento_definitivo=:idTermoRecebimento "
                +"WHERE "
                + "     ofo_id_objeto_fornecimento_contrato=:idObjetoFornecimentoContrato ";
        
        Query query = em.createNativeQuery(queryString);
        query.setParameter("idTermoRecebimento", termo.getId());
        query.setParameter("idObjetoFornecimentoContrato", idObjetoFornecimentoContrato);
        query.executeUpdate();
    }
    
    
    //Seta o valor nulo para um item passado como parametro
    private void retirarTermoDeReferenciaDeUmItemNotaRemessaContrato(Long idItemNotaRemessa){
        String queryString = "UPDATE side.tb_inr_item_nota_remessa_of_contrato "
                +"SET "
                + "     inr_fk_trd_id_termo_recebimento_definitivo=null "
                +"WHERE "
                + "     inr_id_item_nota_remessa_of_contrato=:idItemNotaRemessa ";
        
        Query query = em.createNativeQuery(queryString);
        query.setParameter("idItemNotaRemessa", idItemNotaRemessa);
        query.executeUpdate();
    }
    
    public void atualizarStatusGeracaoTermoDefinitivoPeloId(EnumStatusGeracaoTermoDoacao status, Long idTermoRecebimento){
        String queryString = "UPDATE side.tb_trd_termo_recebimento_definitivo "
                +"SET "
                + "     trd_st_status_termo_recebimento=:status "
                +"WHERE "
                + "     trd_id_termo_recebimento_definitivo=:idTermoRecebimento ";
        
        Query query = em.createNativeQuery(queryString);
        query.setParameter("status", status.getValor());
        query.setParameter("idTermoRecebimento", idTermoRecebimento);
        query.executeUpdate();
    }
    
    private List<Predicate> extractPredicatesSemPaginacao2(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto,CriteriaBuilder criteriaBuilder,Join<Contrato,Programa> rootJoinPrograma,Join<TermoRecebimentoDefinitivo,NotaRemessaOrdemFornecimentoContrato> joinNotaRemessa,Join<ObjetoFornecimentoContrato,TermoRecebimentoDefinitivo> joinTermo,Root<ObjetoFornecimentoContrato> rootOfc,
            Join<ObjetoFornecimentoContrato,Bem> joinBem,Join<Municipio,Uf> joinUf,Join<LocalEntregaEntidade,Entidade> joinEntidade){
        
        List<Predicate> predicates = new ArrayList<Predicate>();
        
        if(termoRecebimentoDefinitivoDto.getPrograma() != null && termoRecebimentoDefinitivoDto.getPrograma().getId() != null){
            predicates.add(criteriaBuilder.equal(rootJoinPrograma.get("id"), termoRecebimentoDefinitivoDto.getPrograma().getId()));
        }
        
        if(termoRecebimentoDefinitivoDto.getNotaRemessaOrdemFornecimentoContrato() != null && termoRecebimentoDefinitivoDto.getNotaRemessaOrdemFornecimentoContrato().getId() != null){
            predicates.add(criteriaBuilder.equal(joinNotaRemessa.get("id"), termoRecebimentoDefinitivoDto.getNotaRemessaOrdemFornecimentoContrato().getId()));
        }
        
        if(termoRecebimentoDefinitivoDto.isBuscarSomenteOsOfosComTermosJaGerados()){
            predicates.add(criteriaBuilder.isNotNull(rootOfc.get("termoRecebimentoDefinitivo")));
        }
        
        if(termoRecebimentoDefinitivoDto.getStatusGeracaoTermoDoacao() != null){
            predicates.add(criteriaBuilder.equal(joinTermo.get("statusTermoRecebimento"), termoRecebimentoDefinitivoDto.getStatusGeracaoTermoDoacao()));
        }
        
        if(termoRecebimentoDefinitivoDto.getEntidade() != null && termoRecebimentoDefinitivoDto.getEntidade().getId() != null){
            Predicate predIdEntidade = criteriaBuilder.equal(joinEntidade.get("id"), termoRecebimentoDefinitivoDto.getEntidade().getId());
             predicates.add(predIdEntidade);
         }
        
        if(termoRecebimentoDefinitivoDto.getNomeBeneciario() != null && !"".equalsIgnoreCase(termoRecebimentoDefinitivoDto.getNomeBeneciario())){
            Predicate predNomeBeneficiario = criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(joinEntidade.get("nomeEntidade"))), "%" + UtilDAO.removerAcentos(termoRecebimentoDefinitivoDto.getNomeBeneciario().toLowerCase()) + "%");
             predicates.add(predNomeBeneficiario);
         }
         
         if(termoRecebimentoDefinitivoDto.getNumeroCnpj() != null && !"".equalsIgnoreCase(termoRecebimentoDefinitivoDto.getNumeroCnpj())){
             Predicate predCnpj = criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(joinEntidade.get("numeroCnpj"))), "%" + UtilDAO.removerAcentos(termoRecebimentoDefinitivoDto.getNumeroCnpj().toLowerCase()) + "%");
             predicates.add(predCnpj);
         }
         
         if(termoRecebimentoDefinitivoDto.getEstado() != null){
             Predicate predEstado = criteriaBuilder.equal(joinUf.get("id"), termoRecebimentoDefinitivoDto.getEstado().getUf().getId());
             predicates.add(predEstado);
         }
         
         if(termoRecebimentoDefinitivoDto.getItem() != null){
             Predicate predNotaRemessa = criteriaBuilder.equal(joinBem.get("id"), termoRecebimentoDefinitivoDto.getItem().getId());
             predicates.add(predNotaRemessa);
         }
        
        return predicates;
    }
    
    private List<Predicate> extractPredicatesSemPaginacao(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto,CriteriaBuilder criteriaBuilder,Join<Contrato,Programa> rootJoinPrograma,Join<TermoRecebimentoDefinitivo,NotaRemessaOrdemFornecimentoContrato> joinNotaRemessa,Root<TermoRecebimentoDefinitivo> rootTermo){
        
        List<Predicate> predicates = new ArrayList<Predicate>();
        
        if(termoRecebimentoDefinitivoDto.getPrograma() != null && termoRecebimentoDefinitivoDto.getPrograma().getId() != null){
            criteriaBuilder.equal(rootJoinPrograma.get("id"), termoRecebimentoDefinitivoDto.getPrograma().getId());
        }
        
        if(termoRecebimentoDefinitivoDto.getNotaRemessaOrdemFornecimentoContrato() != null && termoRecebimentoDefinitivoDto.getNotaRemessaOrdemFornecimentoContrato().getId() != null){
            criteriaBuilder.equal(joinNotaRemessa.get("id"), termoRecebimentoDefinitivoDto.getNotaRemessaOrdemFornecimentoContrato().getId());
        }
        
        if(termoRecebimentoDefinitivoDto.getStatusGeracaoTermoDoacao() != null){
            predicates.add(criteriaBuilder.equal(rootTermo.get("statusTermoRecebimento"), termoRecebimentoDefinitivoDto.getStatusGeracaoTermoDoacao().getValor()));
        }
        
        return predicates;
    }
    
    private List<Predicate> extractPredicates(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto,CriteriaBuilder criteriaBuilder,Join<LocalEntregaEntidade,Entidade> joinEntidade,Join<Municipio,Uf> joinUf,Join<ObjetoFornecimentoContrato,Bem> joinBem,Root<ObjetoFornecimentoContrato> rootOfc,Join<Contrato,Programa> rootJoinPrograma){
        
        List<Predicate> predicates = new ArrayList<Predicate>();
        
        if(termoRecebimentoDefinitivoDto.getPrograma() != null && termoRecebimentoDefinitivoDto.getPrograma().getId() != null){
            Predicate predIdPrograma = criteriaBuilder.equal(rootJoinPrograma.get("id"),termoRecebimentoDefinitivoDto.getPrograma().getId());
             predicates.add(predIdPrograma);
         }
        
        if(termoRecebimentoDefinitivoDto.getTermoRecebimentoDefinitivo() != null && termoRecebimentoDefinitivoDto.getTermoRecebimentoDefinitivo().getId() != null){
            Predicate predIdTermo = criteriaBuilder.equal(rootOfc.get("termoRecebimentoDefinitivo").get("id"),termoRecebimentoDefinitivoDto.getTermoRecebimentoDefinitivo().getId());
             predicates.add(predIdTermo);
         }
        
        if(termoRecebimentoDefinitivoDto.getEntidade() != null && termoRecebimentoDefinitivoDto.getEntidade().getId() != null){
            Predicate predIdEntidade = criteriaBuilder.equal(joinEntidade.get("id"), termoRecebimentoDefinitivoDto.getEntidade().getId());
             predicates.add(predIdEntidade);
         }
        
        if(termoRecebimentoDefinitivoDto.getNomeBeneciario() != null && !"".equalsIgnoreCase(termoRecebimentoDefinitivoDto.getNomeBeneciario())){
            Predicate predNomeBeneficiario = criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(joinEntidade.get("nomeEntidade"))), "%" + UtilDAO.removerAcentos(termoRecebimentoDefinitivoDto.getNomeBeneciario().toLowerCase()) + "%");
             predicates.add(predNomeBeneficiario);
         }
         
         if(termoRecebimentoDefinitivoDto.getNumeroCnpj() != null && !"".equalsIgnoreCase(termoRecebimentoDefinitivoDto.getNumeroCnpj())){
             Predicate predCnpj = criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(joinEntidade.get("numeroCnpj"))), "%" + UtilDAO.removerAcentos(termoRecebimentoDefinitivoDto.getNumeroCnpj().toLowerCase()) + "%");
             predicates.add(predCnpj);
         }
         
         if(termoRecebimentoDefinitivoDto.getEstado() != null){
             Predicate predEstado = criteriaBuilder.equal(joinUf.get("id"), termoRecebimentoDefinitivoDto.getEstado().getUf().getId());
             predicates.add(predEstado);
         }
         
         if(termoRecebimentoDefinitivoDto.getItem() != null){
             Predicate predNotaRemessa = criteriaBuilder.equal(joinBem.get("id"), termoRecebimentoDefinitivoDto.getItem().getId());
             predicates.add(predNotaRemessa);
         }
         
         if(termoRecebimentoDefinitivoDto.getSituacaoGeracaoTermos() != null){
             Predicate predStatusGeracaoTermos = criteriaBuilder.equal(rootOfc.get("situacaoGeracaoTermos"), termoRecebimentoDefinitivoDto.getSituacaoGeracaoTermos());
             predicates.add(predStatusGeracaoTermos); 
         }
         
         //Não irá trazer nenhum OFO com o status do termo de geração nulo e com o termo não gerado.
         if(termoRecebimentoDefinitivoDto.isBuscarSomenteOsOfosComTermosJaGerados()){
             
             Predicate predBuscarTodosOfosComTermosGerados = criteriaBuilder.isNotNull(rootOfc.get("termoRecebimentoDefinitivo"));
             Predicate predStatusGeracaoTermos = criteriaBuilder.notEqual(rootOfc.get("situacaoGeracaoTermos"),EnumSituacaoGeracaoTermos.NAO_GERADO);
             
             predicates.add(predBuscarTodosOfosComTermosGerados); 
             predicates.add(predStatusGeracaoTermos);
         }
         
         return predicates;
    }
    
    

}
