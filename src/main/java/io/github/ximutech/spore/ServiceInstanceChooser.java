package io.github.ximutech.spore;

import io.github.ximutech.spore.exception.ServiceInstanceChooseException;

import java.net.URI;

/**
 * 微服务选择器
 * @author ximu
 */
@FunctionalInterface
public interface ServiceInstanceChooser {

    URI choose(String serviceId);

    class NoValidServiceInstanceChooser implements ServiceInstanceChooser {

        @Override
        public URI choose(String serviceId) {
            throw new ServiceInstanceChooseException("No valid service instance selector, Please configure it!");
        }
    }
}
