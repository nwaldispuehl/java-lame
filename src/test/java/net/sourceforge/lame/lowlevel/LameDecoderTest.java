package net.sourceforge.lame.lowlevel;

import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for the {@link LameDecoder}. Also acts as a demonstrator for the tool.
 */
public class LameDecoderTest extends AbstractLameTest {

  //---- Constants

  private final static String WAVE_MAGIC_NUMBER = "52 49 46 46";
  private static final int WAV_TRACK_LENGTH = 1101356;

  private static final AudioFormat.Encoding WAV_TARGET_ENCODING = AudioFormat.Encoding.PCM_SIGNED;



  //---- Test methods

  @Test
  public void shouldConvertMp3ToWave() throws IOException, URISyntaxException {
    // given
    URL mp3TestFileUrl = LameDecoderTest.class.getResource("/test.mp3");
    File mp3TestFile = new File(mp3TestFileUrl.toURI());

    // when
    byte[] wavBytes = decodeFromMp3(mp3TestFile);
    writeToFile(wavBytes, "build/test_output.wav"); // Just for the records...

    // then
    assertTrue(compareByteArrayWithHexString(firstTwoBytesOf(wavBytes), WAVE_MAGIC_NUMBER));
    assertEquals(WAV_TRACK_LENGTH, wavBytes.length);
  }


  //---- Helper methods

  /**
   * The actual decoding method to be used in your own project.
   */
  private byte[] decodeFromMp3(File mp3File) throws IOException {
    LameDecoder decoder = new LameDecoder(mp3File.getAbsolutePath());

    ByteBuffer buffer = ByteBuffer.allocate(decoder.getBufferSize());
    ByteArrayOutputStream pcm = new ByteArrayOutputStream();

    while (decoder.decode(buffer)) {
      pcm.write(buffer.array());
    }

    return asWav(pcm.toByteArray(), decoder.getSampleRate(), decoder.getChannels());
  }

  /**
   * Converts a PCM byte array into a WAVE byte array, that is, adds the appropriate headers.
   * For this we first convert it into an {@link AudioInputStream}, and can then use this {@link AudioSystem}.
   */
  private byte[] asWav(byte[] pcmBytes, int sampleRate, int channels) throws IOException {
    AudioFormat wavAudioFormat = new AudioFormat(WAV_TARGET_ENCODING, sampleRate, 16, channels, 4, AudioSystem.NOT_SPECIFIED, false);
    AudioInputStream audioInputStream  = new AudioInputStream(new ByteArrayInputStream(pcmBytes), wavAudioFormat, (pcmBytes.length / wavAudioFormat.getFrameSize()));

    ByteArrayOutputStream wav = new ByteArrayOutputStream();
    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wav);
    return wav.toByteArray();
  }



}
