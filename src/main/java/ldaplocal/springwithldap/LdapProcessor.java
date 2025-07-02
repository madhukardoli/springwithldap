
package ldaplocal.springwithldap;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class LdapProcessor {
    private final LdapTemplate ldapTemplate;

    public LdapProcessor(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public String process(String username) {
        try {
            var users = ldapTemplate.search(
                    LdapQueryBuilder.query().where("uid").is(username),
                    (AttributesMapper<String>) attrs -> attrs.get("cn").get().toString()
            );
            return users.isEmpty() ? "User not found" : users.get(0);
        } catch (Exception e) {
            return "Error querying LDAP: " + e.getMessage();
        }
    }
}
