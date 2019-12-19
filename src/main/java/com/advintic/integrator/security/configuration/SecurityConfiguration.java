/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advintic.integrator.security.configuration;

/**
 *
 * @author tele-cloud-server
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.sql.DataSource;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/webjars/**");
        web.ignoring().antMatchers("/static/**","/js/**","/css/**" , "/img/**", "/fonts/**", "/libs/**" , 
                "/callback" ,"/forget_password");
    }
    
     @Override
	  protected void configure(HttpSecurity http) throws Exception { // @formatter:off
	      http.requestMatchers()
	          .antMatchers("/login", "/oauth/authorize" , "/" )
	          .and()
	          .authorizeRequests()
                  .antMatchers("/forget_password", "/login**","/callback/", "/webjars/**", "/error**")
                  .permitAll()
	          .anyRequest()
	          .authenticated();
	  } // @formatter:on
	

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
