package se.familjensmas.tuco.web;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import se.familjensmas.tuco.Duel;
import se.familjensmas.tuco.Status;

/**
 * @author jorgen.smas@entercash.com
 */
@Controller
public class DuelController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private Duel duel;

	@RequestMapping("/startduel.html")
	public String start() {
		duel.start();
		return "redirect:duel.html";
	}

	@RequestMapping("/stopduel.html")
	public String stop() {
		duel.stop();
		return "redirect:index.html";
	}

	@RequestMapping("/duel.html")
	public void game() {
	}

	@RequestMapping(value = "/duelstatus.json", headers = "Accept=*/*", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody
	public Status<?> getStatus(@RequestParam(required = false) Integer modcount) throws InterruptedException {
		try {
			Status<?> s = duel.getStatusManager().getStatus(modcount);
			logger.debug("Return " + s);
			return s;
		} catch (Throwable t) {
			logger.error("Request failed.", t);
			throw t;
		}
	}
}
