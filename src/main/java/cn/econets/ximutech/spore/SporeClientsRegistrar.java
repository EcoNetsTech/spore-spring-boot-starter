package cn.econets.ximutech.spore;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * SporeClients注册器
 * @author ximu
 */
public class SporeClientsRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware {

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;


    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes
                .fromMap(annotationMetadata.getAnnotationAttributes(EnableSporeClients.class.getName()));
        if (annotationAttributes == null) {
            return;
        }

        String[] basePackages = getPackagesToScan(annotationAttributes);

        ClassPathSporeClientScanner scanner = new ClassPathSporeClientScanner(beanDefinitionRegistry, classLoader);
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }
        scanner.registerFilters();
        scanner.doScan(basePackages);

    }

    private String[] getPackagesToScan(AnnotationAttributes attributes) {
        String[] value = attributes.getStringArray("value");
        String[] basePackages = attributes.getStringArray("basePackages");
        Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
        if (!ObjectUtils.isEmpty(value)) {
            Assert.state(ObjectUtils.isEmpty(basePackages),
                    "@EnableSporeClients basePackages and value attributes are mutually exclusive");
        }
        Set<String> packagesToScan = new LinkedHashSet<>();
        packagesToScan.addAll(Arrays.asList(value));
        packagesToScan.addAll(Arrays.asList(basePackages));
        for (Class<?> basePackageClass : basePackageClasses) {
            packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
        }
        return packagesToScan.toArray(new String[0]);
    }
}
