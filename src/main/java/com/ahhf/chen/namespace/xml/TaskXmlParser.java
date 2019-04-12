package com.ahhf.chen.namespace.xml;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class TaskXmlParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String id = element.getAttribute("id");
        String name = element.getAttribute("name");
        String priority = element.getAttribute("priority");
        String relativeTask = element.getAttribute("relativeTask");

        BeanDefinition definition = new RootBeanDefinition(TaskXml.class);
        MutablePropertyValues propertyValues = definition.getPropertyValues();
        propertyValues.addPropertyValue("id", id);
        propertyValues.addPropertyValue("name", name);
        propertyValues.addPropertyValue("priority", priority);
        if (StringUtils.isNotEmpty(relativeTask)) {
            propertyValues.addPropertyValue("relativeTask", relativeTask);
        }
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        registry.registerBeanDefinition(name, definition);

        return definition;
    }

}
