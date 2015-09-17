package freifunk.halle.tools.crawler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import freifunk.halle.tools.Config;

public class FreifunkCrawler {

	private void test() {
		ScheduledExecutorService _newScheduledThreadPool = Executors
				.newScheduledThreadPool(Config.parallelTasks);

	}

}
