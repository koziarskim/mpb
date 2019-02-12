package com.noovitec.mpb.app;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.noovitec.mpb.entity.Supplier;

@Component
class MpbInitializer implements CommandLineRunner {

//    private final GroupRepo repository;

//    public MpbInitializer(GroupRepo repository) {
//        this.repository = repository;
//    }

    @Override
    public void run(String... strings) {
//    	Vendor vendor = Vendor.builder().name("Wallmart").build();
//        Stream.of("Denver JUG", "Utah JUG", "Seattle JUG",
//                "Richmond JUG").forEach(name ->
//                repository.save(new Group(name))
//        );
//
//        Group djug = repository.findByName("Denver JUG");
//        Event e = Event.builder().title("Full Stack Reactive")
//                .description("Reactive with Spring Boot + React")
//                .date(Instant.parse("2018-12-12T18:00:00.000Z"))
//                .build();
//        djug.setEvents(Collections.singleton(e));
//        repository.save(djug);
//
//        repository.findAll().forEach(System.out::println);
    }
}