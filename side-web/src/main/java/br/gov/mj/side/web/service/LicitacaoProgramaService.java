package br.gov.mj.side.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.KitBem;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusLicitacao;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoLocalEntrega;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoLocalEntregaBem;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoLocalEntregaKit;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.BemUf;
import br.gov.mj.side.entidades.programa.licitacao.LicitacaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.SelecaoItem;
import br.gov.mj.side.web.dao.LicitacaoProgramaDAO;
import br.gov.mj.side.web.dto.PesquisaLicitacaoDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.AgrupamentoDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.BemUfLicitacaoDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.EnderecoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class LicitacaoProgramaService {

    @Inject
    private IGenericPersister genericPersister;

    @Inject
    private LicitacaoProgramaDAO licitacaoProgramaDAO;

    @Inject
    private InscricaoProgramaService inscricaoProgramaService;

    public List<AgrupamentoLicitacao> buscarAgrupamentoLicitacao(LicitacaoPrograma licitacaoPrograma) {
        if (licitacaoPrograma == null || licitacaoPrograma.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return licitacaoProgramaDAO.buscarAgrupamentoLicitacao(licitacaoPrograma.getId());
    }

    public List<SelecaoItem> buscarSelecaoItem(AgrupamentoLicitacao agrupamentoLicitacao) {
        if (agrupamentoLicitacao == null || agrupamentoLicitacao.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return licitacaoProgramaDAO.buscarSelecaoItem(agrupamentoLicitacao.getId());
    }

    public List<BemUf> buscarBemUf(SelecaoItem selecaoItem) {
        if (selecaoItem == null || selecaoItem.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return licitacaoProgramaDAO.buscarBemUf(selecaoItem.getId());
    }

    public List<LicitacaoPrograma> buscarPaginado(PesquisaLicitacaoDto pesquisaLicitacaoDto, int first, int size, EnumOrder order, String propertyOrder) {

        if (pesquisaLicitacaoDto == null) {
            throw new IllegalArgumentException("Parâmetro pesquisaLicitacaoDto não pode ser null");
        }

        return licitacaoProgramaDAO.buscarPaginado(pesquisaLicitacaoDto, first, size, order, propertyOrder);
    }

    public List<LicitacaoPrograma> buscarSemPaginacao(PesquisaLicitacaoDto pesquisaLicitacaoDto) {

        if (pesquisaLicitacaoDto == null) {
            throw new IllegalArgumentException("Parâmetro pesquisaLicitacaoDto não pode ser null");
        }

        return licitacaoProgramaDAO.buscarSemPaginacao(pesquisaLicitacaoDto);
    }

    public List<LicitacaoPrograma> buscarSemPaginacaoOrdenado(PesquisaLicitacaoDto pesquisaLicitacaoDto, EnumOrder order, String propertyOrder) {

        if (pesquisaLicitacaoDto == null) {
            throw new IllegalArgumentException("Parâmetro pesquisaLicitacaoDto não pode ser null");
        }

        return licitacaoProgramaDAO.buscarSemPaginacaoOrdenado(pesquisaLicitacaoDto, order, propertyOrder);
    }

    public LicitacaoPrograma buscarPeloId(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe Inscricao com esse id cadastrado */

        LicitacaoPrograma l = licitacaoProgramaDAO.buscarPeloId(id);

        if (l == null) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("MN047", id);
            throw ex;
        }
        return l;
    }

    public LicitacaoPrograma incluirAlterar(LicitacaoPrograma licitacaoPrograma, String usuarioLogado) {

        LicitacaoPrograma licitacaoProgramaRetorno = null;

        validarObjetoNulo(licitacaoPrograma);

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório.");
            throw ex;
        }

        if (licitacaoPrograma.getPrograma() == null || licitacaoPrograma.getPrograma().getId() == null) {
            ex.addErrorMessage("Escolha do Programa Obrigatória.");
            throw ex;
        }

        licitacaoPrograma.setStatusLicitacao(EnumStatusLicitacao.EM_ELABORACAO);

        if (licitacaoPrograma.getId() == null) {
            licitacaoProgramaRetorno = licitacaoProgramaDAO.incluir(licitacaoPrograma, usuarioLogado);
        } else {
            licitacaoProgramaRetorno = licitacaoProgramaDAO.alterar(licitacaoPrograma, usuarioLogado);
        }
        return licitacaoProgramaRetorno;
    }

    /*
     * Caso precise alterar alguma informação da Licitação onde não seja
     * necessário Atualizar as listas.
     */
    public LicitacaoPrograma atualizarInformacoesBasicas(LicitacaoPrograma licitacaoPrograma, String usuarioLogado) {

        validarObjetoNulo(licitacaoPrograma);

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório.");
            throw ex;
        }

        if (licitacaoPrograma.getPrograma() == null || licitacaoPrograma.getPrograma().getId() == null) {
            ex.addErrorMessage("Escolha do Programa Obrigatória.");
            throw ex;
        }

        return licitacaoProgramaDAO.atualizarInformacoesBasicas(licitacaoPrograma, usuarioLogado);
    }

    public void excluir(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe Licitacao com esse id cadastrado */

        LicitacaoPrograma l = genericPersister.findByUniqueProperty(LicitacaoPrograma.class, "id", id);
        if (l == null) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("MN047", id);
            throw ex;
        }

        licitacaoProgramaDAO.excluir(l);

    }

    private void validarObjetoNulo(LicitacaoPrograma licitacaoPrograma) {
        if (licitacaoPrograma == null) {
            throw new IllegalArgumentException("Parâmetro programa não pode ser null");
        }
    }

    public List<AgrupamentoDto> buscarEnderecosDaLicitacao(LicitacaoPrograma licitacaoPrograma) {

        List<BemUfLicitacaoDto> lista = buscarListaEnderecosDesagrupado(licitacaoPrograma);
        Map<Long, AgrupamentoDto> mapa = new HashMap<Long, AgrupamentoDto>();

        for (BemUfLicitacaoDto bemUfLicitacaoDto : lista) {
            Long chave = bemUfLicitacaoDto.getAgrupamento().getId();

            if (mapa.get(chave) != null) {
                mapa.get(chave).getBensUfs().add(bemUfLicitacaoDto);

            } else {
                AgrupamentoDto dto = new AgrupamentoDto();
                dto.setId(bemUfLicitacaoDto.getAgrupamento().getId());
                dto.setNome(bemUfLicitacaoDto.getAgrupamento().getNomeAgrupamento());
                dto.setTipoAgrupamentoLicitacao(bemUfLicitacaoDto.getAgrupamento().getTipoAgrupamentoLicitacao());
                dto.getBensUfs().add(bemUfLicitacaoDto);
                mapa.put(chave, dto);
            }
        }

        return transformaMapEmListaAgrupamentoDto(mapa);

    }

    private List<BemUfLicitacaoDto> buscarListaEnderecosDesagrupado(LicitacaoPrograma licitacaoPrograma) {
        LicitacaoPrograma licitacaoProgramaRetorno = buscarPeloId(licitacaoPrograma.getId());
        Map<String, BemUfLicitacaoDto> mapa = buscarMapaEnderecosPorBemUf(licitacaoProgramaRetorno.getPrograma());

        for (AgrupamentoLicitacao agrupamentoLicitacao : licitacaoProgramaRetorno.getListaAgrupamentoLicitacao()) {
            for (SelecaoItem selecaoItem : agrupamentoLicitacao.getListaSelecaoItem()) {
                for (BemUf bemUf : selecaoItem.getListaBemUf()) {

                    String chaveIdBem = bemUf.getBem().getId().toString();
                    String chaveIdUf = bemUf.getUf().getSiglaUf();
                    String chave = chaveIdBem + "-" + chaveIdUf;

                    if (mapa.get(chave) != null) {
                        mapa.get(chave).setAgrupamento(agrupamentoLicitacao);
                    }

                }
            }
        }

        return transformaMapEmListaBemUfLicitacaoDto(mapa);
    }

    public List<BemUfLicitacaoDto> transformaMapEmListaBemUfLicitacaoDto(Map<String, BemUfLicitacaoDto> mapa) {
        List<BemUfLicitacaoDto> lista = new ArrayList<BemUfLicitacaoDto>();
        lista.addAll(mapa.values());
        return lista;
    }

    public Map<String, BemUfLicitacaoDto> buscarMapaEnderecosPorBemUf(Programa programa) {

        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        List<InscricaoPrograma> listaClassificados = inscricaoProgramaService.buscarListaClassificados(programa);
        Map<String, BemUfLicitacaoDto> mapa = new HashMap<String, BemUfLicitacaoDto>();

        for (InscricaoPrograma inscricaoPrograma : listaClassificados) {
            for (InscricaoLocalEntrega inscricaoLocalEntrega : inscricaoPrograma.getLocaisEntregaInscricao()) {

                for (InscricaoLocalEntregaBem inscricaoLocalEntregaBem : inscricaoLocalEntrega.getBensEntrega()) {
                    populaMap(mapa, inscricaoLocalEntregaBem.getInscricaoProgramaBem().getProgramaBem().getBem(), Long.parseLong(inscricaoLocalEntregaBem.getQuantidade().toString()), inscricaoLocalEntrega.getLocalEntregaEntidade());
                }
                for (InscricaoLocalEntregaKit inscricaoLocalEntregaKit : inscricaoLocalEntrega.getKitsEntrega()) {
                    for (KitBem kitBem : inscricaoLocalEntregaKit.getInscricaoProgramaKit().getProgramaKit().getKit().getKitsBens()) {
                        Long quantidade = Long.parseLong(inscricaoLocalEntregaKit.getQuantidade().toString()) * Long.parseLong(kitBem.getQuantidade().toString());
                        populaMap(mapa, kitBem.getBem(), quantidade, inscricaoLocalEntrega.getLocalEntregaEntidade());
                    }

                }

            }
        }
        return mapa;
    }

    private void populaMap(Map<String, BemUfLicitacaoDto> mapa, Bem bem, Long quantidade, LocalEntregaEntidade localEntregaEntidade) {

        String chaveIdBem = bem.getId().toString();
        String chaveIdUf = localEntregaEntidade.getMunicipio().getUf().getSiglaUf();
        String chave = chaveIdBem + "-" + chaveIdUf;

        EnderecoDto endereco = new EnderecoDto();
        endereco.setLocalEntregaEntidade(localEntregaEntidade);
        endereco.setQuantidade(quantidade);

        if (mapa.get(chave) != null) {
            Long qtd = mapa.get(chave).getQuantidade() + quantidade;
            mapa.get(chave).setQuantidade(qtd);
            mapa.get(chave).getEndereco().add(endereco);
        } else {
            BemUfLicitacaoDto dto = new BemUfLicitacaoDto();
            dto.setBem(bem);
            dto.setUf(localEntregaEntidade.getMunicipio().getUf());
            dto.getEndereco().add(endereco);
            dto.setQuantidade(quantidade);
            mapa.put(chave, dto);
        }
    }

    public List<AgrupamentoDto> transformaMapEmListaAgrupamentoDto(Map<Long, AgrupamentoDto> mapa) {
        List<AgrupamentoDto> lista = new ArrayList<AgrupamentoDto>();
        lista.addAll(mapa.values());

        List<AgrupamentoDto> listaRetorno = new ArrayList<AgrupamentoDto>();
        for (AgrupamentoDto agrupamentoDto : lista) {
            List<BemUfLicitacaoDto> listaBemUfLicitacaoDto = new ArrayList<BemUfLicitacaoDto>();
            for (BemUfLicitacaoDto bemUfLicitacaoDto : agrupamentoDto.getBensUfs()) {
                bemUfLicitacaoDto.setEndereco(agrupaEnderecosIguais(bemUfLicitacaoDto.getEndereco()));
                listaBemUfLicitacaoDto.add(bemUfLicitacaoDto);
            }
            agrupamentoDto.setBensUfs(listaBemUfLicitacaoDto);
            listaRetorno.add(agrupamentoDto);
        }
        return listaRetorno;
    }

    public List<EnderecoDto> agrupaEnderecosIguais(List<EnderecoDto> lista) {
        Map<Long, EnderecoDto> mapa = new HashMap<Long, EnderecoDto>();

        for (EnderecoDto enderecoLista : lista) {

            Long chave = enderecoLista.getLocalEntregaEntidade().getId();
            if (mapa.get(chave) != null) {
                Long qtd = mapa.get(chave).getQuantidade() + enderecoLista.getQuantidade();
                mapa.get(chave).setQuantidade(qtd);
            } else {
                EnderecoDto endereco = new EnderecoDto();
                endereco.setLocalEntregaEntidade(enderecoLista.getLocalEntregaEntidade());
                endereco.setQuantidade(enderecoLista.getQuantidade());
                mapa.put(chave, endereco);
            }
        }
        return transformaMapEmListaEnderecoDto(mapa);
    }

    public List<EnderecoDto> transformaMapEmListaEnderecoDto(Map<Long, EnderecoDto> mapa) {
        List<EnderecoDto> lista = new ArrayList<EnderecoDto>();
        lista.addAll(mapa.values());
        return lista;
    }

}
