package net.sourceforge.lame.lowlevel;

import net.sourceforge.lame.mp3.Lame;
import net.sourceforge.lame.mp3.MPEGMode;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for the {@link LameEncoder}. Also acts as a demonstrator for the tool.
 */
public class LameEncoderTest {

  //---- Constants

  private static final boolean USE_VARIABLE_BITRATE = false;
  private static final int GOOD_QUALITY_BITRATE = 256;

  private static final String MP3_MAGIC_NUMBER = "FF FB";
  private static final int MP3_TRACK_LENGTH = 198477;


  //---- Test methods

  @Test
  public void shouldConvertWaveToMp3() throws IOException, UnsupportedAudioFileException {
    // given
    InputStream wavTestFileInputStream = LameEncoderTest.class.getResourceAsStream("/test.wav");
    AudioInputStream wavTestFileAudioInputStream = AudioSystem.getAudioInputStream(wavTestFileInputStream);

    // when
    byte[] mp3Bytes = encodeToMp3(wavTestFileAudioInputStream);
    writeToFile(mp3Bytes, "build/test.mp3"); // Just for the records...

    // then
    assertTrue(compareByteArrayWithHexString(firstTwoBytesOf(mp3Bytes), MP3_MAGIC_NUMBER));
    assertEquals(MP3_TRACK_LENGTH, mp3Bytes.length);
  }


  //---- Helper methods

  /**
   * The actual encoding method to be used in your own project.
   */
  private byte[] encodeToMp3(AudioInputStream audioInputStream) throws IOException {
    LameEncoder encoder = new LameEncoder(audioInputStream.getFormat(), GOOD_QUALITY_BITRATE, MPEGMode.STEREO, Lame.QUALITY_HIGHEST, USE_VARIABLE_BITRATE);

    ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
    byte[] inputBuffer = new byte[encoder.getPCMBufferSize()];
    byte[] outputBuffer = new byte[encoder.getPCMBufferSize()];

    int bytesRead;
    int bytesWritten;

    while(0 < (bytesRead = audioInputStream.read(inputBuffer))) {
      bytesWritten = encoder.encodeBuffer(inputBuffer, 0, bytesRead, outputBuffer);
      mp3.write(outputBuffer, 0, bytesWritten);
    }

    encoder.close();
    return mp3.toByteArray();
  }

  private void writeToFile(byte[] bytes, String filename) throws IOException {
    try (FileOutputStream stream = new FileOutputStream(filename)) {
      stream.write(bytes);
    }
  }

  private byte[] firstTwoBytesOf(byte[] bytes) {
    return Arrays.copyOfRange(bytes, 0, 2);
  }

  /**
   * Taken from https://github.com/nwaldispuehl/interval-music-compositor/blob/master/intervalmusiccompositor.model/src/main/java/ch/retorte/intervalmusiccompositor/audiofile/AudioFileProperties.java
   */
  private boolean compareByteArrayWithHexString(byte[] byteArray, String hexString) {
    String normalizedHexString = hexString.replaceAll(" ", "");

    for (int i = 0; i < byteArray.length; i++) {
      byte b = byteArray[i];
      String hexItem = normalizedHexString.substring(i * 2, i * 2 + 2);

      if (b != hexStringToByteArray(hexItem)[0]) {
        return false;
      }
    }

    return true;
  }

  /**
   * Taken from http://stackoverflow.com/questions/11208479/how-do-i-initialize-a-byte-array-in-java/11208685#11208685
   */
  private byte[] hexStringToByteArray(String s) {
    s = s.replaceAll(" ", "");
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }

}
