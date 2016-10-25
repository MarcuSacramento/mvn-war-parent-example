package br.gov.mj.side.web.service.ldap;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.apoio.entidades.enums.EnumParametrosSistema;
import br.gov.mj.side.web.dto.UsuarioLDAPDto;
import br.gov.mj.side.web.service.ParametroService;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class LDAPService {

    @Inject
    private ParametroService parametroService;

    private static String LDAP_KEY = "sAMAccountName";
    private static String[] LDAP_ATTRIBUTES = { "sAMAccountName", "cn", "givenName", "mail", "CPF", "physicalDeliveryOfficeName", "telephoneNumber" };
    private static String LDAP_AUTHENTICATION_MODE = "simple";
    private static String LDAP_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

    public UsuarioLDAPDto buscarCredencial(String identificadorUsuario, String senha) {
        return autenticar(identificadorUsuario, senha, parametroService.buscarValorPorChaveSigla(EnumParametrosSistema.LDAP_HOST.getValor()), parametroService.buscarValorPorChaveSigla(EnumParametrosSistema.LDAP_DOMINIO.getValor()), parametroService.buscarValorPorChaveSigla(EnumParametrosSistema.LDAP_CONTEXTO.getValor()));
    }

    private UsuarioLDAPDto autenticar(String identificadorUsuario, String senha, String host, String dominio, String context) {

        if (StringUtils.isBlank(identificadorUsuario) || StringUtils.isBlank(senha) || StringUtils.isBlank(host) || StringUtils.isBlank(dominio) || StringUtils.isBlank(context)) {
            throw new IllegalArgumentException("Todos os parametros do método são obrigatórios.");
        }

        HashMap<String, Object> attributeValues = new HashMap<String, Object>();

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_FACTORY);
        env.put(Context.PROVIDER_URL, host); // para SSL mude a porta
        env.put(Context.SECURITY_AUTHENTICATION, LDAP_AUTHENTICATION_MODE);
        env.put(Context.SECURITY_PRINCIPAL, identificadorUsuario + "@" + dominio);
        env.put(Context.SECURITY_CREDENTIALS, senha);

        try {

            DirContext dirCtx = getInitialDirContext(env);

            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            sc.setReturningAttributes(LDAP_ATTRIBUTES);

            NamingEnumeration<SearchResult> listData = dirCtx.search(context, LDAP_KEY + "=" + identificadorUsuario, sc);
            if (listData.hasMore()) {
                SearchResult result = listData.next();
                BasicAttributes attributes = (BasicAttributes) result.getAttributes();
                listData.close();

                NamingEnumeration<Attribute> listAttributeValues = attributes.getAll();
                while (listAttributeValues.hasMore()) {
                    BasicAttribute attribute = (BasicAttribute) listAttributeValues.next();
                    String key = attribute.getID();
                    Object value = attribute.get(0);
                    attributeValues.put(key, value);
                }

            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return retornaDto(attributeValues);
    }

    protected DirContext getInitialDirContext(Hashtable<String, String> env) throws NamingException {
        return new InitialDirContext(env);
    }

    private UsuarioLDAPDto retornaDto(Map<String, Object> attributeValues) {

        UsuarioLDAPDto usuarioLDAPDto = null;

        if (attributeValues.size() > 0) {

            usuarioLDAPDto = new UsuarioLDAPDto(
                    attributeValues.get("mail") == null ? null : attributeValues.get("mail").toString(), 
                    attributeValues.get("sAMAccountName") == null ? null : attributeValues.get("sAMAccountName").toString(), 
                    attributeValues.get("givenName") == null ? null : attributeValues.get("givenName").toString(), 
                    attributeValues.get("CPF") == null ? null : attributeValues.get("CPF").toString(), 
                    attributeValues.get("cn") == null ? null : attributeValues.get("cn").toString(), 
                    attributeValues.get("physicalDeliveryOfficeName") == null ? null : attributeValues.get("physicalDeliveryOfficeName").toString(),
                    attributeValues.get("telephoneNumber") == null ? null : attributeValues.get("telephoneNumber").toString());

        }

        return usuarioLDAPDto;
    }

}
