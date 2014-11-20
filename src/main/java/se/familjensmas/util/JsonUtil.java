package se.familjensmas.util;

import java.io.StringWriter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author jorgen.smas@entercash.com
 */
public class JsonUtil {

	public static String toJson(Object e) {
		try {
			JsonFactory jf = new JsonFactory();
			ObjectMapper m = new ObjectMapper();
			StringWriter out = new StringWriter();
			JsonGenerator jg = jf.createGenerator(out);
			m.writeValue(jg, e);
			String json = out.toString();
			return json;
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
