package com.prajaram.rx;

import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository extends ReactiveCrudRepository<Person, String>{

	
	@Tailable
	Flux<Person> findWithTailableCursorBy();
	
	Flux<Person> findAllByLastName(Mono<String> lastName);
	
	Mono<Person> findByEmailId(Mono<String> emailId);
	
	Flux<Person> findByFirstNameAndLastName(Mono<String> firstName,Mono<String> lastName);
}
