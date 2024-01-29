package ch.unisg.ics.interactions.hmas.bindings.protocols.http;

import ch.unisg.ics.interactions.hmas.bindings.Output;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class HttpOutput implements Output {

  private final static Logger LOGGER = Logger.getLogger(HttpOutput.class.getCanonicalName());

  private final ClassicHttpResponse response;
  private Optional<String> data;

  public HttpOutput(ClassicHttpResponse response) {

    this.response = response;

    HttpEntity entity = response.getEntity();

    if (entity == null) {
      this.data = Optional.empty();
    } else {
      String encoding = entity.getContentEncoding() == null ? "UTF-8" : entity.getContentEncoding();

      try {
        this.data = Optional.of(IOUtils.toString(entity.getContent(), encoding));
        EntityUtils.consume(entity);
      } catch (IOException e) {
        String msg = e.getMessage();
        LOGGER.log(Level.WARNING, "{0}", msg);
      }
    }
  }

  @Override
  public Optional<Object> getData() {
    return Optional.empty();
  }

  public int getStatusCode() {
    return response.getCode();
  }

  public Map<String, String> getHeaders() {
    Header[] headers = response.getHeaders();
    Map<String, String> headerMap = new Hashtable<>();
    IntStream.range(0, headers.length).forEach(i -> {
      String key = headers[i].getName();
      String value = headers[i].getValue();
      headerMap.put(key, value);
    });
    return headerMap;
  }
}
