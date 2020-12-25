package io.dexiron.cloud.lib.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CloudWrapperConfig {

    String name;
    String host;
    Long port;
    Long ram;

}
