package br.gov.mj.side.web.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.gov.mj.apoio.entidades.Orgao;
import br.gov.mj.apoio.entidades.UnidadeExecutora;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.enums.EnumFormaVerificacaoFormatacao;
import br.gov.mj.side.entidades.enums.EnumOrigemArquivo;
import br.gov.mj.side.entidades.enums.EnumSituacaoBem;
import br.gov.mj.side.entidades.enums.EnumSituacaoGeracaoTermos;
import br.gov.mj.side.entidades.enums.EnumSituacaoPesquisaPatrimoniamento;
import br.gov.mj.side.entidades.enums.EnumTipoPatrimonio;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaPotencialBeneficiarioUf;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContratoResposta;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoObjetoFornecimento;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.patrimoniamento.ArquivoUnico;
import br.gov.mj.side.entidades.programa.patrimoniamento.PatrimonioObjetoFornecimento;
import br.gov.mj.side.web.dto.PatrimoniamentoResultadoPesquisaDto;
import br.gov.mj.side.web.dto.PatrimoniamentoTipo;
import br.gov.mj.side.web.dto.PatrimonioObjetoFornecimentoDto;
import br.gov.mj.side.web.service.ArquivoUnicoService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.service.PublicizacaoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class PatrimonioDAO {

    @Inject
    private EntityManager em;

    @Inject
    private ArquivoUnicoService arquivoUnicoService;
    
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;
    
    public List<PatrimonioObjetoFornecimento> buscarPaginado(PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDto, int first, int size, EnumOrder order, String propertyOrder) {

        List<PatrimonioObjetoFornecimento> lista1 = buscarSemPaginacao(patrimonioObjetoFornecimentoDto, order, propertyOrder);

        // filtra paginado
        List<PatrimonioObjetoFornecimento> listaRetorno = new ArrayList<PatrimonioObjetoFornecimento>();
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

    public List<PatrimonioObjetoFornecimento> buscarSemPaginacao(PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDto) {
        return buscarSemPaginacao(patrimonioObjetoFornecimentoDto, EnumOrder.ASC, "id");
    }
    
    public List<PatrimonioObjetoFornecimento> incluirAlterar(List<PatrimonioObjetoFornecimento> listaPatrimoniamentoAtualizar, List<PatrimonioObjetoFornecimento> listaPatrimoniamentoAnterior) {

        List<PatrimonioObjetoFornecimento> listaPatrimoniamentoMantidos = new ArrayList<PatrimonioObjetoFornecimento>();
        List<PatrimonioObjetoFornecimento> listaPatrimoniamentoRemover = new ArrayList<PatrimonioObjetoFornecimento>();
        List<PatrimonioObjetoFornecimento> listaPatrimoniamentoNovos = new ArrayList<PatrimonioObjetoFornecimento>();
        /*
         * Irá comparar a lista anterior de patrimoniamento com a atual para saber oq manter e o que apagar do banco.
         */
        for (PatrimonioObjetoFornecimento patrimoniamentoAnterior : listaPatrimoniamentoAnterior) {
            if (listaPatrimoniamentoAtualizar.contains(patrimoniamentoAnterior)) {
                listaPatrimoniamentoMantidos.add(patrimoniamentoAnterior);
            }else{
                listaPatrimoniamentoRemover.add(patrimoniamentoAnterior);
            }
        }
        
        //Irá selecionar os patrimoniamentos novos
        for(PatrimonioObjetoFornecimento patrimoniamentoNovo:listaPatrimoniamentoAtualizar){
        	if(patrimoniamentoNovo.getId() == null){
        		listaPatrimoniamentoNovos.add(patrimoniamentoNovo);
        	}
        }
        if(listaPatrimoniamentoAnterior.size()>0){
	        listaPatrimoniamentoAtualizar.clear();
	        listaPatrimoniamentoAtualizar.addAll(listaPatrimoniamentoNovos);
	        listaPatrimoniamentoAtualizar.addAll(listaPatrimoniamentoMantidos);
        }
        
        //Irá atualizar ou salvar os novos patrimoniamentos.
        for(PatrimonioObjetoFornecimento patrimonioObjetoFornecimento:listaPatrimoniamentoAtualizar){
            if(patrimonioObjetoFornecimento != null && patrimonioObjetoFornecimento.getId() != null){
            	if(patrimonioObjetoFornecimento.getArquivoUnico()!=null){//Nao entra aqui quando o item nao for patrimoniavel
	                ArquivoUnico aun = patrimonioObjetoFornecimento.getArquivoUnico();
	                arquivoUnicoService.alterar(aun);
            	}
                alterar(patrimonioObjetoFornecimento);
            }else{
            	if(patrimonioObjetoFornecimento.getArquivoUnico()!=null){//Nao entra aqui quando o item nao for patrimoniavel
	                ArquivoUnico aun = patrimonioObjetoFornecimento.getArquivoUnico();
	                aun.setOrigemArquivo(EnumOrigemArquivo.PATRIMONIAMENTO);
	                ArquivoUnico novo = arquivoUnicoService.incluir(aun);
	                patrimonioObjetoFornecimento.setArquivoUnico(novo);
            	}
                incluir(patrimonioObjetoFornecimento);
                ordemFornecimentoContratoService.alterarStatusObjetoFornecimentoAoInformarPatrimoniamento(patrimonioObjetoFornecimento.getObjetoFornecimentoContrato());
            }
        }
        
        //Irá remover do banco os patrimoniamentos que foram apagados
        for(PatrimonioObjetoFornecimento patrimonioObjetoFornecimento:listaPatrimoniamentoRemover){
            PatrimonioObjetoFornecimento apagar = em.find(PatrimonioObjetoFornecimento.class, patrimonioObjetoFornecimento.getId());
            excluir(apagar);
        }
        
        return listaPatrimoniamentoAtualizar;
    }

    public PatrimonioObjetoFornecimento incluir(PatrimonioObjetoFornecimento patrimonioObjetoFornecimento) {

        patrimonioObjetoFornecimento.setDataCadastro(LocalDateTime.now());
        em.persist(patrimonioObjetoFornecimento);
        return null;
    }
    
    public PatrimonioObjetoFornecimento alterar(PatrimonioObjetoFornecimento patrimonioObjetoFornecimento){
        
        PatrimonioObjetoFornecimento patrimonioMerge = buscarPeloId(patrimonioObjetoFornecimento.getId());
        patrimonioMerge.setConteudo(patrimonioObjetoFornecimento.getConteudo());
        patrimonioMerge.setLatitudeLongitudeFoto(patrimonioObjetoFornecimento.getLatitudeLongitudeFoto());
        patrimonioMerge.setNomeAnexo(patrimonioObjetoFornecimento.getNomeAnexo());
        patrimonioMerge.setNomeItem(patrimonioObjetoFornecimento.getNomeItem());
        patrimonioMerge.setNumeroHashFotoUnica(patrimonioObjetoFornecimento.getNumeroHashFotoUnica());
        patrimonioMerge.setNumeroPatrimonio(patrimonioObjetoFornecimento.getNumeroPatrimonio());
        patrimonioMerge.setObjetoFornecimentoContrato(patrimonioObjetoFornecimento.getObjetoFornecimentoContrato());
        patrimonioMerge.setTamanho(patrimonioObjetoFornecimento.getTamanho());
        patrimonioMerge.setMotivoItemNaoPatrimoniavel(patrimonioObjetoFornecimento.getMotivoItemNaoPatrimoniavel());
        
        return em.merge(patrimonioMerge); 
    }
    
    public void excluir(PatrimonioObjetoFornecimento patrimonioObjetoFornecimento){
        em.remove(patrimonioObjetoFornecimento);
    }
    
    public PatrimonioObjetoFornecimento buscarPeloId(Long id) {
        return em.find(PatrimonioObjetoFornecimento.class, id);
    }

    public List<PatrimonioObjetoFornecimento> buscarSemPaginacaoOrdenado(PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDto, EnumOrder order, String propertyOrder) {
        return buscarSemPaginacao(patrimonioObjetoFornecimentoDto, order, propertyOrder);
    }

    public List<PatrimoniamentoResultadoPesquisaDto> pesquisarListaDeItens(PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDto){
        
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
        
        //Joins até o programa
        Root<ObjetoFornecimentoContrato> rootOfo = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        Join<ObjetoFornecimentoContrato, OrdemFornecimentoContrato> joinOfc = rootOfo.join("ordemFornecimento");
        Join<OrdemFornecimentoContrato, Contrato> joinContrato = joinOfc.join("contrato");
        Join<Contrato, Programa> joinPrograma = joinContrato.join("programa");
        Join<Programa, UnidadeExecutora> joinUnidadeExecutora = joinPrograma.join("unidadeExecutora");
        Join<UnidadeExecutora, Orgao> joinOrgao = joinUnidadeExecutora.join("orgao");
        
        //Join até o bem
        Join<ObjetoFornecimentoContrato, Bem> joinBem = rootOfo.join("item");
        
        //Join até a nota de remessa
        Join<OrdemFornecimentoContrato, NotaRemessaOrdemFornecimentoContrato> joinNotaRemessa = joinOfc.join("contrato");
        
        //Join até a entidade
        Join<ObjetoFornecimentoContrato, LocalEntregaEntidade> joinLocalEntrega = rootOfo.join("localEntrega");
        Join<LocalEntregaEntidade, Entidade> joinEntidade = joinLocalEntrega.join("entidade");
        
        //Irá buscar pelos valores passados como parametro com exceção do número de identificador único que deverá ser buscado via subQuery abaixo
        Predicate[] predicates = extractPredicatesPesquisarListaItens(patrimonioObjetoFornecimentoDto, criteriaBuilder, joinBem,rootOfo,joinPrograma,joinEntidade);
        
        
        //SUBQUERY NA TABELA FORMATACAO OBJETO FORNECIMENTO
        //O path sera a ligação da tabela ObjetoFornecimentoContrato com o resultado desta subquery abaixo
        Path<Object> path = rootOfo.get("id");
        
        Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
        Root<FormatacaoObjetoFornecimento> rootSubQueryFof = subquery.from(FormatacaoObjetoFornecimento.class);
        Join<FormatacaoObjetoFornecimento, FormatacaoItensContratoResposta> joinFir = rootSubQueryFof.join("formatacaoResposta");
        subquery.select(rootSubQueryFof.get("objetoFornecimento").get("id"));
        
        Predicate[] predicatesSubquery =extractPredicatesDaSubquery(patrimonioObjetoFornecimentoDto,criteriaBuilder,rootSubQueryFof,joinFir);
        subquery.where(criteriaBuilder.and(predicatesSubquery));
        
        
        criteriaQuery.multiselect(joinPrograma.get("identificadorProgramaPublicado"),joinPrograma.get("anoPrograma"), joinPrograma.get("nomePrograma"),
                joinUnidadeExecutora.get("id"),
                joinOrgao.get("siglaOrgao"),
                rootOfo.get("id"),rootOfo.get("tipoPatrimonio"),rootOfo.get("situacaoBem"),rootOfo.get("ordemFornecimento").get("id"),
                rootOfo.get("localEntrega").get("id"),rootOfo.get("formaVerificacao"),
                joinBem.get("id"),joinBem.get("nomeBem"),joinBem.get("descricaoBem"));
        criteriaQuery.where(criteriaBuilder.in(path).value(subquery),criteriaBuilder.and(predicates));
            
        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        List<Object> listaObjetos = query.getResultList();
        
        List<PatrimoniamentoResultadoPesquisaDto> listaRetornar = new ArrayList<PatrimoniamentoResultadoPesquisaDto>();
        listaRetornar = montarResultadoPesquisa(listaObjetos);
        return listaRetornar;
    }
    
    private List<PatrimoniamentoResultadoPesquisaDto> montarResultadoPesquisa(List<Object> listaObjetos){
        
        List<PatrimoniamentoResultadoPesquisaDto> listaRetornar = new ArrayList<PatrimoniamentoResultadoPesquisaDto>();
        
        for (Object o : listaObjetos) {
            Object[] object = (Object[]) o;
            
            OrdemFornecimentoContrato ofc = new OrdemFornecimentoContrato();
            LocalEntregaEntidade entrega = new LocalEntregaEntidade();
            
            //Montando o programa
            Programa programa = new Programa();
            programa.setIdentificadorProgramaPublicado((Integer)object[0]);
            programa.setAnoPrograma((Integer)object[1]);
            programa.setNomePrograma((String)object[2]);
            
            UnidadeExecutora unidade = new UnidadeExecutora();
            unidade.setId((Long)object[3]);
            
            Orgao orgao = new Orgao();
            orgao.setSiglaOrgao((String)object[4]);
            unidade.setOrgao(orgao);
            
            programa.setUnidadeExecutora(unidade);
            
            //Montando o ObjetoFornecimentoContrato
            ObjetoFornecimentoContrato objetoFornecimento = new ObjetoFornecimentoContrato();
            objetoFornecimento.setId((Long)object[5]);
            objetoFornecimento.setTipoPatrimonio((EnumTipoPatrimonio)object[6]);
            objetoFornecimento.setSituacaoBem((EnumSituacaoBem)object[7]);
            
            //Montando a Ordem de fornecimento
            ofc.setId((Long)object[8]);
            objetoFornecimento.setOrdemFornecimento(ofc);
            
            //Montando o local de entrega
            entrega.setId((Long)object[9]);
            objetoFornecimento.setLocalEntrega(entrega);
            
            objetoFornecimento.setFormaVerificacao((EnumFormaVerificacaoFormatacao)object[10]);
            
            //Montando o bem
            Bem bem = new Bem();
            bem.setId((Long) object[11]);
            bem.setNomeBem((String) object[12]);
            bem.setDescricaoBem((String) object[13]);
            
            objetoFornecimento.setItem(bem);

            //Monta o patrimonio para adicionar a lista
            PatrimoniamentoResultadoPesquisaDto patrimonio = new PatrimoniamentoResultadoPesquisaDto();
            patrimonio.setPrograma(programa);
            patrimonio.setBem(bem);
            patrimonio.setObjetoFornecimentoContrato(objetoFornecimento);
            
            
            PatrimonioObjetoFornecimentoDto dto = new PatrimonioObjetoFornecimentoDto();
            dto.setObjetoFornecimentoContrato(objetoFornecimento);
            List<PatrimonioObjetoFornecimento> listaDePatrimoniamento = buscarSemPaginacao(dto);
            
            
            //É necessário buscar o número e o nome dos patrimonios já salvos.
            List<PatrimoniamentoTipo> detalhesDoPatrimonio = new ArrayList<PatrimoniamentoTipo>();
            for (PatrimonioObjetoFornecimento patrimonioObjetoFornecimento : listaDePatrimoniamento) {
                
                PatrimoniamentoTipo ptr = new PatrimoniamentoTipo();
                ptr.setNomePatrimonio(patrimonioObjetoFornecimento.getNomeItem());
                ptr.setNumeroPatrimonio(patrimonioObjetoFornecimento.getNumeroPatrimonio());
                detalhesDoPatrimonio.add(ptr);
            }
            
            patrimonio.setDetalhePatrimonio(detalhesDoPatrimonio);
            listaRetornar.add(patrimonio);
        }        
        return listaRetornar;
    }
    
    private List<PatrimonioObjetoFornecimento> buscarSemPaginacao(PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDto, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<PatrimonioObjetoFornecimento> criteriaQuery = criteriaBuilder.createQuery(PatrimonioObjetoFornecimento.class);
        Root<PatrimonioObjetoFornecimento> root = criteriaQuery.from(PatrimonioObjetoFornecimento.class);

        Predicate[] predicates = extractPredicates(patrimonioObjetoFornecimentoDto, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        }

        TypedQuery<PatrimonioObjetoFornecimento> query = em.createQuery(criteriaQuery);
        List<PatrimonioObjetoFornecimento> listaRetornada = query.getResultList();
        
        List<PatrimonioObjetoFornecimento> listaRetornar = new ArrayList<PatrimonioObjetoFornecimento>();
        return listaRetornada;
    }
    
    private Predicate[] extractPredicates(PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDto, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(patrimonioObjetoFornecimentoDto != null){
            
            //Pesquisa pelo id do patrimonio
            if(patrimonioObjetoFornecimentoDto.getId() != null){
                predicates.add(criteriaBuilder.equal(root.get("id"),patrimonioObjetoFornecimentoDto.getId()));
            }
            
            //Pesquisa pelo Objeto Fornecimento Contrato
            if(patrimonioObjetoFornecimentoDto.getObjetoFornecimentoContrato() != null && patrimonioObjetoFornecimentoDto.getObjetoFornecimentoContrato().getId() != null){
                predicates.add(criteriaBuilder.equal(root.get("objetoFornecimentoContrato").get("id"),patrimonioObjetoFornecimentoDto.getObjetoFornecimentoContrato().getId()));
            }
            
          //Pesquisa pelo número do patrimonio
            if(patrimonioObjetoFornecimentoDto.getNumeroPatrimonio() != null && !"".equalsIgnoreCase(patrimonioObjetoFornecimentoDto.getNumeroPatrimonio())){
                predicates.add(criteriaBuilder.equal(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("numeroPatrimonio"))), "%" + UtilDAO.removerAcentos(patrimonioObjetoFornecimentoDto.getNumeroPatrimonio().toLowerCase()) + "%"));
            }
            
            //Pesquisa pelo nome do item
            if(patrimonioObjetoFornecimentoDto.getNomeItem() != null && !"".equalsIgnoreCase(patrimonioObjetoFornecimentoDto.getNomeBem())){
                predicates.add(criteriaBuilder.equal(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("nomeItem"))), "%" + UtilDAO.removerAcentos(patrimonioObjetoFornecimentoDto.getNomeItem().toLowerCase()) + "%"));
            }
        }
        return predicates.toArray(new Predicate[] {});
    }
    
    private Predicate[] extractPredicatesPesquisarListaItens(PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDto, CriteriaBuilder criteriaBuilder, Join<ObjetoFornecimentoContrato, Bem> joinBem,
            Root<ObjetoFornecimentoContrato> rootOfo,Join<Contrato, Programa> joinPrograma, Join<LocalEntregaEntidade, Entidade> joinEntidade) {
        
        List<Predicate> predicates = new ArrayList<>();

        if(patrimonioObjetoFornecimentoDto != null){
            
            //Irá pesquisar pela entidade
            if(patrimonioObjetoFornecimentoDto.getEntidade() != null && patrimonioObjetoFornecimentoDto.getEntidade().getId() != null){
                predicates.add(criteriaBuilder.equal(joinEntidade.get("id"),patrimonioObjetoFornecimentoDto.getEntidade().getId()));
            }
            
            //Irá pesquisar pelo nome do bem
            if(patrimonioObjetoFornecimentoDto.getNomeBem() != null && !"".equalsIgnoreCase(patrimonioObjetoFornecimentoDto.getNomeBem())){
                predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(joinBem.get("nomeBem"))), "%" + UtilDAO.removerAcentos(patrimonioObjetoFornecimentoDto.getNomeBem().toLowerCase()) + "%"));
            }
            
            //Irá pesquisar pelo programa
            if(patrimonioObjetoFornecimentoDto.getPrograma() != null && patrimonioObjetoFornecimentoDto.getPrograma().getId() != null){
                predicates.add(criteriaBuilder.equal(joinPrograma.get("id"),patrimonioObjetoFornecimentoDto.getPrograma().getId()));
            }
            
            //Irá pesquisar pelo número do QR code que é o id do objeto fornecimento contrato
            if(patrimonioObjetoFornecimentoDto.getNumeroQrCode() != null){
                predicates.add(criteriaBuilder.equal(rootOfo.get("id"),patrimonioObjetoFornecimentoDto.getNumeroQrCode()));
            }
            
          //Irá pesquisar pelo número do QR code que é o id do objeto fornecimento contrato
            if(patrimonioObjetoFornecimentoDto.getFormaVerificacao() != null){
                predicates.add(criteriaBuilder.equal(rootOfo.get("formaVerificacao"),patrimonioObjetoFornecimentoDto.getFormaVerificacao()));
            }
            
            //Irá pesquisar pela situação do bem, se entregue, recebido, ou concluido.
            if(patrimonioObjetoFornecimentoDto.getSituacaoBem() != null){
                predicates.add(criteriaBuilder.equal(rootOfo.get("situacaoBem"),patrimonioObjetoFornecimentoDto.getSituacaoBem()));
            }
            predicates.add(criteriaBuilder.notEqual(rootOfo.get("situacaoBem"),EnumSituacaoBem.NAO_ENTREGUE));
            predicates.add(criteriaBuilder.isNotNull(rootOfo.get("situacaoBem")));
        }
        return predicates.toArray(new Predicate[] {});
    }
        
        
       private Predicate[] extractPredicatesDaSubquery(PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDto, CriteriaBuilder criteriaBuilder, 
               Root<FormatacaoObjetoFornecimento> rootSubQueryFof, Join<FormatacaoObjetoFornecimento, FormatacaoItensContratoResposta> joinFir) {
            
            List<Predicate> predicates = new ArrayList<>();

            if(patrimonioObjetoFornecimentoDto != null){
                
                //Irá pesquisar pelo identificador único
                if(patrimonioObjetoFornecimentoDto.getIdentificadorUnico() != null && !"".equalsIgnoreCase(patrimonioObjetoFornecimentoDto.getIdentificadorUnico())){
                    predicates.add(criteriaBuilder.equal(joinFir.get("respostaAlfanumerico"),patrimonioObjetoFornecimentoDto.getIdentificadorUnico()));
                }          
            }
            return predicates.toArray(new Predicate[] {});
        }

}
