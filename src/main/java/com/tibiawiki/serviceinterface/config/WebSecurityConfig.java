package com.tibiawiki.serviceinterface.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(jsr250Enabled = true, securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
              .authorizeRequests()
              .anyRequest()
              .authenticated()
              .and()
              .httpBasic();
    }

  @Override
  public void configure(WebSecurity web) {
    web.debug(true);
  }

    @Autowired(required = false)
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
  //        auth.inMemoryAuthentication()
  //                .withUser("user")
  //                .password("password")
  //                .roles("USER")
  //                .and()
  //                .withUser("admin")
  //                .password("password")
  //                .roles("USER", "ADMIN");
    }
}
