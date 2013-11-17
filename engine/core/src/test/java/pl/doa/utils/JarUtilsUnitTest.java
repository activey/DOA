package pl.doa.utils;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import pl.doa.AbstractUnitTest;
import pl.doa.artifact.IJarEntryMatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.mockito.BDDMockito.given;

public class JarUtilsUnitTest extends AbstractUnitTest {

    @Mock
    private JarFile inputFile;

    @Mock
    private Enumeration<JarEntry> jarEntries;

    @Mock
    private IJarEntryMatcher matcher;

    @Test
    public void shouldNotReturnExistingInputStreamWhenInputIsFileAndEntryMatches() throws IOException {
        // given
        given(jarEntries.hasMoreElements()).willReturn(false);
        given(inputFile.entries()).willReturn(jarEntries);

        // when
        InputStream jarStream = JarUtils.findJarEntry(inputFile, matcher);

        // then
        Assert.assertNull(jarStream);
    }
}
