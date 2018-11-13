/*******************************************************************************
 * Copyright 2013 André Rouél
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sf.uadetector;

import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

final class OperatingSystemSampleReader
{

	/**
	 * Character set of the file
	 */
	private static final String CHARSET = "UTF-8";

	/**
	 * Default log
	 */
	private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(OperatingSystemSampleReader.class.toString());

	public static List<OperatingSystemSample> readAll()
	{
		List<OperatingSystemSample> examples = new ArrayList<>();
		examples.addAll(read("samples/ANDROID.csv"));
		examples.addAll(read("samples/BADA.csv"));
		examples.addAll(read("samples/BSD.csv"));
		examples.addAll(read("samples/IOS.csv"));
		examples.addAll(read("samples/JVM.csv"));
		examples.addAll(read("samples/MAC_OS.csv"));
		examples.addAll(read("samples/OS_X.csv"));
		examples.addAll(read("samples/SYMBIAN.csv"));
		examples.addAll(read("samples/WEBOS.csv"));
		examples.addAll(read("samples/WINDOWS.csv"));
		return examples;
	}

	private static List<OperatingSystemSample> read(String file)
	{
		InputStream stream = UserAgentStringParserIntegrationTest.class.getClassLoader()
		                                                               .getResourceAsStream(file);

		CSVParser csvParser = null;
		try
		{
			csvParser = new CSVParser(new InputStreamReader(stream, CHARSET));
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.log(Level.WARNING, e.getLocalizedMessage(), e);
		}

		List<OperatingSystemSample> examples = new ArrayList<>();
		if (csvParser != null)
		{
			String[] line = null;
			int i = 0;
			do
			{

				try
				{
					line = csvParser.getLine();
				}
				catch (IOException e)
				{
					line = null;
					LOG.log(Level.WARNING, e.getLocalizedMessage(), e);
				}

				if (line != null)
				{
					i++;
					if (line.length == 4)
					{
						OperatingSystemFamily family = OperatingSystemFamily.valueOf(line[0]);
						String name = line[1];
						VersionNumber version = VersionParser.parseVersion(line[2]);
						String userAgent = line[3];
						examples.add(new OperatingSystemSample(family, name, version, userAgent));
					}
					else
					{
						LOG.warning("Can not read operating system example " + i + ", there are too few fields: " + line.length);
					}
				}
			}
			while (line != null);
		}
		return examples;
	}

}
