package io.github.eoinkanro.app.rtostranslator.ocr;

import io.github.eoinkanro.app.rtostranslator.utils.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class WindowsOcrProvider extends FileOcrProvider {

  private static final String SCRIPT_NAME = "WinOCR.ps1";
  private static final String EXIT_COMMAND = "EXIT";

  private final Process process;
  private final BufferedWriter processWriter;
  private final BufferedReader processReader;

  public WindowsOcrProvider() throws IOException, URISyntaxException {
    String pathToScript = Paths.get(WindowsOcrProvider.class.getClassLoader()
            .getResource(SCRIPT_NAME)
            .toURI())
        .toAbsolutePath().toString();

    ProcessBuilder pb = new ProcessBuilder(
        "powershell.exe", "-ExecutionPolicy", "Bypass", "-File", pathToScript
    );
    pb.redirectErrorStream(true);
    process = pb.start();

    processWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
  }

  @Override
  protected String getText(File imageFile) throws IOException {
    processWriter.write(imageFile.getAbsolutePath());
    processWriter.newLine();
    processWriter.flush();

    return processReader.readLine();
  }

  @Override
  public void close() {
    try {
      processWriter.write(EXIT_COMMAND);
      processWriter.newLine();
      processWriter.flush();

      processWriter.close();
      processReader.close();

      process.destroy();
    } catch (Exception e) {
        LogUtils.logError(e);
    }
  }


}
