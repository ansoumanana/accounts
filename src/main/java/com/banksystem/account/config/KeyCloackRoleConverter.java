package com.banksystem.account.config;


import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeyCloackRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {


    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String,Object> realmAcces = ( Map<String,Object>) jwt.getClaims().get("realm_access");

        if (realmAcces!= null || realmAcces.isEmpty())
            return Collections.EMPTY_LIST;
        Collection<GrantedAuthority>   grantedRoles  = ((List<String>) realmAcces.get("roles"))
                .stream().map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return grantedRoles;
    }

}
