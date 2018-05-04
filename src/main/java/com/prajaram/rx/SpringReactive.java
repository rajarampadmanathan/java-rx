package com.prajaram.rx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@EnableReactiveMongoRepositories
@EnableScheduling
public class SpringReactive {

	@Autowired
	PersonRepository presonRepo;

	@Autowired
	ReactiveMongoTemplate template;

	@Autowired
	ReactiveMongoOperations operations;

	public static void main(String[] args) {
		SpringApplication.run(SpringReactive.class, args);

	}

	@GetMapping(value = "/data", produces = { MediaType.TEXT_EVENT_STREAM_VALUE })
	public Object getData() {
		// return Flux.fromArray(new String[] {"Hello", "world", "program"});
		String[] data = new String[200];
		for (int i = 0; i < 200; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			data[i] = String.valueOf(i);
		}
		return data;
		// return new NetflixRx().getData();
	}

	@GetMapping(value = "/users", produces = { MediaType.TEXT_EVENT_STREAM_VALUE })
	public Flux<Person> getUsers() {

		// return presonRepo.findAllByLastName(Mono.just("Ram"));
		return presonRepo.findWithTailableCursorBy();
	}
	
	@GetMapping(value = "/user/{mailId}", produces = { MediaType.TEXT_EVENT_STREAM_VALUE })
	public Mono<Person> getUser( @PathVariable("mailId")String emailId) {

		// return presonRepo.findAllByLastName(Mono.just("Ram"));
		return presonRepo.findByEmailId(Mono.just(emailId));
	}
	
	@GetMapping(value = "/users/name/{fName}/{lName}", produces = { MediaType.TEXT_EVENT_STREAM_VALUE })
	public Flux<Person> getUsers( @PathVariable("fName")String fName,@PathVariable("lName")String lName) {

		// return presonRepo.findAllByLastName(Mono.just("Ram"));
		return presonRepo.findByFirstNameAndLastName(Mono.just(fName), Mono.just(lName));
	}
	
	@GetMapping(value = "/users/name/{lName}", produces = { MediaType.TEXT_EVENT_STREAM_VALUE })
	public Flux<Person> getUsers(@PathVariable("lName")String lName) {

		// return presonRepo.findAllByLastName(Mono.just("Ram"));
		return presonRepo.findAllByLastName( Mono.just(lName));
	}

	@Scheduled(fixedDelay = 5000)
	public void createData() {
		long id = System.currentTimeMillis();
		int i = (int) (id / 10);
		template.insertAll(Flux.just(new Person(i, "Raja", "Ram-"+i%2, "Rajaram" + i + "@gmail.com")).collectList())
				.subscribe(System.out::println, System.err::println);
		// System.out.println(presonRepo.count().subscribe(System.out::println,
		// System.err::println));
		/*
		 * Mono<MongoCollection<Document>> recreateCollection =
		 * operations.collectionExists(Person.class) // .flatMap(exists -> exists ?
		 * operations.dropCollection(Person.class) : Mono.just(exists)) //
		 * .then(operations.createCollection(Person.class, CollectionOptions.empty() //
		 * .size(1024 * 1024) // .maxDocuments(100) // .capped()));
		 * //System.out.println("recreateCollection:"+recreateCollection.subscribe(s->s.
		 * )); Flux<Person> insertAll = template .insertAll(Flux.just(new Person(i,
		 * "Raja", "Ram", "Rajaram" + i + "@gmail.com")).collectList());
		 * System.out.println("Database:"+template.getMongoDatabase().getName());
		 * System.out.println("Collection:"+template.getCollectionNames().subscribe(
		 * System.out::println));
		 * presonRepo.findByLastName(Mono.just("Ram")).subscribe(s->{System.out.println(
		 * s.getEmailId());});
		 */ // System.out.println("Created:" + insertAll.);
	}

}
