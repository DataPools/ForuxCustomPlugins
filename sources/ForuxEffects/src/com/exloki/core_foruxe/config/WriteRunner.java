package com.exloki.core_foruxe.config;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WriteRunner implements Runnable
{
	protected static final Logger LOGGER = Logger.getLogger("Minecraft");
	protected static final Charset UTF8 = Charset.forName("UTF-8");
	
	private final File configFile;
	private final String data;
	private final AtomicInteger pendingDiskWrites;

	WriteRunner(final File configFile, final String data, final AtomicInteger pendingDiskWrites)
	{
		this.configFile = configFile;
		this.data = data;
		this.pendingDiskWrites = pendingDiskWrites;
	}

	@Override
	public void run()
	{
		//long startTime = System.nanoTime();
		synchronized (configFile)
		{
			if (pendingDiskWrites.get() > 1)
			{
				// Writes can be skipped, because they are stored in a queue (in the executor).
				// Only the last is actually written.
				pendingDiskWrites.decrementAndGet();
				//LOGGER.log(Level.INFO, configFile + " skipped writing in " + (System.nanoTime() - startTime) + " nsec.");
				return;
			}
			try
			{
				Files.createParentDirs(configFile);

				if (!configFile.exists())
				{
					try
					{
						LOGGER.log(Level.INFO, "Creating empty configuration file: " + configFile.toString());
						if (!configFile.createNewFile())
						{
							LOGGER.log(Level.SEVERE, "Failed to create configuration file: " + configFile.toString());
							return;
						}
					}
					catch (IOException ex)
					{
						LOGGER.log(Level.SEVERE, "Failed to create configuration file: " + configFile.toString(), ex);
						return;
					}
				}

				final FileOutputStream fos = new FileOutputStream(configFile);
				try
				{
					final OutputStreamWriter writer = new OutputStreamWriter(fos, UTF8);

					try
					{
						writer.write(data);
					}
					finally
					{
						writer.close();
					}
				}
				finally
				{
					fos.close();
				}
			}
			catch (IOException e)
			{
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			finally
			{
				//LOGGER.log(Level.INFO, configFile + " written to disk in " + (System.nanoTime() - startTime) + " nsec.");
				pendingDiskWrites.decrementAndGet();
			}
		}
	}
}