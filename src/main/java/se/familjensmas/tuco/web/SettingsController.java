package se.familjensmas.tuco.web;

import java.io.IOException;
import java.util.function.BiConsumer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import se.familjensmas.tuco.Angle;
import se.familjensmas.tuco.Config;
import se.familjensmas.tuco.Servo;
import se.familjensmas.tuco.TargetId;
import se.familjensmas.tuco.TucoUtil;
import se.familjensmas.util.JsonUtil;

/**
 * @author jorgen.smas@entercash.com
 */
@Controller
public class SettingsController {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource
	private Config config;
	private ServerEventManager sem = new ServerEventManager();

	@PostConstruct
	public void registerConfigListener() {
		config.addListener((e) -> sem.pushEvent(new ServerEvent(toJson(e))));
	}

	private String toJson(Object e) {
		return JsonUtil.toJson(e);
	}

	@RequestMapping("settings.html")
	public void getSettings(Model model) {
		config.refresh();
		model.addAttribute("conf", config);
	}

	@RequestMapping("addconf.html")
	public String add(@RequestParam String key, @RequestParam String value) {
		config.set(key, value);
		return "redirect:settings.html";
	}

	@RequestMapping("removeconf.html")
	public String remove(@RequestParam String key) {
		config.remove(key);
		return "redirect:settings.html";
	}

	@RequestMapping("/config/changes")
	public void changes(HttpServletRequest request, HttpServletResponse response) throws IOException,
			InterruptedException {
		logger.debug("Getting changes...");
		sem.get(request, response);
	}

	@RequestMapping("/up.json")
	@ResponseBody
	public String up(@RequestParam int id) {
		TargetId targetId = TargetId.fromInt(id);
		config.getTarget(targetId).up();
		return "{}";
	}

	@RequestMapping("/down.json")
	@ResponseBody
	public String down(@RequestParam int id) {
		TargetId targetId = TargetId.fromInt(id);
		config.getTarget(targetId).down();
		return "{}";
	}

	@RequestMapping("setconf.json")
	@ResponseBody
	public SettingsResult getSettings(@RequestParam String key, @RequestParam String value) {
		return config.set(key, value);
	}

	@RequestMapping("/setUpAngle")
	@ResponseBody
	public String setUpAngle(@RequestParam int id, @RequestParam int angle) {
		return set(id, angle, config::setUpAngle);
	}

	@RequestMapping("/setCenterAngle")
	@ResponseBody
	public String setCenterAngle(@RequestParam int id, @RequestParam int angle) {
		return set(id, angle, config::setCenterAngle);
	}

	@RequestMapping("/setDownAngle")
	@ResponseBody
	public String setDownAngle(@RequestParam int id, @RequestParam int angle) {
		return set(id, angle, config::setDownAngle);
	}

	@RequestMapping("/setDownDuration")
	@ResponseBody
	public String setDownDuration(@RequestParam int id, @RequestParam long duration) {
		config.setDownDuration(duration);
		return "{}";
	}

	@RequestMapping("/setUpDuration")
	@ResponseBody
	public String setUpDuration(@RequestParam int id, @RequestParam long duration) {
		config.setUpDuration(duration);
		return "{}";
	}

	@RequestMapping("/setCenterFromUpDuration")
	@ResponseBody
	public String setCenterFromUpDuration(@RequestParam int id, @RequestParam long duration) {
		config.setCenterFromUpDuration(duration);
		return "{}";
	}

	@RequestMapping("/setCenterFromDownDuration")
	@ResponseBody
	public String setCenterFromDownDuration(@RequestParam int id, @RequestParam long duration) {
		config.setCenterFromDownDuration(duration);
		return "{}";
	}

	private String set(int id, int angle, BiConsumer<TargetId, Angle> method) {
		try {
			TargetId sid = TargetId.fromInt(id);
			Angle a = Angle.fromInt(angle);
			Servo s = config.getTarget(sid).getServo();
			s.set(a);
			TucoUtil.sleep(200);
			s.off();
			method.accept(sid, a);
			return "{}";
		} catch (Exception e) {
			logger.error("Failed", e);
			return "{}";
		}
	}
}
