package org.projectforge.web.test.config;

import java.util.HashMap;
import java.util.Map;

import org.projectforge.framework.persistence.attr.impl.GuiAttrSchemaService;
import org.projectforge.framework.persistence.attr.impl.GuiAttrSchemaServiceImpl;
import org.projectforge.renderer.custom.Formatter;
import org.projectforge.renderer.custom.FormatterFactory;
import org.projectforge.renderer.custom.MicromataFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import de.micromata.genome.db.jpa.tabattr.api.TimeableService;
import de.micromata.genome.db.jpa.tabattr.impl.TimeableServiceImpl;

@Configuration
@PropertySource("projectforgeTest.properties")
public class ProjectforgeWebTestConfiguration
{
  @Value("${projectforge.base.dir}")
  private String applicationDir;

  @Autowired
  private ApplicationContext applicationContext;

  @Bean
  public GuiAttrSchemaService guiAttrSchemaService()
  {
    GuiAttrSchemaServiceImpl ret = new GuiAttrSchemaServiceImpl();
    ret.setApplicationDir(applicationDir);
    return ret;
  }

  @Bean
  public TimeableService timeableService()
  {
    return new TimeableServiceImpl();
  }

  @Bean
  public FormatterFactory formatterFactory()
  {
    FormatterFactory fac = new FormatterFactory();
    Map<String, Formatter> formatters = new HashMap<>();
    formatters.put("Micromata", applicationContext.getBean(MicromataFormatter.class));
    fac.setFormatters(formatters);
    return fac;
  }

}
