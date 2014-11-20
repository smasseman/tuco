package se.familjensmas.tuco.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author jorgen.smas@entercash.com
 */
@Controller
public class TucoController {

	@RequestMapping("/index.html")
	public void index() {
	}
}
