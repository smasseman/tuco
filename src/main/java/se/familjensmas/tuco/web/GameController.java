package se.familjensmas.tuco.web;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import se.familjensmas.tuco.Game;
import se.familjensmas.tuco.Status;

/**
 * @author jorgen.smas@entercash.com
 */
@Controller
public class GameController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private Game game;

	@RequestMapping("/startgame.html")
	public String start() {
		game.start();
		return "redirect:game.html";
	}

	@RequestMapping("/stopgame.html")
	public String stop() {
		game.stop();
		return "redirect:index.html";
	}

	@RequestMapping("/game.html")
	public void game() {
	}

	@RequestMapping(value = "/gamestatus.json", headers = "Accept=*/*", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody
	public Status<?> getStatus(@RequestParam(required = false) Integer modcount) throws InterruptedException {
		try {
			Status<?> s = game.getStatusManager().getStatus(modcount);
			logger.debug("Return " + s);
			return s;
		} catch (Throwable t) {
			logger.error("Request failed.", t);
			throw t;
		}
	}
}
