package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class FornecedorService {

    @Inject
    private GenericEntidadeService genericEntidadeService;

    // FORNECEDOR

    public List<Entidade> buscarFornecedor(EntidadePesquisaDto entidadePesquisaDto) {
        entidadePesquisaDto.setTipoPerfil(EnumPerfilEntidade.FORNECEDOR);
        BusinessException ex = new BusinessException();

        if (entidadePesquisaDto.getUsuarioLogado() == null || entidadePesquisaDto.getUsuarioLogado().getId() == null) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        return genericEntidadeService.buscar(entidadePesquisaDto);
    }

    public List<Entidade> buscarSemPaginacao(EntidadePesquisaDto entidadePesquisaDto) {
        entidadePesquisaDto.setTipoPerfil(EnumPerfilEntidade.FORNECEDOR);
        return genericEntidadeService.buscarSemPaginacao(entidadePesquisaDto);
    }

    public List<Entidade> buscarPaginado(EntidadePesquisaDto entidadePesquisaDto, int first, int size, EnumOrder order, String propertyOrder) {
        entidadePesquisaDto.setTipoPerfil(EnumPerfilEntidade.FORNECEDOR);
        return genericEntidadeService.buscarPaginado(entidadePesquisaDto, first, size, order, propertyOrder);
    }

    public Entidade incluirAlterar(Entidade entidade, String usuarioLogado) {
        entidade.setPerfilEntidade(EnumPerfilEntidade.FORNECEDOR);
        return genericEntidadeService.incluirAlterar(entidade, usuarioLogado, false);
    }

}
