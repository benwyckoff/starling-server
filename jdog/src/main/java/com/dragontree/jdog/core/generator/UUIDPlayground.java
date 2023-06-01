package com.dragontree.jdog.core.generator;

import java.util.UUID;

public class UUIDPlayground {

    public static void main(String[] args) {

        for(int i = 0; i < 5; i++) {
            String uuid = UUID.randomUUID().toString();
            String[] parts = uuid.split("-");

            System.out.println("-----");
            System.out.println(uuid);
            for (String p : parts) {
                int pos = Math.abs(p.hashCode());
                System.out.printf("%s -> %d, %d\n", p, pos, pos % 6499);
            }
        }

    }
}
