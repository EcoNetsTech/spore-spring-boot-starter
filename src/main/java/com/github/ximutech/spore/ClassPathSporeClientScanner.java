package com.github.ximutech.spore;

import com.github.ximutech.spore.retrofit.RetrofitClientFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * SporeClient路径扫描器
 *
 * @author ximu
 */
public class ClassPathSporeClientScanner extends ClassPathBeanDefinitionScanner {

    private final ClassLoader classLoader;

    public ClassPathSporeClientScanner(BeanDefinitionRegistry registry, ClassLoader classLoader) {
        super(registry, false);
        this.classLoader = classLoader;
    }

    public void registerFilters() {
        addIncludeFilter(new AnnotationTypeFilter(SporeClient.class));

        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            if (!metadataReader.getClassMetadata().isInterface()) {
                return true;
            }
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    /**
     * 扫描并注册到BeanDefinition
     */
    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            logger.warn("No SporeClient interface was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }
        return beanDefinitions;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();
            logger.info("Creating RetrofitClientFactoryBean with name '" + holder.getBeanName()
                    + "' and '" + definition.getBeanClassName() + "' httpInterface");
            definition.getConstructorArgumentValues()
                    .addGenericArgumentValue(Objects.requireNonNull(definition.getBeanClassName()));
            // beanClass全部设置为RetrofitClientFactoryBean
            definition.setBeanClass(RetrofitClientFactoryBean.class);
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        if (beanDefinition.getMetadata().isInterface()) {
            try {
                Class<?> target = ClassUtils.forName(
                        beanDefinition.getMetadata().getClassName(),
                        classLoader);
                return !target.isAnnotation();
            } catch (Exception ex) {
                logger.error("load class exception:", ex);
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            logger.warn("Skipping MapperFactoryBean with name '" + beanName
                    + "' and '" + beanDefinition.getBeanClassName() + "' mapperInterface"
                    + ". Bean already defined with the same name!");
            return false;
        }
    }
}
