package fon.master.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.util.Assert;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.*;
import java.util.function.Function;

@Slf4j
public class CustomLdapAuthoritiesPopular extends DefaultLdapAuthoritiesPopulator {
    private static final Log logger = LogFactory.getLog(CustomLdapAuthoritiesPopular.class);

    private SpringSecurityLdapTemplate ldapTemplate;
    private String groupRoleAttribute = "cn";
    private String groupSearchBase;
    private String groupSearchFilter = "(member={0})";
    private String rolePrefix = "";// "ROLE_";
    private boolean convertToUpperCase = true;
    private Function<Map<String, List<String>>, GrantedAuthority> authorityMapper;

    private final SearchControls searchControls = new SearchControls();

    public CustomLdapAuthoritiesPopular(ContextSource contextSource, String groupSearchBase) {
        super(contextSource, groupSearchBase);
        Assert.notNull(contextSource, "contextSource must not be null");
        this.ldapTemplate = new SpringSecurityLdapTemplate(contextSource);
        this.getLdapTemplate().setSearchControls(this.getSearchControls());
        this.groupSearchBase = groupSearchBase;
        this.setRolePrefix("");
        if (groupSearchBase == null) {
            logger.info("groupSearchBase is null. No group search will be performed.");
        } else if (groupSearchBase.length() == 0) {
            logger.info("groupSearchBase is empty. Searches will be performed from the context source base");
        }

        this.authorityMapper = (record) -> {
            String role = (String)((List)record.get(this.groupRoleAttribute)).get(0);
            if (this.convertToUpperCase) {
                role = role.toUpperCase();
            }

            return new SimpleGrantedAuthority(this.rolePrefix + role);
        };
    }

    private SearchControls getSearchControls() {
        return this.searchControls;
    }

    protected Set<GrantedAuthority> getAdditionalRoles(DirContextOperations user, String username) {
        Set<GrantedAuthority> additionalRoles = new HashSet<>();

        String userDn = "uid="+username+",ou=users,dc=master,dc=fon";
        String filter = "(member={0})";

        String[] attributes = { "cn", "businessCategory" };
        logger.info("PERFORMING GROUP SEARCH");
        logger.info("user DN : "+userDn);
        logger.info("user DN  user.getNameInNamespace(): "+user.getNameInNamespace());
        try {
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            searchControls.setReturningAttributes(attributes);

            NamingEnumeration<SearchResult> result = getContextSource().getReadOnlyContext().search(
                    "ou=groups",
                    "(&(objectClass=groupOfUniqueNames)(uniqueMember=UID="+username+",OU=users,DC=master,DC=fon))",
                    searchControls);

            while(result.hasMore()) {
                SearchResult searchResult = (SearchResult) result.next();
                Attributes attrs = searchResult.getAttributes();
                String groupsCN = attrs.get("cn").toString().split(":")[1].trim();

                logger.info("group cn "+attrs.get("cn"));
                logger.info("group cn "+attrs.get("businessCategory"));

                additionalRoles.add(new SimpleGrantedAuthority("ROLE_" + groupsCN));

                if(attrs.get("businessCategory") != null ) {
                    String authorities[] = attrs.get("businessCategory").toString().split(":")[1].split(", ");

                    Arrays.stream(authorities).forEach(a -> logger.info("LDAP authorities: "+a));

                    Arrays.stream(authorities).forEach(a -> additionalRoles.add(new SimpleGrantedAuthority(a.trim())));
                }
            }
            logger.info("LDAP AFTER LOADING AUTHORITIES: "+additionalRoles);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        logger.info("fetched authorities based on group membership: ");
        additionalRoles.stream()
                .forEach(x -> logger.info(x.toString())
            );
        return additionalRoles;
    }


}
