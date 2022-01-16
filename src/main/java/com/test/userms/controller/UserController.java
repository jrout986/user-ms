package com.test.userms.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.test.userms.bean.User;
import com.test.userms.exception.UserNotFoundException;
import com.test.userms.repository.UserRepository;

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;
	
	@GetMapping("/users")
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	@GetMapping("/users/{userId}")
	public EntityModel<User> getUserByUserId(@PathVariable Integer userId){
		Optional<User> ifUser=userRepository.findById(userId);
		if(ifUser.isPresent()) {
			//return new ResponseEntity<User>(ifUser.get(), HttpStatus.FOUND);
			List<Link> links=new ArrayList<>();
			Link link=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllUsers()).withRel("all-users");
			links.add(link);
			EntityModel<User> model=EntityModel.of(ifUser.get(), links);
			return model;
		}else {
			throw new UserNotFoundException("userId-"+userId+" could not be found");
			//return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/users")
	public ResponseEntity<Object> saveUser(@RequestBody @Valid User user){
		User newUser=null;
		if(user.getId()==null) {
			newUser=userRepository.save(user);
		}else {
			throw new RuntimeException("User already exists");
		}
		URI uri= ServletUriComponentsBuilder.fromCurrentRequest()
		.path("/{userId}").buildAndExpand(newUser!=null?newUser.getId():"").toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<Object> deleteUserById(@PathVariable Integer userId){
		Optional<User> ifUser=userRepository.findById(userId);
		if(ifUser.isPresent()) {
			userRepository.deleteById(userId);
			return new ResponseEntity<Object>(ifUser.get(), HttpStatus.OK);
		}else {
			throw new UserNotFoundException("userId-"+userId+" could not be found");
		}
	}
}
