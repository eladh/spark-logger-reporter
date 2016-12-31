import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import static spark.Spark.post;

public class LogServer {

	private LoadingCache<String, Reporter> reporterMap = CacheBuilder.newBuilder().build(new CacheLoader<String, Reporter>() {
		@Override
		public Reporter load(String id) throws Exception {
			return new Reporter(id);
		}
	});

	public void startLine(String id, String lineId, String text, String system) {
		reporterMap.getUnchecked(id).startLine(lineId, text,system);
	}

	private void endLine(String id, String lineId, String text, String system) {
		Reporter reporter = reporterMap.getUnchecked(id);
		if (reporter.endLine(lineId, text,system)) {
			reporterMap.invalidate(id);
		}
	}

	public static void main(String[] args) {

		final LogServer server = new LogServer();

		post("/startLine", (request, response) -> {
			String system = request.queryParams("system");
			String id = request.queryParams("id");
			String lineId = request.queryParams("lineId");
			String text = request.queryParams("text");
			server.startLine(id, lineId, text, system);
			return "OK";
		});
		post("/endLine", (request, response) -> {
			String system = request.queryParams("system");
			String id = request.queryParams("id");
			String lineId = request.queryParams("lineId");
			String text = request.queryParams("text");
			server.endLine(id, lineId, text, system);
			return "OK";
		});
	}

}