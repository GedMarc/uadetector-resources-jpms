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

import net.sf.uadetector.service.UADetectorServiceFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class KnownAgentsReader
{

	/**
	 * Character set of the file
	 */
	private static final String CHARSET = "UTF-8";

	/**
	 * File to read
	 */
	private static final String FILE = "known_agents.txt";

	/**
	 * Default log
	 */
	private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(KnownAgentsReader.class.toString());

	/**
	 * Parser to detect informations about an user agent
	 */
	private static final UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
	private static final char DELIMITER = ',';
	private static final char ESCAPE = '"';
	private static final char NEWLINE = '\n';
	/**
	 * CSV printer for detected version numbers to be particularly examined
	 */
	private final OutputStreamWriter printerForUncertainVersions;
	private final Map<OperatingSystemFamily, OutputStreamWriter> printers = new HashMap<>();

	/**
	 * Constructs an instance
	 *
	 * @throws FileNotFoundException
	 */
	public KnownAgentsReader() throws FileNotFoundException
	{
		File directory = new File("target/test-os-version-detection");
		String extension = ".csv";
		if (!directory.exists())
		{
			directory.mkdir();
		}
		for (OperatingSystemFamily family : OperatingSystemFamily.values())
		{
			printers.put(family, new OutputStreamWriter(new FileOutputStream(new File(directory.getPath() + "/" + family.toString()
			                                                                          + extension))));
		}
		printerForUncertainVersions = new OutputStreamWriter(new FileOutputStream(new File(directory.getPath() + "/" + "_uncertain"
		                                                                                   + extension)));
	}

	public void read()
	{
		InputStream stream = UserAgentStringParserIntegrationTest.class.getClassLoader()
		                                                               .getResourceAsStream(FILE);
		try
		{
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(stream, CHARSET));
			String userAgentString = null;
			do
			{
				userAgentString = reader.readLine();
				if (userAgentString != null)
				{
					ReadableUserAgent agent = parser.parse(userAgentString);
					OperatingSystemFamily family = agent.getOperatingSystem()
					                                    .getFamily();
					String name = agent.getOperatingSystem()
					                   .getName();
					String version = agent.getOperatingSystem()
					                      .getVersionNumber()
					                      .toVersionString();
					OutputStreamWriter printer = printers.get(family);
					if (OperatingSystemFamily.WINDOWS == family)
					{
						if (name.equals("Windows 3.x") && version.equals("3.1"))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else if (name.equals("Windows 95") && version.equals("95"))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else if (name.equals("Windows 98") && version.equals("98"))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else if (name.equals("Windows NT") && version.equals("4.0"))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else if (name.equals("Windows NT") && version.equals("4.1"))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else if (name.equals("Windows ME") && version.equals("4.90"))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else if (name.equals("Windows 2000") && version.equals("5.0"))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else if (name.equals("Windows XP") && version.equals("5.1"))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else if (name.equals("Windows 2003 Server") && version.equals("5.2"))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else if (name.equals("Windows Vista") && version.equals("6.0"))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else if (name.equals("Windows 7") && version.equals("6.1"))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else if (name.equals("Windows Phone 7") && version.matches("7((\\.\\d+)+)?"))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else if (name.equals("Windows 8") && version.equals("6.2"))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else if (name.equals("Windows") && version.matches("(\\d+)((\\.\\d+)+)?"))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else if (VersionNumber.UNKNOWN.equals(agent.getOperatingSystem()
						                                           .getVersionNumber()))
						{
							print(userAgentString, family, name, version, printer);
							continue;
						}
						else
						{
							print(userAgentString, family, name, version, printerForUncertainVersions);
						}
					}
					else
					{
						print(userAgentString, family, name, version, printer);
					}
				}
			}
			while (userAgentString != null);

		}
		catch (IOException e)
		{
			LOG.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Prints the given informations a CSV printer.
	 *
	 * @param userAgentString
	 * 		user agent string
	 * @param family
	 * 		operating system family
	 * @param name
	 * 		operating system name
	 * @param version
	 * 		detected version of the operating system
	 * @param printer
	 * 		CSV printer to print on
	 *
	 * @throws IOException
	 */
	private void print(String userAgentString, OperatingSystemFamily family, String name, String version,
	                   OutputStreamWriter printer) throws IOException
	{
		printer.append(ESCAPE);
		printer.append(family.toString());
		printer.append(ESCAPE);
		printer.append(DELIMITER);
		printer.append(ESCAPE);
		printer.append(name);
		printer.append(ESCAPE);
		printer.append(DELIMITER);
		printer.append(ESCAPE);
		printer.append(version);
		printer.append(ESCAPE);
		printer.append(DELIMITER);
		printer.append(ESCAPE);
		printer.append(userAgentString);
		printer.append(ESCAPE);
		printer.append(NEWLINE);
		printer.flush();
	}
}
