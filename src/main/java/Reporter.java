
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.fusesource.jansi.Ansi.ansi;

/**
 *
 */
@SuppressWarnings("AssignmentToMethodParameter")
public class Reporter {

	private static final Logger reporter = LoggerFactory.getLogger(Reporter.class);

	private final String id;

	private List<Triple<String, String, String>> lines = Lists.newArrayList();

	private int index;

	public Reporter(String id) {
		this.id = id;
	}

	public void startLine(String id, String text, String system) {
		addLine(id, text,system);
		index++;
	}


	/**
	 * @return if report has ended
	 */
	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	public boolean endLine(String id, String line, String system) {
		index--;
		addLine(id, line, system);
		//print once first line is ending
		boolean shouldReport = index < 1;
		if (shouldReport) {
			createReport();
		}
		return shouldReport;
	}

	private void createReport() {
		//build report
		final StringBuilder sb = new StringBuilder(16);
		for (int i = 0; i < lines.size(); i++) {
			//if exist next row && check if same id in the next line , do not add this line to report
			boolean nextRowExist = lines.size() > i + 1;
			Triple<String, String, String> line = lines.get(i);
			String system = line.getRight();

			boolean sameIdInNextRow = nextRowExist && line.getLeft().equals(lines.get(i + 1).getLeft()) && system.equals(lines.get(i + 1).getRight());
			if (!sameIdInNextRow) {
				//add to report
				sb.append(line.getMiddle()).append(ansi().fgBrightBlack().a(String.format(" [%s]", system))).append("\n");
			}
		}
		reporter.info("\n" + sb.toString());
		//clear lines for next round
		lines.clear();
	}

	private void addLine(String id, String line, String system) {
		lines.add(Triple.of(id, addTabs(line),system));
	}

	private String addTabs(String line) {
		for (int i = 0; i < index; i++) {
			line = String.format("\t%s", line);
		}
		switch (index) {
			case 0:
				return ansi().fgBlue().a(line).toString();
			case 1:
				return ansi().fgBrightBlue().a(line).toString();
			case 2:
				return ansi().fgBrightMagenta().a(line).toString();
			case 3:
				return ansi().fgMagenta().a(line).toString();
		}

		return ansi().fgDefault().a(line).toString();
	}

}