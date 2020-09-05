package com.mahendracandi.chatbotgeneratereportapp.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahendracandi.chatbotgeneratereportapp.model.AuthRequest;
import com.mahendracandi.chatbotgeneratereportapp.model.AuthResponse;
import com.mahendracandi.chatbotgeneratereportapp.security.MyUserDetailService;
import com.mahendracandi.chatbotgeneratereportapp.util.JwtUtil;

@RestController
@CrossOrigin
@RequestMapping("/token")
public class AuthController {
	
	private static final Logger log = LogManager.getLogger(AuthController.class);
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	MyUserDetailService userDetailService;
	
	@Autowired
	JwtUtil jwtUtil;

	@PostMapping("/generateToken")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception{
		
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							authRequest.getUsername(), authRequest.getPassword()));
		} catch (BadCredentialsException e) {
			log.error("Bad credential {}", e);
			throw new Exception();
		}
		
		final UserDetails userDetail = userDetailService.loadUserByUsername(authRequest.getUsername());
		
		final String token = jwtUtil.generateToken(userDetail);
		
		return ResponseEntity.ok().body(new AuthResponse(token));
	}
}
