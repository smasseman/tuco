package se.familjensmas.tuco.web;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import se.familjensmas.tuco.Training;

/**
 * @author jorgen.smas@entercash.com
 */
@Controller
public class TrainingController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private Training traning;

	@RequestMapping("/starttraining.html")
	public String start() {
		logger.debug("Start game.");
		traning.start();
		return "redirect:training.html";
	}

	@RequestMapping("/stoptraining.html")
	public String stop() {
		logger.debug("Stop game.");
		traning.stop();
		return "redirect:index.html";
	}

	@RequestMapping("/training.html")
	public void training(Model model) {
		model.addAttribute("running", traning.isRunning());
	}
}
