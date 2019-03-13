package com.mzivkovic.flow.domain;

import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;

public class UserStore {

    private final ConcurrentHashMap<Long, User> map = new ConcurrentHashMap<>();

    public void init(long number) {
        for (long i = 0; i < number; i++) {
            put(new User(i, "department_" + i));
        }
    }


    public void put(User user) {
        map.put(user.getId(), user);
    }

    public User get(long id) {
        return map.get(id);
    }

    public Flux<User> departments() {
        return Flux.fromIterable(map.values());
    }

}
