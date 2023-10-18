package com.dragontreesoftware.odyssey.generator;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class DataSetGenerator2 {

    public static void main(String[] args) {

        DataSetGenerator2 generator = new DataSetGenerator2();
        generator.test();
    }

    public void test() {
        Path localPublishDir = new File("/tmp/hollow/teams").toPath();

        HollowFilesystemPublisher publisher = new HollowFilesystemPublisher(localPublishDir);
        HollowFilesystemAnnouncer announcer = new HollowFilesystemAnnouncer(localPublishDir);

        HollowProducer producer = HollowProducer
                .withPublisher(publisher)
                .withAnnouncer(announcer)
                .build();

        producer.initializeDataModel(Team.class, Manager.class, Player.class);

        producer.runCycle(state -> {
            for(int i = 100; i < 132; i++) {
                state.add(buildTeam(i));
            }
        });

    }

    public static class Player {
        private String name;
        private List<Integer> positions = new LinkedList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Integer> getPositions() {
            return positions;
        }

        public void setPositions(List<Integer> positions) {
            this.positions = positions;
        }
    }

    public static class Manager {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @HollowPrimaryKey(fields="name")
    public static class Team {
        private String name;

        private Manager manager;

        private List<Player> players = new LinkedList<>();



        public Team() {
        }

        public Team(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Manager getManager() {
            return manager;
        }

        public void setManager(Manager manager) {
            this.manager = manager;
        }

        public List<Player> getPlayers() {
            return players;
        }

        public void setPlayers(List<Player> players) {
            this.players = players;
        }

    }

    private Team buildTeam(int id) {
        Random rand = new Random();

        Team team = new Team();
        team.setName("Team-" + id);

        Manager m = new Manager();
        m.setName("Bobby " + (char)('A' + id % 26));

        team.setManager(m);

        for(int i = 0; i < 9; i++) {
            Player p = new Player();
            p.setName("Speedy " + (char)('A' + (id + i + 16) % 26));
            int basePos = rand.nextInt(1, 6);
            for(int j = 0; j < rand.nextInt(1, 4); j++) {
                p.getPositions().add(j + basePos);
            }
            team.getPlayers().add(p);
        }
        return team;
    }

}
