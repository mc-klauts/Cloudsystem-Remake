package io.dexiron.cloud.master;

public class Main {

    private final CloudBootsrap cloudBootsrap = new CloudBootsrap();

    public Main() {
        cloudBootsrap.startMaster();

       // Runtime.getRuntime().addShutdownHook(new Thread(() -> cloudBootsrap.stopMaster()));
    }

    public static void main(String[] args) {
        new Main();
    }

}
