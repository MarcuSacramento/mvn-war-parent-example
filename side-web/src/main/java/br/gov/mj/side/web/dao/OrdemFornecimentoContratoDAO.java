package br.gov.mj.side.web.dao;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
import br.gov.mj.side.entidades.enums.EnumAnaliseFinalItem;
import br.gov.mj.side.entidades.enums.EnumFormaVerificacaoFormatacao;
import br.gov.mj.side.entidades.enums.EnumOrigemArquivo;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumResponsavelPreencherFormatacaoItem;
import br.gov.mj.side.entidades.enums.EnumSituacaoAvaliacaoPreliminarPreenchimentoItem;
import br.gov.mj.side.entidades.enums.EnumSituacaoBem;
import br.gov.mj.side.entidades.enums.EnumSituacaoGeracaoTermos;
import br.gov.mj.side.entidades.enums.EnumSituacaoPreenchimentoItemFormatacaoBeneficiario;
import br.gov.mj.side.entidades.enums.EnumSituacaoPreenchimentoItemFormatacaoFornecedor;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaBeneficiario;
import br.gov.mj.side.entidades.enums.EnumStatusFormatacao;
import br.gov.mj.side.entidades.enums.EnumStatusOrdemFornecimento;
import br.gov.mj.side.entidades.enums.EnumTipoCampoFormatacao;
import br.gov.mj.side.entidades.enums.EnumTipoObjeto;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContratoResposta;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoObjetoFornecimento;
import br.gov.mj.side.entidades.programa.licitacao.contrato.HistoricoComunicacaoGeracaoOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensFormatacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.patrimoniamento.ArquivoUnico;
import br.gov.mj.side.web.dto.FormatacaoObjetoFornecimentoAmbosDto;
import br.gov.mj.side.web.dto.ImagensIguaisConformidadesDto;
import br.gov.mj.side.web.dto.ItemFormatacaoDto;
import br.gov.mj.side.web.dto.ListaLoteUnitariaDto;
import br.gov.mj.side.web.dto.TermoRecebimentoDefinitivoDto;
import br.gov.mj.side.web.dto.formatacaoObjetoFornecimento.FormatacaoItensContratoDtot;
import br.gov.mj.side.web.dto.formatacaoObjetoFornecimento.FormatacaoItensContratoRespostaDto;
import br.gov.mj.side.web.dto.formatacaoObjetoFornecimento.FormatacaoObjetoFornecimentoDto;
import br.gov.mj.side.web.service.ArquivoUnicoService;
import br.gov.mj.side.web.service.FormatacaoItensContratoService;
import br.gov.mj.side.web.service.NotaRemessaService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class OrdemFornecimentoContratoDAO {

    @Inject
    private EntityManager em;
    
    @Inject
    private ArquivoUnicoService arquivoUnicoService;

    @Inject
    private NotaRemessaService notaRemessaService;
    
    @Inject
    private FormatacaoItensContratoService formatacaoItensContratoService;

    /*
    * Este metodo irá trazer exatamente o objeto clicado, mesmo que ele tenha sido devolvido.
    */
    public ObjetoFornecimentoContrato buscarObjetoFornecimentoContratoClicado(Long id) {
        return buscaPeloObjetoFornecimentoContrato(id);
    }

    /*
     * Este metodo irá trazer o último objeto cadastrado, se o objeto original de id 16 foi devolvido, ao chamar este metodo
     * buscando por este objeto será retornado o novo, o que foi devolvido.
     */
    public ObjetoFornecimentoContrato buscarObjetoFornecimentoContrato(Long id) {        
        Long idDevolvido = buscarObjetoFornecimentoContratoDevolvido(id);
        return buscaPeloObjetoFornecimentoContrato(idDevolvido);
    }
    
    private ObjetoFornecimentoContrato buscaPeloObjetoFornecimentoContrato(Long id){
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ObjetoFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ObjetoFornecimentoContrato.class);
        Root<ObjetoFornecimentoContrato> root = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ObjetoFornecimentoContrato> query = em.createQuery(criteriaQuery);

        List<ObjetoFornecimentoContrato> lista = new ArrayList<ObjetoFornecimentoContrato>();
        lista = query.getResultList();

        if (lista.isEmpty()) {
            return null;
        } else {
            return lista.get(0);
        }
    }
    
    public Long buscarObjetoFornecimentoContratoDevolvido(Long id){
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
        Root<ObjetoFornecimentoContrato> root = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        List<Predicate> predicates = new ArrayList<>();
        
        predicates.add(criteriaBuilder.equal(root.get("objetoFornecimentoContratoPai"),id));
        criteriaQuery.select(root.get("id")).where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));
        
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        
        List<Object> lista = new ArrayList<Object>();
        lista = query.getResultList();

        if (lista.isEmpty()) {
            return id;
        } else {
            return (Long) lista.get(0);
        }
    }

    public List<ObjetoFornecimentoContrato> buscarListaObjetoFornecimentoContrato(Long idOrdemFornecimento, LocalEntregaEntidade localEntregaEntidade, EnumTipoObjeto tipoObjeto) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ObjetoFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ObjetoFornecimentoContrato.class);
        Root<ObjetoFornecimentoContrato> root = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        List<Predicate> predicates = new ArrayList<>();
        if (idOrdemFornecimento != null) {
            predicates.add(criteriaBuilder.equal(root.get("ordemFornecimento").get("id"), idOrdemFornecimento));
        }
        if (localEntregaEntidade != null) {
            predicates.add(criteriaBuilder.equal(root.get("localEntrega").get("id"), localEntregaEntidade.getId()));
        }
        if(tipoObjeto == EnumTipoObjeto.ORIGINAIS){
            predicates.add(criteriaBuilder.isNull(root.get("objetoFornecimentoContratoPai")));
        }else{
            if(tipoObjeto == EnumTipoObjeto.TODOS_DEVOLVIDOS){
                predicates.add(criteriaBuilder.isNotNull(root.get("objetoFornecimentoContratoPai")));
            }else{
                if(tipoObjeto == EnumTipoObjeto.DEVOLVIDOS_SEM_VINCULO_COM_NOTA_REMESSA){
                    predicates.add(criteriaBuilder.isNotNull(root.get("objetoFornecimentoContratoPai")));
                    predicates.add(criteriaBuilder.isNull(root.get("notaRemessaOrdemFornecimentoContrato")));
                }else{
                    if(tipoObjeto == EnumTipoObjeto.SEM_VINCULO_COM_NOTA_REMESSA){
                        predicates.add(criteriaBuilder.isNull(root.get("notaRemessaOrdemFornecimentoContrato")));
                    }
                }
            }
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));
        TypedQuery<ObjetoFornecimentoContrato> query = em.createQuery(criteriaQuery);
        List<ObjetoFornecimentoContrato> lista = query.getResultList();
        return lista;
    }
    
    public List<ObjetoFornecimentoContrato> retirarObjetosQueNaoSaoDoPerfil(List<ObjetoFornecimentoContrato> listaObjetos, EnumPerfilEntidade perfilDaBusca){        
        
        List<ObjetoFornecimentoContrato> listaRetornar = new ArrayList<ObjetoFornecimentoContrato>();
        for (ObjetoFornecimentoContrato objetoFornecimentoContrato : listaObjetos) {            
            if(perfilDaBusca == EnumPerfilEntidade.FORNECEDOR){                
                if(objetoFornecimentoContrato.getQuantidadeQuesitosFornecedor() != 0){
                    listaRetornar.add(objetoFornecimentoContrato);
                }
            }else{
                if(objetoFornecimentoContrato.getQuantidadeQuesitosBeneficiario() != 0){
                    listaRetornar.add(objetoFornecimentoContrato);
                }
            }
        }
        return listaRetornar;
    }

    public List<FormatacaoObjetoFornecimentoDto> buscarListaFormatacaoObjetoFornecimento(ObjetoFornecimentoContrato objetoFornecimentoContrato, EnumPerfilEntidade perfil, boolean isOpcional) {
        List<FormatacaoObjetoFornecimentoDto> listaRetorno = new ArrayList<FormatacaoObjetoFornecimentoDto>();
        List<?> listaFormatacaoObjetoFornecimento = em
                .createNativeQuery(
                        "select a.fof_id_formatacao_objeto_fornecimento,a.fof_fk_fic_id_formatacao_itens_contrato,a.fof_fk_fir_id_formatacao_itens_contrato_resposta,a.fof_fk_ofo_id_objeto_fornecimento_contrato,a.fof_tp_responsavel_formatacao,b.fic_bo_possui_informacao_opcional from side.tb_fof_formatacao_objeto_fornecimento a,side.tb_fic_formatacao_itens_contrato b where a.fof_fk_ofo_id_objeto_fornecimento_contrato=:idPai  and a.fof_tp_responsavel_formatacao=:paramPerfil and b.fic_id_formatacao_itens_contrato=a.fof_fk_fic_id_formatacao_itens_contrato  and b.fic_bo_possui_informacao_opcional=:paramOpcional  order by a.fof_id_formatacao_objeto_fornecimento asc")
                .setParameter("idPai", objetoFornecimentoContrato.getId()).setParameter("paramPerfil", perfil.getValor()).setParameter("paramOpcional", isOpcional).getResultList();
        for (Object object : listaFormatacaoObjetoFornecimento) {
            Object[] o = (Object[]) object;

            FormatacaoObjetoFornecimentoDto objeto = new FormatacaoObjetoFornecimentoDto();

            Long id = new Long(((BigInteger) o[0]).toString());
            FormatacaoItensContratoDtot formatacao = buscaFormatacao(new Long(((BigInteger) o[1]).toString()));
            FormatacaoItensContratoRespostaDto formatacaoResposta = null;
            if (o[2] != null) {
                formatacaoResposta = buscaFormatacaoResposta(new Long(((BigInteger) o[2]).toString()), formatacao);
            }

            objeto.setId(id);
            objeto.setFormatacao(formatacao);
            objeto.setFormatacaoResposta(formatacaoResposta);
            String valorPerfilEntidade = (String) o[4];
            EnumPerfilEntidade perfilEntidade = null;

            if (valorPerfilEntidade.equals(EnumPerfilEntidade.FORNECEDOR.getValor())) {
                perfilEntidade = EnumPerfilEntidade.FORNECEDOR;
            } else if (valorPerfilEntidade.equals(EnumPerfilEntidade.BENEFICIARIO.getValor())) {
                perfilEntidade = EnumPerfilEntidade.BENEFICIARIO;
            }
            objeto.setResponsavelFormatacao(perfilEntidade);

            boolean isValorOpcional = (boolean) o[5];
            objeto.setValorOpcional(isValorOpcional);
            listaRetorno.add(objeto);
        }
        return listaRetorno;
    }

    private FormatacaoItensContratoDtot buscaFormatacao(Long id) {

        List<FormatacaoItensContratoDtot> listaRetorno = new ArrayList<FormatacaoItensContratoDtot>();
        List<?> objetos = em
                .createQuery(
                        "select a.id, a.formaVerificacao, a.tipoCampo, a.tituloQuesito, a.orientacaoFornecedores, a.possuiIdentificadorUnico, a.possuiInformacaoOpcional, a.possuiDispositivoMovel, a.possuiGPS, a.possuiData, a.responsavelFormatacao from FormatacaoItensContrato a where a.id=:id order by a.id asc")
                .setParameter("id", id).getResultList();

        for (Object object : objetos) {
            Object[] o = (Object[]) object;
            FormatacaoItensContratoDtot formatacaoItensContratoDto = new FormatacaoItensContratoDtot();

            formatacaoItensContratoDto.setId((Long) o[0]);
            formatacaoItensContratoDto.setFormaVerificacao((EnumFormaVerificacaoFormatacao) o[1]);
            formatacaoItensContratoDto.setTipoCampo((EnumTipoCampoFormatacao) o[2]);
            formatacaoItensContratoDto.setTituloQuesito((String) o[3]);
            formatacaoItensContratoDto.setOrientacaoFornecedores((String) o[4]);
            formatacaoItensContratoDto.setPossuiIdentificadorUnico((Boolean) o[5]);
            formatacaoItensContratoDto.setPossuiInformacaoOpcional((Boolean) o[6]);
            formatacaoItensContratoDto.setPossuiDispositivoMovel((Boolean) o[7]);
            formatacaoItensContratoDto.setPossuiGPS((Boolean) o[8]);
            formatacaoItensContratoDto.setPossuiData((Boolean) o[9]);
            formatacaoItensContratoDto.setResponsavelFormatacao((EnumResponsavelPreencherFormatacaoItem) o[10]);
            listaRetorno.add(formatacaoItensContratoDto);
        }

        if (listaRetorno.size() > 0) {
            return listaRetorno.get(0);
        } else {
            return null;
        }
    }

    private FormatacaoItensContratoRespostaDto buscaFormatacaoResposta(Long id, FormatacaoItensContratoDtot formatacao) {
        FormatacaoItensContratoRespostaDto formatacaoRespostaFotos = buscaFormatacaoRespostaFotos(id, formatacao);
        FormatacaoItensContratoRespostaDto formatacaoRespostaSemFoto = buscaFormatacaoRespostaTodosSemAnexo(id, formatacao);

        if (formatacaoRespostaFotos == null) {
            return formatacaoRespostaSemFoto;
        } else {
            return formatacaoRespostaFotos;
        }
    }

    private FormatacaoItensContratoRespostaDto buscaFormatacaoRespostaFotos(Long id, FormatacaoItensContratoDtot formatacao) {
        List<FormatacaoItensContratoRespostaDto> listaRetorno = new ArrayList<FormatacaoItensContratoRespostaDto>();
        List<EnumTipoCampoFormatacao> listaTipo = Arrays.asList(EnumTipoCampoFormatacao.FOTO);
        List<?> objetos = em
                .createQuery(
                        "select a.id, "
                        + "a.respostaAlfanumerico, "
                        + "a.conteudo, "
                        + "a.nomeAnexo, "
                        + "a.respostaBooleana, "
                        + "a.respostaData, "
                        + "a.dataFoto, "
                        + "a.latitudeLongitudeFoto, "
                        + "a.respostaTexto, "
                        + "a.tamanho, "
                        + "a.statusFormatacao, "
                        + "a.motivoNaoConformidade, "
                        + "a.arquivoUnico "
                        + "from FormatacaoItensContratoResposta a "
                        + "where "
                        + "a.id=:id "
                        + "and a.formatacao.tipoCampo in(:paramTipo) order by a.id asc")
                .setParameter("id", id).setParameter("paramTipo", listaTipo).getResultList();

        for (Object object : objetos) {
            Object[] o = (Object[]) object;
            FormatacaoItensContratoRespostaDto formatacaoItensContratoRespostaDto = new FormatacaoItensContratoRespostaDto();

            formatacaoItensContratoRespostaDto.setId((Long) o[0]);
            formatacaoItensContratoRespostaDto.setFormatacao(formatacao);
            formatacaoItensContratoRespostaDto.setRespostaAlfanumerico((String) o[1]);
            formatacaoItensContratoRespostaDto.setConteudo((byte[]) o[2]);
            formatacaoItensContratoRespostaDto.setNomeAnexo((String) o[3]);
            formatacaoItensContratoRespostaDto.setRespostaBooleana((Boolean) o[4]);
            formatacaoItensContratoRespostaDto.setRespostaData((LocalDate) o[5]);
            formatacaoItensContratoRespostaDto.setDataFoto((LocalDateTime) o[6]);
            formatacaoItensContratoRespostaDto.setLatitudeLongitudeFoto((String) o[7]);
            formatacaoItensContratoRespostaDto.setRespostaTexto((String) o[8]);
            formatacaoItensContratoRespostaDto.setTamanho((Long) o[9]);
            formatacaoItensContratoRespostaDto.setStatusFormatacaoResposta((EnumStatusFormatacao) o[10]);
            formatacaoItensContratoRespostaDto.setMotivoNaoConformidade((String) o[11]);
            formatacaoItensContratoRespostaDto.setArquivoUnico((ArquivoUnico) o[12]);
            

            listaRetorno.add(formatacaoItensContratoRespostaDto);
        }

        if (listaRetorno.size() > 0) {
            return listaRetorno.get(0);
        } else {
            return null;
        }
    }

    private FormatacaoItensContratoRespostaDto buscaFormatacaoRespostaTodosSemAnexo(Long id, FormatacaoItensContratoDtot formatacao) {
        List<FormatacaoItensContratoRespostaDto> listaRetorno = new ArrayList<FormatacaoItensContratoRespostaDto>();
        List<EnumTipoCampoFormatacao> listaTipo = Arrays.asList(EnumTipoCampoFormatacao.FOTO);
        List<?> objetos = em
                .createQuery("select a.id, a.respostaAlfanumerico, a.nomeAnexo, a.respostaBooleana, a.respostaData, a.dataFoto, a.latitudeLongitudeFoto, a.respostaTexto, a.tamanho, a.statusFormatacao, a.motivoNaoConformidade from FormatacaoItensContratoResposta a where a.id=:id and a.formatacao.tipoCampo not in(:paramTipo) order by a.id asc")
                .setParameter("id", id).setParameter("paramTipo", listaTipo).getResultList();

        for (Object object : objetos) {
            Object[] o = (Object[]) object;
            FormatacaoItensContratoRespostaDto formatacaoItensContratoRespostaDto = new FormatacaoItensContratoRespostaDto();

            formatacaoItensContratoRespostaDto.setId((Long) o[0]);
            formatacaoItensContratoRespostaDto.setFormatacao(formatacao);
            formatacaoItensContratoRespostaDto.setRespostaAlfanumerico((String) o[1]);
            formatacaoItensContratoRespostaDto.setConteudo(null);
            formatacaoItensContratoRespostaDto.setNomeAnexo((String) o[2]);
            formatacaoItensContratoRespostaDto.setRespostaBooleana((Boolean) o[3]);
            formatacaoItensContratoRespostaDto.setRespostaData((LocalDate) o[4]);
            formatacaoItensContratoRespostaDto.setDataFoto((LocalDateTime) o[5]);
            formatacaoItensContratoRespostaDto.setLatitudeLongitudeFoto((String) o[6]);
            formatacaoItensContratoRespostaDto.setRespostaTexto((String) o[7]);
            formatacaoItensContratoRespostaDto.setTamanho((Long) o[8]);
            formatacaoItensContratoRespostaDto.setStatusFormatacaoResposta((EnumStatusFormatacao) o[9]);
            formatacaoItensContratoRespostaDto.setMotivoNaoConformidade((String) o[10]);

            listaRetorno.add(formatacaoItensContratoRespostaDto);
        }

        if (listaRetorno.size() > 0) {
            return listaRetorno.get(0);
        } else {
            return null;
        }
    }

    public List<OrdemFornecimentoContrato> buscarOrdemFornecimentoContrato(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<OrdemFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(OrdemFornecimentoContrato.class);
        Root<OrdemFornecimentoContrato> root = criteriaQuery.from(OrdemFornecimentoContrato.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("contrato").get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<OrdemFornecimentoContrato> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public HistoricoComunicacaoGeracaoOrdemFornecimentoContrato buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(Long id, boolean somenteComunicacao) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(HistoricoComunicacaoGeracaoOrdemFornecimentoContrato.class);
        Root<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> root = criteriaQuery.from(HistoricoComunicacaoGeracaoOrdemFornecimentoContrato.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("ordemFornecimento").get("id"), id));
        }
        if (somenteComunicacao) {
            predicates.add(criteriaBuilder.equal(root.get("possuiComunicado"), Boolean.TRUE));
            predicates.add(criteriaBuilder.equal(root.get("possuiCancelamento"), Boolean.FALSE));
        }
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> query = em.createQuery(criteriaQuery);

        List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> lista = new ArrayList<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato>();
        lista = query.getResultList();

        if (lista.isEmpty()) {
            return null;
        } else {
            return lista.get(0);
        }
    }

    public List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> buscarHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(Long id, boolean somenteComunicacao) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(HistoricoComunicacaoGeracaoOrdemFornecimentoContrato.class);
        Root<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> root = criteriaQuery.from(HistoricoComunicacaoGeracaoOrdemFornecimentoContrato.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("ordemFornecimento").get("id"), id));
        }

        if (somenteComunicacao) {
            predicates.add(criteriaBuilder.equal(root.get("possuiComunicado"), Boolean.TRUE));
        }

        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<ItensOrdemFornecimentoContrato> buscarItensOrdemFornecimentoContrato(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ItensOrdemFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ItensOrdemFornecimentoContrato.class);
        Root<ItensOrdemFornecimentoContrato> root = criteriaQuery.from(ItensOrdemFornecimentoContrato.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("ordemFornecimento").get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ItensOrdemFornecimentoContrato> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public ItensOrdemFornecimentoContrato buscarItensOrdemFornecimentoContratoPeloId(Long idItensOrdemFornecimentoContrato){
        ItensOrdemFornecimentoContrato idEncotrado = em.find(ItensOrdemFornecimentoContrato.class, idItensOrdemFornecimentoContrato);
        return idEncotrado;
    }
    
    public ItensOrdemFornecimentoContrato buscarItemDaOrdemDeFornecimentoPeloObjetoFornecimento(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ItensOrdemFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ItensOrdemFornecimentoContrato.class);
        Root<ItensOrdemFornecimentoContrato> root = criteriaQuery.from(ItensOrdemFornecimentoContrato.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("ordemFornecimento"),objetoFornecimentoContrato.getOrdemFornecimento().getId()));
        predicates.add(criteriaBuilder.equal(root.get("item").get("id"),objetoFornecimentoContrato.getItem().getId()));
        predicates.add(criteriaBuilder.equal(root.get("localEntrega").get("id"),objetoFornecimentoContrato.getLocalEntrega().getId()));

        predicates.toArray(new Predicate[] {});
        
        criteriaQuery.select(criteriaQuery.getSelection()).where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));
        TypedQuery<ItensOrdemFornecimentoContrato> query = em.createQuery(criteriaQuery);
        List<ItensOrdemFornecimentoContrato> lista = query.getResultList();
        if(lista != null && !lista.isEmpty()){
            return lista.get(0);
        }else{
            return null;
        }
    }
    
    private List<ItensOrdemFornecimentoContrato> buscarSemPaginacao(Contrato contrato, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ItensOrdemFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ItensOrdemFornecimentoContrato.class);
        Root<ItensOrdemFornecimentoContrato> root = criteriaQuery.from(ItensOrdemFornecimentoContrato.class);

        Predicate[] predicates = extractPredicates(contrato, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        }
        TypedQuery<ItensOrdemFornecimentoContrato> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private Predicate[] extractPredicates(Contrato contrato, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        // id
        if (contrato.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), contrato.getId()));
        }

        return predicates.toArray(new Predicate[] {});
    }
    
    private Predicate[] extractPredicatesObjetoFornecimentoContrato(ObjetoFornecimentoContrato objetoFornecimentoContrato, EnumTipoObjeto tipoObjeto,CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (objetoFornecimentoContrato != null) {
            if (objetoFornecimentoContrato.getItem() != null) {
                predicates.add(criteriaBuilder.equal(root.get("item").get("id"), objetoFornecimentoContrato.getItem().getId()));
            }

            if (objetoFornecimentoContrato.getLocalEntrega() != null) {
                predicates.add(criteriaBuilder.equal(root.get("localEntrega").get("id"), objetoFornecimentoContrato.getLocalEntrega().getId()));
            }

            if (objetoFornecimentoContrato.getOrdemFornecimento() != null) {
                predicates.add(criteriaBuilder.equal(root.get("ordemFornecimento").get("id"), objetoFornecimentoContrato.getOrdemFornecimento().getId()));
            }

            if (objetoFornecimentoContrato.getFormaVerificacao() != null) {
                predicates.add(criteriaBuilder.equal(root.get("formaVerificacao"), objetoFornecimentoContrato.getFormaVerificacao()));
            }
            
            if(tipoObjeto != null){
                if(tipoObjeto == EnumTipoObjeto.ORIGINAIS){
                    predicates.add(criteriaBuilder.isNull(root.get("objetoFornecimentoContratoPai")));
                }else{
                    if(tipoObjeto == EnumTipoObjeto.TODOS_DEVOLVIDOS){
                        predicates.add(criteriaBuilder.isNotNull(root.get("objetoFornecimentoContratoPai")));
                    }else{
                        if(tipoObjeto == EnumTipoObjeto.DEVOLVIDOS_SEM_VINCULO_COM_NOTA_REMESSA){
                            predicates.add(criteriaBuilder.isNotNull(root.get("objetoFornecimentoContratoPai")));
                            predicates.add(criteriaBuilder.isNull(root.get("notaRemessaOrdemFornecimentoContrato")));
                        }
                    }
                }
            }
            
            if(objetoFornecimentoContrato.getObjetoDevolvido() != null){
                predicates.add(criteriaBuilder.equal(root.get("objetoDevolvido"), objetoFornecimentoContrato.getObjetoDevolvido()));
            }
            
        }
        return predicates.toArray(new Predicate[] {});
    }
    
    private List<Predicate> extractPredicates(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto,CriteriaBuilder criteriaBuilder,Join<LocalEntregaEntidade,Entidade> joinEntidade,Join<Municipio,Uf> joinUf,Join<ObjetoFornecimentoContrato,Bem> joinBem,Root<ObjetoFornecimentoContrato> rootOfc){
        
        List<Predicate> predicates = new ArrayList<Predicate>();
        
        if(termoRecebimentoDefinitivoDto.getTermoRecebimentoDefinitivo() != null && termoRecebimentoDefinitivoDto.getTermoRecebimentoDefinitivo().getId() != null){
            Predicate predIdTermo = criteriaBuilder.equal(rootOfc.get("termoRecebimentoDefinitivo").get("id"),termoRecebimentoDefinitivoDto.getTermoRecebimentoDefinitivo().getId());
             predicates.add(predIdTermo);
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

    public List<Long> buscarIdsDasOrdensFornecimentoComunicadaPorPrograma(Programa programa) {

        String sql = "SELECT ofc.id from OrdemFornecimentoContrato as ofc " + " INNER JOIN ofc.contrato con" + " INNER JOIN con.programa prg" + " WHERE prg.id=:idPrograma and " + " ofc.statusComunicacaoOrdemFornecimento='COMU'";
        Query query = em.createQuery(sql);
        query.setParameter("idPrograma", programa.getId());
        List<?> objetos = query.getResultList();

        List<Long> idsOfs = new ArrayList<Long>();
        for (Object object : objetos) {

            idsOfs.add((Long) object);
        }
        return idsOfs;
    }
    
    public List<FormatacaoObjetoFornecimento> buscarFormatacoesObjetosPorPerfil(ObjetoFornecimentoContrato ofc, EnumResponsavelPreencherFormatacaoItem perfilResponsavelFormatacao){
        
        List<FormatacaoObjetoFornecimentoDto> listaRetorno = new ArrayList<FormatacaoObjetoFornecimentoDto>();
                                StringBuilder string = new StringBuilder();
                                string.append("select "
                        + "     a.fof_id_formatacao_objeto_fornecimento,"
                        + "     a.fof_fk_fic_id_formatacao_itens_contrato,"
                        + "     a.fof_fk_fir_id_formatacao_itens_contrato_resposta,"
                        + "     a.fof_fk_ofo_id_objeto_fornecimento_contrato,"
                        + "     a.fof_tp_responsavel_formatacao,"
                        + "     b.fic_bo_possui_informacao_opcional "
                        + " from "
                        + "     side.tb_fof_formatacao_objeto_fornecimento a,"
                        + "     side.tb_fic_formatacao_itens_contrato b "
                        + " where "
                        + "     a.fof_fk_ofo_id_objeto_fornecimento_contrato=:idPai  and ");
                        
                                //Se o perfil não for nulo adicionar esta condição abaixo
                        if(perfilResponsavelFormatacao != null){
                               string.append("b.fic_tp_responsavel_formatacao=:paramPerfil and ");
                        }
                                
                        string.append("b.fic_id_formatacao_itens_contrato=a.fof_fk_fic_id_formatacao_itens_contrato "
                        + " order by "
                        + " b.fic_id_formatacao_itens_contrato asc,"
                        + " a.fof_tp_responsavel_formatacao asc");
                
        Query query = em.createNativeQuery(string.toString());
        query.setParameter("idPai", ofc.getId());
        
        //Se o perfil não for nulo adicionar este parametro abaixo
        if(perfilResponsavelFormatacao != null){
            query.setParameter("paramPerfil", perfilResponsavelFormatacao.getValor());
        }
        
        List<?> listaFormatacaoObjetoFornecimento = query.getResultList();
        for (Object object : listaFormatacaoObjetoFornecimento) {
            Object[] o = (Object[]) object;

            FormatacaoObjetoFornecimentoDto objeto = new FormatacaoObjetoFornecimentoDto();
            
            Long id = new Long(((BigInteger) o[0]).toString());
            FormatacaoItensContratoDtot formatacao = buscaFormatacao(new Long(((BigInteger) o[1]).toString()));
            FormatacaoItensContratoRespostaDto formatacaoResposta = null;
            if (o[2] != null) {
                formatacaoResposta = buscaFormatacaoResposta(new Long(((BigInteger) o[2]).toString()), formatacao);
            }

            objeto.setId(id);
            objeto.setFormatacao(formatacao);
            objeto.setFormatacaoResposta(formatacaoResposta);
            objeto.setIdObjetoFornecimentoContrato(new Long(((BigInteger) o[3]).toString()));
            String valorPerfilEntidade = (String) o[4];
            EnumPerfilEntidade perfilEntidade = null;

            if (valorPerfilEntidade.equals(EnumPerfilEntidade.FORNECEDOR.getValor())) {
                perfilEntidade = EnumPerfilEntidade.FORNECEDOR;
            } else if (valorPerfilEntidade.equals(EnumPerfilEntidade.BENEFICIARIO.getValor())) {
                perfilEntidade = EnumPerfilEntidade.BENEFICIARIO;
            }
            objeto.setResponsavelFormatacao(perfilEntidade);

            boolean isValorOpcional = (boolean) o[5];
            objeto.setValorOpcional(isValorOpcional);
            listaRetorno.add(objeto);
        }
        return SideUtil.convertDtoToEntityFormatacaoObjetoFornecimentoDto(listaRetorno);
    }
    
    public List<FormatacaoObjetoFornecimento> buscarFormatacoesObjetosPorPerfilSemAsRespostas(ObjetoFornecimentoContrato ofc, EnumResponsavelPreencherFormatacaoItem perfilResponsavelFormatacao){
        
        List<FormatacaoObjetoFornecimentoDto> listaRetorno = new ArrayList<FormatacaoObjetoFornecimentoDto>();
                                StringBuilder string = new StringBuilder();
                                string.append("select "
                        + "     a.fof_id_formatacao_objeto_fornecimento,"
                        + "     a.fof_fk_fic_id_formatacao_itens_contrato,"
                        + "     a.fof_fk_fir_id_formatacao_itens_contrato_resposta,"
                        + "     a.fof_fk_ofo_id_objeto_fornecimento_contrato,"
                        + "     a.fof_tp_responsavel_formatacao,"
                        + "     b.fic_bo_possui_informacao_opcional "
                        + " from "
                        + "     side.tb_fof_formatacao_objeto_fornecimento a,"
                        + "     side.tb_fic_formatacao_itens_contrato b "
                        + " where "
                        + "     a.fof_fk_ofo_id_objeto_fornecimento_contrato=:idPai  and ");
                        
                                //Se o perfil não for nulo adicionar esta condição abaixo
                        if(perfilResponsavelFormatacao != null){
                               string.append("b.fic_tp_responsavel_formatacao=:paramPerfil and ");
                        }
                                
                        string.append("b.fic_id_formatacao_itens_contrato=a.fof_fk_fic_id_formatacao_itens_contrato "
                        + " order by "
                        + " b.fic_id_formatacao_itens_contrato asc,"
                        + " a.fof_tp_responsavel_formatacao asc");
                
        Query query = em.createNativeQuery(string.toString());
        query.setParameter("idPai", ofc.getId());
        
        //Se o perfil não for nulo adicionar este parametro abaixo
        if(perfilResponsavelFormatacao != null){
            query.setParameter("paramPerfil", perfilResponsavelFormatacao.getValor());
        }
        
        List<?> listaFormatacaoObjetoFornecimento = query.getResultList();
        for (Object object : listaFormatacaoObjetoFornecimento) {
            Object[] o = (Object[]) object;

            FormatacaoObjetoFornecimentoDto objeto = new FormatacaoObjetoFornecimentoDto();
            
            Long id = new Long(((BigInteger) o[0]).toString());
            FormatacaoItensContratoDtot formatacao = buscaFormatacao(new Long(((BigInteger) o[1]).toString()));
            FormatacaoItensContratoRespostaDto formatacaoResposta = null;

            objeto.setId(id);
            objeto.setFormatacao(formatacao);
            objeto.setFormatacaoResposta(formatacaoResposta);
            objeto.setIdObjetoFornecimentoContrato(new Long(((BigInteger) o[3]).toString()));
            String valorPerfilEntidade = (String) o[4];
            EnumPerfilEntidade perfilEntidade = null;

            if (valorPerfilEntidade.equals(EnumPerfilEntidade.FORNECEDOR.getValor())) {
                perfilEntidade = EnumPerfilEntidade.FORNECEDOR;
            } else if (valorPerfilEntidade.equals(EnumPerfilEntidade.BENEFICIARIO.getValor())) {
                perfilEntidade = EnumPerfilEntidade.BENEFICIARIO;
            }
            objeto.setResponsavelFormatacao(perfilEntidade);

            boolean isValorOpcional = (boolean) o[5];
            objeto.setValorOpcional(isValorOpcional);
            listaRetorno.add(objeto);
        }
        return SideUtil.convertDtoToEntityFormatacaoObjetoFornecimentoDto(listaRetorno);
    }


    public List<Long> buscarIdsDasOrdensFornecimentoComunicadaPorContrato(Contrato contrato) {

        String sql = "SELECT ofc.id from OrdemFornecimentoContrato as ofc " + " INNER JOIN ofc.contrato con" + " WHERE con.id=:idContrato " + " AND  ofc.statusComunicacaoOrdemFornecimento='COMU'";
        Query query = em.createQuery(sql);
        query.setParameter("idContrato", contrato.getId());
        List<?> objetos = query.getResultList();

        List<Long> idsOfs = new ArrayList<Long>();
        for (Object object : objetos) {

            idsOfs.add((Long) object);
        }
        return idsOfs;
    }

    public List<Long> buscarIdsNotasFiscaisSemEntregaEfetivaPorOrdemFornecimento(OrdemFornecimentoContrato ordemFornecimento) {
        String sql = "SELECT nrc.id from NotaRemessaOrdemFornecimentoContrato as nrc " + " INNER JOIN nrc.ordemFornecimento ofc" + " WHERE ofc.id=:idOrdemFornecimento" + " AND nrc.dataEfetivaEntrega is null";
        Query query = em.createQuery(sql);

        query.setParameter("idOrdemFornecimento", ordemFornecimento.getId());
        List<?> objetos = query.getResultList();

        List<Long> idsNotasRemessas = new ArrayList<Long>();
        for (Object object : objetos) {

            idsNotasRemessas.add((Long) object);
        }
        return idsNotasRemessas;
    }
    
    public ObjetoFornecimentoContrato buscarObjetoFornecimentoContratoPeloItemOrdemFornecimento(ItensOrdemFornecimentoContrato item){
        
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ObjetoFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ObjetoFornecimentoContrato.class);
        Root<ObjetoFornecimentoContrato> root = criteriaQuery.from(ObjetoFornecimentoContrato.class);

        List<Predicate> predicates = new ArrayList<>();
        
        predicates.add(criteriaBuilder.equal(root.get("item").get("id"), item.getItem().getId()));
        predicates.add(criteriaBuilder.equal(root.get("localEntrega").get("entidade").get("id"), item.getLocalEntrega().getEntidade().getId()));
        predicates.add(criteriaBuilder.equal(root.get("ordemFornecimento").get("id"), item.getOrdemFornecimento().getId()));
        
        criteriaQuery.select(criteriaQuery.getSelection()).where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));

        TypedQuery<ObjetoFornecimentoContrato> query = em.createQuery(criteriaQuery);
        
        List<ObjetoFornecimentoContrato> lista = new ArrayList<ObjetoFornecimentoContrato>();
        lista = query.getResultList();
        if(lista != null && !lista.isEmpty()){
            return lista.get(0);
        }else{
            return null;
        }
    }
    
    public List<ObjetoFornecimentoContrato> buscarTodosObjetosFornecimentoContratoPeloItemOrdemFornecimento(ItensOrdemFornecimentoContrato item){
        
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ObjetoFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ObjetoFornecimentoContrato.class);
        Root<ObjetoFornecimentoContrato> root = criteriaQuery.from(ObjetoFornecimentoContrato.class);

        List<Predicate> predicates = new ArrayList<>();
        
        predicates.add(criteriaBuilder.equal(root.get("item").get("id"), item.getItem().getId()));
        predicates.add(criteriaBuilder.equal(root.get("localEntrega").get("entidade").get("id"), item.getLocalEntrega().getEntidade().getId()));
        predicates.add(criteriaBuilder.equal(root.get("ordemFornecimento").get("id"), item.getOrdemFornecimento().getId()));
        
        criteriaQuery.select(criteriaQuery.getSelection()).where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));

        TypedQuery<ObjetoFornecimentoContrato> query = em.createQuery(criteriaQuery);
        
        List<ObjetoFornecimentoContrato> lista = new ArrayList<ObjetoFornecimentoContrato>();
        lista = query.getResultList();
        if(lista != null && !lista.isEmpty()){
            return lista;
        }else{
            return null;
        }
    }
    
    public ObjetoFornecimentoContrato buscarObjetoFornecimento(ObjetoFornecimentoContrato objetoFornecimentoContrato,EnumTipoObjeto tipoObjeto){
        
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ObjetoFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ObjetoFornecimentoContrato.class);
        Root<ObjetoFornecimentoContrato> root = criteriaQuery.from(ObjetoFornecimentoContrato.class);

        Predicate[] predicates = extractPredicatesObjetoFornecimentoContrato(objetoFornecimentoContrato, tipoObjeto, criteriaBuilder, root);
               
        criteriaQuery.select(criteriaQuery.getSelection()).where(criteriaBuilder.and(predicates));

        TypedQuery<ObjetoFornecimentoContrato> query = em.createQuery(criteriaQuery);
        
        List<ObjetoFornecimentoContrato> lista = new ArrayList<ObjetoFornecimentoContrato>();
        lista = query.getResultList();
        if(lista != null && !lista.isEmpty()){
            return lista.get(0);
        }else{
            return null;
        }
    }
    
    //Irá trazer os objetosFornecimento de um item.
    public List<ObjetoFornecimentoContrato> buscarListaObjetoFornecimentoContratoPeloItemOrdemFornecimento(ItensOrdemFornecimentoContrato item, EnumTipoObjeto tipoObjeto){
        
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ObjetoFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ObjetoFornecimentoContrato.class);
        Root<ObjetoFornecimentoContrato> root = criteriaQuery.from(ObjetoFornecimentoContrato.class);

        List<Predicate> predicates = new ArrayList<>();
        
        predicates.add(criteriaBuilder.equal(root.get("item").get("id"), item.getItem().getId()));
        predicates.add(criteriaBuilder.equal(root.get("localEntrega").get("id"), item.getLocalEntrega().getId()));
        predicates.add(criteriaBuilder.equal(root.get("ordemFornecimento").get("id"), item.getOrdemFornecimento().getId()));
        
        if(tipoObjeto != null){
            if(tipoObjeto == EnumTipoObjeto.ORIGINAIS){
                predicates.add(criteriaBuilder.isNull(root.get("objetoFornecimentoContratoPai")));
            }else{
                if(tipoObjeto == EnumTipoObjeto.TODOS_DEVOLVIDOS){
                    predicates.add(criteriaBuilder.isNotNull(root.get("objetoFornecimentoContratoPai")));
                }else{
                    if(tipoObjeto == EnumTipoObjeto.DEVOLVIDOS_SEM_VINCULO_COM_NOTA_REMESSA){
                        predicates.add(criteriaBuilder.isNotNull(root.get("objetoFornecimentoContratoPai")));
                        predicates.add(criteriaBuilder.isNull(root.get("notaRemessaOrdemFornecimentoContrato")));
                    }
                }
            }
        }
        
        criteriaQuery.select(criteriaQuery.getSelection()).where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));

        TypedQuery<ObjetoFornecimentoContrato> query = em.createQuery(criteriaQuery);
        
        List<ObjetoFornecimentoContrato> lista = new ArrayList<ObjetoFornecimentoContrato>();
        lista = query.getResultList();
        if(lista != null && !lista.isEmpty()){
            return lista;
        }else{
            return null;
        }
    }

    public List<ItensOrdemFornecimentoContrato> buscarPaginado(Contrato contrato, int first, int size, EnumOrder order, String propertyOrder) {

        List<ItensOrdemFornecimentoContrato> lista1 = buscarSemPaginacao(contrato, order, propertyOrder);

        // filtra paginado
        List<ItensOrdemFornecimentoContrato> listaRetorno = new ArrayList<ItensOrdemFornecimentoContrato>();
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

    public List<ItensOrdemFornecimentoContrato> buscarSemPaginacao(Contrato contrato) {
        return buscarSemPaginacao(contrato, EnumOrder.ASC, "id");
    }

    public ItensOrdemFornecimentoContrato buscarPeloId(Long id) {
        return em.find(ItensOrdemFornecimentoContrato.class, id);
    }

    public List<ItensOrdemFornecimentoContrato> buscarSemPaginacaoOrdenado(Contrato contrato, EnumOrder order, String propertyOrder) {
        return buscarSemPaginacao(contrato, order, propertyOrder);
    }
    
    public List<?> buscarRespostasSeBemEntregueEstaDeAcordo(Long idOfo) {
        

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
        Root<ObjetoFornecimentoContrato> root = criteriaQuery.from(ObjetoFornecimentoContrato.class);

        List<Predicate> predicates = new ArrayList<>();
        Predicate criterio1 = criteriaBuilder.equal(root.get("id"), idOfo);        
        predicates.add(criteriaBuilder.or(criterio1));
        
        criteriaQuery.multiselect(root.get("estadoDeNovo"),root.get("funcionandoDeAcordo"),root.get("configuradoDeAcordo")).where(predicates.toArray(new Predicate[] {}));

        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        List<?> listaContratos = query.getResultList();
        return listaContratos;
    }


    public OrdemFornecimentoContrato incluir(OrdemFornecimentoContrato ordemFornecimentoContrato, String usuarioLogado) {

        /* atender referencia bidirecional */
        ordemFornecimentoContrato.setContrato(em.find(Contrato.class, ordemFornecimentoContrato.getContrato().getId()));

        ordemFornecimentoContrato.setDataCadastro(LocalDateTime.now());
        ordemFornecimentoContrato.setUsuarioCadastro(usuarioLogado);

        List<ItensOrdemFornecimentoContrato> listaItensContrato = new ArrayList<ItensOrdemFornecimentoContrato>();
        for (ItensOrdemFornecimentoContrato itensFormatacaoNovo : ordemFornecimentoContrato.getListaItensOrdemFornecimento()) {
            itensFormatacaoNovo.setOrdemFornecimento(ordemFornecimentoContrato);
            listaItensContrato.add(itensFormatacaoNovo);
        }
        ordemFornecimentoContrato.getListaItensOrdemFornecimento().clear();
        ordemFornecimentoContrato.getListaItensOrdemFornecimento().addAll(listaItensContrato);

        em.persist(ordemFornecimentoContrato);
        return ordemFornecimentoContrato;
    }

    public OrdemFornecimentoContrato alterar(OrdemFornecimentoContrato ordemFornecimentoContrato, String usuarioLogado) {
        OrdemFornecimentoContrato ordemFornecimentoContratoParaMerge = em.find(OrdemFornecimentoContrato.class, ordemFornecimentoContrato.getId());

        ordemFornecimentoContratoParaMerge.setDataAlteracao(LocalDateTime.now());
        ordemFornecimentoContratoParaMerge.setUsuarioAlteracao(usuarioLogado);
        sincronizarItensOrdem(ordemFornecimentoContrato.getListaItensOrdemFornecimento(), ordemFornecimentoContratoParaMerge);
        em.merge(ordemFornecimentoContratoParaMerge);
        return ordemFornecimentoContratoParaMerge;
    }

    private void sincronizarItensOrdem(List<ItensOrdemFornecimentoContrato> itensContratoDaOrdem, OrdemFornecimentoContrato ordemAtual) {

        List<ItensOrdemFornecimentoContrato> listaItensOrdemAtualParaAtualizar = new ArrayList<ItensOrdemFornecimentoContrato>();
        List<ItensOrdemFornecimentoContrato> listaItensOrdemAdicionar = new ArrayList<ItensOrdemFornecimentoContrato>();

        /*
         * seleciona apenas os Itens que foram mantidas na lista vinda do service
         */

        for (ItensOrdemFornecimentoContrato itensOrdemAtual : ordemAtual.getListaItensOrdemFornecimento()) {
            if (itensContratoDaOrdem.contains(itensOrdemAtual)) {
                listaItensOrdemAtualParaAtualizar.add(itensOrdemAtual);
            }

        }

        /* remove a lista atual de itens */
        ordemAtual.getListaItensOrdemFornecimento().clear();

        /* adiciona os novos na lista de Itens */
        for (ItensOrdemFornecimentoContrato itensOrdemNovo : itensContratoDaOrdem) {
            if (itensOrdemNovo.getId() == null) {
                /* atender referencia bidirecional */
                itensOrdemNovo.setOrdemFornecimento(ordemAtual);
                ordemAtual.getListaItensOrdemFornecimento().add(itensOrdemNovo);
            }
        }

        /*
         * atualiza atributos nos Itens vindos do service para persistir
         */
        for (ItensOrdemFornecimentoContrato itensOrdemParaAtualizar : listaItensOrdemAtualParaAtualizar) {
            for (ItensOrdemFornecimentoContrato itensOrdem : itensContratoDaOrdem) {
                if (itensOrdem.getId() != null && itensOrdem.getId().equals(itensOrdemParaAtualizar.getId())) {

                    itensOrdemParaAtualizar.setQuantidade(itensOrdem.getQuantidade());

                    listaItensOrdemAdicionar.add(itensOrdemParaAtualizar);
                }
            }
        }

        /* adiciona os Itens atualizados */
        ordemAtual.getListaItensOrdemFornecimento().addAll(listaItensOrdemAdicionar);
    }

    public void excluir(OrdemFornecimentoContrato ordemFornecimentoContrato) {
        em.remove(ordemFornecimentoContrato);

    }

    public HistoricoComunicacaoGeracaoOrdemFornecimentoContrato gerarMinuta(OrdemFornecimentoContrato ordemFornecimentoContrato, byte[] minuta) {

        HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoNovo = new HistoricoComunicacaoGeracaoOrdemFornecimentoContrato();

        historicoNovo.setOrdemFornecimento(em.find(OrdemFornecimentoContrato.class, ordemFornecimentoContrato.getId()));
        historicoNovo.setDataGeracao(LocalDateTime.now());
        historicoNovo.setMinutaGerada(minuta);

        historicoNovo.setDataComunicacao(null);
        historicoNovo.setPossuiCancelamento(Boolean.FALSE);
        historicoNovo.setPossuiComunicado(Boolean.FALSE);

        em.persist(historicoNovo);
        return historicoNovo;
    }

    public HistoricoComunicacaoGeracaoOrdemFornecimentoContrato gerarNovoHistorico(OrdemFornecimentoContrato ordemFornecimentoContrato, HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historico) {

        HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoNovo = new HistoricoComunicacaoGeracaoOrdemFornecimentoContrato();

        historicoNovo.setOrdemFornecimento(em.find(OrdemFornecimentoContrato.class, ordemFornecimentoContrato.getId()));
        historicoNovo.setMinutaGerada(historico.getMinutaGerada());

        historicoNovo.setDataGeracao(historico.getDataGeracao());
        historicoNovo.setNumeroDocumentoSei(historico.getNumeroDocumentoSei());
        historicoNovo.setDataComunicacao(null);
        historicoNovo.setPossuiCancelamento(Boolean.FALSE);
        historicoNovo.setPossuiComunicado(Boolean.FALSE);

        em.persist(historicoNovo);
        return historicoNovo;
    }

    public HistoricoComunicacaoGeracaoOrdemFornecimentoContrato comunicarMinuta(Long idHistorico, String numeroDocumentoSei) {

        HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoParaMerge = em.find(HistoricoComunicacaoGeracaoOrdemFornecimentoContrato.class, idHistorico);

        historicoParaMerge.setDataComunicacao(LocalDateTime.now());
        historicoParaMerge.setPossuiComunicado(Boolean.TRUE);
        historicoParaMerge.setPossuiCancelamento(Boolean.FALSE);

        if (historicoParaMerge.getNumeroDocumentoSei() == null) {
            historicoParaMerge.setNumeroDocumentoSei(numeroDocumentoSei);
        }

        em.merge(historicoParaMerge);
        return historicoParaMerge;
    }

    public HistoricoComunicacaoGeracaoOrdemFornecimentoContrato cancelarComunicacao(Long idHistorico, String motivoCancelamento) {

        HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoParaMerge = em.find(HistoricoComunicacaoGeracaoOrdemFornecimentoContrato.class, idHistorico);
        historicoParaMerge.setDataCancelamento(LocalDateTime.now());
        historicoParaMerge.setPossuiCancelamento(Boolean.TRUE);
        historicoParaMerge.setMotivoCancelamento(motivoCancelamento);
        em.merge(historicoParaMerge);
        return historicoParaMerge;
    }

    private Predicate[] extractPredicatesDuplicado(HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historico, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (historico != null && historico.getOrdemFornecimento() != null && historico.getOrdemFornecimento().getId() != null) {
            predicates.add(criteriaBuilder.notEqual(root.get("ordemFornecimento").get("id"), historico.getOrdemFornecimento().getId()));
        }

        if (historico != null && historico.getNumeroDocumentoSei() != null) {
            predicates.add(criteriaBuilder.equal(root.get("numeroDocumentoSei"), historico.getNumeroDocumentoSei()));
        }
        return predicates.toArray(new Predicate[] {});

    }

    private Predicate[] extractPredicatesExclusao(Long idOrdemFornecimento, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (idOrdemFornecimento != null) {
            predicates.add(criteriaBuilder.equal(root.get("ordemFornecimento").get("id"), idOrdemFornecimento));
        }
        return predicates.toArray(new Predicate[] {});
    }

    public List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> buscarDuplicado(HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historico) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(HistoricoComunicacaoGeracaoOrdemFornecimentoContrato.class);
        Root<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> root = criteriaQuery.from(HistoricoComunicacaoGeracaoOrdemFornecimentoContrato.class);
        Predicate[] predicates = extractPredicatesDuplicado(historico, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);
        TypedQuery<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public void removeObjetoFornecimentoDaOrdemFornecimento(Long idOrdemFornecimento) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ObjetoFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ObjetoFornecimentoContrato.class);
        Root<ObjetoFornecimentoContrato> root = criteriaQuery.from(ObjetoFornecimentoContrato.class);
        Predicate[] predicates = extractPredicatesExclusao(idOrdemFornecimento, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);
        TypedQuery<ObjetoFornecimentoContrato> query = em.createQuery(criteriaQuery);
        List<ObjetoFornecimentoContrato> lista = query.getResultList();

        for (ObjetoFornecimentoContrato item : lista) {
            for (FormatacaoObjetoFornecimento fof : item.getListaFormatacaoObjetoFornecimento()) {
                em.remove(fof);
            }
            em.remove(item);
        }
        em.flush();
    }

    public void inserirObjetoFornecimentoDaOrdemFornecimento(Long idOrdemFornecimento) {
        List<ListaLoteUnitariaDto> listaFinal = buscaObjetoFornecimentoDaOrdemFornecimento(idOrdemFornecimento);
        for (ListaLoteUnitariaDto dto : listaFinal) {
            insereLote(dto.getItemOf(), dto.getListaLote(),null);
            insereUnitaria(dto.getItemOf(), dto.getListaUnitaria(),null);
        }
    }
    
    //Metodo chamado quando um objeto for devolvido para correção
    public void inserirObjetoFornecimentoEnviadoParaCorrecao(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        List<ListaLoteUnitariaDto> listaFinal = buscaObjetoFornecimentoQueFoiDevolvidoParaCorrecao(objetoFornecimentoContrato);
        
        for (ListaLoteUnitariaDto dto : listaFinal) {
            dto.getItemOf().setQuantidade(1);
            
            //Somente será criado um lote se o objeto não tiver formatações unitárias, exemplo: 3 itens entregues = 3 QRCodes, se este objeto tiver Lote então
            // será outro QRCode para ele, no total serão 4 QRCodes... neste caso se devolver 1 item não será criado novamente o QRCdode do Lote, mas se houver somente
            // formatação em lote poderá ser criado normalmente.
            if(dto.getListaUnitaria() !=null && dto.getListaUnitaria().size() == 0 ){
                insereLote(dto.getItemOf(), dto.getListaLote(),objetoFornecimentoContrato);
            }
            insereUnitaria(dto.getItemOf(), dto.getListaUnitaria(),objetoFornecimentoContrato);
        }
        //Irá alterar o status deste objeto para objetoDevolvido=true
        alterarStatusDoObjetoParaDevolvido(objetoFornecimentoContrato.getId(),true);

        objetoFornecimentoContrato.getOrdemFornecimento().setStatusOrdemFornecimento(EnumStatusOrdemFornecimento.DEVOLVIDA);
        alterarStatusDaOrdemFornecimento(objetoFornecimentoContrato.getOrdemFornecimento());
        //throw new IllegalArgumentException("Parâmetro id não pode ser null"); 
    }
    
    private void alterarStatusDoObjetoParaDevolvido(Long id, boolean objetoDevolvido){
        
        //Pega a data atual
        LocalDateTime ldt = LocalDateTime.now();
        Timestamp t = Timestamp.valueOf(ldt);
        LocalDateTime ldt2 = t.toLocalDateTime();
        
        String stringQuery = "UPDATE "+
                " side.tb_ofo_objeto_fornecimento_contrato "+
                " SET "+
                " ofo_bo_objeto_devolvido=:status, "+
                " ofo_dt_data_devolucao_item =:dataDevolucao "+
                " WHERE "+
                " ofo_id_objeto_fornecimento_contrato=:idObjeto ";
        Query query = em.createNativeQuery(stringQuery);
        query.setParameter("idObjeto", id);
        query.setParameter("status", objetoDevolvido);
        query.setParameter("dataDevolucao", t);
        query.executeUpdate();
    }

    public List<ListaLoteUnitariaDto> buscaObjetoFornecimentoDaOrdemFornecimento(Long idOrdemFornecimento) {
        OrdemFornecimentoContrato ordemFornecimento = em.find(OrdemFornecimentoContrato.class, idOrdemFornecimento);
        Map<Long, ItemFormatacaoDto> mapa = retornaListaItemFormatacaoDto(ordemFornecimento);
        List<ListaLoteUnitariaDto> listaFinal = new ArrayList<ListaLoteUnitariaDto>();
        for (ItensOrdemFornecimentoContrato item : ordemFornecimento.getListaItensOrdemFornecimento()) {
            separaObjetoFornecimentoLoteUnitaria(item, mapa.get(item.getItem().getId()), listaFinal);
        }
        return listaFinal;
    }
    
    //Este metodo irá montar o objetoFornecimentoContrato novo que foi devolvido para correção
    public List<ListaLoteUnitariaDto> buscaObjetoFornecimentoQueFoiDevolvidoParaCorrecao(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        OrdemFornecimentoContrato ordemFornecimento = em.find(OrdemFornecimentoContrato.class, objetoFornecimentoContrato.getOrdemFornecimento().getId());
        
        List<ItensOrdemFornecimentoContrato> listaDeFormatacaoItemDevolvido = new ArrayList<ItensOrdemFornecimentoContrato>();
        List<ItensOrdemFornecimentoContrato> listaDeFormatacaoCompleto = new ArrayList<ItensOrdemFornecimentoContrato>();
        for(ItensOrdemFornecimentoContrato itensDaOrdemDeFornecimento:ordemFornecimento.getListaItensOrdemFornecimento()){
            if(itensDaOrdemDeFornecimento.getItem().getId().longValue() == objetoFornecimentoContrato.getItem().getId().longValue() &&
                    itensDaOrdemDeFornecimento.getLocalEntrega().getId().longValue() == objetoFornecimentoContrato.getLocalEntrega().getId().longValue()){
                listaDeFormatacaoItemDevolvido.add(itensDaOrdemDeFornecimento);
            }
            listaDeFormatacaoCompleto.add(itensDaOrdemDeFornecimento);
        }
        
        /*List<ObjetoFornecimentoContrato> listaObjetoFornecimentoContratoItemDevolvido = new ArrayList<ObjetoFornecimentoContrato>();
        for(ObjetoFornecimentoContrato ofo:ordemFornecimento.getListaObjetoFornecimentoContrato()){
            if(ofo.getId().longValue() == objetoFornecimentoContrato.getId().longValue()){
                listaObjetoFornecimentoContratoItemDevolvido.add(ofo);
            }
        }*/
        
        /*ordemFornecimento.getListaItensOrdemFornecimento().clear();
        ordemFornecimento.getListaObjetoFornecimentoContrato().clear();
        ordemFornecimento.setListaItensOrdemFornecimento(listaDeFormatacaoItemDevolvido);*/
        //ordemFornecimento.setListaObjetoFornecimentoContrato(listaObjetoFornecimentoContratoItemDevolvido);
        
        Map<Long, ItemFormatacaoDto> mapa = retornaListaItemFormatacaoDto(ordemFornecimento);
        List<ListaLoteUnitariaDto> listaFinal = new ArrayList<ListaLoteUnitariaDto>();
        for (ItensOrdemFornecimentoContrato item : ordemFornecimento.getListaItensOrdemFornecimento()) {
            if(item.getItem().getId().longValue() == objetoFornecimentoContrato.getItem().getId().longValue() &&
                    item.getLocalEntrega().getId().longValue() == objetoFornecimentoContrato.getLocalEntrega().getId().longValue()){
                separaObjetoFornecimentoLoteUnitaria(item, mapa.get(item.getItem().getId()), listaFinal);
            }
        }
        return listaFinal;
    }

    private Map<Long, ItemFormatacaoDto> retornaListaItemFormatacaoDto(OrdemFornecimentoContrato ordemFornecimento) {

        List<ItemFormatacaoDto> listaItemFormatacaoDtoFinal = new ArrayList<ItemFormatacaoDto>();
        for (FormatacaoContrato formatacaoContrato : ordemFornecimento.getContrato().getListaFormatacao()) {

            // seta os itens
            List<ItemFormatacaoDto> listaItemFormatacaoDto = new ArrayList<ItemFormatacaoDto>();
            for (ItensFormatacao itensFormatacao : formatacaoContrato.getItens()) {
                ItemFormatacaoDto itemFormatacaoDto = new ItemFormatacaoDto();
                itemFormatacaoDto.setItem(itensFormatacao.getItem());
                listaItemFormatacaoDto.add(itemFormatacaoDto);
            }

            // pega as formatacoes
            List<FormatacaoItensContrato> listaFormatacao = new ArrayList<FormatacaoItensContrato>();
            for (FormatacaoItensContrato formatacaoItensContrato : formatacaoContrato.getListaItensFormatacao()) {
                listaFormatacao.add(formatacaoItensContrato);
            }

            // seta para cada item as n formatacoes
            for (ItemFormatacaoDto itemFormatacaoDto : listaItemFormatacaoDto) {
                itemFormatacaoDto.getListaFormatacao().addAll(new ArrayList<FormatacaoItensContrato>(listaFormatacao));
                listaItemFormatacaoDtoFinal.add(itemFormatacaoDto);
            }
        }

        Map<Long, ItemFormatacaoDto> mapa = new HashMap<Long, ItemFormatacaoDto>();

        for (ItemFormatacaoDto itemFormatacaoDto : listaItemFormatacaoDtoFinal) {
            mapa.put(itemFormatacaoDto.getItem().getId(), itemFormatacaoDto);
        }
        return mapa;
    }

    private void separaObjetoFornecimentoLoteUnitaria(ItensOrdemFornecimentoContrato itemOf, ItemFormatacaoDto itemFormatacaoDto, List<ListaLoteUnitariaDto> listaFinal) {

        ListaLoteUnitariaDto dto = new ListaLoteUnitariaDto();
        List<FormatacaoItensContrato> listaLote = new ArrayList<FormatacaoItensContrato>();
        List<FormatacaoItensContrato> listaUnitaria = new ArrayList<FormatacaoItensContrato>();

        dto.setItemOf(itemOf);

        for (FormatacaoItensContrato formatacao : itemFormatacaoDto.getListaFormatacao()) {
            if (formatacao.getFormaVerificacao().equals(EnumFormaVerificacaoFormatacao.LOTE)) {
                listaLote.add(formatacao);
            }
            if (formatacao.getFormaVerificacao().equals(EnumFormaVerificacaoFormatacao.UNITARIA)) {
                listaUnitaria.add(formatacao);
            }

        }
        dto.setListaLote(listaLote);
        dto.setListaUnitaria(listaUnitaria);
        listaFinal.add(dto);
    }

    private void insereLote(ItensOrdemFornecimentoContrato itemOf, List<FormatacaoItensContrato> itensFormatacao, ObjetoFornecimentoContrato objetoFornecimentoContratoPai) {
        if (!itensFormatacao.isEmpty()) {
            ObjetoFornecimentoContrato ofc = new ObjetoFornecimentoContrato();

            ofc.setItem(itemOf.getItem());
            ofc.setFormaVerificacao(EnumFormaVerificacaoFormatacao.LOTE);
            ofc.setLocalEntrega(itemOf.getLocalEntrega());
            ofc.setOrdemFornecimento(itemOf.getOrdemFornecimento());
            ofc.setQuantidade(itemOf.getQuantidade());
            ofc.setSituacaoPreenchimentoFornecedor(EnumSituacaoPreenchimentoItemFormatacaoFornecedor.NAO_PREENCHIDO);
            ofc.setSituacaoPreenchimentoBeneficiario(EnumSituacaoPreenchimentoItemFormatacaoBeneficiario.NAO_PREENCHIDO);
            
            //Se o objeto pai não for nulo então não é a inclusão de um objeto novo e sim a devolução de um objeto para correção
            // este objeto pai é o objeto original que esta sendo devolvido e será setado como parametro para este novo cadastrado.
            if(objetoFornecimentoContratoPai != null){
                if(objetoFornecimentoContratoPai.getObjetoFornecimentoContratoPai() == null){
                    ofc.setObjetoFornecimentoContratoPai(objetoFornecimentoContratoPai.getId());
                }else{
                    ofc.setObjetoFornecimentoContratoPai(objetoFornecimentoContratoPai.getObjetoFornecimentoContratoPai());
                }
            }

            List<FormatacaoObjetoFornecimento> listaFormatacao = new ArrayList<FormatacaoObjetoFornecimento>();
            for (FormatacaoItensContrato formatacao : itensFormatacao) {

                if (formatacao.getResponsavelFormatacao().equals(EnumResponsavelPreencherFormatacaoItem.FORNECEDOR)) {
                    FormatacaoObjetoFornecimento fof = new FormatacaoObjetoFornecimento();
                    fof.setObjetoFornecimento(ofc);
                    fof.setFormatacao(formatacao);
                    fof.setResponsavelFormatacao(EnumPerfilEntidade.FORNECEDOR);
                    listaFormatacao.add(fof);
                } else if (formatacao.getResponsavelFormatacao().equals(EnumResponsavelPreencherFormatacaoItem.BENEFICIARIO)) {
                    FormatacaoObjetoFornecimento fof = new FormatacaoObjetoFornecimento();
                    fof.setObjetoFornecimento(ofc);
                    fof.setFormatacao(formatacao);
                    fof.setResponsavelFormatacao(EnumPerfilEntidade.BENEFICIARIO);
                    listaFormatacao.add(fof);
                } else if (formatacao.getResponsavelFormatacao().equals(EnumResponsavelPreencherFormatacaoItem.AMBOS)) {
                    // para fornecedor
                    FormatacaoObjetoFornecimento fofFornecedor = new FormatacaoObjetoFornecimento();
                    fofFornecedor.setObjetoFornecimento(ofc);
                    fofFornecedor.setFormatacao(formatacao);
                    fofFornecedor.setResponsavelFormatacao(EnumPerfilEntidade.FORNECEDOR);
                    listaFormatacao.add(fofFornecedor);
                    // para benefici�rio
                    FormatacaoObjetoFornecimento fofBeneficiario = new FormatacaoObjetoFornecimento();
                    fofBeneficiario.setObjetoFornecimento(ofc);
                    fofBeneficiario.setFormatacao(formatacao);
                    fofBeneficiario.setResponsavelFormatacao(EnumPerfilEntidade.BENEFICIARIO);
                    listaFormatacao.add(fofBeneficiario);
                }

            }
            ofc.setListaFormatacaoObjetoFornecimento(listaFormatacao);
            ofc.setQuantidadeQuesitosFornecedor(retornaQuantidadeItensPorPerfil(listaFormatacao, EnumPerfilEntidade.FORNECEDOR));
            ofc.setQuantidadeQuesitosBeneficiario(retornaQuantidadeItensPorPerfil(listaFormatacao, EnumPerfilEntidade.BENEFICIARIO));
            ofc.setQuantidadeQuesitosObrigatoriosPreenchidosFornecedor(0);
            ofc.setQuantidadeQuesitosObrigatoriosPreenchidosBeneficiario(0);
            ofc.setQuantidadeQuesitosOpcionaisPreenchidosFornecedor(0);
            ofc.setQuantidadeQuesitosOpcionaisPreenchidosBeneficiario(0);
            ofc.setSituacaoPreenchimentoFornecedor(EnumSituacaoPreenchimentoItemFormatacaoFornecedor.NAO_PREENCHIDO);
            ofc.setSituacaoPreenchimentoBeneficiario(EnumSituacaoPreenchimentoItemFormatacaoBeneficiario.NAO_PREENCHIDO);
            ofc.setSituacaoAvaliacaoPreliminarPreenchimentoItem(EnumSituacaoAvaliacaoPreliminarPreenchimentoItem.SEM_AVALIACAO);
            ofc.setAnaliseFinalItem(EnumAnaliseFinalItem.NAO_ANALISADO);
            ofc.setSituacaoGeracaoTermos(EnumSituacaoGeracaoTermos.NAO_GERADO);
            ofc.setSituacaoBem(EnumSituacaoBem.NAO_ENTREGUE);
            ofc.setObjetoDevolvido(false);

            // retorna somente a quantidade de itens opcionais do beneficiario
            ofc.setQuantidadeQuesitosOpcionaisBeneficiario(retornaQuantidadeItensObrigatoriosPorPerfil(listaFormatacao, EnumPerfilEntidade.BENEFICIARIO, true));

            // retorna somente a quantidade de itens obrigatorios do
            // beneficiario
            ofc.setQuantidadeQuesitosObrigatoriosBeneficiario(retornaQuantidadeItensObrigatoriosPorPerfil(listaFormatacao, EnumPerfilEntidade.BENEFICIARIO, false));

            // retorna somente a quantidade de itens opcionais do fornecedor
            ofc.setQuantidadeQuesitosOpcionaisFornecedor(retornaQuantidadeItensObrigatoriosPorPerfil(listaFormatacao, EnumPerfilEntidade.FORNECEDOR, true));

            // retorna somente a quantidade de itens obrigatorios do fornecedor
            ofc.setQuantidadeQuesitosObrigatoriosFornecedor(retornaQuantidadeItensObrigatoriosPorPerfil(listaFormatacao, EnumPerfilEntidade.FORNECEDOR, false));

            em.persist(ofc);
            em.flush();
        }
    }

    private void insereUnitaria(ItensOrdemFornecimentoContrato itemOf, List<FormatacaoItensContrato> itensFormatacao, ObjetoFornecimentoContrato objetoFornecimentoContratoPai) {

        if (!itensFormatacao.isEmpty()) {

            int quantidade = itemOf.getQuantidade();
            for (int i = 0; i < quantidade; i++) {

                ObjetoFornecimentoContrato ofc = new ObjetoFornecimentoContrato();

                ofc.setItem(itemOf.getItem());
                ofc.setFormaVerificacao(EnumFormaVerificacaoFormatacao.UNITARIA);
                ofc.setLocalEntrega(itemOf.getLocalEntrega());
                ofc.setOrdemFornecimento(itemOf.getOrdemFornecimento());
                ofc.setSituacaoPreenchimentoFornecedor(EnumSituacaoPreenchimentoItemFormatacaoFornecedor.NAO_PREENCHIDO);
                ofc.setSituacaoPreenchimentoBeneficiario(EnumSituacaoPreenchimentoItemFormatacaoBeneficiario.NAO_PREENCHIDO);
                
              //Se o objeto pai não for nulo então não é a inclusão de um objeto novo e sim a devolução de um objeto para correção
                // este objeto pai é o objeto original que esta sendo devolvido e será setado como parametro para este novo cadastrado.
                if(objetoFornecimentoContratoPai != null){
                    if(objetoFornecimentoContratoPai.getObjetoFornecimentoContratoPai() == null){
                        ofc.setObjetoFornecimentoContratoPai(objetoFornecimentoContratoPai.getId());
                    }else{
                        ofc.setObjetoFornecimentoContratoPai(objetoFornecimentoContratoPai.getObjetoFornecimentoContratoPai());
                    }
                }

                List<FormatacaoObjetoFornecimento> listaFormatacao = new ArrayList<FormatacaoObjetoFornecimento>();
                for (FormatacaoItensContrato formatacao : itensFormatacao) {

                    if (formatacao.getResponsavelFormatacao().equals(EnumResponsavelPreencherFormatacaoItem.FORNECEDOR)) {
                        FormatacaoObjetoFornecimento fof = new FormatacaoObjetoFornecimento();
                        fof.setObjetoFornecimento(ofc);
                        fof.setFormatacao(formatacao);
                        fof.setResponsavelFormatacao(EnumPerfilEntidade.FORNECEDOR);
                        listaFormatacao.add(fof);
                    } else if (formatacao.getResponsavelFormatacao().equals(EnumResponsavelPreencherFormatacaoItem.BENEFICIARIO)) {
                        FormatacaoObjetoFornecimento fof = new FormatacaoObjetoFornecimento();
                        fof.setObjetoFornecimento(ofc);
                        fof.setFormatacao(formatacao);
                        fof.setResponsavelFormatacao(EnumPerfilEntidade.BENEFICIARIO);
                        listaFormatacao.add(fof);
                    } else if (formatacao.getResponsavelFormatacao().equals(EnumResponsavelPreencherFormatacaoItem.AMBOS)) {
                        // para fornecedor
                        FormatacaoObjetoFornecimento fofFornecedor = new FormatacaoObjetoFornecimento();
                        fofFornecedor.setObjetoFornecimento(ofc);
                        fofFornecedor.setFormatacao(formatacao);
                        fofFornecedor.setResponsavelFormatacao(EnumPerfilEntidade.FORNECEDOR);
                        listaFormatacao.add(fofFornecedor);
                        // para benefici�rio
                        FormatacaoObjetoFornecimento fofBeneficiario = new FormatacaoObjetoFornecimento();
                        fofBeneficiario.setObjetoFornecimento(ofc);
                        fofBeneficiario.setFormatacao(formatacao);
                        fofBeneficiario.setResponsavelFormatacao(EnumPerfilEntidade.BENEFICIARIO);
                        listaFormatacao.add(fofBeneficiario);
                    }

                }

                ofc.setListaFormatacaoObjetoFornecimento(listaFormatacao);
                ofc.setQuantidadeQuesitosFornecedor(retornaQuantidadeItensPorPerfil(listaFormatacao, EnumPerfilEntidade.FORNECEDOR));
                ofc.setQuantidadeQuesitosBeneficiario(retornaQuantidadeItensPorPerfil(listaFormatacao, EnumPerfilEntidade.BENEFICIARIO));
                ofc.setQuantidadeQuesitosObrigatoriosPreenchidosFornecedor(0);
                ofc.setQuantidadeQuesitosObrigatoriosPreenchidosBeneficiario(0);
                ofc.setQuantidadeQuesitosOpcionaisPreenchidosFornecedor(0);
                ofc.setQuantidadeQuesitosOpcionaisPreenchidosBeneficiario(0);
                
                ofc.setSituacaoPreenchimentoFornecedor(EnumSituacaoPreenchimentoItemFormatacaoFornecedor.NAO_PREENCHIDO);
                ofc.setSituacaoPreenchimentoBeneficiario(EnumSituacaoPreenchimentoItemFormatacaoBeneficiario.NAO_PREENCHIDO);
                ofc.setSituacaoAvaliacaoPreliminarPreenchimentoItem(EnumSituacaoAvaliacaoPreliminarPreenchimentoItem.SEM_AVALIACAO);
                ofc.setAnaliseFinalItem(EnumAnaliseFinalItem.NAO_ANALISADO);
                ofc.setSituacaoBem(EnumSituacaoBem.NAO_ENTREGUE);
                ofc.setSituacaoGeracaoTermos(EnumSituacaoGeracaoTermos.NAO_GERADO);
                
                ofc.setObjetoDevolvido(false);

                // retorna somente a quantidade de itens opcionais do
                // beneficiario
                ofc.setQuantidadeQuesitosOpcionaisBeneficiario(retornaQuantidadeItensObrigatoriosPorPerfil(listaFormatacao, EnumPerfilEntidade.BENEFICIARIO, true));

                // retorna somente a quantidade de itens obrigatorios do
                // beneficiario
                ofc.setQuantidadeQuesitosObrigatoriosBeneficiario(retornaQuantidadeItensObrigatoriosPorPerfil(listaFormatacao, EnumPerfilEntidade.BENEFICIARIO, false));

                // retorna somente a quantidade de itens opcionais do fornecedor
                ofc.setQuantidadeQuesitosOpcionaisFornecedor(retornaQuantidadeItensObrigatoriosPorPerfil(listaFormatacao, EnumPerfilEntidade.FORNECEDOR, true));

                // retorna somente a quantidade de itens obrigatorios do
                // fornecedor
                ofc.setQuantidadeQuesitosObrigatoriosFornecedor(retornaQuantidadeItensObrigatoriosPorPerfil(listaFormatacao, EnumPerfilEntidade.FORNECEDOR, false));

                em.persist(ofc);
                em.flush();

            }

        }

    }

    // Irá retornar a quantidade de itens opcionais e obrigatórios da lista de
    // FormataçãoObjetoFornecimento
    // opcional = true irá trazer somente os campos opcionais, false irá trazer
    // os obrigatórios
    private int retornaQuantidadeItensObrigatoriosPorPerfil(List<FormatacaoObjetoFornecimento> itensFormatacao, EnumPerfilEntidade perfil, boolean opcional) {

        int quantidadeItensPerfil = 0;
        for (FormatacaoObjetoFornecimento formatacaoObjetoFornecimento : itensFormatacao) {
            if (formatacaoObjetoFornecimento.getResponsavelFormatacao().getValor().equals(perfil.getValor())) {
                if (opcional == formatacaoObjetoFornecimento.getFormatacao().getPossuiInformacaoOpcional()) {
                    quantidadeItensPerfil++;
                }
            }
        }

        return quantidadeItensPerfil;
    }

    private int retornaQuantidadeItensPorPerfil(List<FormatacaoObjetoFornecimento> itensFormatacao, EnumPerfilEntidade perfil) {

        int quantidadeItensPerfil = 0;
        for (FormatacaoObjetoFornecimento formatacaoObjetoFornecimento : itensFormatacao) {
            if (formatacaoObjetoFornecimento.getResponsavelFormatacao().getValor().equals(perfil.getValor())) {
                quantidadeItensPerfil++;
            }
        }

        return quantidadeItensPerfil;
    }

    public OrdemFornecimentoContrato alterarStatusDaOrdemFornecimento(OrdemFornecimentoContrato ordemFornecimentoContrato) {
        OrdemFornecimentoContrato ordemAlterar = em.find(OrdemFornecimentoContrato.class, ordemFornecimentoContrato.getId());
        ordemAlterar.setStatusOrdemFornecimento(ordemFornecimentoContrato.getStatusOrdemFornecimento());
        return em.merge(ordemAlterar);
    }

    // Este metodo irá setar o status do último histórico da comunicação, se foi
    // cancelado ou não.
    public OrdemFornecimentoContrato alterarStatusComunicacaoDaOrdemFornecimento(OrdemFornecimentoContrato ordemFornecimentoContrato) {
        OrdemFornecimentoContrato ordemAlterar = em.find(OrdemFornecimentoContrato.class, ordemFornecimentoContrato.getId());
        ordemAlterar.setStatusComunicacaoOrdemFornecimento(ordemFornecimentoContrato.getStatusComunicacaoOrdemFornecimento());
        ordemAlterar.setStatusOrdemFornecimento(ordemFornecimentoContrato.getStatusOrdemFornecimento());
        ordemAlterar.setDataComunicacao(ordemFornecimentoContrato.getDataComunicacao());
        //return ordemAlterar;
        return em.merge(ordemAlterar);
    }

    public FormatacaoObjetoFornecimento alterar(FormatacaoObjetoFornecimento formatacaoObjetoFornecimento) {

        FormatacaoItensContratoResposta formatacaoItensContratoRespostaParaMerge = em.find(FormatacaoItensContratoResposta.class, formatacaoObjetoFornecimento.getFormatacaoResposta().getId());

        if (formatacaoObjetoFornecimento.getFormatacaoResposta().getConteudo() != null) {
            formatacaoItensContratoRespostaParaMerge.setTamanho(new Long(formatacaoObjetoFornecimento.getFormatacaoResposta().getConteudo().length));
        }
        formatacaoItensContratoRespostaParaMerge.setConteudo(formatacaoObjetoFornecimento.getFormatacaoResposta().getConteudo());
        formatacaoItensContratoRespostaParaMerge.setDataFoto(formatacaoObjetoFornecimento.getFormatacaoResposta().getDataFoto());
        formatacaoItensContratoRespostaParaMerge.setLatitudeLongitudeFoto(formatacaoObjetoFornecimento.getFormatacaoResposta().getLatitudeLongitudeFoto());
        formatacaoItensContratoRespostaParaMerge.setNomeAnexo(formatacaoObjetoFornecimento.getFormatacaoResposta().getNomeAnexo());
        formatacaoItensContratoRespostaParaMerge.setRespostaAlfanumerico(formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaAlfanumerico());
        formatacaoItensContratoRespostaParaMerge.setRespostaBooleana(formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaBooleana());
        formatacaoItensContratoRespostaParaMerge.setRespostaData(formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaData());
        formatacaoItensContratoRespostaParaMerge.setRespostaTexto(formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaTexto());

        em.merge(formatacaoItensContratoRespostaParaMerge);
        definirSituacaoObjetoFornecimentoContratoMobile(formatacaoObjetoFornecimento.getObjetoFornecimento());

        return em.find(FormatacaoObjetoFornecimento.class, formatacaoObjetoFornecimento.getId());
    }
    
    //Quando informar se um bem, é patrimoniável ou não
    public void alterarStatusObjetoFornecimentoAoInformarPatrimoniamento(ObjetoFornecimentoContrato objetoFornecimentoContrato){
        
        String stringSql = "UPDATE "
                + " side.tb_ofo_objeto_fornecimento_contrato "+
                  " SET "+
                  " ofo_bo_item_patrimoniavel=:itemPatrimoniavel, " +
                  " ofo_no_motivo_item_nao_patrimoniavel=:motivoNaoPatrimoniavel, "+
                  " ofo_tp_tipo_patrimonio=:tipoPatrimonio "+
                  " WHERE "+
                  " ofo_id_objeto_fornecimento_contrato=:idOfo ";
        Query query = em.createNativeQuery(stringSql);
        query.setParameter("itemPatrimoniavel", objetoFornecimentoContrato.getItemPatrimoniavel());
        query.setParameter("motivoNaoPatrimoniavel", objetoFornecimentoContrato.getMotivoItemNaoPatrimoniavel());
        query.setParameter("tipoPatrimonio", objetoFornecimentoContrato.getTipoPatrimonio().getValor());
        query.setParameter("idOfo", objetoFornecimentoContrato.getId());
        query.executeUpdate();
    }

    public FormatacaoObjetoFornecimento incluir(FormatacaoObjetoFornecimento formatacaoObjetoFornecimento) {

        FormatacaoObjetoFornecimento formatacaoObjetoFornecimentoParaMerge = em.find(FormatacaoObjetoFornecimento.class, formatacaoObjetoFornecimento.getId());

        formatacaoObjetoFornecimento.getFormatacaoResposta().setFormatacao(em.find(FormatacaoItensContrato.class, formatacaoObjetoFornecimentoParaMerge.getFormatacao().getId()));

        if (formatacaoObjetoFornecimento.getFormatacaoResposta().getConteudo() != null) {
            formatacaoObjetoFornecimento.getFormatacaoResposta().setTamanho(new Long(formatacaoObjetoFornecimento.getFormatacaoResposta().getConteudo().length));
        }

        em.persist(formatacaoObjetoFornecimento.getFormatacaoResposta());
        em.flush();

        formatacaoObjetoFornecimentoParaMerge.setFormatacaoResposta(formatacaoObjetoFornecimento.getFormatacaoResposta());
        em.merge(formatacaoObjetoFornecimentoParaMerge);

        definirSituacaoObjetoFornecimentoContratoMobile(formatacaoObjetoFornecimento.getObjetoFornecimento());

        return formatacaoObjetoFornecimentoParaMerge;
    }

    public ObjetoFornecimentoContrato incluirAlterar(ObjetoFornecimentoContrato objetoFornecimentoContrato) {

        ObjetoFornecimentoContrato objetoFornecimentoContratoParaMerge = em.find(ObjetoFornecimentoContrato.class, objetoFornecimentoContrato.getId());

        objetoFornecimentoContratoParaMerge.setEstadoDeNovo(objetoFornecimentoContrato.getEstadoDeNovo());
        objetoFornecimentoContratoParaMerge.setFuncionandoDeAcordo(objetoFornecimentoContrato.getFuncionandoDeAcordo());
        objetoFornecimentoContratoParaMerge.setConfiguradoDeAcordo(objetoFornecimentoContrato.getConfiguradoDeAcordo());
        objetoFornecimentoContratoParaMerge.setDescricaoNaoConfiguradoDeAcordo(objetoFornecimentoContrato.getDescricaoNaoConfiguradoDeAcordo());
        objetoFornecimentoContratoParaMerge.setDescricaoNaoFuncionandoDeAcordo(objetoFornecimentoContrato.getDescricaoNaoFuncionandoDeAcordo());

        for (FormatacaoObjetoFornecimento formatacaoObjetoFornecimentoMerge : objetoFornecimentoContratoParaMerge.getListaFormatacaoObjetoFornecimento()) {
            for (FormatacaoObjetoFornecimento formatacaoObjetoFornecimento : objetoFornecimentoContrato.getListaFormatacaoObjetoFornecimento()) {

                if (formatacaoObjetoFornecimentoMerge.getId().equals(formatacaoObjetoFornecimento.getId())) {

                    // se possui resposta
                    if (formatacaoObjetoFornecimento.getFormatacaoResposta() != null) {
                        // nova resposta
                        if (formatacaoObjetoFornecimento.getFormatacaoResposta().getId() == null) {

                            if (formatacaoObjetoFornecimento.getFormatacaoResposta().getConteudo() != null) {
                                formatacaoObjetoFornecimento.getFormatacaoResposta().setTamanho(new Long(formatacaoObjetoFornecimento.getFormatacaoResposta().getConteudo().length));
                                formatacaoObjetoFornecimento.getFormatacaoResposta().setStatusFormatacao(EnumStatusFormatacao.NAO_PREENCHIDO);
                            }

                            em.persist(formatacaoObjetoFornecimento.getFormatacaoResposta());
                            em.flush();

                            FormatacaoObjetoFornecimento formatacaoObjetoFornecimentoParaMerge = em.find(FormatacaoObjetoFornecimento.class, formatacaoObjetoFornecimento.getId());
                            formatacaoObjetoFornecimentoParaMerge.setFormatacaoResposta(formatacaoObjetoFornecimento.getFormatacaoResposta());
                            em.merge(formatacaoObjetoFornecimentoParaMerge);
                            em.flush();

                            // atualizacao de resposta
                        } else {
                            
                            FormatacaoItensContratoResposta formatacaoItensContratoRespostaParaMerge = new FormatacaoItensContratoResposta();
                            
                            if(formatacaoObjetoFornecimentoMerge.getFormatacaoResposta() != null && formatacaoObjetoFornecimentoMerge.getFormatacaoResposta().getId() != null){
                                formatacaoItensContratoRespostaParaMerge = em.find(FormatacaoItensContratoResposta.class, formatacaoObjetoFornecimentoMerge.getFormatacaoResposta().getId());
                            }else{
                                formatacaoItensContratoRespostaParaMerge.setFormatacao(formatacaoObjetoFornecimento.getFormatacao());
                            }
                            

                            if (formatacaoObjetoFornecimento.getFormatacaoResposta().getConteudo() != null) {
                                formatacaoItensContratoRespostaParaMerge.setTamanho(new Long(formatacaoObjetoFornecimento.getFormatacaoResposta().getConteudo().length));
                            }

                            formatacaoItensContratoRespostaParaMerge.setConteudo(formatacaoObjetoFornecimento.getFormatacaoResposta().getConteudo());
                            formatacaoItensContratoRespostaParaMerge.setDataFoto(formatacaoObjetoFornecimento.getFormatacaoResposta().getDataFoto());
                            formatacaoItensContratoRespostaParaMerge.setLatitudeLongitudeFoto(formatacaoObjetoFornecimento.getFormatacaoResposta().getLatitudeLongitudeFoto());
                            formatacaoItensContratoRespostaParaMerge.setNomeAnexo(formatacaoObjetoFornecimento.getFormatacaoResposta().getNomeAnexo());
                            formatacaoItensContratoRespostaParaMerge.setRespostaAlfanumerico(formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaAlfanumerico());
                            formatacaoItensContratoRespostaParaMerge.setRespostaBooleana(formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaBooleana());
                            formatacaoItensContratoRespostaParaMerge.setRespostaData(formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaData());
                            formatacaoItensContratoRespostaParaMerge.setRespostaTexto(formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaTexto());
                            formatacaoItensContratoRespostaParaMerge.setStatusFormatacao(formatacaoObjetoFornecimento.getFormatacaoResposta().getStatusFormatacao());
                            em.merge(formatacaoItensContratoRespostaParaMerge);
                            em.flush();
                        }
                    }
                }
            }
            atualizarStatusDaNotaRemessaRecebendoResposta(formatacaoObjetoFornecimentoMerge); 
        }
        
        EnumStatusOrdemFornecimento statusOf = objetoFornecimentoContratoParaMerge.getOrdemFornecimento().getStatusOrdemFornecimento();
        boolean ofExecutada = ((statusOf == EnumStatusOrdemFornecimento.EXECUTADA) || (statusOf == EnumStatusOrdemFornecimento.ENTREGUE) || (statusOf == EnumStatusOrdemFornecimento.CONCLUIDA));
        
        definirSituacaoObjetoFornecimentoContrato(objetoFornecimentoContratoParaMerge,ofExecutada);
        verificarSituacaoDeConformidadesDoItem(objetoFornecimentoContrato,objetoFornecimentoContratoParaMerge);
        em.merge(objetoFornecimentoContratoParaMerge);
        return objetoFornecimentoContratoParaMerge;
    }
    
    private void atualizarStatusDaNotaRemessaRecebendoResposta(FormatacaoObjetoFornecimento formatacaoObjetoFornecimento){
        if(formatacaoObjetoFornecimento.getResponsavelFormatacao() == EnumPerfilEntidade.BENEFICIARIO){
            List<BigInteger> listaIds = notaRemessaService.buscarIdsNotaRemessaPorFormatacaoObjetoFornecimento(formatacaoObjetoFornecimento);
            for (BigInteger long1 : listaIds) {
                notaRemessaService.atualizarStatusNotaRemessaBeneficiarioPeloIdEStatus(EnumStatusExecucaoNotaRemessaBeneficiario.EM_ANALISE,long1.longValue());
            }
        }
    }

    public FormatacaoItensContratoRespostaDto buscarFormatacaoItensContratoRespostaDownloadPeloId(Long id) {
        List<FormatacaoItensContratoRespostaDto> listaRetorno = new ArrayList<FormatacaoItensContratoRespostaDto>();
        List<?> objetos = em.createQuery("select a.id, a.respostaAlfanumerico, a.conteudo, a.nomeAnexo, a.respostaBooleana, a.respostaData, a.dataFoto, a.latitudeLongitudeFoto, a.respostaTexto, a.tamanho from FormatacaoItensContratoResposta a where a.id=:id order by a.id asc")
                .setParameter("id", id).getResultList();

        for (Object object : objetos) {
            Object[] o = (Object[]) object;
            FormatacaoItensContratoRespostaDto formatacaoItensContratoRespostaDto = new FormatacaoItensContratoRespostaDto();

            formatacaoItensContratoRespostaDto.setId((Long) o[0]);
            formatacaoItensContratoRespostaDto.setRespostaAlfanumerico((String) o[1]);
            formatacaoItensContratoRespostaDto.setConteudo((byte[]) o[2]);
            formatacaoItensContratoRespostaDto.setNomeAnexo((String) o[3]);
            formatacaoItensContratoRespostaDto.setRespostaBooleana((Boolean) o[4]);
            formatacaoItensContratoRespostaDto.setRespostaData((LocalDate) o[5]);
            formatacaoItensContratoRespostaDto.setDataFoto((LocalDateTime) o[6]);
            formatacaoItensContratoRespostaDto.setLatitudeLongitudeFoto((String) o[7]);
            formatacaoItensContratoRespostaDto.setRespostaTexto((String) o[8]);
            formatacaoItensContratoRespostaDto.setTamanho((Long) o[9]);

            listaRetorno.add(formatacaoItensContratoRespostaDto);
        }

        if (listaRetorno.size() > 0) {
            return listaRetorno.get(0);
        } else {
            return null;
        }
    }
    
    public void atualizarStatusOrdemFornecimentoPeloId(Long id, EnumStatusOrdemFornecimento status){
        
        String sqlAtualizar = "UPDATE side.tb_ofc_ordem_fornecimento_contrato "
                +" SET "
                +" ofc_st_status_ordem_fornecimento=:status "
                +" WHERE "
                + " ofc_id_ordem_fornecimento_contrato=:idOfc";
        Query query = em.createNativeQuery(sqlAtualizar);
        query.setParameter("status", status.getValor());
        query.setParameter("idOfc",id );
        query.executeUpdate();
    }
    
    
    //Metodo que irá verificar se o identificador único informado esta em uso neste programa
    public List<String> identificadorUnicoEmUsoNestePrograma(ObjetoFornecimentoContrato objetoFornecimentoContrato){
        
        List<String> listaIdentificadoresRepetidos = new ArrayList<String>();
        
        List<FormatacaoObjetoFornecimento> lista = objetoFornecimentoContrato.getListaFormatacaoObjetoFornecimento();
        for (FormatacaoObjetoFornecimento fof : lista) {
            
            if(fof.getFormatacao().getPossuiIdentificadorUnico() && fof.getFormatacaoResposta() != null){
                if(fof.getFormatacaoResposta().getRespostaAlfanumerico() != null && !"".equalsIgnoreCase(fof.getFormatacaoResposta().getRespostaAlfanumerico())){
                    listaIdentificadoresRepetidos.addAll(buscarIdentificadorUnicoRepetido(fof, objetoFornecimentoContrato.getOrdemFornecimento().getContrato().getPrograma()));
                }
            }
        }
        
        return listaIdentificadoresRepetidos;
    }
    
    private List<String> buscarIdentificadorUnicoRepetido(FormatacaoObjetoFornecimento fof, Programa programa){
        
        List<String> listaIdentificadoresRepetidos = new ArrayList<String>();
        
        String string = " SELECT "+
                " fir.fir_fk_fic_id_formatacao_itens_contrato " +
                " FROM "+
                " side.tb_ofo_objeto_fornecimento_contrato ofo, "+
                " side.tb_fof_formatacao_objeto_fornecimento fof, "+
                " side.tb_fir_formatacao_itens_contrato_resposta fir, "+ 
                " side.tb_fic_formatacao_itens_contrato fic, " +
                " side.tb_foc_formatacao_contrato foc, " +
                " side.tb_con_contrato con, " +
                " side.tb_prg_programa prg "+
                " WHERE "+
                " fof.fof_fk_ofo_id_objeto_fornecimento_contrato = ofo.ofo_id_objeto_fornecimento_contrato AND "+
                " fof.fof_fk_fir_id_formatacao_itens_contrato_resposta = fir.fir_id_formatacao_itens_contrato_resposta AND "+
                " fir.fir_fk_fic_id_formatacao_itens_contrato = fic.fic_id_formatacao_itens_contrato AND "+
                " fic.fic_fk_foc_id_formatacao_contrato = foc.foc_id_formatacao_contrato AND "+
                " foc.foc_fk_con_id_contrato = con.con_id_contrato AND "+
                " con.con_fk_prg_id_programa = prg.prg_id_programa AND "+
                " fir.fir_ds_resposta_alfa_numerico = :identificadorUnico AND  "+
                " prg.prg_id_programa = :idPrograma ";
        
        Query query = em.createNativeQuery(string);
        query.setParameter("idPrograma", programa.getId());
        query.setParameter("identificadorUnico", fof.getFormatacaoResposta().getRespostaAlfanumerico());
        
        List<BigInteger> listaRespostas = new ArrayList<BigInteger>();
        listaRespostas = query.getResultList();
        
        for(BigInteger id:listaRespostas){
            if(fof.getFormatacao() != null){
                if(!id.equals(new BigInteger(fof.getFormatacao().getId().toString()))){
                    listaIdentificadoresRepetidos.add(fof.getFormatacaoResposta().getRespostaAlfanumerico());
                    break;
                }
            }
        }
        
        return listaIdentificadoresRepetidos;
    }

    private void definirSituacaoObjetoFornecimentoContratoMobile(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        ObjetoFornecimentoContrato objetoFornecimentoContratoParaMerge = em.find(ObjetoFornecimentoContrato.class, objetoFornecimentoContrato.getId());
        definirSituacaoObjetoFornecimentoContrato(objetoFornecimentoContratoParaMerge, EnumPerfilEntidade.FORNECEDOR);
        definirSituacaoObjetoFornecimentoContrato(objetoFornecimentoContratoParaMerge, EnumPerfilEntidade.BENEFICIARIO);

        objetoFornecimentoContratoParaMerge.setEstadoDeNovo(objetoFornecimentoContrato.getEstadoDeNovo());
        objetoFornecimentoContratoParaMerge.setFuncionandoDeAcordo(objetoFornecimentoContrato.getFuncionandoDeAcordo());
        objetoFornecimentoContratoParaMerge.setConfiguradoDeAcordo(objetoFornecimentoContrato.getConfiguradoDeAcordo());
        objetoFornecimentoContratoParaMerge.setDescricaoNaoConfiguradoDeAcordo(objetoFornecimentoContrato.getDescricaoNaoConfiguradoDeAcordo());
        objetoFornecimentoContratoParaMerge.setDescricaoNaoFuncionandoDeAcordo(objetoFornecimentoContrato.getDescricaoNaoFuncionandoDeAcordo());

        verificarSituacaoDeConformidadesDoItem(objetoFornecimentoContratoParaMerge, objetoFornecimentoContratoParaMerge);
        
        em.merge(objetoFornecimentoContratoParaMerge);
    }

    private void definirSituacaoObjetoFornecimentoContrato(ObjetoFornecimentoContrato objetoFornecimentoContrato, boolean ofExecutada) {
        definirSituacaoObjetoFornecimentoContrato(objetoFornecimentoContrato, EnumPerfilEntidade.FORNECEDOR);
        
        if(ofExecutada){
            definirSituacaoObjetoFornecimentoContrato(objetoFornecimentoContrato, EnumPerfilEntidade.BENEFICIARIO);
        }
    }
    

    public void verificarSituacaoDeConformidadesDoItem(ObjetoFornecimentoContrato ofc, ObjetoFornecimentoContrato objetoFornecimentoContratoParaMerge){
        
        List<FormatacaoObjetoFornecimento> listaAmbos = buscarFormatacoesObjetosPorPerfil(ofc, null);
        List<FormatacaoObjetoFornecimentoAmbosDto> listaComFormatacoes = SideUtil.convertListaFofPorFormatacaoItem(listaAmbos,null);
        
        List<FormatacaoObjetoFornecimento> listaDigitadaAgora = ofc.getListaFormatacaoObjetoFornecimento();
        
        //Ira popular as respostas digitadas agora com as respostas já salvas do banco
        for (FormatacaoObjetoFornecimento fofDigitado : listaDigitadaAgora) {
            
            for(FormatacaoObjetoFornecimentoAmbosDto ambosDto :listaComFormatacoes){
                
                if(fofDigitado.getResponsavelFormatacao() == EnumPerfilEntidade.BENEFICIARIO 
                        && (ambosDto.getResponsavelFormacatao() == EnumResponsavelPreencherFormatacaoItem.BENEFICIARIO || ambosDto.getResponsavelFormacatao() == EnumResponsavelPreencherFormatacaoItem.AMBOS)){
                    if(fofDigitado.getFormatacaoResposta() != null){
                        if(ambosDto.getFormatacaoFornecedor() != null){
                            if(fofDigitado.getId().equals(ambosDto.getFormatacaoBeneficiario().getId())){
                                ambosDto.getFormatacaoBeneficiario().setFormatacaoResposta(new FormatacaoItensContratoResposta());
                                ambosDto.getFormatacaoBeneficiario().setFormatacaoResposta(fofDigitado.getFormatacaoResposta());
                                continue;
                            }
                        }
                    }
                }
                
                if(fofDigitado.getResponsavelFormatacao() == EnumPerfilEntidade.FORNECEDOR
                        && (ambosDto.getResponsavelFormacatao() == EnumResponsavelPreencherFormatacaoItem.FORNECEDOR || ambosDto.getResponsavelFormacatao() == EnumResponsavelPreencherFormatacaoItem.AMBOS)){
                    if(fofDigitado.getFormatacaoResposta() != null){
                        if(ambosDto.getFormatacaoFornecedor() != null){
                            if(fofDigitado.getId().equals(ambosDto.getFormatacaoFornecedor().getId())){
                                ambosDto.getFormatacaoFornecedor().setFormatacaoResposta(new FormatacaoItensContratoResposta());
                                ambosDto.getFormatacaoFornecedor().setFormatacaoResposta(fofDigitado.getFormatacaoResposta());
                                continue;
                            }
                        }
                    }
                }
            }
        }
        
        StringBuilder stringNaoConformidade = new StringBuilder();
        boolean conformidade = true;
        boolean conformidadeOfo = true;
        
        EnumStatusOrdemFornecimento statusOf = ofc.getOrdemFornecimento().getStatusOrdemFornecimento();
        boolean ofExecutada = ((statusOf == EnumStatusOrdemFornecimento.EXECUTADA) || 
                (statusOf == EnumStatusOrdemFornecimento.RECEBIDA) || (statusOf == EnumStatusOrdemFornecimento.ENTREGUE));
        
        FormatacaoObjetoFornecimento formatacao = null;
        NotaRemessaOrdemFornecimentoContrato notaRemessaAtual = null;
        
        //List<ImagensIguaisConformidadesDto> listaImagensUsadas = new ArrayList<ImagensIguaisConformidadesDto>();
        Map<String,ImagensIguaisConformidadesDto> mapImagensUsadas = new HashMap<String,ImagensIguaisConformidadesDto>();
        
        //Irá verificar as conformidades de todas as respostas informadas no cadastro do item
        for (FormatacaoObjetoFornecimentoAmbosDto fof : listaComFormatacoes) {
            
            conformidadeOfo = true;
            FormatacaoObjetoFornecimento formatacaoBeneficiario = fof.getFormatacaoBeneficiario();
            FormatacaoObjetoFornecimento formatacaoFornecedor = fof.getFormatacaoFornecedor();
            EnumResponsavelPreencherFormatacaoItem responsavelFormatacao = fof.getResponsavelFormacatao();
            StringBuilder stringNaoConformidadeDesteItem = new StringBuilder();
            StringBuilder stringNaoConformidadeBeneficiario = new StringBuilder();
            StringBuilder stringNaoConformidadeFornecedor = new StringBuilder();
            StringBuilder conformidadeBeneficiario = new StringBuilder("0");
            StringBuilder conformidadeFornecedor = new StringBuilder("0");
            
            //Irá recuperar a nota fiscal desta formatacaoObjetoFornecimento
            if(formatacao == null){
                formatacao = new FormatacaoObjetoFornecimento();
                
                ObjetoFornecimentoContrato objeto = new ObjetoFornecimentoContrato();
                objeto.setLocalEntrega(ofc.getLocalEntrega());
                objeto.setItem(ofc.getItem());
                objeto.setOrdemFornecimento(ofc.getOrdemFornecimento());
                
                formatacao.setObjetoFornecimento(objeto);
                
                List<BigInteger> idNotaRemessa = notaRemessaService.buscarIdsNotaRemessaPorFormatacaoObjetoFornecimentoSemStatus(formatacao);
                
                if(idNotaRemessa != null && !idNotaRemessa.isEmpty()){
                    notaRemessaAtual = notaRemessaService.buscarPeloId(new Long(idNotaRemessa.get(0).toString()));
                }
                
                //Este metodo irá verificar todos os identificadores únicos repetidos no programa
                conformidade = verificarIdentificadoresUnicos(conformidade,stringNaoConformidadeDesteItem,objetoFornecimentoContratoParaMerge);
                //Irá verificar se o código gerado aleatoriamente para o beneficiario no momento da execução da O.F. é o mesmo informado pelo fornecedor.
                conformidade = verificarCodigoDeBeneficiarioEFornecedor(conformidade,stringNaoConformidadeDesteItem,notaRemessaAtual);
            }
            
            //Se a resposta a ser analisada agora foi especifica para 'AMBOS' então serão verificadas as respostas
            //do beneficiário e do fornecedor.
            if(responsavelFormatacao == EnumResponsavelPreencherFormatacaoItem.AMBOS){                
                if(fof.getFormatacaoBeneficiario().getFormatacao().getTipoCampo() == EnumTipoCampoFormatacao.FOTO){
                    conformidadeOfo = verificarConformidadesDaFoto(formatacaoBeneficiario, formatacaoFornecedor,conformidade,stringNaoConformidadeDesteItem,stringNaoConformidadeBeneficiario, stringNaoConformidadeFornecedor,conformidadeBeneficiario, conformidadeFornecedor,ofc,notaRemessaAtual,ofExecutada);
                }
                
                if(fof.getFormatacaoBeneficiario().getFormatacao().getTipoCampo() == EnumTipoCampoFormatacao.ALFANUMERICO ||
                        fof.getFormatacaoBeneficiario().getFormatacao().getTipoCampo() == EnumTipoCampoFormatacao.TEXTO ||
                        fof.getFormatacaoBeneficiario().getFormatacao().getTipoCampo() == EnumTipoCampoFormatacao.NUMERICO){
                    conformidadeOfo = verificarConformidadesDoCampoDeTexto(formatacaoBeneficiario, formatacaoFornecedor,conformidade,stringNaoConformidadeDesteItem,stringNaoConformidadeBeneficiario,stringNaoConformidadeFornecedor,conformidadeBeneficiario, conformidadeFornecedor,ofc,notaRemessaAtual,ofExecutada);
                }
            }
            
            //Irá atualizar o status da FormatacaoObjetoFornecimento.
            FormatacaoItensContratoResposta firBeneficiario = null;
            FormatacaoItensContratoResposta firFornecedor = null;
            
            //Irá verificar as informações que idependem se é de Ambos, tipo se a data informada da foto já esta em uso em outra foto
            if(formatacaoBeneficiario != null){
                firBeneficiario = formatacaoBeneficiario.getFormatacaoResposta() == null?null:formatacaoBeneficiario.getFormatacaoResposta();
                
                if(fof.getFormatacaoBeneficiario().getFormatacao().getTipoCampo() == EnumTipoCampoFormatacao.FOTO && firBeneficiario != null){
                    verificarSeAFotoJaFoiPostadaAnteriormente(firBeneficiario,"beneficiário",fof.getFormatacaoBeneficiario(),stringNaoConformidadeDesteItem,stringNaoConformidadeBeneficiario,conformidadeBeneficiario,conformidade);
                    conformidade = verificarSeAFotoJaEstaEmUsoNestaTela("beneficiario",firBeneficiario,mapImagensUsadas,stringNaoConformidadeBeneficiario,conformidadeBeneficiario,conformidade);
                }
            }
            
            if(formatacaoFornecedor != null){
                
                firFornecedor = formatacaoFornecedor.getFormatacaoResposta() == null?null:formatacaoFornecedor.getFormatacaoResposta();            
                
                if(fof.getFormatacaoFornecedor().getFormatacao().getTipoCampo() == EnumTipoCampoFormatacao.FOTO && firFornecedor != null){
                    verificarSeAFotoJaFoiPostadaAnteriormente(firFornecedor,"fornecedor",fof.getFormatacaoFornecedor(),stringNaoConformidadeDesteItem,stringNaoConformidadeFornecedor,conformidadeFornecedor,conformidade);
                    conformidade = verificarSeAFotoJaEstaEmUsoNestaTela("fornecedor",firFornecedor,mapImagensUsadas,stringNaoConformidadeBeneficiario,conformidadeBeneficiario,conformidade);
                }
            }
            
            if(conformidade == true){
                conformidade = conformidadeOfo;
            }
            
            EnumStatusFormatacao statusFornecedor = null;
            EnumStatusFormatacao statusBeneficiario = null;
            
            if(fof.getFormatacaoBeneficiario() != null && fof.getFormatacaoBeneficiario().getResponsavelFormatacao() == EnumPerfilEntidade.BENEFICIARIO){
                if(!conformidadeBeneficiario.toString().equalsIgnoreCase("0")){
                    statusBeneficiario = EnumStatusFormatacao.NAO_CONFORMIDADE;
                }else{
                    if(firBeneficiario == null){
                        statusBeneficiario = EnumStatusFormatacao.NAO_PREENCHIDO;
                    }else{
                        statusBeneficiario = EnumStatusFormatacao.CONFORMIDADE;
                    }
                }
            }
            
            if(fof.getFormatacaoFornecedor() != null && fof.getFormatacaoFornecedor().getResponsavelFormatacao() == EnumPerfilEntidade.FORNECEDOR){
                if(!conformidadeFornecedor.toString().equalsIgnoreCase("0")){
                    statusFornecedor = EnumStatusFormatacao.NAO_CONFORMIDADE;
                }else{
                    if(firFornecedor == null){
                        statusFornecedor = EnumStatusFormatacao.NAO_PREENCHIDO;
                    }else{
                        statusFornecedor = EnumStatusFormatacao.CONFORMIDADE;
                    }
                }
            }
            
            stringNaoConformidade.append(stringNaoConformidadeDesteItem);
            
                if(firBeneficiario != null){
                    FormatacaoItensContratoResposta fir = em.find(FormatacaoItensContratoResposta.class, firBeneficiario.getId());
                    if(fir != null){
                        fir.setStatusFormatacao(statusBeneficiario);
                        fir.setMotivoNaoConformidade(stringNaoConformidadeBeneficiario.toString());
                        
                        if(firBeneficiario.getArquivoUnico() != null){
                            if(firBeneficiario.getArquivoUnico().getId() == null){
                                ArquivoUnico au = arquivoUnicoService.incluir(firBeneficiario.getArquivoUnico());
                                fir.setArquivoUnico(au);
                            }else{
                                arquivoUnicoService.alterar(firBeneficiario.getArquivoUnico());
                            }
                        }
                    }
                    em.merge(fir);
                    //atualizarStatusItemContratoRespostaPeloId(statusBeneficiario,stringNaoConformidadeBeneficiario.toString(),formatacaoBeneficiario.getFormatacaoResposta().getId());
                }
                if(firFornecedor != null){
                    FormatacaoItensContratoResposta fir = em.find(FormatacaoItensContratoResposta.class, firFornecedor.getId());
                    
                    if(fir!=null){
                        fir.setStatusFormatacao(statusFornecedor);
                        fir.setMotivoNaoConformidade(stringNaoConformidadeFornecedor.toString());
                        
                        if(firFornecedor.getArquivoUnico() != null){
                            if(firFornecedor.getArquivoUnico().getId() == null){
                                ArquivoUnico au = arquivoUnicoService.incluir(firFornecedor.getArquivoUnico());
                                fir.setArquivoUnico(au);
                            }else{
                                arquivoUnicoService.alterar(firFornecedor.getArquivoUnico());
                            }
                        }
                        em.merge(fir);
                    }
                    //atualizarStatusItemContratoRespostaPeloId(statusFornecedor,stringNaoConformidadeFornecedor.toString(),formatacaoFornecedor.getFormatacaoResposta().getId());
                }
        }
        
        if(notaRemessaAtual == null){
            if(ofExecutada){
                stringNaoConformidade.append("<ul>O Beneficiário ainda não respondeu nenhuma das 3 perguntas obrigatórias.</ul>");
                conformidade = false;
            }
        }else{
          //Verificar se o beneficiário respondeu não a alguma das perguntas fixas do item.        
            if(notaRemessaAtual.getStatusExecucaoBeneficiario() == EnumStatusExecucaoNotaRemessaBeneficiario.ENVIADO || notaRemessaAtual.getStatusExecucaoBeneficiario() == EnumStatusExecucaoNotaRemessaBeneficiario.EM_ANALISE){
                List<?> listaPerguntasConformidade = buscarRespostasSeBemEntregueEstaDeAcordo(ofc.getId());
                for (Object object : listaPerguntasConformidade) {
                    Object[] o = (Object[]) object;
                    
                    Boolean estadoDeNovo = (Boolean) o[0] != null? (Boolean)o[0]:null;
                    Boolean funcionandoDeAcordo = (Boolean) o[1] != null? (Boolean)o[1]:null;
                    Boolean configuradoDeAcordo = (Boolean) o[2] != null? (Boolean)o[2]:null;
                    
                    if(estadoDeNovo == null){
                        conformidade = false;
                        stringNaoConformidade.append("<li>O Beneficiário não informou se o Item entregue esta em estado de novo.</li>");
                    }else{
                        if(!estadoDeNovo){
                            conformidade = false;
                            stringNaoConformidade.append("<li>O Beneficiário informou que o Item entregue não esta em estado de novo.</li>");
                        }
                    }
                    
                    if(configuradoDeAcordo == null){
                        conformidade = false;
                        stringNaoConformidade.append("<li>O Beneficiário não informou se o Item entregue esta configurado de acordo.</li>");
                    }else{
                        if(!configuradoDeAcordo){
                            conformidade = false;
                            stringNaoConformidade.append("<li>O Beneficiário informou que o Item entregue não esta configurado de acordo.</li>");
                        }
                    }
                    
                    if(funcionandoDeAcordo == null){
                        conformidade = false;
                        stringNaoConformidade.append("<li>O Beneficiário não informou se o Item entregue esta funcionando de acordo.</li>");
                    }else{
                        if(!funcionandoDeAcordo){
                            conformidade = false;
                            stringNaoConformidade.append("<li>O Beneficiário informou que o Item entregue não esta funcionando de acordo.</li>");
                        }
                    }
                }
            }
        }
        
        objetoFornecimentoContratoParaMerge.setMotivoNaoConformidade(stringNaoConformidade.toString());
        if(!conformidade){
            objetoFornecimentoContratoParaMerge.setSituacaoAvaliacaoPreliminarPreenchimentoItem(EnumSituacaoAvaliacaoPreliminarPreenchimentoItem.NAO_CONFORMIDADE);
            //objetoFornecimentoContratoParaMerge.setSituacaoPreenchimentoBeneficiario(EnumSituacaoPreenchimentoItemFormatacaoBeneficiario.PREENCHIDO_COM_PENDENCIA);
        }else{
            objetoFornecimentoContratoParaMerge.setSituacaoAvaliacaoPreliminarPreenchimentoItem(EnumSituacaoAvaliacaoPreliminarPreenchimentoItem.EM_CONFORMIDADE);
        }
    }
    
    private boolean verificarCodigoDeBeneficiarioEFornecedor(boolean conformidade, StringBuilder stringNaoConformidade,NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato){        
        if(notaRemessaOrdemFornecimentoContrato != null){
            if(notaRemessaOrdemFornecimentoContrato.getCodigoGerado() != null && notaRemessaOrdemFornecimentoContrato.getCodigoInformadoPeloFornecedor() !=null){
                if(!notaRemessaOrdemFornecimentoContrato.getCodigoGerado().equalsIgnoreCase(notaRemessaOrdemFornecimentoContrato.getCodigoInformadoPeloFornecedor())){
                    conformidade = false;
                    stringNaoConformidade.append("<li>O cógido de recebimento dos bens informado pelo beneficiário e fornecedor não são iguais.</li>");
                }
            }
        }
        return conformidade;
    }
    
    private boolean verificarIdentificadoresUnicos(boolean conformidade, StringBuilder stringNaoConformidade,ObjetoFornecimentoContrato objetoFornecimentoContratoParaMerge){
        List<String> listaIdentificadoresUnicos = identificadorUnicoEmUsoNestePrograma(objetoFornecimentoContratoParaMerge);
        
        if(listaIdentificadoresUnicos != null && !listaIdentificadoresUnicos.isEmpty()){
            
            Map <String,Integer> mapQuantidadeIdentificadores = new HashMap<String,Integer>();
            
            conformidade = false;
            stringNaoConformidade.append("Os seguintes identificadores únicos estão repetidos neste programa:<br />");
            for(String identificador:listaIdentificadoresUnicos){
                if(!mapQuantidadeIdentificadores.containsKey(identificador)){
                    mapQuantidadeIdentificadores.put(identificador, 1);
                }else{
                    Integer qtd = mapQuantidadeIdentificadores.get(identificador);
                    qtd +=1;
                    mapQuantidadeIdentificadores.put(identificador, qtd);
                }
            }
            
            for (Map.Entry<String,Integer> pair : mapQuantidadeIdentificadores.entrySet()) {
                stringNaoConformidade.append("<li>"+pair.getKey()+":"+pair.getValue()+" vez(es).</li>");
            }
            stringNaoConformidade.append("<hr/>");
        } 
        return conformidade;
    }
    
    private boolean verificarSeAFotoJaEstaEmUsoNestaTela(String responsavel,FormatacaoItensContratoResposta resposta,Map<String,ImagensIguaisConformidadesDto> mapImagensUsadas,StringBuilder stringNaoConformidadeIndividual,StringBuilder conformidadeIndividual,boolean conformidade){
     
        String hashImagem = SideUtil.retornarHashDoArquivo(resposta.getConteudo());
        
        for(ImagensIguaisConformidadesDto imagemUsada:mapImagensUsadas.values()){
            
            if(mapImagensUsadas.containsKey(hashImagem)&& imagemUsada.getIdResposta().longValue()!=resposta.getId().longValue()){
                stringNaoConformidadeIndividual.append("<li>A foto de nome:'"+resposta.getNomeAnexo()+"' que o "+responsavel+" usou está repetida na tela.</li>");
                conformidadeIndividual.append(1);
                conformidade = false;
            }else{
                ImagensIguaisConformidadesDto imagem = new ImagensIguaisConformidadesDto();
                imagem.setCodigoImagemUnica(hashImagem);
                imagem.setIdResposta(resposta.getId());
                mapImagensUsadas.put(hashImagem,imagem);
            }
        }
        
        if(mapImagensUsadas.values().size()==0){
            ImagensIguaisConformidadesDto imagem = new ImagensIguaisConformidadesDto();
            imagem.setCodigoImagemUnica(hashImagem);
            imagem.setIdResposta(resposta.getId());
            mapImagensUsadas.put(hashImagem,imagem);
        }
        return conformidade;
    }
    
    private boolean verificarSeAFotoJaFoiPostadaAnteriormente(FormatacaoItensContratoResposta resposta,String responsavel,FormatacaoObjetoFornecimento fof, StringBuilder stringNaoConformidade, StringBuilder stringNaoConformidadeIndividual,StringBuilder conformidadeIndividual,boolean conformidade){
        
        List<FormatacaoObjetoFornecimentoDto> listaDatasFotosRepetidas = new ArrayList<FormatacaoObjetoFornecimentoDto>();
        List<FormatacaoObjetoFornecimentoDto> listaLongitudesRepetidas = new ArrayList<FormatacaoObjetoFornecimentoDto>();
        boolean dataOk = true;
        boolean latitudeOk = true;
        boolean fotoUnica = true;
        
        FormatacaoItensContratoRespostaDto dtoFotos = new FormatacaoItensContratoRespostaDto();
        FormatacaoItensContratoRespostaDto dtoLatitude = new FormatacaoItensContratoRespostaDto();
        
        //Ira buscar fotos com a mesma data
        dtoFotos.setDataFoto(resposta.getDataFoto());
        listaDatasFotosRepetidas = formatacaoItensContratoService.buscarFormatacaoItensResposta(dtoFotos);
        dataOk = verificarDadosRepetidosDaFoto(listaDatasFotosRepetidas,fof,responsavel);
        if(!dataOk){
            
            if(fof.getFormatacaoResposta().getNomeAnexo() != null){
                //stringNaoConformidade.append("<li>A foto de nome:'"+fof.getFormatacaoResposta().getNomeAnexo()+"' que o "+responsavel+" usou esta com a mesma data de outra(s) foto(s).</li>");
                stringNaoConformidadeIndividual.append("<li>A foto de nome:'"+fof.getFormatacaoResposta().getNomeAnexo()+"' que o "+responsavel+" usou esta com a mesma data de outra(s) foto(s).</li>");
                conformidadeIndividual.append(1);
            }else{
                //stringNaoConformidade.append("<li>O "+responsavel+" postou uma foto que já esta em uso.</li>");
                stringNaoConformidadeIndividual.append("<li>O "+responsavel+" postou uma foto que já esta em uso.</li>");
                conformidadeIndividual.append(1);
            }
        }
        
        //Irá verificar se esta foto já foi postada anteriormente em qualquer lugar do sistema
        if(resposta.getConteudo() != null){
            String hashImagem = SideUtil.retornarHashDoArquivo(resposta.getConteudo());
            if(resposta.getCodigoUnicoFoto() != null){
                
                ArquivoUnico auBusca = new ArquivoUnico();
                auBusca.setCodigoUnico(resposta.getCodigoUnicoFoto());
                List<ArquivoUnico> listaFotosComMesmoHash = arquivoUnicoService.buscarSemPaginacao(auBusca);
                
                if(resposta.getArquivoUnico() != null){                    
                    if(listaFotosComMesmoHash != null && listaFotosComMesmoHash.size() == 1){
                        if(listaFotosComMesmoHash.get(0).getId().longValue() != resposta.getArquivoUnico().getId().longValue()){
                            fotoUnica = false;
                        }
                    }
                    
                    if(listaFotosComMesmoHash != null && listaFotosComMesmoHash.size() > 1){
                        fotoUnica = false;
                    }
                }else{
                    if(listaFotosComMesmoHash != null && listaFotosComMesmoHash.size() > 0){
                        fotoUnica = false;
                    }
                }
                
                if(!fotoUnica){
                    stringNaoConformidadeIndividual.append("<li>A foto de nome:'"+fof.getFormatacaoResposta().getNomeAnexo()+"' que o "+responsavel+" enviou já foi cadastrada no sistema.</li>");
                    conformidadeIndividual.append(1);
                }
                
                
                if(resposta.getArquivoUnico()!=null){
                    resposta.getArquivoUnico().setCodigoUnico(resposta.getCodigoUnicoFoto());
                }else if(resposta.getArquivoUnico()==null){
                    ArquivoUnico au = new ArquivoUnico();
                    au.setCodigoUnico(resposta.getCodigoUnicoFoto());
                    au.setOrigemArquivo(EnumOrigemArquivo.CADASTRO_CONFORMIDADES);
                    arquivoUnicoService.incluir(au);
                    em.flush();
                    resposta.setArquivoUnico(au);
                }
            }
        }
        
        //Irá verificar se esta foto possui a obrigatoriedade do GPS e se foi postada foto com GPS.
        boolean fotoSemLatitue = (resposta.getLatitudeLongitudeFoto() == null || resposta.getLatitudeLongitudeFoto().contains("undefined"));
        boolean gpsObrigatorio = resposta.getFormatacao().getPossuiGPS();
        if(gpsObrigatorio && fotoSemLatitue){

            //stringNaoConformidade.append("<li>A foto de nome:'"+fof.getFormatacaoResposta().getNomeAnexo()+"' que o "+responsavel+" enviou não possui informações de GPS.</li>");
            stringNaoConformidadeIndividual.append("<li>A foto de nome:'"+fof.getFormatacaoResposta().getNomeAnexo()+"' que o "+responsavel+" enviou não possui informações de GPS.</li>");
            conformidadeIndividual.append(1);
            latitudeOk = false;
            
        }else{
            
            if(gpsObrigatorio){
              //Irá buscar fotos com a mesma latitude e longitude
                dtoLatitude.setLatitudeLongitudeFoto(resposta.getLatitudeLongitudeFoto());
                listaLongitudesRepetidas = formatacaoItensContratoService.buscarFormatacaoItensResposta(dtoLatitude);
                latitudeOk = verificarDadosRepetidosDaFoto(listaLongitudesRepetidas,fof,responsavel);
                if(!latitudeOk){
                    
                    if(fof.getFormatacaoResposta().getNomeAnexo() != null){
                        //stringNaoConformidade.append("<ul>A foto de nome:'"+fof.getFormatacaoResposta().getNomeAnexo()+"' que o "+responsavel+" usou possui a mesma latitude e longitude outra(s) foto(s).</ul>");
                        stringNaoConformidadeIndividual.append("<li>A foto de nome:'"+fof.getFormatacaoResposta().getNomeAnexo()+"' que o "+responsavel+" usou possui a mesma latitude e longitude outra(s) foto(s).</li>");
                        conformidadeIndividual.append(1);
                    }else{
                        //stringNaoConformidade.append("<ul>O "+responsavel+" postou uma foto que já esta em uso.</ul>");
                        stringNaoConformidadeIndividual.append("<li>O "+responsavel+" postou uma foto que já esta em uso.</li>");
                        conformidadeIndividual.append(1);
                    }
                }
            }
            
        }
      
        
        if(!dataOk || !latitudeOk || !fotoUnica)
        {
            conformidade = false;
        }
        
        return conformidade;
    }
      
    //Metodo que irá verificar se alguma foto já foi postada anteriormente
    private boolean verificarDadosRepetidosDaFoto(List<FormatacaoObjetoFornecimentoDto> listaDatasFotosRepetidas, FormatacaoObjetoFornecimento fof, String responsavel){
        
        if(listaDatasFotosRepetidas != null && !listaDatasFotosRepetidas.isEmpty()){
            for(FormatacaoObjetoFornecimentoDto ficd:listaDatasFotosRepetidas){
                if(ficd.getFormatacaoResposta() != null){
                    
                    if(ficd.getFormatacaoResposta().getId() != null && !ficd.getFormatacaoResposta().getFormatacao().getId().equals(fof.getFormatacao().getId())){                            
                        return false;
                    }
                }
            }
        }
        return true;
    }
     

    private void definirSituacaoObjetoFornecimentoContrato(ObjetoFornecimentoContrato objetoFornecimentoContrato, EnumPerfilEntidade perfil) {
        List<FormatacaoObjetoFornecimentoDto> listaObrigatorio = buscarListaFormatacaoObjetoFornecimento(objetoFornecimentoContrato, perfil, false);
        List<FormatacaoObjetoFornecimentoDto> listaOpcional = buscarListaFormatacaoObjetoFornecimento(objetoFornecimentoContrato, perfil, true);

        // define quantidade de quisitos
        int quantidadeQuesitosObrigatorios = listaObrigatorio.size();
        int quantidadeQuesitosOpcionais = listaOpcional.size();
        int quantidadeQuesitos = quantidadeQuesitosObrigatorios + quantidadeQuesitosOpcionais;
        if (perfil.getValor().equals(EnumPerfilEntidade.FORNECEDOR.getValor())) {
            objetoFornecimentoContrato.setQuantidadeQuesitosFornecedor(quantidadeQuesitos);
            objetoFornecimentoContrato.setQuantidadeQuesitosObrigatoriosFornecedor(quantidadeQuesitosObrigatorios);
            objetoFornecimentoContrato.setQuantidadeQuesitosOpcionaisFornecedor(quantidadeQuesitosOpcionais);
        } else if (perfil.getValor().equals(EnumPerfilEntidade.BENEFICIARIO.getValor())) {
            objetoFornecimentoContrato.setQuantidadeQuesitosBeneficiario(quantidadeQuesitos);
            objetoFornecimentoContrato.setQuantidadeQuesitosObrigatoriosBeneficiario(quantidadeQuesitosObrigatorios);
            objetoFornecimentoContrato.setQuantidadeQuesitosOpcionaisBeneficiario(quantidadeQuesitosOpcionais);
        }

        int quantidadeQuesitosPreenchidosObrigatorio = 0;
        for (FormatacaoObjetoFornecimentoDto formatacaoObjetoFornecimentoDto : listaObrigatorio) {
            if (formatacaoObjetoFornecimentoDto.getFormatacaoResposta() != null) {
                quantidadeQuesitosPreenchidosObrigatorio++;
            }
        }

        int quantidadeQuesitosPreenchidosOpcional = 0;
        for (FormatacaoObjetoFornecimentoDto formatacaoObjetoFornecimentoDto : listaOpcional) {
            if (formatacaoObjetoFornecimentoDto.getFormatacaoResposta() != null) {
                quantidadeQuesitosPreenchidosOpcional++;
            }
        }

        // define quantidade de quesitos preenchidos

        if (perfil.getValor().equals(EnumPerfilEntidade.FORNECEDOR.getValor())) {
            objetoFornecimentoContrato.setQuantidadeQuesitosObrigatoriosPreenchidosFornecedor(quantidadeQuesitosPreenchidosObrigatorio);
            objetoFornecimentoContrato.setQuantidadeQuesitosOpcionaisPreenchidosFornecedor(quantidadeQuesitosPreenchidosOpcional);
        } else if (perfil.getValor().equals(EnumPerfilEntidade.BENEFICIARIO.getValor())) {
            objetoFornecimentoContrato.setQuantidadeQuesitosObrigatoriosPreenchidosBeneficiario(quantidadeQuesitosPreenchidosObrigatorio);
            objetoFornecimentoContrato.setQuantidadeQuesitosOpcionaisPreenchidosBeneficiario(quantidadeQuesitosPreenchidosOpcional);
        }

        // define situacaoPreecnhimento pelo perfil
        if (EnumPerfilEntidade.FORNECEDOR.getValor().equals(perfil.getValor())) {
            EnumSituacaoPreenchimentoItemFormatacaoFornecedor situacaoPreenchimento = null;

            if (quantidadeQuesitosObrigatorios == 0) {
                situacaoPreenchimento = EnumSituacaoPreenchimentoItemFormatacaoFornecedor.PREENCHIDO;
            } else if (quantidadeQuesitosObrigatorios > quantidadeQuesitosPreenchidosObrigatorio) {
                situacaoPreenchimento = EnumSituacaoPreenchimentoItemFormatacaoFornecedor.PREENCHIDO_INCOMPLETO;
            } else if (quantidadeQuesitosObrigatorios == quantidadeQuesitosPreenchidosObrigatorio) {
                situacaoPreenchimento = EnumSituacaoPreenchimentoItemFormatacaoFornecedor.PREENCHIDO;
            }

            objetoFornecimentoContrato.setSituacaoPreenchimentoFornecedor(situacaoPreenchimento);

        } else if (EnumPerfilEntidade.BENEFICIARIO.getValor().equals(perfil.getValor())) {

            EnumSituacaoPreenchimentoItemFormatacaoBeneficiario situacaoPreenchimento = null;

            if (quantidadeQuesitosObrigatorios == 0) {
                situacaoPreenchimento = EnumSituacaoPreenchimentoItemFormatacaoBeneficiario.PREENCHIDO;
            } else if (quantidadeQuesitosObrigatorios > quantidadeQuesitosPreenchidosObrigatorio) {
                situacaoPreenchimento = EnumSituacaoPreenchimentoItemFormatacaoBeneficiario.PREENCHIDO_INCOMPLETO;
            } else if (quantidadeQuesitosObrigatorios == quantidadeQuesitosPreenchidosObrigatorio) {
                situacaoPreenchimento = EnumSituacaoPreenchimentoItemFormatacaoBeneficiario.PREENCHIDO;
            }
            objetoFornecimentoContrato.setSituacaoPreenchimentoBeneficiario(situacaoPreenchimento);
        }
    }

    public ObjetoFornecimentoContrato incluirAlterarConformidadeItem(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        ObjetoFornecimentoContrato objetoFornecimentoContratoParaMerge = em.find(ObjetoFornecimentoContrato.class, objetoFornecimentoContrato.getId());

        objetoFornecimentoContratoParaMerge.setEstadoDeNovo(objetoFornecimentoContrato.getEstadoDeNovo());
        objetoFornecimentoContratoParaMerge.setFuncionandoDeAcordo(objetoFornecimentoContrato.getFuncionandoDeAcordo());
        objetoFornecimentoContratoParaMerge.setConfiguradoDeAcordo(objetoFornecimentoContrato.getConfiguradoDeAcordo());
        objetoFornecimentoContratoParaMerge.setDescricaoNaoConfiguradoDeAcordo(objetoFornecimentoContrato.getDescricaoNaoConfiguradoDeAcordo());
        objetoFornecimentoContratoParaMerge.setDescricaoNaoFuncionandoDeAcordo(objetoFornecimentoContrato.getDescricaoNaoFuncionandoDeAcordo());

        EnumStatusOrdemFornecimento statusOf = objetoFornecimentoContratoParaMerge.getOrdemFornecimento().getStatusOrdemFornecimento();
        boolean ofExecutada = statusOf == EnumStatusOrdemFornecimento.EXECUTADA;
        
        definirSituacaoObjetoFornecimentoContrato(objetoFornecimentoContratoParaMerge,ofExecutada);
        em.merge(objetoFornecimentoContratoParaMerge);
        return objetoFornecimentoContratoParaMerge;
    }

    // METODOS PRIVATE
    
    private boolean verificarConformidadesDoCampoDeTexto(FormatacaoObjetoFornecimento beneficiario, FormatacaoObjetoFornecimento fornecedor,boolean conformidade,StringBuilder stringNaoConformidade,StringBuilder stringNaoConformidadeBeneficiario,
            StringBuilder stringNaoConformidadeFornecedor, StringBuilder conformidadeBeneficiario, StringBuilder conformidadeFornecedor,ObjetoFornecimentoContrato ofc,
            NotaRemessaOrdemFornecimentoContrato notaRemessaAtual, boolean ofExecutada){
        
        boolean dadosDigitados = true;
        boolean beneficiarioOpcional = beneficiario.getFormatacao().getPossuiInformacaoOpcional();
        boolean fornecedorOpcional = fornecedor.getFormatacao().getPossuiInformacaoOpcional();
        boolean identificadorUnico = fornecedor.getFormatacao().getPossuiIdentificadorUnico();
        EnumTipoCampoFormatacao tipoFormatacao = fornecedor.getFormatacao().getTipoCampo();
        
        if(beneficiarioOpcional && fornecedorOpcional){
            return conformidade;
        }
        
        if(beneficiario.getFormatacaoResposta() == null && ofExecutada){
            conformidade = false;
            dadosDigitados = false;
            stringNaoConformidade.append("<li>O Beneficiário não digitou dados no campo "+beneficiario.getFormatacao().getTipoCampo().getDescricao()+".</li>");
            stringNaoConformidadeBeneficiario.append("<li>O Beneficiário não digitou dados no campo "+beneficiario.getFormatacao().getTipoCampo().getDescricao()+".</li>");
            conformidadeBeneficiario.append(1);
        }
        
        if(fornecedor.getFormatacaoResposta() == null){
            conformidade = false;
            dadosDigitados = false;
            stringNaoConformidade.append("<li>O Fornecedor não digitou dados no campo "+fornecedor.getFormatacao().getTipoCampo().getDescricao()+".</li>");
            stringNaoConformidadeFornecedor.append("<li>O Fornecedor não digitou dados no campo "+beneficiario.getFormatacao().getTipoCampo().getDescricao()+".</li>");
            conformidadeFornecedor.append(1);
        }
        
        //Somente irá entrar se o beneficiário e fornecedor digitaram os dados e a O.F. foi executada.
        if(dadosDigitados && ofExecutada){
            if(tipoFormatacao == EnumTipoCampoFormatacao.ALFANUMERICO || tipoFormatacao == EnumTipoCampoFormatacao.NUMERICO && identificadorUnico){
                if(!beneficiario.getFormatacaoResposta().getRespostaAlfanumerico().equalsIgnoreCase(fornecedor.getFormatacaoResposta().getRespostaAlfanumerico())){
                    conformidade = false;
                    stringNaoConformidade.append("<li>Os dados do campo alfanumérico do beneficiário e do fornecedor não são iguais.</li>");
                    stringNaoConformidadeBeneficiario.append("<li>Os dados do campo alfanumérico do beneficiário e do fornecedor não são iguais.</li>");
                    stringNaoConformidadeFornecedor.append("<li>Os dados do campo alfanumérico do beneficiário e do fornecedor não são iguais.</li>");
                    conformidadeBeneficiario.append(1);
                    conformidadeFornecedor.append(1);
                }
            }
            
            if(tipoFormatacao == EnumTipoCampoFormatacao.TEXTO && identificadorUnico){
                if(!beneficiario.getFormatacaoResposta().getRespostaTexto().equalsIgnoreCase(fornecedor.getFormatacaoResposta().getRespostaTexto())){
                    conformidade = false;
                    stringNaoConformidade.append("<li>Os dados do campo de texto do beneficiário e do fornecedor não são iguais.</li>");
                    stringNaoConformidadeBeneficiario.append("<li>Os dados do campo de texto do beneficiário e do fornecedor não são iguais.</li>");
                    stringNaoConformidadeFornecedor.append("<li>Os dados do campo de texto do beneficiário e do fornecedor não são iguais.</li>");
                    conformidadeBeneficiario.append(1);
                    conformidadeFornecedor.append(1);
                }
            }
        }
        
        return conformidade;
    }
    
    //Verifica somente as conformidades das fotos que são para Ambos
    private boolean verificarConformidadesDaFoto(FormatacaoObjetoFornecimento beneficiario, FormatacaoObjetoFornecimento fornecedor,boolean conformidade,StringBuilder stringNaoConformidade, StringBuilder stringNaoConformidadeBeneficiario,
            StringBuilder stringNaoConformidadeFornecedor, StringBuilder conformidadeBeneficiario,StringBuilder conformidadeFornecedor,ObjetoFornecimentoContrato ofc,
            NotaRemessaOrdemFornecimentoContrato notaRemessaAtual, boolean ofExecutada){
        
        boolean fotoAnexada = true;
        boolean beneficiarioOpcional = beneficiario.getFormatacao().getPossuiInformacaoOpcional();
        boolean fornecedorOpcional = fornecedor.getFormatacao().getPossuiInformacaoOpcional();
        boolean possuiIdentificadorUnico = beneficiario.getFormatacao().getPossuiIdentificadorUnico();
        
        if(beneficiarioOpcional && fornecedorOpcional){
            return conformidade;
        }
        
        if(beneficiario.getFormatacaoResposta() == null && ofExecutada){
            conformidade = false;
            fotoAnexada = false;
            stringNaoConformidadeBeneficiario.append("<li>O Beneficiário não anexou a foto.</li>");
            conformidadeBeneficiario.append(1);
        }
        
        if(fornecedor.getFormatacaoResposta() == null){
            conformidade = false;
            fotoAnexada = false;
            stringNaoConformidadeFornecedor.append("<li>O Fornecedor não anexou a foto.</li>");
            conformidadeFornecedor.append(1);
        }
        
      //Somente irá comparar as fotos se o beneficiário e o fornecedor anexarem uma foto e a O.F. já estiver sido comunicada
        if(fotoAnexada && ofExecutada){
            
            //Se os campos forem obrigatórios será necessário verificar as conformidades.
            if(!beneficiarioOpcional && !fornecedorOpcional){
              //Irá verificar se os dados de georreferenciamento das duas fotos são iguais
                
                if(fornecedor.getFormatacaoResposta().getLatitudeLongitudeFoto() != null && beneficiario.getFormatacaoResposta().getLatitudeLongitudeFoto() != null){
                   
                    if(fornecedor.getFormatacaoResposta().getLatitudeLongitudeFoto().contains("undefined") ||
                            beneficiario.getFormatacaoResposta().getLatitudeLongitudeFoto().contains("undefined")){
                        
                    }else{
                        if(fornecedor.getFormatacaoResposta().getLatitudeLongitudeFoto().equalsIgnoreCase(beneficiario.getFormatacaoResposta().getLatitudeLongitudeFoto())){
                            conformidade = false;
                            //stringNaoConformidade.append("<li>Os dados de georreferenciamento das fotos do fornecedor e do beneficiário são iguais.</li>");
                            stringNaoConformidadeBeneficiario.append("<li>Os dados de georreferenciamento da foto do fornecedor e do beneficiário são iguais.</li>");
                            stringNaoConformidadeFornecedor.append("<li>Os dados de georreferenciamento da foto do fornecedor e do beneficiário são iguais.</li>");
                            conformidadeBeneficiario.append(1);
                            conformidadeFornecedor.append(1);
                        }
                    }
                }
                
                
                if(fornecedor.getFormatacaoResposta().getDataFoto() != null && beneficiario.getFormatacaoResposta().getDataFoto() != null){
                  //Irá verificar se a data das fotos são iguais.
                    if(fornecedor.getFormatacaoResposta().getDataFoto().isEqual(beneficiario.getFormatacaoResposta().getDataFoto())){
                        conformidade = false;
                        //stringNaoConformidade.append("<li>As datas das fotos do fornecedor e do beneficiário são iguais.</li>");
                        stringNaoConformidadeBeneficiario.append("<li>As datas das fotos do fornecedor e do beneficiário são iguais.</li>");
                        stringNaoConformidadeFornecedor.append("<li>As datas das fotos do fornecedor e do beneficiário são iguais.</li>");
                        conformidadeBeneficiario.append(1);
                        conformidadeFornecedor.append(1);
                    }
                }                
                
                
                // Irá verificar se a foto do beneficiário foi tirada antes da data efetiva da entrega.
                if(notaRemessaAtual.getDataEfetivaEntrega() != null){
                        
                        LocalDate dataEntrega = notaRemessaAtual.getDataEfetivaEntrega()!=null?notaRemessaAtual.getDataEfetivaEntrega():null;
                        LocalDateTime dataResposta = beneficiario.getFormatacaoResposta().getDataFoto() != null?beneficiario.getFormatacaoResposta().getDataFoto():null;
                        
                        if(dataEntrega != null && dataResposta != null){
                            LocalDateTime data = LocalDateTime.of(dataEntrega.getYear(),dataEntrega.getMonth(),dataEntrega.getDayOfMonth(),0,0,0);
                            if(data.isAfter(dataResposta)){
                                conformidade = false;
                                //stringNaoConformidade.append("<li>A foto do beneficiário foi tirada antes da data efetiva da entrega.</li>");
                                stringNaoConformidadeBeneficiario.append("<li>A foto do beneficiário foi tirada antes da data efetiva da entrega.</li>");
                                conformidadeBeneficiario.append(1);
                        }
                        
                    }
                }
            }
        }
        
        
        //Irá verificar se a data da foto do fornecedor é anterior a comunicação da O.F.
        if(fornecedor.getFormatacaoResposta() != null && fornecedor.getResponsavelFormatacao() == EnumPerfilEntidade.FORNECEDOR){
            
            LocalDateTime dataResposta = fornecedor.getFormatacaoResposta().getDataFoto() != null?fornecedor.getFormatacaoResposta().getDataFoto():null;
            LocalDateTime dataComunicacao = ofc.getOrdemFornecimento().getDataComunicacao() != null?ofc.getOrdemFornecimento().getDataComunicacao():null;
            
            if(dataResposta != null){
                if(dataResposta != null && dataComunicacao != null){
                    if(dataResposta.isBefore(dataComunicacao)){
                        conformidade = false;
                        stringNaoConformidade.append("<li>A data da foto do fornecedor é anterior a comunicação da Ordem de Fornecimento.</li>");
                        stringNaoConformidadeFornecedor.append("<li>A data da foto do fornecedor é anterior a comunicação da Ordem de Fornecimento.</li>");
                        conformidadeFornecedor.append(1);
                    }
                }else{
                    conformidade = false;
                    stringNaoConformidade.append("<li>O fornecedor não anexou a foto.</li>");
                    stringNaoConformidadeFornecedor.append("<li>O fornecedor não anexou a foto.</li>");
                    conformidadeFornecedor.append(1);
                    
                }
            }
        }
        
        
        boolean identificadorDigitado = true;
        if(possuiIdentificadorUnico){
            
            String identificadorBeneficiario = null;
            String identificadorFornecedor = null;
            if(ofExecutada && beneficiario.getFormatacaoResposta() != null && beneficiario.getFormatacaoResposta().getRespostaAlfanumerico() != null){
                identificadorBeneficiario = beneficiario.getFormatacaoResposta().getRespostaAlfanumerico();
            }
            
            if(fornecedor.getFormatacaoResposta() != null && fornecedor.getFormatacaoResposta().getRespostaAlfanumerico() != null){
                identificadorFornecedor = fornecedor.getFormatacaoResposta().getRespostaAlfanumerico();
            }
            
            if(identificadorBeneficiario == null && ofExecutada){
                conformidade = false;
                stringNaoConformidade.append("<li>O beneficiário não informou o dado da identificação única.</li>");
                stringNaoConformidadeBeneficiario.append("<li>O beneficiário não informou o dado da identificação única.</li>");
                identificadorDigitado = false;
                conformidadeBeneficiario.append(1);
            }
            
            if(identificadorFornecedor == null){
                conformidade = false;
                stringNaoConformidade.append("<li>O fornecedor não informou o dado da identificação única.</li>");
                stringNaoConformidadeFornecedor.append("<li>O fornecedor não informou o dado da identificação única.</li>");
                identificadorDigitado = false;
                conformidadeFornecedor.append(1);
            }
            
            if(identificadorDigitado && ofExecutada){
                if(!identificadorBeneficiario.equalsIgnoreCase(identificadorFornecedor)){
                    conformidade = false;
                    stringNaoConformidade.append("<li>Os dados de identificação única das fotos não são iguais.</li>");
                    stringNaoConformidadeBeneficiario.append("<li>Os dados de identificação única das fotos não são iguais.</li>");
                    stringNaoConformidadeFornecedor.append("<li>Os dados de identificação única das fotos não são iguais.</li>");
                    conformidadeBeneficiario.append(1);
                    conformidadeFornecedor.append(1);
                }
            }
        }
        return conformidade;
    }
    
    public void atualizarStatusItemContratoRespostaPeloId(EnumStatusFormatacao status, String motivoNaoConformidade,Long id){
        String sqlAtualizar = "UPDATE side.tb_fir_formatacao_itens_contrato_resposta " + 
                " SET fir_st_status_formatacao=:status, " +
                " fir_ds_motivo_nao_conformidade=:motivoNaoConformidade "+
                " WHERE fir_id_formatacao_itens_contrato_resposta=:idFir";
        Query query = em.createNativeQuery(sqlAtualizar);
        query.setParameter("status", status.getValor());
        query.setParameter("motivoNaoConformidade", motivoNaoConformidade);
        query.setParameter("idFir", id);
        query.executeUpdate();
    }
    
    //Atualiza somente o status do bem: Entregue, Recebido, Doado
    public void atualizarSituacaoDoItemObjetoFornecimentoContratoPeloId(EnumSituacaoBem situacao, Long idObjetoFornecimentoContrato){
        
        String sqlAtualizar = " UPDATE side.tb_ofo_objeto_fornecimento_contrato " + 
                " SET ofo_st_situacao_bem=:status " + 
                " WHERE ofo_id_objeto_fornecimento_contrato=:idFir";
        Query query = em.createNativeQuery(sqlAtualizar);
        query.setParameter("status", situacao.getValor());
        query.setParameter("idFir", idObjetoFornecimentoContrato);
        query.executeUpdate();
    }
    
}
