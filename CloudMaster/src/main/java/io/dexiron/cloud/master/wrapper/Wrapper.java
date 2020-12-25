package io.dexiron.cloud.master.wrapper;

import io.dexiron.cloud.master.CloudBootsrap;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

public class Wrapper {

    @Getter
    private String name;

    @Getter
    @Setter
    private Channel channel;

    @Getter
    @Setter
    private int availableRam;

    @Getter
    @Setter
    private boolean ready;

    public Wrapper(String name, int availableRam) {
        this.name = name;
        this.availableRam = availableRam;
        this.ready = false;
    }

    public void onDisconnect() {
        CloudBootsrap.getInstance().getCloudManager().removeWrapper(this);
        System.out.println(String.format("Wrapper %s was unregistered on Master", this.name));
    }

    public void onConnect(Channel channel) {
        setChannel(channel);
        CloudBootsrap.getInstance().getCloudManager().getWrappperMap().put(name, this);
        System.out.println(String.format("Wrapper %s was registered on Master", this.name));

    }

}
