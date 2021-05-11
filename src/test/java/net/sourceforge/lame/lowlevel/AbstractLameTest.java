package net.sourceforge.lame.lowlevel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Abstract base class holding some util methods.
 */
abstract class AbstractLameTest {

  protected void writeToFile(byte[] bytes, String filename) throws IOException {
    try (FileOutputStream stream = new FileOutputStream(filename)) {
      stream.write(bytes);
    }
  }

  protected byte[] firstTwoBytesOf(byte[] bytes) {
    return Arrays.copyOfRange(bytes, 0, 2);
  }

  /**
   * Taken from https://github.com/nwaldispuehl/interval-music-compositor/blob/master/intervalmusiccompositor.model/src/main/java/ch/retorte/intervalmusiccompositor/audiofile/AudioFileProperties.java
   */
  protected boolean compareByteArrayWithHexString(byte[] byteArray, String hexString) {
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
  protected byte[] hexStringToByteArray(String s) {
    s = s.replaceAll(" ", "");
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }

}
