package io.dexiron.cloud.wrapper;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main {

    private CloudBootsrap cloudBootsrap;

    public Main() {
        try {
            cloudBootsrap = new CloudBootsrap();
            cloudBootsrap.startWrapper();
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    cloudBootsrap.stopWrapper();
                }
            }));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Main();
    }

}
