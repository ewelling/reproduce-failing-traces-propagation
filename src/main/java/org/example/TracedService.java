package org.example;

import io.opentelemetry.extension.annotations.WithSpan;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TracedService {

  private static final Logger LOG = Logger.getLogger(TracedService.class);

  @WithSpan()
  public void logEvent(MyEvent event) {
    LOG.infof("Event: %s", event);
  }
}
