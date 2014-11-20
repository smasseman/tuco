package se.familjensmas.tuco.web;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import se.familjensmas.tuco.Config;
import se.familjensmas.tuco.Status;
import se.familjensmas.tuco.StatusManager;
import se.familjensmas.tuco.Target;
import se.familjensmas.tuco.TargetId;

/**
 * @author jorgen.smas@entercash.com
 */
@Controller
public class ControlpanelController {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource
	private Config config;

	private StatusManager<SystemStatus> statusMgr;

	private SystemStatus status;

	@PostConstruct
	public void init() {
		status = new SystemStatus();
		statusMgr = new StatusManager<SystemStatus>(status);
		config.addTargetListener((target) -> statusMgr.update(status));
	}

	@RequestMapping("controlpanel.html")
	public void getIndex(Model model) {
		status.setTargets(new ArrayList<Target>(config.allTargets()));
		model.addAttribute("status", status);
	}

	@RequestMapping("status.json")
	@ResponseBody
	public Status<SystemStatus> getStatus(@RequestParam(required = false) int modcount) throws InterruptedException {
		Status<SystemStatus> s = statusMgr.getStatus(modcount);
		logger.debug("Returned " + s);
		return s;
	}

	@RequestMapping("hit.json")
	@ResponseBody
	public String hit(@RequestParam int id) throws InterruptedException {
		config.getTarget(TargetId.fromInt(id)).getHitListener().trigger();
		return "OK";
	}
}
