package com.test.userms.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.test.userms.bean.DemoBean;
import com.test.userms.bean.User;
import com.test.userms.exception.UserNotFoundException;
import com.test.userms.repository.UserRepository;
import com.test.userms.util.MyProperties;

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MyProperties properties;
	
	@GetMapping("/users")
	public List<User> getAllUsers(){
		List<User> users=userRepository.findAll();
		System.out.println("Value from my-properties is:-"+properties.getValue());
		/*
		 * MappingJacksonValue mapping=new MappingJacksonValue(users);
		 * SimpleBeanPropertyFilter
		 * filter=SimpleBeanPropertyFilter.filterOutAllExcept("name","age","gender");
		 * FilterProvider filters=new SimpleFilterProvider().addFilter("display-user",
		 * filter); mapping.setFilters(filters);
		 */
		return users;
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
	
	@GetMapping("/test")
	public MappingJacksonValue test() {
		List<DemoBean> demos=new ArrayList<>();
		DemoBean demo1=new DemoBean("d1p1", "d1p2", "d1p3", "d1p4");
		DemoBean demo2=new DemoBean("d2p1", "d2p2", "d2p3", "d2p4");
		DemoBean demo3=new DemoBean("d3p1", "d3p2", "d3p3", "d3p4");
		DemoBean demo4=new DemoBean("d4p1", "d4p2", "d4p3", "d4p4");
		demos=Arrays.asList(demo1,demo2,demo3,demo4);
		
		MappingJacksonValue mapping=new MappingJacksonValue(demos);
		SimpleBeanPropertyFilter filter=SimpleBeanPropertyFilter.filterOutAllExcept("param1","param2");
		FilterProvider filters=new SimpleFilterProvider().addFilter("DemoBean-display", filter);
		mapping.setFilters(filters);
		return mapping;
	}
}
